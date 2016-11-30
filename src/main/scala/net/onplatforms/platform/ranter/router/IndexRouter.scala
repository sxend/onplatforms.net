package net.onplatforms.platform.ranter.router

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import net.onplatforms.platform.lib.directive.Directives._
import net.onplatforms.platform.lib.directive.TemplateDirective
import net.onplatforms.platform.lib.directive.TemplateDirective.Implicits
import net.onplatforms.platform.ranter.RanterSystem._

import scala.util.Success

class IndexRouter(env: {
                    val templateDirectiveImplicits: TemplateDirective.Implicits
                    val version: String
                  }) {
  implicit val templateImplicits: Implicits = env.templateDirectiveImplicits

  val routes = index

  def index: Route = {
    get(path("") {
      val attributes =
        Map("bootstrap" -> getConfigInNamespace("static").getString("bootstrap-url"))
      template("ranter/templates/index.html", attributes) {
        case Success(html) => complete(htmlEntity(html))
        case _             => reject
      }
    })
  }
  private def htmlEntity(html: String) =
    HttpEntity(ContentTypes.`text/html(UTF-8)`, html)
}
