package net.onplatforms.platform.ranter

import akka.actor.ActorSystem
import akka.event.Logging.InfoLevel
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import net.onplatforms.platform.lib.directive.{AuthenticationDirective, TemplateDirective}
import net.onplatforms.platform.lib.kvs.Memcached
import net.onplatforms.platform.ranter.router.{AccountRouter, IndexRouter, RantRouter}
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContext

object RanterSystem {
  val config: Config = ConfigFactory.load
  val namespace = "net.onplatforms.platform.ranter"
  def withNamespace(suffix: String) = s"$namespace.$suffix"
  def getConfigInNamespace(suffix: String = ""): Config = config.getConfig(withNamespace(suffix))
  def main(args: Array[String]): Unit = {
    val env = new {
      val config: Config = RanterSystem.config
      val namespace: String = RanterSystem.namespace
      val version: String = "latest"
      implicit val system = ActorSystem("platform-system", this.config)
      implicit val materializer = ActorMaterializer()
      val blockingContext: ExecutionContext =
        system.dispatchers.lookup(withNamespace("dispatchers.blocking-io-dispatcher"))
      val logger: LoggingAdapter = system.log
      val templateDirectiveImplicits = TemplateDirective.Implicits(this)
      val accountRouter = new AccountRouter(this)
      val rantRouter = new RantRouter(this)
      val indexRouter = new IndexRouter(this)
    }
    import env._

    val mapping = Seq(
      indexRouter.routes,
      rantRouter.routes,
      accountRouter.routes
    ).foldLeft[Route](reject)(_ ~ _)

    val route = logRequest("access-log", InfoLevel)(mapping)
    val wwwConfig = getConfigInNamespace("system")
    Http().bindAndHandle(route, wwwConfig.getString("listen-address"), wwwConfig.getInt("listen-port"))
  }
}
