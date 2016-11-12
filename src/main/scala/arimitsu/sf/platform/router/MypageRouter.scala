package arimitsu.sf.platform.router

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.Credentials
import arimitsu.sf.platform.Directives._
import arimitsu.sf.platform.directive.TemplateDirective

import scala.concurrent.Future
import scala.util.Success

class MypageRouter(env: {
  val system: ActorSystem
}) {
  implicit val system = env.system
  implicit val templateImplicit = TemplateDirective.Implicits(system)

  def handle = authenticateOAuth2Async(realm = "", authenticator) { userName =>
    template("templates/mypage.html", Map("userName" -> userName)) {
      case Success(html) => complete(htmlEntity(html))
      case _             => reject
    }
  }
  private def authenticator(credentials: Credentials): Future[Option[String]] = {
    import system.dispatcher
    credentials match {
      case p @ Credentials.Provided(token) =>
        Future(Option(token))
      case _ => Future.successful(None)
    }
  }

  private def htmlEntity(html: String) =
    HttpEntity(ContentTypes.`text/html(UTF-8)`, html)
}
