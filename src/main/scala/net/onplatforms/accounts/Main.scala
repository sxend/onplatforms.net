package net.onplatforms.accounts

import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{ActorRef, ActorRefFactory, ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.event.Logging._
import akka.http.scaladsl._
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import net.onplatforms.accounts.provider.SessionProvider
import net.onplatforms.accounts.service.{AuthenticationService, CacheService}
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
    val cacheService: CacheService = new CacheService(this)
    val indexRouter = new router.IndexRouter(this)
    val signupRouter = new router.AuthenticationRouter(this)
    val homeRouter = new router.HomeRouter(this)
  }
  def main(args: Array[String]): Unit = {
    import env._
    val server = new {} with Runnable with SessionProvider {
      override val cacheService: CacheService = env.cacheService
      override def run(): Unit = {
        val mapping = {
          pathPrefix("api" / "v1") {
            checkCSRFToken {
              signupRouter.routes ~ // self token setting
                withNewCSRFToken {
                  homeRouter.routes
                }
            } ~
              withNewCSRFToken(post(path("token")(complete(Empty())))) // token genarete endpoint
          } ~
            path("favicon.ico")(getFromResource("favicon.ico")) ~
            get(indexRouter.handle) // index page
        } ~ reject
        val route = access(InfoLevel)(mapping)
        Http().bindAndHandle(route, systemConfig.getString("listen-address"), systemConfig.getInt("listen-port"))
      }
    }
    server.run()
  }

  def access(level: LogLevel): Directive0 = Directive { inner => ctx =>
    env.logger.log(level, s"access: ${ctx.request.copy(entity = HttpEntity("#concealing#"))}")
    inner(())(ctx)
  }

  object ActorNames {
    case class Generator(basename: String) {
      private val seqNr = new AtomicInteger(0)
      def name: String = s"${basename}_${seqNr.incrementAndGet()}"
    }
    val AuthenticationService = Generator("authentication_service")
  }
}
