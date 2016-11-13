package arimitsu.sf.platform.router

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpHeader.ParsingResult
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials
import arimitsu.sf.platform.Directives._
import arimitsu.sf.platform.directive.{ AuthenticationDirective, TemplateDirective }
import arimitsu.sf.platform.external.Twitter
import arimitsu.sf.platform.kvs.Memcached
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{ Failure, Success }

class SigninRouter(env: {
  val system: ActorSystem
  val templateDirectiveImplicits: TemplateDirective.Implicits
  val authenticationDirectiveImplicits: AuthenticationDirective.Implicits
  val memcached: Memcached
  val twitter: Twitter
}) {

  import SigninRouter._

  implicit val templateImplicits = env.templateDirectiveImplicits
  implicit val authenticationImplicits = env.authenticationDirectiveImplicits

  import env._
  import system.dispatcher
  private val logger = system.log

  def handle = template("templates/signin.html") {
    case Success(html) => complete(htmlEntity(html))
    case _             => reject
  }

  def twitter = onComplete(env.twitter.getAuthenticationURL) {
    case Success(url) => redirect(url, StatusCodes.Found)
    case _            => reject
  }

  def twitterCallback = parameters("oauth_token", "oauth_verifier") { (oauthToken, oauthVerifier) =>
    onComplete(env.twitter.verify(oauthVerifier)) {
      case Success((twitterUserId, twitterUserName)) =>
        println(s"twitterUserId: $twitterUserId, twitterUserName: $twitterUserName")
        onComplete(env.twitter.getMappedUser(twitterUserId = twitterUserId)) {
          case Success(Some(userId)) =>
            setSession(UUID.randomUUID().toString, userId)(redirect("/mypage", StatusCodes.Found))
          case Success(None) =>
            val userId = UUID.randomUUID().toString
            onComplete(env.twitter.mappingUser(twitterUserId, userId)) {
              case Success(_) =>
                onComplete(registerUser(userId, twitterUserName)) {
                  case Success(_) =>
                    setSession(UUID.randomUUID().toString, userId)(redirect("/mypage", StatusCodes.Found))
                  case Failure(t) =>
                    logger.error(t, t.getMessage)
                    top
                }
              case Failure(t) =>
                logger.error(t, t.getMessage)
                top
            }
          case Failure(t) =>
            logger.error(t, t.getMessage)
            top
        }
      case Failure(t) =>
        logger.error(t, t.getMessage)
        top
    }
  }
  private def top = redirect("/", StatusCodes.Found)

  private def registerUser(userId: String, name: String) = {
    env.memcached.client.set(userId, name, Int.MaxValue.seconds).map { x =>
      println(s"registerUser: $userId, $name, $x")
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

object SigninRouter {

}