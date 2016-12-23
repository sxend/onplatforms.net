package net.onplatforms.accounts

import java.util.UUID
import java.util.concurrent.atomic.{AtomicInteger, AtomicReference}

import akka.actor.{ActorRef, ActorRefFactory, ActorSystem, Props, TypedActor, TypedProps}
import akka.event.{Logging, LoggingAdapter}
import akka.event.Logging._
import akka.http.scaladsl._
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import net.onplatforms.accounts.provider.SessionProvider
import net.onplatforms.accounts.service.{AuthenticationService, CacheService, UserService}
import net.onplatforms.lib.directive.TemplateDirective
import net.onplatforms.lib.kvs.Memcached
import net.onplatforms.lib.rdb.MySQL

import scala.concurrent.{ExecutionContext, Future, Promise}

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
    lazy val authenticationService: () => ActorRef =
      singleton(system.actorOf(Props(classOf[AuthenticationService], this), ActorNames.AuthenticationService.name))
    lazy val userService: () => ActorRef =
      singleton(system.actorOf(Props(classOf[UserService], this), ActorNames.UserService.name))
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
            }
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
  private def singleton[A](creator: => A): () => A = {
    var actor: Option[A] = None // non thread-safe!!!
    () => {
      if (actor.isEmpty) {
        actor = Option(creator)
      }
      actor.get
    }
  }
  object ActorNames {
    class Generator(basename: String) {
      private val seqNr = new AtomicInteger(0)
      def name: String = s"${basename}_${seqNr.incrementAndGet()}"
    }
    class SingletonGenerator(basename: String) extends Generator(basename) {
      override def name: String = basename
    }
    val AuthenticationService: Generator = new SingletonGenerator("authentication_service")
    val UserService: Generator = new SingletonGenerator("user_service")
  }
}
