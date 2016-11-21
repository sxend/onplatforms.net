package arimitsu.sf.platform.accounts.router

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import arimitsu.sf.platform.accounts.AccountsSystem
import arimitsu.sf.platform.accounts.external.TwitterOps
import arimitsu.sf.platform.lib.directive.Directives._
import arimitsu.sf.platform.lib.directive.{ AuthenticationDirective, TemplateDirective }
import arimitsu.sf.platform.lib.kvs.Memcached
import twitter4j.Twitter

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

class SignupRouter(env: {
  val system: ActorSystem
  val templateDirectiveImplicits: TemplateDirective.Implicits
  val authenticationDirectiveImplicits: AuthenticationDirective.Implicits
  val memcached: Memcached
  val twitter: TwitterOps
  val version: String
}) {

  implicit val templateImplicits = env.templateDirectiveImplicits
  implicit val authenticationImplicits = env.authenticationDirectiveImplicits

  def handle = parameter("returnTo".?) { returnToOpt =>
    val returnToParam = returnToOpt.map(url => s"?returnTo=$url").getOrElse("")
    template("accounts/templates/signup.html", Map("returnToParam" -> returnToParam, "version" -> env.version)) {
      case Success(html) => complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, html))
      case _             => reject
    }
  }

  def signout = invalidateSession(redirect("/", StatusCodes.Found))

  private def succeedSignIn(userId: String, returnToOpt: Option[String] = None) =
    newSession { session =>
      persistSession(session ++ Map("userId" -> userId)) {
        val url = returnToOpt.getOrElse("/mypage")
        redirect(url, StatusCodes.Found)
      }
    }
  private def failedSignIn(t: Throwable): Route = {
    env.system.log.error(t, t.getMessage)
    reject
  }
  // twitter signin integration
  def twitterSignin = TwitterSignin.signin

  def twitterCallback = TwitterSignin.callback

  private def registerUser(name: String) = {
    val userId = UUID.randomUUID().toString
    env.memcached.client.set(userId, name, Int.MaxValue.seconds).map(_ => userId)(env.system.dispatcher)
  }

  object TwitterSignin {
    private val config = AccountsSystem.getConfigInNamespace("external.twitter")
    private val signinCallbackUrl = config.getString("callback-url")
    def signin = {
      parameter("returnTo".?) { returnToOpt =>
        val returnTo = returnToOpt.getOrElse("")
        getOrNewSession { session =>
          implicit val twitter = TwitterOps.newTwitter
          onComplete(env.twitter.getAuthenticationURL(signinCallbackUrl + s"?returnTo=$returnTo")) {
            case Success(url) =>
              val route = redirect(url, StatusCodes.Found)
              persistSession(session ++ Map("twitter" -> twitter))(route)
            case Failure(t) => failedSignIn(t)
          }
        }
      }
    }
    def callback = {
      parameters("oauth_token", "oauth_verifier", "returnTo".?) { (oauthToken, oauthVerifier, returnToOpt) =>
        requireValidSession { session =>
          session.get("twitter").filter(_.isInstanceOf[Twitter]).map(_.asInstanceOf[Twitter]) match {
            case Some(tw: Twitter) =>
              import env.system.dispatcher
              implicit val twitter = tw
              val userIdF = for {
                (twitterUserId, twitterUserName) <- env.twitter.verify(oauthVerifier)
                userId <- TwitterSignin.register(twitterUserId, twitterUserName)
              } yield userId
              onComplete(userIdF) {
                case Success(userId) => succeedSignIn(userId, returnToOpt)
                case Failure(t)      => failedSignIn(t)
              }
            case _ => reject
          }
        }

      }
    }

    private def register(twitterUserId: String, twitterUserName: String)(implicit ec: ExecutionContext): Future[String] =
      env.twitter.findUser(twitterUserId).flatMap {
        case Some(userId) => Future(userId)(env.system.dispatcher)
        case None         => registerUser(twitterUserName).flatMap(mappingUser(twitterUserId, _))
      }

    private def mappingUser(twitterUserId: String, userId: String)(implicit ec: ExecutionContext) = {
      env.twitter.mappingUser(twitterUserId, userId).map(_ => userId)
    }
  }
}

object SignupRouter {

}