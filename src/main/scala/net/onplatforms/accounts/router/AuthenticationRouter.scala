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
import net.onplatforms.accounts.service.AuthenticationService.Protocol
import net.onplatforms.lib.kvs.Memcached
import spray.json._

import scala.util.{Failure, Success}
import scala.concurrent.duration._

class AuthenticationRouter(
  env: {
    val system: ActorSystem
    val authenticationService: ActorRefFactory => ActorRef
    val memcached: Memcached
  }
) extends JsonProtocol with SessionProvider {
  override val memcached: Memcached = env.memcached
  implicit private val timeout = Timeout(2.seconds)
  private val authenticationService = env.authenticationService(env.system)
  def routes: Route = post {
    path("signup") {
      signup
    } ~
      path("signin") {
        signin
      } ~
      path("signin" / Segment) {
        case "twitter" => twitterSignin
        case _         => reject
      } ~
      path("signout") {
        deleteCookie("sid", ".onplatforms.net", "/")(complete(""))
      }
  }

  private def signup = entity(as[Signup]) { signup =>
    val protocol = Protocol.Signup(signup.userName, signup.email, signup.password)
    onComplete(askSignup(protocol)) {
      case Success(user: Protocol.NewUser) => withNewSession { session =>
        complete(SignupResult(user.id))
      }
      case Success(_: Protocol.AlreadyExists) => complete(StatusCodes.BadRequest, jsonMsg(s"${signup.email} account already exists"))
      case Failure(t)                         => failWith(t)
    }
  }
  private def signin = withNewSession { session =>
    complete(Empty())
  }
  private def twitterSignin = complete(Empty())
  private def twitterCallback = complete(Empty())

  private def askSignup(s: AuthenticationService.Protocol.Signup) =
    authenticationService.ask(s).mapTo[Protocol.SignupResult]

  private def htmlEntity(html: String) =
    HttpEntity(ContentTypes.`text/html(UTF-8)`, html)
}
