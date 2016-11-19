package arimitsu.sf.platform.www.router

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import arimitsu.sf.platform.lib.directive.Directives._

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration._
import scala.util.{ Failure, Success, Try }

class SignupRouter(env: {
  val blockingContext: ExecutionContext
}) {

  def handle = getCallbackUrl { callbackUrl =>
    redirect(s"https://accounts.arimit.su/signup?returnTo=$callbackUrl", StatusCodes.Found)
  }

  def getCallbackUrl(route: String => Route) =
    onComplete(Future("https://www.arimit.su")(env.blockingContext)) {
      case Success(callbackUrl) => route(callbackUrl)
      case _                    => reject
    }
}

object SignupRouter {

}