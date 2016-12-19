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
import net.onplatforms.accounts.provider.SessionProvider
import net.onplatforms.accounts.service.AuthenticationService
import spray.json._

import scala.util.{Failure, Success}
import scala.concurrent.duration._

class AuthenticationRouter(
  env: {
    val system: ActorSystem
    val authenticationService: ActorRefFactory => ActorRef
  }
) extends JsonProtocol with SessionProvider {
  implicit private val timeout = Timeout(2.seconds)
  private val authenticationService = env.authenticationService(env.system)
  def routes: Route = post {
    path("signup") {
      signup
    } ~
      path("signin" / Segment) {
        case "owned"   => ownedSignin
        case "twitter" => twitterSignin
        case _         => reject
      } ~
      path("signout") {
        deleteCookie("sid", ".onplatforms.net", "/")(complete(""))
      }
  }

  private def signup = entity(as[OwnedSignup]) { signup =>
    val protocol = AuthenticationService.Protocol.Owned(signup.userName, signup.email, signup.password)
    onComplete(askOwned(protocol)) {
      case Success(user) => withSessionId { sid =>
        setToken(sid)(complete(OwnedSignupResult(user.id)))
      }
      case Failure(t) => failWith(t)
    }
  }
  private def ownedSignin = withSessionId { sid =>
    complete(Empty())
  }
  private def twitterSignin = complete(Empty())
  private def twitterCallback = complete(Empty())

  private def askOwned(owned: AuthenticationService.Protocol.Owned) =
    authenticationService.ask(owned).mapTo[User]

  private def htmlEntity(html: String) =
    HttpEntity(ContentTypes.`text/html(UTF-8)`, html)
}
