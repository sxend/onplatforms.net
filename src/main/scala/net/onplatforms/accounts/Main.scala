package net.onplatforms.accounts

import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{ActorRef, ActorRefFactory, ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.event.Logging._
import akka.http.scaladsl._
import akka.http.scaladsl.model.headers.{HttpCookie, RawHeader}
import akka.http.scaladsl.server.Directives.{pathPrefix, _}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import net.onplatforms.accounts.provider.SessionProvider
import net.onplatforms.accounts.router.AuthenticationRouter
import net.onplatforms.accounts.service.AuthenticationService
import net.onplatforms.lib.directive.TemplateDirective
import net.onplatforms.lib.kvs.Memcached
import net.onplatforms.lib.rdb.MySQL

import scala.concurrent.ExecutionContext

object Main {
  val config: Config = ConfigFactory.load
  val namespace = "net.onplatforms.accounts"
  def withNamespace(suffix: String) = s"$namespace.$suffix"
  def getConfigInNamespace(suffix: String): Config = config.getConfig(withNamespace(suffix))
  val systemConfig: Config = getConfigInNamespace("system")
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
    val memcached: Memcached = new Memcached(this)
    val templateDirectiveImplicits = TemplateDirective.Implicits(this)
    val authenticationService: (ActorRefFactory) => ActorRef = (context: ActorRefFactory) => context.actorOf(Props(classOf[AuthenticationService], this), ActorNames.AuthenticationService.name)
    val indexRouter = new router.IndexRouter(this)
    val signupRouter = new router.AuthenticationRouter(this)
    val homeRouter = new router.HomeRouter(this)
  }
  def main(args: Array[String]): Unit = {
    import env._
    val server = new {} with Runnable with SessionProvider {
      override val memcached: Memcached = env.memcached
      override def run(): Unit = {
        val mapping = {
          pathPrefix("api" / "v1") {
            checkCSRFToken {
              signupRouter.routes ~
              homeRouter.routes
            } ~
              setCSRFToken(path("token")(complete(Empty())))
          } ~
            setCSRFToken(get(indexRouter.handle))
        } ~ reject
        val route = logRequest("access", InfoLevel)(mapping)
        Http().bindAndHandle(route, systemConfig.getString("listen-address"), systemConfig.getInt("listen-port"))
      }
    }
    server.run()
  }

  object ActorNames {
    case class Generator(basename: String) {
      private val seqNr = new AtomicInteger(0)
      def name: String = s"${basename}_${seqNr.incrementAndGet()}"
    }
    val AuthenticationService = Generator("authentication_service")
  }
}
