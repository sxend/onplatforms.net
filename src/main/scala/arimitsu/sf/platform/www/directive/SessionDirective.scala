package arimitsu.sf.platform.www.directive

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import arimitsu.sf.platform.www.kvs.Memcached
import com.typesafe.config.ConfigFactory
import org.apache.commons.lang3.SerializationUtils
import shade.memcached.MemcachedCodecs

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{ Failure, Success }

trait SessionDirective extends AnyRef with MemcachedCodecs {
  import SessionDirective._
  def requireValidSession(f: => Session => Route)(implicit implicits: Implicits) =
    optionalCookie(cookieKey) {
      case Some(pair) =>
        onComplete(getFromMemcache(pair.value)) {
          case Success(Some(bytes)) => onDeserialize(bytes)(f)
          case Success(None)        => reject
          case Failure(t)           => failWith(t)
        }
      case _ => reject
    }
  def getOrNewSession(f: => Session => Route)(implicit implicits: Implicits) = {
    optionalCookie(cookieKey) {
      case Some(pair) =>
        onComplete(getFromMemcache(pair.value)) {
          case Success(Some(bytes)) => onDeserialize(bytes)(f, _ => newSession(f))
          case _                    => newSession(f)
        }
      case _ => newSession(f)
    }
  }
  def newSession(f: Session => Route, onFail: => OnFail = failWith)(implicit implicits: Implicits) = {
    val newSessionId = UUID.randomUUID().toString
    val newSession: Session = Map("id" -> newSessionId)
    onSerialize(newSession) { bytes =>
      onComplete(setToMemcache(newSessionId, bytes)) {
        case Success(_) => setCookie(HttpCookie(cookieKey, newSessionId, maxAge = Option(86400 * 30 * 12), path = Option("/"))) {
          f(newSession)
        }
        case Failure(t) => onFail(t)
      }
    }
  }

  def invalidateAndNewSession(route: Session => Route)(implicit implicits: Implicits) = newSession(route)

  def invalidateSession(route: Route)(implicit implicits: Implicits) =
    deleteCookie(cookieKey, path = "/")(route)

  def persistSession(newSession: Session)(route: Route, onFail: => OnFail = failWith)(implicit implicits: Implicits) =
    onSerialize(newSession) { bytes =>
      onComplete(setToMemcache(newSession("id").toString, bytes)) {
        case Success(_) => route
        case Failure(t) => onFail(t)
      }
    }

  private def setToMemcache(id: String, bytes: Array[Byte])(implicit implicits: Implicits) =
    implicits.env.memcached.client.set[Array[Byte]](id, bytes, Int.MaxValue.seconds)
  private def getFromMemcache(id: String)(implicit implicits: Implicits) =
    implicits.env.memcached.client.get[Array[Byte]](id)

  private def onSerialize(newSession: Session)(route: Array[Byte] => Route, onFail: => OnFail = failWith)(implicit implicits: Implicits) =
    onComplete(serialize(newSession)) {
      case Success(bytes) => route(bytes)
      case Failure(t)     => onFail(t)
    }
  private def onDeserialize(bytes: Array[Byte])(route: Session => Route, onFail: => OnFail = failWith)(implicit implicits: Implicits) =
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
  type OnFail = Throwable => Route
  private val config = ConfigFactory.load.getConfig("arimitsu.sf.platform.directives.session")
  private val cookieKey = config.getString("cookie.key")
  case class Implicits(env: {
    val memcached: Memcached
    val system: ActorSystem
  }) {
    val ioDispatcher = env.system.dispatchers.lookup("arimitsu.sf.platform.dispatchers.blocking-io-dispatcher")
  }
}