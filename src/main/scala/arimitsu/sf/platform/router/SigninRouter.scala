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
import scala.util.{ Failure, Success }

class SigninRouter(env: {
  val system: ActorSystem
  val templateDirectiveImplicits: TemplateDirective.Implicits
  val authenticationDirectiveImplicits: AuthenticationDirective.Implicits
  val sessionDirectiveImplicits: SessionDirective.Implicits
  val memcached: Memcached
  val twitter: TwitterOps
}) {

  import SigninRouter._

  implicit val templateImplicits = env.templateDirectiveImplicits
  implicit val authenticationImplicits = env.authenticationDirectiveImplicits
  implicit val sessionImplicits = env.sessionDirectiveImplicits

  import env.system.dispatcher

  def handle = template("templates/signin.html") {
    case Success(html) => complete(htmlEntity(html))
    case _             => reject
  }

  def signout = invalidateSession(redirect("/", StatusCodes.Found))

  def twitterSignin = provide(TwitterOps.newTwitter) { implicit twitter =>
    onComplete(env.twitter.getAuthenticationURL) {
      case Success(url) => withSession { session =>
        persistSession(session ++ Map("twitter" -> twitter)) {
          redirect(url, StatusCodes.Found)
        }
      }
      case _ => reject
    }
  }

  def twitterCallback = withSession { session =>
    provide(session.get("twitter").map(_.asInstanceOf[Twitter])) {
      case Some(twitter) => TwitterSignin.callback(twitter)
      case _             => reject
    }
  }

  private def genUUID = UUID.randomUUID().toString
  private def succeed(userId: String) =
    setSession(genUUID, userId)(redirect("/mypage", StatusCodes.Found))

  private def registerUser(userId: String, name: String) =
    env.memcached.client.set(userId, name, Int.MaxValue.seconds)

  private def htmlEntity(html: String) =
    HttpEntity(ContentTypes.`text/html(UTF-8)`, html)
  object TwitterSignin {
    def callback(implicit twitter: Twitter) =
      parameters("oauth_token", "oauth_verifier") { (oauthToken, oauthVerifier) =>
        onComplete(env.twitter.verify(oauthVerifier)) {
          case Success((twitterUserId, twitterUserName)) =>
            TwitterSignin.verify(twitterUserId, twitterUserName)
          case Failure(t) => failWith(t)
        }
      }
    private def verify(twitterUserId: String, twitterUserName: String) =
      onComplete(env.twitter.getMappedUser(twitterUserId = twitterUserId)) {
        case Success(Some(userId)) =>
          succeed(userId)
        case Success(None) =>
          mappingAndRegister(genUUID, twitterUserId, twitterUserName)
        case Failure(t) => failWith(t)
      }
    private def mappingAndRegister(userId: String, twitterUserId: String, twitterUserName: String) =
      onComplete(env.twitter.mappingUser(twitterUserId, userId)) {
        case Success(_) =>
          registerTwitterUser(userId, twitterUserName)
        case Failure(t) => failWith(t)
      }
    private def registerTwitterUser(userId: String, twitterUserName: String) =
      onComplete(registerUser(userId, twitterUserName)) {
        case Success(_) =>
          succeed(userId)
        case Failure(t) => failWith(t)
      }
  }
}

object SigninRouter {

}