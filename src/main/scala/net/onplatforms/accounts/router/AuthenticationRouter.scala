package net.onplatforms.accounts.router

import java.util.UUID

import akka.actor.{ActorRef, ActorRefFactory, ActorSystem}
import akka.pattern._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import net.onplatforms.accounts.entity._
import net.onplatforms.accounts.provider.SessionProvider
import net.onplatforms.accounts.service.{AuthenticationService, CacheService}
import net.onplatforms.accounts.service.AuthenticationService.Protocol
import net.onplatforms.lib.kvs.Memcached
import spray.json._

import scala.util.{Failure, Success}
import scala.concurrent.duration._

class AuthenticationRouter(
  env: {
    val system: ActorSystem
    val authenticationService: () => ActorRef
    val cacheService: CacheService
  }
) extends JsonProtocol with SessionProvider {
  override val cacheService: CacheService = env.cacheService
  implicit private val timeout = Timeout(2.seconds)
  private val authenticationService: ActorRef = env.authenticationService()
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
        signout
      }
  }

  private def signup = entity(as[Signup]) { signup =>
    val protocol = Protocol.Signup(signup.userName, signup.email, signup.password)
    onComplete(askSignup(protocol)) {
      case Success(user: Protocol.NewUser) => withNewSession { session =>
        setNewCSRFToken(session.copy(userId = Option(user.id)))(complete(SignupResponse(location = "/home")))
      }
      case Success(_: Protocol.AlreadyExists) => complete(StatusCodes.BadRequest, jsonMsg(s"${signup.email} account already exists"))
      case Failure(t)                         => failWith(t)
    }
  }

  private def signin = entity(as[Signin]) { signin =>
    val protocol = Protocol.Signin(signin.email, signin.password)
    onComplete(askSignin(protocol)) {
      case Success(success: Protocol.Success) => withNewSession { session =>
        setNewCSRFToken(session.copy(userId = Option(success.userId)))(complete(SigninResponse(location = "/home")))
      }
      case Success(_: Protocol.Fail) => complete(StatusCodes.BadRequest, jsonMsg(s"failed to signin ${signin.email}"))
      case Failure(t)                => failWith(t)
    }
  }

  private def signout = entity(as[Signout]) { signout =>
    deleteSession(complete(SignoutResponse(location = "/")))
  }

  private def twitterSignin = complete(Empty())
  private def twitterCallback = complete(Empty())

  private def askSignup(s: AuthenticationService.Protocol.Signup) =
    authenticationService.ask(s).mapTo[Protocol.SignupResult]
  private def askSignin(s: AuthenticationService.Protocol.Signin) =
    authenticationService.ask(s).mapTo[Protocol.SigninResult]
  private def htmlEntity(html: String) =
    HttpEntity(ContentTypes.`text/html(UTF-8)`, html)
}
