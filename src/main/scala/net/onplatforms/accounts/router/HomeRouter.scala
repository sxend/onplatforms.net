package net.onplatforms.accounts.router

import java.util.UUID

import akka.actor.{ActorRef, ActorRefFactory, ActorSystem}
import akka.event.LoggingAdapter
import akka.pattern._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import net.onplatforms.accounts.entity._
import net.onplatforms.accounts.provider.SessionProvider
import net.onplatforms.accounts.service.{AuthenticationService, CacheService}
import net.onplatforms.lib.kvs.Memcached
import spray.json._

class HomeRouter(
  env: {
    val cacheService: CacheService
    val logger: LoggingAdapter
  }
) extends JsonProtocol with SessionProvider {
  override val cacheService: CacheService = env.cacheService
  def routes = home
  private def home = get(path("home") {
    withSession {
      case Session(sid, Some(userId), token) =>
        complete(HomeResponse(userId))
      case msg =>
        complete(StatusCodes.BadRequest, jsonMsg("home api is private"))
    }
  })
}
