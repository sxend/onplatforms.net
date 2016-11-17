package arimitsu.sf.platform.accounts.router

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import arimitsu.sf.platform.lib.directive.TemplateDirective
import arimitsu.sf.platform.www.directive.Directives._

import scala.util.Success

class IndexRouter(env: {
  val templateDirectiveImplicits: TemplateDirective.Implicits
}) {
  implicit val templateImplicits = env.templateDirectiveImplicits

  def handle = parameter("callbackUrl".?) { callbackUrlOpt =>
    val callbackUrlParam = callbackUrlOpt.map(url => s"?callbackUrl=$url").getOrElse("")
    template("www/templates/index.html", Map("callbackUrlParam" -> callbackUrlParam)) {
      case Success(html) => complete(htmlEntity(html))
      case _             => reject
    }
  }
  private def htmlEntity(html: String) =
    HttpEntity(ContentTypes.`text/html(UTF-8)`, html)
}
