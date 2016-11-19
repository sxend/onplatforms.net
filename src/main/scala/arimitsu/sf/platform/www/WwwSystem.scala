package arimitsu.sf.platform.www

import akka.actor.ActorSystem
import akka.event.Logging._
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import arimitsu.sf.platform.lib.directive.{ AuthenticationDirective, TemplateDirective }
import arimitsu.sf.platform.lib.kvs.Memcached
import arimitsu.sf.platform.www.router._
import com.typesafe.config.{ Config, ConfigFactory }

import scala.concurrent.ExecutionContext

object WwwSystem {
  val config: Config = ConfigFactory.load
  val namespace = "arimitsu.sf.platform.www"
  def withNamespace(suffix: String) = s"$namespace.$suffix"
  def getConfigInNamespace(suffix: String) = config.getConfig(withNamespace(suffix))
  def main(args: Array[String]): Unit = {
    val env = new {
      val config: Config = WwwSystem.config
      val namespace: String = WwwSystem.namespace
      val version: String = "latest"
      implicit val system = ActorSystem("platform-system", this.config)
      implicit val materializer = ActorMaterializer()
      val blockingContext: ExecutionContext =
        system.dispatchers.lookup(withNamespace("dispatchers.blocking-io-dispatcher"))
      val logger = system.log
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
      get(path("signup")(signupRouter.handle)),
      get(path("mypage")(mypageRouter.handle))
    ).foldLeft(get(Directives.reject))(_ ~ _)

    val route = logRequest("access-log", InfoLevel)(mapping)
    val wwwConfig = getConfigInNamespace("system")
    Http().bindAndHandle(route, wwwConfig.getString("listen-address"), wwwConfig.getInt("listen-port"))
  }
}
