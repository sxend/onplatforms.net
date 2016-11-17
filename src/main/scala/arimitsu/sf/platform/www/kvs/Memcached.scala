package arimitsu.sf.platform.www.kvs

import akka.actor.ActorSystem
import arimitsu.sf.platform.www.PlatformSystem
import com.typesafe.config.{ Config, ConfigFactory }
import shade.memcached.{ Configuration, Memcached => Shade }

import scala.concurrent.ExecutionContext

class Memcached(env: {
  val blockingContext: ExecutionContext
}) {
  private val config = PlatformSystem.getConfigInNamespace("kvs.memcached")
  private val host = config.getString("host")
  private val port = config.getInt("port")
  val client = Shade(Configuration(s"$host:$port"))(env.blockingContext)
}
