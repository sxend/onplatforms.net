package arimitsu.sf.platform.www.directive

import java.io.{ StringWriter, Writer }

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.BasicDirectives.{ extractSettings => _, pass => _ }
import akka.http.scaladsl.server.directives.CacheConditionDirectives.{ conditional => _ }
import akka.http.scaladsl.server.directives.RouteDirectives.{ complete => _, reject => _ }
import arimitsu.sf.platform.www.PlatformSystem
import com.mitchellbosecke.pebble.PebbleEngine

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

trait TemplateDirective {

  import TemplateDirective._

  def template(resourceName: String, attributes: Map[String, Any] = Map.empty)(f: Try[String] => Route)(implicit implicits: TemplateDirective.Implicits) =
    onComplete(Future {
      import scala.collection.JavaConverters._
      val writer = new StringWriter()
      engine.getTemplate(resourceName).evaluate(writer.asInstanceOf[Writer], attributes.asJava.asInstanceOf[java.util.Map[String, java.lang.Object]])
      writer.toString
    }(implicits.env.blockingContext))(f)

}

object TemplateDirective extends TemplateDirective {
  private val config = PlatformSystem.getConfigInNamespace("directives.template")
  private val engine = new PebbleEngine.Builder()
    .cacheActive(config.getBoolean("enabled-cache"))
    .build()

  case class Implicits(env: {
    val blockingContext: ExecutionContext
  }) {
  }
}
