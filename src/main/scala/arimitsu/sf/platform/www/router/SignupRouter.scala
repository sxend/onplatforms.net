package arimitsu.sf.platform.www.router

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller

import spray.json._
import scala.concurrent.ExecutionContext

class SignupRouter(env: {
  val blockingContext: ExecutionContext
}) {
  def handle = parameter('returnTo.?) { (returnToOpt) =>
    val returnTo = returnToOpt.getOrElse("https://www.arimit.su")
    redirect(s"https://accounts.arimit.su/signup?returnTo=$returnTo", StatusCodes.Found)
  }
//  def handle0 = entity(as[String]) { (paramOpt) =>
//    val param = paramOpt.flatMap(_.split("=").tail.headOption).map(_.parseJson.convertTo[SignupParam])
//    val returnTo = paramOpt.

//  }

}

object SignupRouter {

}