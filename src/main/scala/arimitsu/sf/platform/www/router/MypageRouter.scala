package arimitsu.sf.platform.www.router

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import arimitsu.sf.platform.lib.directive.Directives._
import arimitsu.sf.platform.lib.directive.{ AuthenticationDirective, TemplateDirective }

import scala.util.Success

class MypageRouter(env: {
  val templateDirectiveImplicits: TemplateDirective.Implicits
  val authenticationDirectiveImplicits: AuthenticationDirective.Implicits
}) {
  implicit val templateImplicits = env.templateDirectiveImplicits
  implicit val authenticationImplicits = env.authenticationDirectiveImplicits

  def handle = authenticated { (userId, userName) =>
    template("www/templates/mypage.html", Map("userId" -> userId, "userName" -> userName)) {
      case Success(html) => complete(htmlEntity(html))
      case _             => reject
    }
  }

  private def htmlEntity(html: String) =
    HttpEntity(ContentTypes.`text/html(UTF-8)`, html)
}
