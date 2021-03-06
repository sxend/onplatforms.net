package net.onplatforms.www

import akka.actor.ActorSystem
import akka.event.Logging
import akka.event.Logging._
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import net.onplatforms.lib.directive.TemplateDirective
import net.onplatforms.www.router._

import scala.concurrent.ExecutionContext

object Main {
  val config: Config = ConfigFactory.load
  val namespace = "net.onplatforms.www"
  def withNamespace(suffix: String) = s"$namespace.$suffix"
  def getConfigInNamespace(suffix: String): Config = config.getConfig(withNamespace(suffix))
  val systemConfig: Config = getConfigInNamespace("system")
  def main(args: Array[String]): Unit = {
    val env = new {
      val config: Config = Main.config
      val namespace: String = Main.namespace
      val version: String = systemConfig.getString("version")
      implicit val system = ActorSystem("www-system", this.config)
      val logger = Logging(system.eventStream, getClass)
      implicit val materializer = ActorMaterializer()
      val blockingContext: ExecutionContext =
        system.dispatchers.lookup(withNamespace("dispatchers.blocking-io-dispatcher"))
      val templateDirectiveImplicits = TemplateDirective.Implicits(this)
      val indexRouter = new IndexRouter(this)
      val socketRouter = new SocketRouter(this)
    }
    import env._

    val mapping = concat(
      get(path("")(indexRouter.handle)),
      path("socket")(socketRouter.handle)
    )

    val route = logRequest("access", InfoLevel)(mapping)
    Http().bindAndHandle(route, systemConfig.getString("listen-address"), systemConfig.getInt("listen-port"))
  }
}
