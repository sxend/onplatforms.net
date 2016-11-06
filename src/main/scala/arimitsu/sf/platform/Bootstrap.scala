package arimitsu.sf.platform

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.stream.ActorMaterializer
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import router._

object Bootstrap {
  def main(args: Array[String]): Unit = {
    val env = new {
      implicit val system = ActorSystem("platform-system")
      implicit val materializer = ActorMaterializer()
      lazy val indexRouter = new IndexRouter(this)
    }
    import env._
    val route = logRequestResult("access-log", Logging.InfoLevel) {
      get(path("")(indexRouter.handle))
    }
    Http().bindAndHandle(route, "0.0.0.0", 8080)
  }
}
