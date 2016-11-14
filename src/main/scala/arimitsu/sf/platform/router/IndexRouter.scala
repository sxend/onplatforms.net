package arimitsu.sf.platform.router

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import arimitsu.sf.platform.Directives._
import arimitsu.sf.platform.directive.TemplateDirective

import scala.util.Success

class IndexRouter(env: {
  val templateDirectiveImplicits: TemplateDirective.Implicits
}) {
  implicit val templateImplicits = env.templateDirectiveImplicits

  def handle = template("templates/index.html", Map("value" -> "World")) {
    case Success(html) => complete(htmlEntity(html))
    case _             => reject
  }
  private def htmlEntity(html: String) =
    HttpEntity(ContentTypes.`text/html(UTF-8)`, html)
}
