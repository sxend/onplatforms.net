package arimitsu.sf.platform.www.router

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import arimitsu.sf.platform.lib.directive.TemplateDirective
import arimitsu.sf.platform.lib.directive.Directives._

import scala.util.Success

class IndexRouter(env: {
  val templateDirectiveImplicits: TemplateDirective.Implicits
  val version: String
}) {
  implicit val templateImplicits = env.templateDirectiveImplicits

  def handle = template("www/templates/index.html", Map("version" -> env.version)) {
    case Success(html) => complete(htmlEntity(html))
    case _             => reject
  }
  private def htmlEntity(html: String) =
    HttpEntity(ContentTypes.`text/html(UTF-8)`, html)
}
