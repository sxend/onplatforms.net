package net.onplatforms.platform.ranter.router

import akka.http.scaladsl.server.Route
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._

class AccountRouter(env: {
                    }) {
  val routes = signup ~ signin ~ signout

  def signup: Route = {
    get(path("signup")(reject))
  } ~ {
    post(path("signup")(reject))
  }
  def signin: Route = {
    get(path("signup")(reject))
  } ~ {
    post(path("signup")(reject))
  }
  def signout: Route = post(path("signout")(reject))
}
