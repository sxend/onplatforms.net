package net.onplatforms.accounts.service

import akka.event.LoggingAdapter
import com.typesafe.config.Config
import net.onplatforms.accounts.entity.Session
import net.onplatforms.lib.kvs.Memcached

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class CacheService(
  env: {
    val logger: LoggingAdapter
    val config: Config
    val namespace: String
    val blockingContext: ExecutionContext
    val memcached: Memcached
  }
) {
  import env.memcached.Imports._
  private val memcached = env.memcached

  def setSession(sid: String, session: Session, exp: Int)(implicit ec: ExecutionContext): Future[Session] =
    memcached.client.set(sid, session, exp.seconds).map(_ => session)

  def getSession(sid: String): Future[Option[Session]] =
    memcached.client.get[Session](sid)
}
