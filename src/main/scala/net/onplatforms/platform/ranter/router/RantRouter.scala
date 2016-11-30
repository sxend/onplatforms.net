package net.onplatforms.platform.ranter.router

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import net.onplatforms.platform.lib.directive.Directives._
import net.onplatforms.platform.lib.directive.TemplateDirective
import net.onplatforms.platform.lib.directive.TemplateDirective.Implicits
import net.onplatforms.platform.ranter.RanterSystem._

import scala.util.Success
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

class RantRouter(env: {
                   val templateDirectiveImplicits: TemplateDirective.Implicits
                   val version: String
                 }) extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val templateImplicits: Implicits = env.templateDirectiveImplicits

  case class Rant(content: String)
  implicit val rantFormat = jsonFormat1(Rant)

  val routes = rant

  def rant: Route = {
    post(path("rant") {
      entity(as[Rant]) { rant =>
        println(rant)
        complete(StatusCodes.OK)
      }
    })
  }

  private def htmlEntity(html: String) =
    HttpEntity(ContentTypes.`text/html(UTF-8)`, html)
}
