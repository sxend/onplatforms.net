package net.onplatforms.accounts.router

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import net.onplatforms.accounts.entity._
import spray.json.DefaultJsonProtocol
import spray.json._

trait JsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  case class Empty()
  case class Message(message: String)
  def jsonMsg(msg: String) = Message(msg)
  implicit val EmptyFormat = jsonFormat0(Empty)
  implicit val MessageFormat = jsonFormat1(Message)
  implicit val TwitterSighupFormat = jsonFormat0(TwitterSighup)
  implicit val SignupFormat = jsonFormat3(Signup)
  implicit val SignupResponseFormat = jsonFormat1(SignupResponse)
  implicit val SigninFormat = jsonFormat2(Signin)
  implicit val SigninResponseFormat = jsonFormat1(SigninResponse)
  implicit val SignoutFormat = jsonFormat0(Signout)
  implicit val SignoutResponseFormat = jsonFormat1(SignoutResponse)
  implicit val HomeFormat = jsonFormat0(Home)
  implicit val HomeResponseFormat = jsonFormat1(HomeResponse)
}
