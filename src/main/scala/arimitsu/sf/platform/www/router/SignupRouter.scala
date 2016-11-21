package arimitsu.sf.platform.www.router

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Success

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