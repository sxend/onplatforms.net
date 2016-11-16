package arimitsu.sf.platform.www

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import arimitsu.sf.platform.www.directive.{ AuthenticationDirective, SessionDirective, TemplateDirective }
import arimitsu.sf.platform.www.external.TwitterOps
import arimitsu.sf.platform.www.kvs.Memcached
import arimitsu.sf.platform.www.router._
import com.typesafe.config.{ Config, ConfigFactory }

import scala.concurrent.ExecutionContext

object PlatformSystem {
  val config: Config = ConfigFactory.load("www/application.conf").withFallback(ConfigFactory.load())
  val namespace = "arimitsu.sf.platform.www"
  def withNamespace(suffix: String) = s"$namespace.$suffix"
  def getConfigWithNamespace(suffix: String) = config.getConfig(withNamespace(suffix))
  def main(args: Array[String]): Unit = {
    val env = new {
      val config: Config = PlatformSystem.config
      implicit val system = ActorSystem("platform-system", this.config)
      implicit val materializer = ActorMaterializer()
      val blockingContext: ExecutionContext =
        system.dispatchers.lookup(withNamespace("dispatchers.blocking-io-dispatcher"))
      val logger = system.log
      val templateDirectiveImplicits = TemplateDirective.Implicits(this)
      val authenticationDirectiveImplicits = AuthenticationDirective.Implicits(this)
      val sessionDirectiveImplicits = SessionDirective.Implicits(this)
      val indexRouter = new IndexRouter(this)
      val mypageRouter = new MypageRouter(this)
      val signinRouter = new SigninRouter(this)
      val memcached = new Memcached(this)
      val twitter = new TwitterOps(this)
    }
    import env._
    val route = logRequest("access-log", Logging.InfoLevel) {
      get(path("")(indexRouter.handle)) ~
        get(path("mypage")(mypageRouter.handle)) ~
        get(path("signin")(signinRouter.handle)) ~
        get(path("signin" / "twitter")(signinRouter.twitterSignin)) ~
        get(path("signin" / "twitter-callback")(signinRouter.twitterCallback)) ~
        get(path("signout")(signinRouter.signout))
    }
    Http().bindAndHandle(route, "0.0.0.0", 8080)
  }
}
