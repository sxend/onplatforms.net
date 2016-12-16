package net.onplatforms.lib.rdb

import akka.actor.ActorSystem
import com.typesafe.config.Config

import slick._
import scala.concurrent.ExecutionContext

class MySQL(env: {
              val system: ActorSystem
              val blockingContext: ExecutionContext
              val namespace: String
              val config: Config
            }) {
  private val config = env.config.getConfig(s"${env.namespace}.rdb.mysql")
  private val host = config.getString("host")
  private val port = config.getInt("port")
  private val user = config.getString("user")
  private val pass = config.getString("pass")
}
