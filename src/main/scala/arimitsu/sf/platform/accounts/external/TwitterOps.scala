package arimitsu.sf.platform.accounts.external

import java.io.File

import akka.actor.ActorSystem
import arimitsu.sf.platform.accounts.AccountsSystem
import arimitsu.sf.platform.lib.kvs.Memcached
import com.typesafe.config.ConfigFactory
import twitter4j.{ Twitter, TwitterFactory }

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }

class TwitterOps(env: {
                   val system: ActorSystem
                   val memcached: Memcached
                   val blockingContext: ExecutionContext
                 }) {
  def getAuthenticationURL(callbackUrl: String)(implicit twitter: Twitter): Future[String] =
    Future {
      twitter.getOAuthRequestToken(callbackUrl).getAuthenticationURL
    }(env.blockingContext)

  def verify(verifier: String)(implicit twitter: Twitter): Future[(String, String)] = {
    Future {
      val result = twitter.getOAuthAccessToken(verifier)
      (result.getUserId.toString, result.getScreenName)
    }(env.blockingContext)
  }
  def findUser(twitterUserId: String): Future[Option[String]] =
    env.memcached.client.get[String](twitterUserId)

  def mappingUser(twitterUserId: String, userId: String): Future[Unit] =
    env.memcached.client.set(twitterUserId, userId, Int.MaxValue.seconds)

}

object TwitterOps {
  private val config =
    ConfigFactory.parseFile(new File(s"${System.getenv().get("HOME")}/.twitter/credentials")).getConfig("twitter")
      .withFallback(AccountsSystem.getConfigInNamespace("external.twitter"))
  private val consumerKey = config.getString("consumer-key")
  private val consumerKeySecret = config.getString("consumer-key-secret")
  def newTwitter = {
    val twitter = new TwitterFactory().getInstance()
    twitter.setOAuthConsumer(consumerKey, consumerKeySecret)
    twitter
  }
}