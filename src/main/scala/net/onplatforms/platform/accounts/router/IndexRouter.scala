package net.onplatforms.platform.accounts.router

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import net.onplatforms.platform.lib.directive.TemplateDirective
import net.onplatforms.platform.lib.directive.TemplateDirective._

import scala.util.Success

class IndexRouter(env: {
                    val templateDirectiveImplicits: TemplateDirective.Implicits
                    val version: String
                  }) {
  implicit val templateImplicits = env.templateDirectiveImplicits

  def handle = parameter("returnTo".?) { returnToOpt =>
    val returnToParam = returnToOpt.map(url => s"?returnTo=$url").getOrElse("")
    template("accounts/templates/index.html", Map("returnToParam" -> returnToParam, "version" -> env.version)) {
      case Success(html) => complete(htmlEntity(html))
      case _             => reject
    }
  }

  private def htmlEntity(html: String) =
    HttpEntity(ContentTypes.`text/html(UTF-8)`, html)
}
