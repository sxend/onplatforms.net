package arimitsu.sf.platform.www.router

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._

class SignupRouter(env: {
                   }) {
  def handle = parameter('returnTo.?) { (returnToOpt) =>
    val returnTo = returnToOpt.getOrElse("https://www.arimit.su")
    redirect(s"https://accounts.arimit.su/signup?returnTo=$returnTo", StatusCodes.Found)
  }

}

object SignupRouter {

}