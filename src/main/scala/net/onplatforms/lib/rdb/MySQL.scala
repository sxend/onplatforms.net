package net.onplatforms.lib.rdb

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import com.typesafe.config.{Config, ConfigFactory}
import slick._
import slick.driver.MySQLDriver.api.Database

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class MySQL(
  env: {
    val system: ActorSystem
    val blockingContext: ExecutionContext
    val namespace: String
    val config: Config
    val logger: LoggingAdapter
  }
) {
  private val config = env.config.getConfig(s"${env.namespace}.rdb.mysql")

  val DB = Database.forConfig("net.onplatforms.accounts.rdb.mysql")
  private val interval = 30.seconds

  env.system.scheduler.schedule(interval, interval) {
    val session = DB.createSession()
    try {
      val rs = session.prepareStatement("select 1 as one").executeQuery()
      if (rs.next()) {
        env.logger.info(s"db connection heartbeat: ${rs.getInt("one") == 1}")
      } else {
        env.logger.warning(s"db connection heartbeat is not available.")
      }
    } catch {
      case t: Throwable =>
        env.logger.error(t, t.getMessage)
        throw t
    } finally {
      session.close()
    }
  }(env.blockingContext)

  //  val DB = JdbcBackend.Database.forURL(s"jdbc:mysql://$host:$port/accounts.onplatforms.net", user, pass, props)
}
