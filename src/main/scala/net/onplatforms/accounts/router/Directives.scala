package net.onplatforms.accounts.router

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._

trait Directives extends JsonProtocol {
  def notFound(message: String) =
    complete(StatusCodes.NotFound, jsonMsg(message))
}
