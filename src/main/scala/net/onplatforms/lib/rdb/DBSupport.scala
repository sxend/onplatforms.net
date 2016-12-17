package net.onplatforms.lib.rdb

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import com.typesafe.config.Config
import slick.driver.MySQLDriver.api._
import slick.jdbc.GetResult

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
) {
  private implicit val blocking = env.blockingContext

  implicit val getCoffeeResult = GetResult(r => HeartBeat(r.<<))

  private val heartBeatQuery = sql"select 1 as count".as[HeartBeat]

  private val config = env.config.getConfig(s"${env.namespace}.rdb.mysql")

  val DB: slick.driver.MySQLDriver.backend.Database =
    Database.forConfig("net.onplatforms.accounts.rdb.mysql")

  env.system.scheduler.schedule(1.minutes, 30.minutes) {
    DB.run(heartBeatQuery).map(_.head).map(_.count).onComplete {
      case Success(count) if count == 1 =>
        env.logger.info(s"db connection heartbeat succeed.")
      case Success(count) =>
        env.logger.info(s"db response is invalid count: $count")
      case Failure(t) =>
        env.logger.error(t, t.getMessage)
        throw t
    }
  }(env.blockingContext)

}

case class HeartBeat(count: Int)
