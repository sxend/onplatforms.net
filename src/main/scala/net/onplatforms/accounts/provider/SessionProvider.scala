package net.onplatforms.accounts.provider

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.{HttpCookie, HttpCookiePair, RawHeader}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive, Directive0, Directive1, Route}
import akka.http.scaladsl.model._
import net.onplatforms.accounts.router.JsonProtocol
import net.onplatforms.lib.kvs.Memcached
import spray.json._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.util.Tupler
import akka.http.scaladsl.util.FastFuture._
import net.onplatforms.accounts.entity.Session

import scala.concurrent.Future
import scala.concurrent.duration._

trait SessionProvider extends AnyRef with JsonProtocol {
  val memcached: Memcached
  import memcached.Imports._

  def newSession0: Directive1[Session] =
    optionalCookie("sid").flatMap {
      case Some(pair) => getOrCreateSession(pair.value)
      case _ => getOrCreateSession(UUID.randomUUID().toString).flatMap { session =>
        val cookie = HttpCookie("sid", session.sid,
          maxAge = Option(2592000), domain = Option(".onplatforms.net"), path = Option("/"))
        setCookie(cookie).tmap(_ => session)
      }
    }

  private def getOrCreateSession(sid: String): Directive1[Session] = Directive { inner => ctx =>
    import ctx.executionContext
    val future = for {
      cacheDataOpt <- memcached.client.get[Session](sid)
      session <- cacheDataOpt match {
        case Some(cacheData) => Future.successful(cacheData)
        case _ =>
          val newSession = Session(sid)
          memcached.client.set(sid, newSession, 2.seconds).map(_ => newSession)
      }
    } yield session
    future.flatMap(t => inner(Tuple1(t))(ctx))
  }

  def withSession(route: String => Route): Route = optionalCookie("sid") {
    case None       => withNewSession(sid => route(sid))
    case Some(pair) => route(pair.value)
  }
  def withNewSession(route: String => Route): Route = {
    val sid = UUID.randomUUID.toString
    setCookie(HttpCookie("sid", sid, // TODO: uuid persist
      maxAge = Option(2592000), domain = Option(".accounts.onplatforms.net"), path = Option("/"))) {
      setToken(sid)(route(sid))
    }
  }

  def csrfProtect(route: Route): Route = checkToken(registerToken(route))

  def registerToken(route: Route): Route = withSession { sid =>
    setToken(sid)(route)
  }
  def setToken(sid: String)(route: Route): Route = {
    val token = UUID.randomUUID().toString
    val tokenHeader = RawHeader("X-CSRF-Token", token)
    SessionProvider._cache.put(sid, token)
    respondWithHeader(tokenHeader)(route)
  }
  def checkToken(route: Route): Route = withSession { sid =>
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
