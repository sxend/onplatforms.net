package net.onplatforms.accounts.router

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import net.onplatforms.accounts.entity._
import spray.json.DefaultJsonProtocol
import spray.json._

trait JsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val TwitterSighupFormat = jsonFormat0(TwitterSighup)
  implicit val OwnedSignupFormat = jsonFormat3(OwnedSignup)
  implicit val OwnedSignupResultFormat = jsonFormat1(OwnedSignupResult)
}
