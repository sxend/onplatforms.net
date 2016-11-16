package arimitsu.sf.platform.www.external

import java.io.File

import akka.actor.ActorSystem
import arimitsu.sf.platform.www.kvs.Memcached
import com.typesafe.config.ConfigFactory
import twitter4j.{ Twitter, TwitterFactory }

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration._

class TwitterOps(env: {
  val system: ActorSystem
  val memcached: Memcached
  val blockingContext: ExecutionContext
}) {

  def getAuthenticationURL(implicit twitter: Twitter): Future[String] =
    Future {
      twitter.getOAuthRequestToken("http://localhost:8080/signin/twitter-callback").getAuthenticationURL
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
  private val config = ConfigFactory.parseFile(new File(s"${System.getenv().get("HOME")}/.twitter/credentials"))
  private val consumerKey = config.getString("twitter.consumer-key")
  private val consumerKeySecret = config.getString("twitter.consumer-key-secret")
  def newTwitter = {
    val tw = new TwitterFactory().getInstance()
    tw.setOAuthConsumer(consumerKey, consumerKeySecret)
    tw
  }
}