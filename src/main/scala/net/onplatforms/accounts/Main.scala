package net.onplatforms.accounts

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{ActorRefFactory, ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.event.Logging._
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import net.onplatforms.accounts.service.AuthenticationService
import net.onplatforms.lib.directive.TemplateDirective
import net.onplatforms.lib.rdb.MySQL

import scala.concurrent.ExecutionContext

object Main {
  val config: Config = ConfigFactory.load
  val namespace = "net.onplatforms.accounts"
  def withNamespace(suffix: String) = s"$namespace.$suffix"
  def getConfigInNamespace(suffix: String): Config = config.getConfig(withNamespace(suffix))
  val systemConfig: Config = getConfigInNamespace("system")
  def main(args: Array[String]): Unit = {
    val env = new {
      val config: Config = Main.config
      val namespace: String = Main.namespace
      val version: String = systemConfig.getString("version")
      implicit val system: ActorSystem = ActorSystem("accounts-system", this.config)
      val logger: LoggingAdapter = Logging(system.eventStream, getClass)
      implicit val materializer = ActorMaterializer()
      val blockingContext: ExecutionContext =
        system.dispatchers.lookup(withNamespace("dispatchers.blocking-io-dispatcher"))
      val mysql: MySQL = new MySQL(this)
      val templateDirectiveImplicits = TemplateDirective.Implicits(this)
      val authenticationService = (context: ActorRefFactory) => context.actorOf(Props(classOf[AuthenticationService], this), ActorNames.AuthenticationService.name)
      val indexRouter = new router.IndexRouter(this)
      val signupRouter = new router.SignupRouter(this)
    }
    import env._

    val mapping = Seq(
      get(path("")(indexRouter.handle)),
      signupRouter.routes
    ).foldLeft[Route](reject)(_ ~ _)

    val route = logRequest("access", InfoLevel)(mapping)
    Http().bindAndHandle(route, systemConfig.getString("listen-address"), systemConfig.getInt("listen-port"))
  }
  object ActorNames {
    case class Generator(basename: String) {
      private val seqNr = new AtomicInteger(0)
      def name: String = s"${basename}_${seqNr.incrementAndGet()}"
    }
    val AuthenticationService = Generator("authentication_service")
  }
}
