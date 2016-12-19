package net.onplatforms.accounts.provider

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.{HttpCookie, RawHeader}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model._
import net.onplatforms.accounts.router.JsonProtocol
import spray.json._

trait SessionProvider extends AnyRef with JsonProtocol {

  def withDefaultSessionId(route: String => Route): Route = optionalCookie("sid") {
    case None       => withSessionId(sid => route(sid))
    case Some(pair) => route(pair.value)
  }
  def withSessionId(route: String => Route): Route = {
    val sid = UUID.randomUUID.toString
    setCookie(HttpCookie("sid", sid, // TODO: uuid persist
      maxAge = Option(2592000), domain = Option(".accounts.onplatforms.net"), path = Option("/"))) {
      setToken(sid)(route(sid))
    }
  }

  def csrfProtect(route: Route): Route = checkToken(registerToken(route))

  def registerToken(route: Route): Route = withDefaultSessionId { sid =>
    setToken(sid)(route)
  }
  def setToken(sid: String)(route: Route): Route = {
    val token = UUID.randomUUID().toString
    val tokenHeader = RawHeader("X-CSRF-Token", token)
    SessionProvider._cache.put(sid, token)
    respondWithHeader(tokenHeader)(route)
  }
  def checkToken(route: Route): Route = withDefaultSessionId { sid =>
    headerValueByName("X-CSRF-Token") { token =>
      Option(SessionProvider._cache.get(sid)) match {
        case Some(sendToken) if sendToken == token =>
          SessionProvider._cache.remove(sid)
          route
        case _ =>
          complete(StatusCodes.BadRequest, "Invalid X-CSRF-Token")
      }
    }
  }
  def tokenEndpoint: Route = post(path("token") {
    registerToken(complete(Empty()))
  })
}

object SessionProvider {
  val _cache = new ConcurrentHashMap[String, String]()
}