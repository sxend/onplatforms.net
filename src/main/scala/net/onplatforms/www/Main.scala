package net.onplatforms.www

import akka.actor.ActorSystem
import akka.event.Logging._
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import net.onplatforms.lib.directive.{AuthenticationDirective, TemplateDirective}
import net.onplatforms.lib.kvs.Memcached
import net.onplatforms.www.router._
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContext

object Main {
  val config: Config = ConfigFactory.load
  val namespace = "net.onplatforms.www"
  def withNamespace(suffix: String) = s"$namespace.$suffix"
  def getConfigInNamespace(suffix: String): Config = config.getConfig(withNamespace(suffix))
  val systemConfig = getConfigInNamespace("system")
  def main(args: Array[String]): Unit = {
    val env = new {
      val config: Config = Main.config
      val namespace: String = Main.namespace
      val version: String = systemConfig.getString("version")
      implicit val system = ActorSystem("www-system", this.config)
      val log = Logging(system.eventStream, getClass)
      implicit val materializer = ActorMaterializer()
      val blockingContext: ExecutionContext =
        system.dispatchers.lookup(withNamespace("dispatchers.blocking-io-dispatcher"))
      val logger: LoggingAdapter = system.log
      val templateDirectiveImplicits = TemplateDirective.Implicits(this)
      val indexRouter = new IndexRouter(this)
    }
    import env._

    val mapping = Seq(
      get(path("")(indexRouter.handle))
    ).foldLeft[Route](reject)(_ ~ _)

    val route = logRequest("access-log", InfoLevel)(mapping)
    Http().bindAndHandle(route, systemConfig.getString("listen-address"), systemConfig.getInt("listen-port"))
  }
}
