package net.onplatforms.accounts.router

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.typesafe.config.Config
import net.onplatforms.accounts.Main
import net.onplatforms.accounts.provider.SessionProvider
import net.onplatforms.accounts.service.CacheService
import net.onplatforms.lib.directive.Directives._
import net.onplatforms.lib.directive.TemplateDirective
import net.onplatforms.lib.directive.TemplateDirective.Implicits

import scala.util.Success

class IndexRouter(
  env: {
    val templateDirectiveImplicits: TemplateDirective.Implicits
    val version: String
    val cacheService: CacheService
  }
) extends JsonProtocol with SessionProvider {
  override protected val cacheService: CacheService = env.cacheService

  implicit val templateImplicits: Implicits = env.templateDirectiveImplicits
  private val config = Main.getConfigInNamespace("script")
  def handle: Route = withSession { session =>
    val attr = Map("version" -> env.version,
      "bootstrap" -> config.getString("bootstrap"),
      "X_CSRF_Token" -> session.csrfToken.getOrElse(""))
    template("accounts/templates/index.html", attr) {
      case Success(html) => complete(htmlEntity(html))
      case _             => reject
    }
  }

  private def htmlEntity(html: String) =
    HttpEntity(ContentTypes.`text/html(UTF-8)`, html)
}
