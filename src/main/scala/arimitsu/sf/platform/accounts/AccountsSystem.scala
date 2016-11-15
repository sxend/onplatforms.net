package arimitsu.sf.platform.accounts

import akka.http.scaladsl.Http
import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import arimitsu.sf.platform.accounts.router.SignupRouter

object AccountsSystem {
  def main(args: Array[String]): Unit = {
    val env = new {
      implicit val system = ActorSystem("platform-system")
      implicit val materializer = ActorMaterializer()
      val logger = system.log
      val signupRouter = new SignupRouter(this)
    }
    import env._
    val route = logRequest("access-log", Logging.InfoLevel) {
      get(path("")(redirect("/signup", StatusCodes.Found))) ~
        get(path("signup")(env.signupRouter.signup))
    }
    Http().bindAndHandle(route, "0.0.0.0", 8080)
  }
}
