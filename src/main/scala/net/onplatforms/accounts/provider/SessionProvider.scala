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
import net.onplatforms.accounts.entity.Session

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

trait SessionProvider extends AnyRef with JsonProtocol {
  val memcached: Memcached
  import memcached.Imports._

  def withSession: Directive1[Session] =
    optionalCookie("sid").flatMap {
      case Some(pair) if pair.value.nonEmpty => getOrCreateSession(pair.value)
      case _                                 => withNewSession
    }

  def withNewSession: Directive1[Session] =
    getOrCreateSession(newSessionId).flatMap { session =>
      setCookie(cookieHeader(session.sid)).tmap(_ => session)
    }

  def setSession(sid: String, session: Session): Directive0 = Directive { inner => ctx =>
    import ctx.executionContext
    setCache(sid, session).flatMap(_ => inner(())(ctx))
  }

  private def getOrCreateSession(sid: String): Directive1[Session] = Directive { inner => ctx =>
    import ctx.executionContext
    val future = for {
      cachedSessionOpt <- getCache(sid)
      session <- cachedSessionOpt match {
        case Some(cachedSession) => Future.successful(cachedSession)
        case _                   => setCache(sid, Session(sid))
      }
    } yield session
    future.flatMap(t => inner(Tuple1(t))(ctx))
  }

  private def setCache(sid: String, session: Session)(implicit ec: ExecutionContext): Future[Session] =
    memcached.client.set(sid, session, 2.seconds).map(_ => session)

  private def getCache(sid: String): Future[Option[Session]] =
    memcached.client.get[Session](sid)

  def tokenEndpoint: Route = post(path("token") {
    complete(Empty())
  })

  private def cookieHeader(sid: String) =
    HttpCookie("sid", sid,
      maxAge = Option(2592000), domain = Option(".onplatforms.net"), path = Option("/"))

  private def newSessionId = UUID.randomUUID().toString

}

object SessionProvider {
  val _cache = new ConcurrentHashMap[String, String]()
}
