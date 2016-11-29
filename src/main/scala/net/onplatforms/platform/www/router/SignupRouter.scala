package net.onplatforms.platform.www.router

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._

class SignupRouter(env: {
                   }) {
  def handle = parameter('returnTo.?) { (returnToOpt) =>
    val returnTo = returnToOpt.getOrElse("https://www.onplatforms.net")
    redirect(s"https://accounts.onplatforms.net/signup?returnTo=$returnTo", StatusCodes.Found)
  }

}

object SignupRouter {

}