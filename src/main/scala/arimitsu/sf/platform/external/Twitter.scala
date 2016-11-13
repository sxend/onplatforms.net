package arimitsu.sf.platform.external

import java.io.File

import akka.actor.ActorSystem
import arimitsu.sf.platform.kvs.Memcached
import com.typesafe.config.ConfigFactory
import twitter4j.TwitterFactory

import scala.concurrent.Future
import scala.concurrent.duration._

class Twitter(env: {
  val system: ActorSystem
  val memcached: Memcached
}) {
  import Twitter._
  implicit val ioDispatcher = env.system.dispatchers.lookup("arimitsu.sf.platform.dispatchers.blocking-io-dispatcher")

  def getAuthenticationURL: Future[String] =
    Future {
      twitter.getOAuthRequestToken("http://localhost:8080/signin/twitter-callback").getAuthenticationURL
    }(ioDispatcher)
  def verify(verifier: String): Future[(String, String)] = {
    Future {
      val result = twitter.getOAuthAccessToken(verifier)
      (result.getUserId.toString, result.getScreenName)
    }(ioDispatcher)
  }
  def getMappedUser(twitterUserId: String): Future[Option[String]] = {
    env.memcached.client.get[String](twitterUserId).map { x =>
      println(s"getMappedUser: $twitterUserId, $x")
      x
    }
  }

  def mappingUser(twitterUserId: String, userId: String): Future[Unit] = {
    env.memcached.client.set(twitterUserId, userId, Int.MaxValue.seconds).map { x =>
      println(s"mappingUser: $twitterUserId, $userId, $x")
    }
  }

}

object Twitter {
  val config = ConfigFactory.load
    .withFallback(ConfigFactory.parseFile(new File(s"${System.getenv().get("HOME")}/.twitter/credentials")))
  val consumerKey = config.getString("twitter.consumer-key")
  val consumerKeySecret = config.getString("twitter.consumer-key-secret")
  val twitter = {
    val tw = new TwitterFactory().getInstance()
    tw.setOAuthConsumer(consumerKey, consumerKeySecret)
    tw
  }

}