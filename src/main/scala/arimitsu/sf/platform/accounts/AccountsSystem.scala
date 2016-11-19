package arimitsu.sf.platform.accounts

import akka.actor.ActorSystem
import akka.event.Logging
import akka.event.Logging._
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import arimitsu.sf.platform.lib.directive.AuthenticationDirective
import arimitsu.sf.platform.accounts.external.TwitterOps
import arimitsu.sf.platform.lib.kvs.Memcached
import arimitsu.sf.platform.accounts.router._
import arimitsu.sf.platform.lib.directive.TemplateDirective
import com.typesafe.config.{ Config, ConfigFactory }

import scala.concurrent.ExecutionContext

object AccountsSystem {
  val config: Config = ConfigFactory.load
  val namespace = "arimitsu.sf.platform.accounts"
  def withNamespace(suffix: String) = s"$namespace.$suffix"
  def getConfigInNamespace(suffix: String) = config.getConfig(withNamespace(suffix))
  def main(args: Array[String]): Unit = {
    val env = new {
      val config: Config = AccountsSystem.config
      val namespace: String = AccountsSystem.namespace
      val version: String = "latest"
      implicit val system = ActorSystem("platform-system", this.config)
      implicit val materializer = ActorMaterializer()
      val blockingContext: ExecutionContext =
        system.dispatchers.lookup(withNamespace("dispatchers.blocking-io-dispatcher"))
      val logger = system.log
      val templateDirectiveImplicits = TemplateDirective.Implicits(this)
      val authenticationDirectiveImplicits = AuthenticationDirective.Implicits(this)
      val signupRouter = new SignupRouter(this)
      val indexRouter = new IndexRouter(this)
      val _indexRouter = new IndexRouter(this)
      val memcached = new Memcached(this)
      val twitter = new TwitterOps(this)
    }
    import env._
    val mapping = Seq(
      get(path("")(indexRouter.handle)),
      get(path("signup")(signupRouter.handle)),
      get(path("signin" / "twitter")(signupRouter.twitterSignin)),
      get(path("signin" / "twitter-callback")(signupRouter.twitterCallback)),
      get(path("signout")(signupRouter.signout))
    ).foldLeft(get(Directives.reject))(_ ~ _)

    val route = logRequest("access-log", InfoLevel)(mapping)
    val accountsConfig = getConfigInNamespace("system")
    Http().bindAndHandle(route, accountsConfig.getString("listen-address"), accountsConfig.getInt("listen-port"))
  }
}
