package arimitsu.sf.platform.lib.directive

import java.io.{ StringWriter, Writer }

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.BasicDirectives.{ extractSettings => _, pass => _ }
import akka.http.scaladsl.server.directives.CacheConditionDirectives.{ conditional => _ }
import akka.http.scaladsl.server.directives.RouteDirectives.{ complete => _, reject => _ }
import com.mitchellbosecke.pebble.PebbleEngine
import com.typesafe.config.Config

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

trait TemplateDirective {

  def template(resourceName: String, attributes: Map[String, Any] = Map.empty)(
    fn: Try[String] => Route)(implicit implicits: TemplateDirective.Implicits): Route =
    onComplete(Future {
      import scala.collection.JavaConverters._
      val writer = new StringWriter().asInstanceOf[Writer]
      val attributeJavaMap = attributes.asJava.asInstanceOf[java.util.Map[String, java.lang.Object]]
      implicits.engine.getTemplate(resourceName).evaluate(writer, attributeJavaMap)
      writer.toString
    }(implicits.env.blockingContext))(fn)

}

object TemplateDirective extends TemplateDirective with Lib {

  case class Implicits(env: {
                         val config: Config
                         val blockingContext: ExecutionContext
                       }) {
    private[lib] val templateConfig = getConfigInNamespace("directives.template")(env)
    private[lib] val engine = new PebbleEngine.Builder()
      .cacheActive(templateConfig.getBoolean("enabled-cache"))
      .build()
  }
}
