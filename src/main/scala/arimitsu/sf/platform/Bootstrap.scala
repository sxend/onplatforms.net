package arimitsu.sf.platform

import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.ActorMaterializer
import akka.http.scaladsl._
import arimitsu.sf.platform.directive.{ AuthenticationDirective, TemplateDirective }
import arimitsu.sf.platform.external.Twitter
import arimitsu.sf.platform.kvs.Memcached
import server.Directives._
import router._

object Bootstrap {
  def main(args: Array[String]): Unit = {
    val env = new {
      implicit val system = ActorSystem("platform-system")
      implicit val materializer = ActorMaterializer()
      val logger = system.log
      val templateDirectiveImplicits = TemplateDirective.Implicits(this)
      val authenticationDirectiveImplicits = AuthenticationDirective.Implicits(this)
      lazy val indexRouter = new IndexRouter(this)
      lazy val mypageRouter = new MypageRouter(this)
      lazy val signinRouter = new SigninRouter(this)
      lazy val memcached = new Memcached(this)
      lazy val twitter = new Twitter(this)
    }
    import env._
    val route = logRequest("access-log", Logging.InfoLevel) {
      get(path("")(indexRouter.handle)) ~
        get(path("mypage")(mypageRouter.handle)) ~
        get(path("signin")(signinRouter.handle)) ~
        get(path("signin" / "twitter")(signinRouter.twitter)) ~
        get(path("signin" / "twitter-callback")(signinRouter.twitterCallback))
    }
    Http().bindAndHandle(route, "0.0.0.0", 8080)
  }
}
