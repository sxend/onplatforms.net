package net.onplatforms.accounts.router

import java.util.UUID

import akka.actor.{ActorRef, ActorRefFactory, ActorSystem}
import akka.event.LoggingAdapter
import akka.pattern._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import net.onplatforms.lib.directive.Directives._
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.headers.HttpCookie
import akka.util.Timeout
import net.onplatforms.accounts.entity._
import net.onplatforms.accounts.provider.SessionProvider
import net.onplatforms.accounts.service.AuthenticationService
import net.onplatforms.accounts.service.AuthenticationService.Protocol
import net.onplatforms.lib.kvs.Memcached
import spray.json._

import scala.util.{Failure, Success}
import scala.concurrent.duration._

class HomeRouter(
  env: {
    val memcached: Memcached
    val logger: LoggingAdapter
  }
) extends JsonProtocol with SessionProvider {
  override val memcached = env.memcached
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
