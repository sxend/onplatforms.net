package net.onplatforms.accounts.router

import java.util.UUID

import akka.actor.{ActorRef, ActorRefFactory, ActorSystem}
import akka.pattern._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import net.onplatforms.lib.directive.Directives._
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.headers.HttpCookie
import akka.util.Timeout
import net.onplatforms.accounts.entity._
import net.onplatforms.accounts.service.AuthenticationService
import spray.json._

import scala.util.{Failure, Success}
import scala.concurrent.duration._

class SignupRouter(env: {
                     val system: ActorSystem
                     val version: String
                     val authenticationService: ActorRefFactory => ActorRef
                   }) extends JsonProtocol {
  implicit private val timeout = Timeout(2.seconds)
  private val authenticationService = env.authenticationService(env.system)

  def handle: Route = entity(as[Signup]) { signup =>
    if (signup.ownedSignupOpt.nonEmpty) {
      val owned = signup.ownedSignupOpt.get
      val protocol = AuthenticationService.Protocol.Owned(owned.userName, owned.email, owned.password)
      onComplete(askOwned(protocol)) {
        case Success(user) =>
          val result = SignupResult(user.id)
          setCookie(HttpCookie("sid", UUID.randomUUID.toString, // TODO: uuid persist
            maxAge = Option(2592000), domain = Option(".onplatforms.net"), path = Option("/"))) {
            complete(result)
          }
        case Failure(t) => failWith(t)
      }
    } else if (signup.twitterSignupOpt.nonEmpty) {
      reject
    } else {
      reject
    }
  }
  private def askOwned(owned: AuthenticationService.Protocol.Owned) =
    authenticationService.ask(owned).mapTo[User]

  def twitterCallback = reject

  private def htmlEntity(html: String) =
    HttpEntity(ContentTypes.`text/html(UTF-8)`, html)
}
