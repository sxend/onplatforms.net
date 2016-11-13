package arimitsu.sf.platform.directive

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import arimitsu.sf.platform.kvs.Memcached
import com.typesafe.config.ConfigFactory

import scala.util.Success
import scala.concurrent.duration._

trait AuthenticationDirective {
  import AuthenticationDirective._
  def authenticated(route: Route)(implicit implicits: AuthenticationDirective.Implicits) = cookie(key) { cookiePair =>
    import implicits._
    onComplete(env.memcached.client.get[String](cookiePair.value)) {
      case Success(Some(v)) => route
      case _                => reject
    }
  }
  def setSession(value: String)(route: Route)(implicit implicits: AuthenticationDirective.Implicits) = cookie(key) { cookiePair =>
    import implicits._
    onComplete(env.memcached.client.set[String](key, value, timeout)) {
      case Success(u) => route
      case _          => reject
    }
  }
}

object AuthenticationDirective extends AuthenticationDirective {
  private val config = ConfigFactory.load.getConfig("arimitsu.sf.platform.directives.authentication")
  private val key = config.getString("session-key")
  private val timeout = config.getInt("timeout").millis
  case class Implicits(env: {
    val memcached: Memcached
  })
}