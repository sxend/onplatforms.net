package arimitsu.sf.platform.directive

import akka.actor.ActorSystem
import akka.http.scaladsl.server.directives.BasicDirectives.{ extractSettings => _, pass => _ }
import akka.http.scaladsl.server.directives.CacheConditionDirectives.{ conditional => _ }
import akka.http.scaladsl.server.directives.RouteDirectives.{ complete => _, reject => _ }
import com.typesafe.config.ConfigFactory
import org.fusesource.scalate.TemplateEngine
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

trait TemplateDirective {

  import TemplateDirective._

  def template(resourceName: String, attributes: Map[String, Any] = Map.empty)(f: Try[String] => Route)(implicit implicits: TemplateDirective.Implicits) =
    onComplete(Future(engine.layout(resourceName, attributes))(implicits.ioDispatcher))(f)

}

object TemplateDirective extends TemplateDirective {
  private val config = ConfigFactory.load.getConfig("arimitsu.sf.platform.directives.template-engine")
  private val engine = {
    val _engine = new TemplateEngine(mode = config.getString("mode"))
    _engine.codeGenerators = _engine.codeGenerators ++ Map("html" -> _engine.codeGenerators("mustache"))
    _engine
  }
  case class Implicits(env: {
    val system: ActorSystem
  }) {
    val ioDispatcher: ExecutionContext =
      env.system.dispatchers.lookup("arimitsu.sf.platform.dispatchers.blocking-io-dispatcher")
  }
}