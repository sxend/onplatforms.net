package arimitsu.sf.platform.router

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import arimitsu.sf.platform.Directives._
import arimitsu.sf.platform.directive.{ AuthenticationDirective, SessionDirective, TemplateDirective }
import arimitsu.sf.platform.external.TwitterOps
import arimitsu.sf.platform.kvs.Memcached
import twitter4j.Twitter

import scala.concurrent.duration._
import scala.util.{ Failure, Success, Try }

class SigninRouter(env: {
  val system: ActorSystem
  val templateDirectiveImplicits: TemplateDirective.Implicits
  val authenticationDirectiveImplicits: AuthenticationDirective.Implicits
  val sessionDirectiveImplicits: SessionDirective.Implicits
  val memcached: Memcached
  val twitter: TwitterOps
}) {

  implicit val templateImplicits = env.templateDirectiveImplicits
  implicit val authenticationImplicits = env.authenticationDirectiveImplicits
  implicit val sessionImplicits = env.sessionDirectiveImplicits

  def handle = template("templates/signin.html") {
    case Success(html) => complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, html))
    case _             => reject
  }

  def signout = invalidateSession(redirect("/", StatusCodes.Found))

  private def succeedSignIn(userId: String) =
    newSession { session =>
      persistSession(session ++ Map("userId" -> userId)) {
        redirect("/mypage", StatusCodes.Found)
      }
    }
  private def failedSignIn(t: Throwable): Route = {
    env.system.log.error(t, t.getMessage)
    reject
  }
  // twitter signin integration
  def twitterSignin = TwitterSignin.signin

  def twitterCallback = TwitterSignin.callback

  private def registerUser(name: String)(f: Try[String] => Route) = {
    val userId = UUID.randomUUID().toString
    onComplete(env.memcached.client.set(userId, name, Int.MaxValue.seconds)) {
      case Success(_) => f(Success(userId))
      case Failure(t) => f(Failure(t))
    }
  }

  object TwitterSignin {
    def signin = {
      getOrNewSession { session =>
        implicit val twitter = TwitterOps.newTwitter
        onComplete(env.twitter.getAuthenticationURL) {
          case Success(url) =>
            val route = redirect(url, StatusCodes.Found)
            persistSession(session ++ Map("twitter" -> twitter))(route)
          case Failure(t) => failedSignIn(t)
        }
      }
    }
    def callback = {
      requireValidSession { session =>
        implicit val twitter = session("twitter").asInstanceOf[Twitter]
        parameters("oauth_token", "oauth_verifier") { (oauthToken, oauthVerifier) =>
          onComplete(env.twitter.verify(oauthVerifier)) {
            case Success((twitterUserId, twitterUserName)) =>
              TwitterSignin.register(twitterUserId, twitterUserName)
            case Failure(t) => failedSignIn(t)
          }
        }
      }
    }

    private def register(twitterUserId: String, twitterUserName: String) =
      onComplete(env.twitter.findUser(twitterUserId))(onFindUser(twitterUserId, twitterUserName))

    private def onFindUser(twitterUserId: String, twitterUserName: String): Try[Option[String]] => Route = {
      case Success(Some(userId)) =>
        succeedSignIn(userId)
      case Success(None) =>
        registerUser(twitterUserName)(onRegister(twitterUserId))
      case Failure(t) => failedSignIn(t)
    }
    private def onRegister(twitterUserId: String): Try[String] => Route = {
      case Success(userId) => mappingUser(twitterUserId, userId)(succeedSignIn(userId))
      case Failure(t)      => failedSignIn(t)
    }
    private def mappingUser(twitterUserId: String, userId: String)(route: Route) = {
      onComplete(env.twitter.mappingUser(twitterUserId, userId)) {
        case Success(_) => route
        case Failure(t) => failedSignIn(t)
      }
    }
  }
}

object SigninRouter {

}