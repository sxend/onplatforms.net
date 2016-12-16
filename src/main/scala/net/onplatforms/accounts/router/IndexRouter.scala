package net.onplatforms.accounts.router

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import net.onplatforms.lib.directive.Directives._
import net.onplatforms.lib.directive.TemplateDirective
import net.onplatforms.lib.directive.TemplateDirective.Implicits

import scala.util.Success

class IndexRouter(
  env: {
    val templateDirectiveImplicits: TemplateDirective.Implicits
    val version: String
  }
) {

  implicit val templateImplicits: Implicits = env.templateDirectiveImplicits

  def handle: Route = template("accounts/templates/index.html", Map("version" -> env.version)) {
    case Success(html) => complete(htmlEntity(html))
    case _             => reject
  }

  private def htmlEntity(html: String) =
    HttpEntity(ContentTypes.`text/html(UTF-8)`, html)
}
