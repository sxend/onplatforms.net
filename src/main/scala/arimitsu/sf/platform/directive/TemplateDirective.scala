package arimitsu.sf.platform.directive

import java.io.{ StringWriter, Writer }

import akka.actor.ActorSystem
import akka.http.scaladsl.server.directives.BasicDirectives.{ extractSettings => _, pass => _ }
import akka.http.scaladsl.server.directives.CacheConditionDirectives.{ conditional => _ }
import akka.http.scaladsl.server.directives.RouteDirectives.{ complete => _, reject => _ }
import com.typesafe.config.ConfigFactory
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
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
    }(implicits.ioDispatcher))(f)

}

object TemplateDirective extends TemplateDirective {
  private val config = ConfigFactory.load.getConfig("arimitsu.sf.platform.directives.template-engine")
  private val engine = new PebbleEngine.Builder().build()

  case class Implicits(env: {
    val system: ActorSystem
  }) {
    val ioDispatcher: ExecutionContext =
      env.system.dispatchers.lookup("arimitsu.sf.platform.dispatchers.blocking-io-dispatcher")
  }
}
