package net.onplatforms.accounts.provider

import java.util.UUID

import akka.http.scaladsl.model.headers.{HttpCookie, RawHeader}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive, Directive0, Directive1}
import net.onplatforms.accounts.router.JsonProtocol
import akka.http.scaladsl.server.Directives._
import com.typesafe.config.ConfigFactory
import net.onplatforms.accounts.entity.Session
import net.onplatforms.accounts.service.CacheService

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

trait SessionProvider extends AnyRef with JsonProtocol {
  protected val cacheService: CacheService

  private val SESSION_EXPIRE = 2592000
  private val sessionConfig = ConfigFactory.load.getConfig("net.onplatforms.accounts.session")

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

  def deleteSession: Directive0 = deleteCookie("sid", sessionConfig.getString("domain"), "/")

  private def getOrCreateSession(sid: String): Directive1[Session] = Directive { inner => ctx =>
    import ctx.executionContext
    val future = for {
      cachedSessionOpt <- getCache(sid)
      session <- cachedSessionOpt match {
        case Some(cachedSession) => Future.successful(cachedSession)
        case _                   => setCache(sid, Session(sid, None, Option(generateCSRFToken)))
      }
    } yield session
    future.flatMap(t => inner(Tuple1(t))(ctx))
  }

  private def setCache(sid: String, session: Session)(implicit ec: ExecutionContext): Future[Session] =
    cacheService.setSession(sid, session, SESSION_EXPIRE)

  private def getCache(sid: String): Future[Option[Session]] =
    cacheService.getSession(sid)

  def withNewCSRFToken: Directive0 = withSession.flatMap { session =>
    setNewCSRFToken(session)
  }

  def setNewCSRFToken(session: Session): Directive0 = {
    val token = generateCSRFToken
    setSession(session.sid, session.copy(csrfToken = Option(token))).tflatMap { _ =>
      respondWithDefaultHeader(RawHeader("X-CSRF-Token", token))
    }
  }

  def checkCSRFToken: Directive0 =
    headerValueByName("X-CSRF-Token").flatMap { sendToken =>
      withSession.flatMap {
        case Session(_, _, Some(token)) if sendToken == token =>
          pass
        case _ => reject
      }
    }

  private def cookieHeader(sid: String) =
    HttpCookie("sid", sid,
      maxAge = Option(SESSION_EXPIRE), domain = Option(sessionConfig.getString("domain")), path = Option("/"))

  private def newSessionId = UUID.randomUUID().toString
  private def generateCSRFToken = UUID.randomUUID().toString

}

object SessionProvider {
}
