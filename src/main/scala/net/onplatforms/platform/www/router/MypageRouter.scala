package net.onplatforms.platform.www.router

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import net.onplatforms.platform.lib.directive.Directives._
import net.onplatforms.platform.lib.directive.{AuthenticationDirective, TemplateDirective}
import net.onplatforms.platform.lib.directive.TemplateDirective.Implicits

import scala.util.Success

class MypageRouter(env: {
                     val templateDirectiveImplicits: Implicits
                     val authenticationDirectiveImplicits: AuthenticationDirective.Implicits
                   }) {
  implicit val templateImplicits: TemplateDirective.Implicits = env.templateDirectiveImplicits
  implicit val authenticationImplicits: AuthenticationDirective.Implicits = env.authenticationDirectiveImplicits

  def handle: Route = authenticated { (userId, userName) =>
    template("www/templates/mypage.html", Map("userId" -> userId, "userName" -> userName)) {
      case Success(html) => complete(htmlEntity(html))
      case _             => reject
    }
  }

  private def htmlEntity(html: String) =
    HttpEntity(ContentTypes.`text/html(UTF-8)`, html)
}
