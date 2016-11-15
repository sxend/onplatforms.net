package arimitsu.sf.platform.www.kvs

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import shade.memcached.{ Configuration, Memcached => Shade }

import scala.concurrent.ExecutionContext

class Memcached(env: {
  val system: ActorSystem
  val blockingContext: ExecutionContext
}) {

  private val config = env.system.settings.config.getConfig("arimitsu.sf.platform.www.kvs.memcached")
  private val host = config.getString("host")
  private val port = config.getInt("port")
  val client = Shade(Configuration(s"$host:$port"))(env.blockingContext)
}
