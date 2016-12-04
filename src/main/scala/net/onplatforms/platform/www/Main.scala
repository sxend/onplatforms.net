package net.onplatforms.platform.www

import akka.actor.ActorSystem
import akka.event.Logging._
import akka.event.LoggingAdapter
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import net.onplatforms.platform.lib.directive.{AuthenticationDirective, TemplateDirective}
import net.onplatforms.platform.lib.kvs.Memcached
import net.onplatforms.platform.www.router._
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContext

object Main {
  val config: Config = ConfigFactory.load
  val namespace = "net.onplatforms.platform.www"
  def withNamespace(suffix: String) = s"$namespace.$suffix"
  def getConfigInNamespace(suffix: String): Config = config.getConfig(withNamespace(suffix))
  val systemConfig = getConfigInNamespace("system")
  def main(args: Array[String]): Unit = {
    val env = new {
      val config: Config = Main.config
      val namespace: String = Main.namespace
      val version: String = systemConfig.getString("version")
      implicit val system = ActorSystem("platform-system", this.config)
      implicit val materializer = ActorMaterializer()
      val blockingContext: ExecutionContext =
        system.dispatchers.lookup(withNamespace("dispatchers.blocking-io-dispatcher"))
      val logger: LoggingAdapter = system.log
      val templateDirectiveImplicits = TemplateDirective.Implicits(this)
      val authenticationDirectiveImplicits = AuthenticationDirective.Implicits(this)
      val indexRouter = new IndexRouter(this)
      val mypageRouter = new MypageRouter(this)
      val signupRouter = new SignupRouter(this)
      val memcached = new Memcached(this)
    }
    import env._

    val mapping = Seq(
      get(path("")(indexRouter.handle)),
      post(path("signup")(signupRouter.handle)),
      get(path("mypage")(mypageRouter.handle))
    ).foldLeft[Route](reject)(_ ~ _)

    val route = logRequest("access-log", InfoLevel)(mapping)
    Http().bindAndHandle(route, systemConfig.getString("listen-address"), systemConfig.getInt("listen-port"))
  }
}
