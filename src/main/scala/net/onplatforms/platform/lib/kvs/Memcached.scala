package net.onplatforms.platform.lib.kvs

import com.typesafe.config.Config
import shade.memcached.{Configuration, Memcached => Shade}

import scala.concurrent.ExecutionContext

class Memcached(env: {
                  val blockingContext: ExecutionContext
                  val config: Config
                  val namespace: String
                }) {
  private val config = env.config.getConfig(s"${env.namespace}.kvs.memcached")
  private val host = config.getString("host")
  private val port = config.getInt("port")
  val client: Shade = Shade(Configuration(s"$host:$port"))(env.blockingContext)
}
