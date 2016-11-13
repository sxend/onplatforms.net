package arimitsu.sf.platform

import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.ActorMaterializer
import akka.http.scaladsl._
import arimitsu.sf.platform.directive.{ AuthenticationDirective, AuthenticationDirective$, TemplateDirective }
import arimitsu.sf.platform.kvs.Memcached
import server.Directives._
import router._

import scala.util.Success

object Bootstrap {
  def main(args: Array[String]): Unit = {
    val env = new {
      implicit val system = ActorSystem("platform-system")
      implicit val materializer = ActorMaterializer()
      implicit val templateDirectiveImplicits: TemplateDirective.Implicits = TemplateDirective.Implicits(this)
      implicit val authenticationDirectiveImplicits: AuthenticationDirective.Implicits = AuthenticationDirective.Implicits(this)
      lazy val indexRouter = new IndexRouter(this)
      lazy val mypageRouter = new MypageRouter(this)
      lazy val memcached = new Memcached(this)
    }
    import env._
    val route = logRequest("access-log", Logging.InfoLevel) {
      get(path("")(indexRouter.handle)) ~
        get(path("mypage")(mypageRouter.handle))
    }
    Http().bindAndHandle(route, "0.0.0.0", 8080)
  }
}
