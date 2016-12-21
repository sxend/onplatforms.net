package net.onplatforms.lib.rdb

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import com.typesafe.config.Config
import net.onplatforms.accounts.io.rdb.Tables
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class MySQL(
  env: {
    val system: ActorSystem
    val blockingContext: ExecutionContext
    val namespace: String
    val config: Config
    val logger: LoggingAdapter
  }
) extends Tables {
  override val profile: JdbcProfile = slick.driver.MySQLDriver

  private implicit val blocking = env.blockingContext

  val DB: slick.driver.MySQLDriver.backend.Database =
    Database.forConfig("net.onplatforms.lib.rdb.mysql")

  env.system.scheduler.schedule(1.minutes, 30.minutes) {
    DB.run(Users.countDistinct.result).onComplete {
      case Success(count) if count >= 0 =>
        env.logger.info(s"db connection heartbeat succeed.")
      case Success(count) =>
        env.logger.info(s"db response is invalid count: $count")
      case Failure(t) =>
        env.logger.error(t, t.getMessage)
        throw t
    }
  }(env.blockingContext)

}

