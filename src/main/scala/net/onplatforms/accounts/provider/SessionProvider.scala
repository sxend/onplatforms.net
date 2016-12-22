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
import akka.http.scaladsl.server.Directives._
import net.onplatforms.accounts.entity.Session

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

trait SessionProvider extends AnyRef with JsonProtocol {
  val memcached: Memcached
  import memcached.Imports._
  private val SESSION_EXPIRE = 2592000

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

  def deleteSession: Directive0 = deleteCookie("sid", ".onplatforms.local", "/")

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
    memcached.client.set(sid, session, SESSION_EXPIRE.seconds).map(_ => session)

  private def getCache(sid: String): Future[Option[Session]] =
    memcached.client.get[Session](sid)

  def setCSRFToken: Directive0 = withSession.flatMap { session =>
    reCoverCSRFToken(session)
  }

  def reCoverCSRFToken(session: Session): Directive0 = {
    val token = UUID.randomUUID().toString
    setSession(session.sid, session.copy(csrfToken = Option(token))).tflatMap { _ =>
      respondWithDefaultHeader(RawHeader("X-CSRF-Token", token))
    }
  }

  def protectCSRF: Directive0 =
    headerValueByName("X-CSRF-Token").flatMap { sendToken =>
      withSession.flatMap {
        case Session(_, Some(token)) if sendToken == token =>
          Directive.Empty
        case _ => reject
      }
    }

  private def cookieHeader(sid: String) =
    HttpCookie("sid", sid,
      maxAge = Option(SESSION_EXPIRE), domain = Option(".onplatforms.local"), path = Option("/"))

  private def newSessionId = UUID.randomUUID().toString

}

object SessionProvider {
}
