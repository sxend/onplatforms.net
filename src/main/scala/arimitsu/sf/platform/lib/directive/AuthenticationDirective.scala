package arimitsu.sf.platform.lib.directive

import java.util.UUID

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import arimitsu.sf.platform.lib.kvs.Memcached
import com.typesafe.config.Config
import org.apache.commons.lang3.SerializationUtils

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }
trait AuthenticationDirective {
  import AuthenticationDirective._
  def authenticated(route: (String, String) => Route)(implicit implicits: AuthenticationDirective.Implicits) = {
    import implicits._
    requireValidSession { session =>
      val userId = session("userId").asInstanceOf[String]
      onComplete(env.memcached.client.get[String](userId)) {
        case Success(Some(userName)) => route(userId, userName)
        case Success(_)              => gotoTop
        case Failure(t) =>
          env.logger.error(t, t.getMessage)
          gotoTop
      }
    }
  }
  def requireValidSession(f: => Session => Route)(implicit implicits: Implicits) =
    optionalCookie(implicits.cookieKey) {
      case Some(pair) =>
        onComplete(getFromMemcache(pair.value)) {
          case Success(Some(bytes)) => onDeserialize(bytes)(f)
          case Success(None)        => reject
          case Failure(t)           => failWith(t)
        }
      case _ => reject
    }
  def getOrNewSession(f: => Session => Route)(implicit implicits: Implicits) = {
    optionalCookie(implicits.cookieKey) {
      case Some(pair) =>
        onComplete(getFromMemcache(pair.value)) {
          case Success(Some(bytes)) => onDeserialize(bytes)(f, _ => newSession(f))
          case _                    => newSession(f)
        }
      case _ => newSession(f)
    }
  }
  def newSession(f: Session => Route, onFail: => OnFail = failWith)(implicit implicits: Implicits) = {
    import implicits._
    val newSessionId = UUID.randomUUID().toString
    val newSession: Session = Map(cookieKey -> newSessionId)
    onSerialize(newSession) { bytes =>
      onComplete(setToMemcache(newSessionId, bytes)) {
        case Success(_) => setCookie(HttpCookie(cookieKey, newSessionId,
          maxAge = Some(maxAge),
          path = Some(path),
          domain = Some(domain))) {
          f(newSession)
        }
        case Failure(t) => onFail(t)
      }
    }
  }

  def invalidateAndNewSession(route: Session => Route)(implicit implicits: Implicits) = newSession(route)

  def invalidateSession(route: Route)(implicit implicits: Implicits) =
    deleteCookie(implicits.cookieKey, path = implicits.path, domain = implicits.domain)(route)

  def persistSession(newSession: Session)(route: Route, onFail: => OnFail = failWith)(implicit implicits: Implicits) =
    onSerialize(newSession) { bytes =>
      onComplete(setToMemcache(newSession(implicits.cookieKey).toString, bytes)) {
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
    Future(SerializationUtils.serialize(session.asInstanceOf[Serializable]))(implicits.env.blockingContext)
  private def deserialize(bytes: Array[Byte])(implicit implicits: Implicits): Future[Session] =
    Future(SerializationUtils.deserialize[Session](bytes))(implicits.env.blockingContext)
  private def gotoTop = redirect("/", StatusCodes.Found)
}

object AuthenticationDirective extends AuthenticationDirective {
  import net.ceedubs.ficus.Ficus._
  type Session = Map[String, Any]
  type OnFail = Throwable => Route

  case class Implicits(env: {
    val memcached: Memcached
    val logger: LoggingAdapter
    val blockingContext: ExecutionContext
    val config: Config
    val namespace: String
  }) {
    private val config = env.config.getConfig(s"${env.namespace}.directives.authentication")
    private val sessionConfig = config.getConfig("session")
    val cookieKey = sessionConfig.getString("cookie.key")
    val maxAge = sessionConfig.as[FiniteDuration]("cookie.max-age").toSeconds
    val path = sessionConfig.getString("cookie.path")
    val domain = sessionConfig.getString("cookie.domain")
  }
}