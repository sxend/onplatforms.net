package arimitsu.sf.platform

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import arimitsu.sf.platform.directive.{ AuthenticationDirective, SessionDirective, TemplateDirective }
import arimitsu.sf.platform.external.TwitterOps
import arimitsu.sf.platform.kvs.Memcached
import arimitsu.sf.platform.router._

object Bootstrap {
  def main(args: Array[String]): Unit = {
    val env = new {
      implicit val system = ActorSystem("platform-system")
      implicit val materializer = ActorMaterializer()
      val logger = system.log
      val templateDirectiveImplicits = TemplateDirective.Implicits(this)
      val authenticationDirectiveImplicits = AuthenticationDirective.Implicits(this)
      val sessionDirectiveImplicits = SessionDirective.Implicits(this)
      lazy val indexRouter = new IndexRouter(this)
      lazy val mypageRouter = new MypageRouter(this)
      lazy val signinRouter = new SigninRouter(this)
      lazy val memcached = new Memcached(this)
      lazy val twitter = new TwitterOps(this)
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
