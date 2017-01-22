package net.onplatforms.accounts.router

import akka.actor.{ActorRef, ActorSystem}
import akka.event.LoggingAdapter
import akka.http.scaladsl.server.Directives._
import akka.pattern._
import akka.util.Timeout
import net.onplatforms.accounts.entity._
import net.onplatforms.accounts.provider.SessionProvider
import net.onplatforms.accounts.service.{CacheService, UserService}

import scala.concurrent.duration._
import scala.util._

class HomeRouter(
  env: => {
    val system: ActorSystem
    def cacheService: CacheService
    def userService: ActorRef
    val logger: LoggingAdapter
  }
) extends JsonProtocol
  with SessionProvider with Directives {
  override val cacheService: CacheService = env.cacheService
  private val userService: ActorRef = env.userService
  private implicit val timeout = Timeout(2.seconds)

  def routes = home
  private def home = get(path("home") {
    withSession {
      case Session(_, Some(userId), _) =>
        onComplete(getProfile(userId)) {
          case Success(profile) if profile.singupUser.isDefined =>
            complete(HomeResponse(profile.singupUser.map(_.userName).getOrElse("anonymous")))
          case Success(_) => notFound("user notfound")
          case Failure(t) =>
            env.logger.error(t, t.getMessage)
            notFound("user notfound")
        }
      case msg =>
        notFound("home api is private")
    }
  })

  private def getProfile(userId: String) =
    userService.ask(UserService.Protocol.FindProfileByUserId(userId)).mapTo[UserService.Protocol.Profile]
}
