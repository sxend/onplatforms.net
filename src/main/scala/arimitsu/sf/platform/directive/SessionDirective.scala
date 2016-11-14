package arimitsu.sf.platform.directive

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import arimitsu.sf.platform.kvs.Memcached
import org.apache.commons.lang3.SerializationUtils
import shade.memcached.MemcachedCodecs

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{ Failure, Success }

trait SessionDirective extends AnyRef with MemcachedCodecs {
  import SessionDirective._
  def withSession(f: => Session => Route)(implicit implicits: Implicits) = {
    optionalCookie("session-id") {
      case Some(pair) =>
        onComplete(implicits.env.memcached.client.get[Array[Byte]](pair.value)) {
          case Success(Some(bytes)) => onComplete(deserialize(bytes)) {
            case Success(session) => f(session)
            case _                => newScope(f)
          }
          case _ => newScope(f)
        }
      case _ => newScope(f)
    }
  }
  private def newScope(f: Session => Route)(implicit implicits: Implicits) = {
    val newSessionId = UUID.randomUUID().toString
    val newSession: Session = Map("id" -> newSessionId)
    onComplete(serialize(newSession)) {
      case Success(bytes) =>
        onComplete(implicits.env.memcached.client.set[Array[Byte]](newSessionId, bytes, Int.MaxValue.seconds)) {
          case Success(_) => setCookie(HttpCookie("session-id", newSessionId, path = Option("/"), maxAge = Option(86400 * 30))) {
            f(newSession)
          }
          case Failure(t) => failWith(t)
        }
      case Failure(t) => failWith(t)
    }

  }

  def invalidateAndNewSession(route: Session => Route)(implicit implicits: Implicits) = newScope(route)

  def invalidateSession(route: Route)(implicit implicits: Implicits) =
    deleteCookie("session-id", path = "/")(route)

  def persistSession(newSession: Session)(route: Route)(implicit implicits: Implicits) =
    onComplete(serialize(newSession)) {
      case Success((bytes)) =>
        onComplete(implicits.env.memcached.client.set[Array[Byte]](newSession("id").toString, bytes, Int.MaxValue.seconds)) {
          case Success(_) => route
          case Failure(t) => failWith(t)
        }
      case Failure(t) => failWith(t)
    }

  private def serialize(session: Session)(implicit implicits: Implicits): Future[Array[Byte]] = {
    Future(SerializationUtils.serialize(session.asInstanceOf[Serializable]))(implicits.ioDispatcher)
  }

  private def deserialize(bytes: Array[Byte])(implicit implicits: Implicits): Future[Session] = {
    Future(SerializationUtils.deserialize[Session](bytes))(implicits.ioDispatcher)
  }
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