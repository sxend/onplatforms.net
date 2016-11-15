package arimitsu.sf.platform.external

import java.io.File

import akka.actor.ActorSystem
import arimitsu.sf.platform.kvs.Memcached
import com.typesafe.config.ConfigFactory
import twitter4j.{ Twitter, TwitterFactory }

import scala.concurrent.Future
import scala.concurrent.duration._

class TwitterOps(env: {
  val system: ActorSystem
  val memcached: Memcached
}) {
  implicit val ioDispatcher = env.system.dispatchers.lookup("arimitsu.sf.platform.dispatchers.blocking-io-dispatcher")

  def getAuthenticationURL(implicit twitter: Twitter): Future[String] =
    Future {
      twitter.getOAuthRequestToken("http://localhost:8080/signin/twitter-callback").getAuthenticationURL
    }(ioDispatcher)

  def verify(verifier: String)(implicit twitter: Twitter): Future[(String, String)] = {
    Future {
      val result = twitter.getOAuthAccessToken(verifier)
      (result.getUserId.toString, result.getScreenName)
    }(ioDispatcher)
  }
  def findUser(twitterUserId: String): Future[Option[String]] =
    env.memcached.client.get[String](twitterUserId)

  def mappingUser(twitterUserId: String, userId: String): Future[Unit] =
    env.memcached.client.set(twitterUserId, userId, Int.MaxValue.seconds)

}

object TwitterOps {
  val config = ConfigFactory.load
    .withFallback(ConfigFactory.parseFile(new File(s"${System.getenv().get("HOME")}/.twitter/credentials")))
  val consumerKey = config.getString("twitter.consumer-key")
  val consumerKeySecret = config.getString("twitter.consumer-key-secret")
  def newTwitter = {
    val tw = new TwitterFactory().getInstance()
    tw.setOAuthConsumer(consumerKey, consumerKeySecret)
    tw
  }
}