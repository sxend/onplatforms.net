package arimitsu.sf.platform.www.kvs

import akka.actor.ActorSystem
import arimitsu.sf.platform.www.PlatformSystem
import com.typesafe.config.{ Config, ConfigFactory }
import shade.memcached.{ Configuration, Memcached => Shade }

import scala.concurrent.ExecutionContext

class Memcached(env: {
  val system: ActorSystem
  val blockingContext: ExecutionContext
  val config: Config
}) {

  private val config = env.config.getConfig(PlatformSystem.withPrefix("kvs.memcached"))
  private val host = config.getString("host")
  private val port = config.getInt("port")
  val client = Shade(Configuration(s"$host:$port"))(env.blockingContext)
}
