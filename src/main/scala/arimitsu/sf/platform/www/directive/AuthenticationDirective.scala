package arimitsu.sf.platform.www.directive

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import arimitsu.sf.platform.www.kvs.Memcached
import com.typesafe.config.{ Config, ConfigFactory }

import scala.concurrent.duration._
import scala.util.{ Failure, Success }
import Directives._
import arimitsu.sf.platform.www.PlatformSystem
trait AuthenticationDirective {
  import AuthenticationDirective._
  def authenticated(route: (String, String) => Route)(implicit implicits: AuthenticationDirective.Implicits) = {
    import implicits._
    implicit val sessionImplicits = implicits.env.sessionDirectiveImplicits
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
  private def gotoTop = redirect("/", StatusCodes.Found)
}

object AuthenticationDirective extends AuthenticationDirective {
  case class Implicits(env: {
    val memcached: Memcached
    val logger: LoggingAdapter
    val sessionDirectiveImplicits: SessionDirective.Implicits
  }) {
    private val config = PlatformSystem.getConfigWithNamespace("directives.session")
    val cookieKey = config.getString("cookie.key")
  }
}