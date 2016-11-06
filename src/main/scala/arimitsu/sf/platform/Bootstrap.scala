package arimitsu.sf.platform

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.stream.ActorMaterializer
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import arimitsu.sf.platform.interceptor.LoggingInterceptor
import router._

object Bootstrap {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("platform-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher
    val env = new {
      val loggingInterceptor = () => new LoggingInterceptor(this)
      val indexRouter = () => IndexRouter(this)
    }
    val logging = env.loggingInterceptor()
    val route = {
      logging.intercept {
        get(path("")(env.indexRouter().handle))
      }
    }
    Http().bindAndHandle(route, "0.0.0.0", 8080)
  }
}
