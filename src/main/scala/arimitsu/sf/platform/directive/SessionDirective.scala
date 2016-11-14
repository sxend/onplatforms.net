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
import scala.collection.JavaConverters._
import scala.util.{ Failure, Success }

trait SessionDirective extends AnyRef with MemcachedCodecs {
  import SessionDirective._
  def withSession(f: => Session => Route)(implicit implicits: Implicits) = {
    optionalCookie("session-id") {
      case Some(pair) =>
        onComplete(implicits.env.memcached.client.get[Array[Byte]](pair.value)) {
          case Success(Some(bytes)) => f(deserialize(bytes))
          case _                    => newScope(f)
        }
      case _ => newScope(f)
    }
  }
  private def newScope(f: Session => Route)(implicit implicits: Implicits) = {
    val newSessionId = UUID.randomUUID().toString
    val newSession: Session = Map("id" -> newSessionId)
    onComplete(implicits.env.memcached.client.set[Array[Byte]](newSessionId, serialize(newSession), Int.MaxValue.seconds)) {
      case Success(_) => setCookie(HttpCookie("session-id", newSessionId, path = Option("/"), maxAge = Option(86400 * 30))) {
        f(newSession)
      }
      case Failure(t) => failWith(t)
    }
  }

  def invalidateAndNewSession(route: Route)(implicit implicits: Implicits) = newScope((_) => route)

  def invalidateSession(route: Route)(implicit implicits: Implicits) =
    deleteCookie("session-id", path = "/")(route)

  def persistSession(newSession: Session)(route: Route)(implicit implicits: Implicits) =
    onComplete(implicits.env.memcached.client.set[Array[Byte]](newSession("id").toString, serialize(newSession), Int.MaxValue.seconds)) {
      case Success(_) => route
      case Failure(t) => failWith(t)
    }
  private def serialize(session: Session): Array[Byte] = {
    SerializationUtils.serialize(session.asInstanceOf[Serializable])
  }

  private def deserialize(bytes: Array[Byte]): Session = {
    SerializationUtils.deserialize[Session](bytes)
  }
}

object SessionDirective extends SessionDirective {
  type Session = Map[String, Any]
  case class Implicits(env: {
    val memcached: Memcached
    val system: ActorSystem
  }) {
  }
}