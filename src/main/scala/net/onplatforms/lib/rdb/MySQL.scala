package net.onplatforms.lib.rdb

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}
import slick._

import scala.concurrent.ExecutionContext

class MySQL(
  env: {
    val system: ActorSystem
    val blockingContext: ExecutionContext
    val namespace: String
    val config: Config
  }
) {
  private val config = env.config.getConfig(s"${env.namespace}.rdb.mysql")
  private val host = config.getString("host")
  private val port = config.getInt("port")
  private val user = config.getString("user")
  private val pass = config.getString("pass")

  import slick._
  import slick.jdbc._
  val props = {
    val p = new java.util.Properties()
    p.setProperty("verifyServerCertificate", "false")
    p.setProperty("useSSL", "false")
    p.setProperty("autocommit", "false")
    p
  }
  val DB = JdbcBackend.Database.forURL(s"jdbc:mysql://$host:$port/accounts.onplatforms.net", user, pass, props)
}
