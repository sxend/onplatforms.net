package arimitsu.sf.platform.directive

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import arimitsu.sf.platform.kvs.Memcached
import org.apache.commons.lang3.SerializationUtils
import shade.memcached.MemcachedCodecs

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{ Failure, Success }

trait SessionDirective extends AnyRef with MemcachedCodecs {
  import SessionDirective._
  def withSession(f: => Session => Route)(implicit implicits: Implicits) = {
    optionalCookie("session-id") {
      case Some(pair) =>
        onComplete(getFromMemcache(pair.value)) {
          case Success(Some(bytes)) => onDeserialize(bytes)(f)(_ => newScope(f))
          case _                    => newScope(f)
        }
      case _ => newScope(f)
    }
  }
  private def newScope(f: Session => Route)(implicit implicits: Implicits) = {
    val newSessionId = UUID.randomUUID().toString
    val newSession: Session = Map("id" -> newSessionId)
    onSerialize(newSession) { bytes =>
      onComplete(setToMemcache(newSessionId, bytes)) {
        case Success(_) => setCookie(HttpCookie("session-id", newSessionId, path = Option("/"), maxAge = Option(86400 * 30))) {
          f(newSession)
        }
        case Failure(t) => failWith(t)
      }
    }
  }

  def invalidateAndNewSession(route: Session => Route)(implicit implicits: Implicits) = newScope(route)

  def invalidateSession(route: Route)(implicit implicits: Implicits) =
    deleteCookie("session-id", path = "/")(route)

  def persistSession(newSession: Session)(route: Route)(implicit implicits: Implicits) =
    onSerialize(newSession) { bytes =>
      onComplete(setToMemcache(newSession("id").toString, bytes)) {
        case Success(_) => route
        case Failure(t) => failWith(t)
      }
    }

  private def setToMemcache(id: String, bytes: Array[Byte])(implicit implicits: Implicits) =
    implicits.env.memcached.client.set[Array[Byte]](id, bytes, Int.MaxValue.seconds)
  private def getFromMemcache(id: String)(implicit implicits: Implicits) =
    implicits.env.memcached.client.get[Array[Byte]](id)

  private def onSerialize(newSession: Session)(route: Array[Byte] => Route)(implicit implicits: Implicits) =
    onComplete(serialize(newSession)) {
      case Success(bytes) => route(bytes)
      case Failure(t)     => failWith(t)
    }
  private def onDeserialize(bytes: Array[Byte])(route: Session => Route)(onFail: => Throwable => Route = failWith)(implicit implicits: Implicits) =
    onComplete(deserialize(bytes)) {
      case Success(session) => route(session)
      case Failure(t)       => onFail(t)
    }
  private def serialize(session: Session)(implicit implicits: Implicits): Future[Array[Byte]] =
    Future(SerializationUtils.serialize(session.asInstanceOf[Serializable]))(implicits.ioDispatcher)
  private def deserialize(bytes: Array[Byte])(implicit implicits: Implicits): Future[Session] =
    Future(SerializationUtils.deserialize[Session](bytes))(implicits.ioDispatcher)
}

object SessionDirective extends SessionDirective {
  type Session = Map[String, Any]
  case class Implicits(env: {
    val memcached: Memcached
    val system: ActorSystem
  }) {
    val ioDispatcher = env.system.dispatchers.lookup("arimitsu.sf.platform.dispatchers.blocking-io-dispatcher")
  }
}