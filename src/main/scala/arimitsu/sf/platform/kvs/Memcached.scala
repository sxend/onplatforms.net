package arimitsu.sf.platform.kvs

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import shade.memcached.{ Configuration, Memcached => Shade }

import scala.concurrent.ExecutionContext

class Memcached(env: {
  val system: ActorSystem
}) {
  private val config = ConfigFactory.load.getConfig("arimitsu.sf.platform.kvs.memcached")
  private val host = config.getString("host")
  private val port = config.getInt("port")
  private val dispatcher = config.getString("dispatcher")
  private implicit val ec: ExecutionContext = env.system.dispatchers.lookup(s"arimitsu.sf.platform.dispatchers.$dispatcher")
  val client = Shade(Configuration(s"$host:$port"))
}
