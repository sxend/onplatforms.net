package arimitsu.sf.platform.accounts

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import arimitsu.sf.platform.accounts.directive.{AuthenticationDirective, TemplateDirective}
import arimitsu.sf.platform.accounts.external.TwitterOps
import arimitsu.sf.platform.accounts.kvs.Memcached
import arimitsu.sf.platform.accounts.router._
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContext

object AccountsSystem {
  val config: Config = ConfigFactory.load
  val namespace = "arimitsu.sf.platform.accounts"
  def withNamespace(suffix: String) = s"$namespace.$suffix"
  def getConfigInNamespace(suffix: String) = config.getConfig(withNamespace(suffix))
  def main(args: Array[String]): Unit = {
    val env = new {
      val config: Config = AccountsSystem.config
      implicit val system = ActorSystem("platform-system", this.config)
      implicit val materializer = ActorMaterializer()
      val blockingContext: ExecutionContext =
        system.dispatchers.lookup(withNamespace("dispatchers.blocking-io-dispatcher"))
      val logger = system.log
      val templateDirectiveImplicits = TemplateDirective.Implicits(this)
      val authenticationDirectiveImplicits = AuthenticationDirective.Implicits(this)
      val accountsRouter = new SigninRouter(this)
      val memcached = new Memcached(this)
      val twitter = new TwitterOps(this)
    }
    import env._
    val route = logRequest("access-log", Logging.InfoLevel) {
      get(path("")(accountsRouter.handle)) ~
        get(path("signin")(accountsRouter.signup)) ~
        get(path("signin" / "twitter")(accountsRouter.twitterSignin)) ~
        get(path("signin" / "twitter-callback")(accountsRouter.twitterCallback)) ~
        get(path("signout")(accountsRouter.signout))
    }
    val accountsConfig = getConfigInNamespace("system")
    Http().bindAndHandle(route, accountsConfig.getString("listen-address"), accountsConfig.getInt("listen-port"))
  }
}
