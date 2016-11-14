package arimitsu.sf.platform.directive

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import arimitsu.sf.platform.kvs.Memcached
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.util.{ Failure, Success }

trait AuthenticationDirective {
  import AuthenticationDirective._
  def authenticated(route: (String, String) => Route)(implicit implicits: AuthenticationDirective.Implicits) = cookie(key) { cookiePair =>
    import implicits._
    onComplete(env.memcached.client.get[String](cookiePair.value)) {
      case Success(Some(userId)) =>
        onComplete(env.memcached.client.get[String](userId)) {
          case Success(Some(userName)) => route(userId, userName)
          case Success(_)              => top
          case Failure(t) =>
            env.logger.error(t, t.getMessage)
            top
        }
      case Success(_) => top
      case Failure(t) =>
        env.logger.error(t, t.getMessage)
        top
    }
  }
  private def top = redirect("/", StatusCodes.Found)
  def setSession(sessionId: String, userId: String)(route: Route)(implicit implicits: AuthenticationDirective.Implicits) = {
    import implicits._
    onComplete(env.memcached.client.set[String](sessionId, userId, timeout)) {
      case Success(u) =>
        setCookie(HttpCookie(key, sessionId, maxAge = Option(86400 * 30 * 12), path = Option("/")))(route)
      case _ => reject
    }
  }
}

object AuthenticationDirective extends AuthenticationDirective {
  private val config = ConfigFactory.load.getConfig("arimitsu.sf.platform.directives.authentication")
  private val key = config.getString("session-key")
  private val timeout = config.getInt("timeout").seconds
  case class Implicits(env: {
    val memcached: Memcached
    val logger: LoggingAdapter
  })
}