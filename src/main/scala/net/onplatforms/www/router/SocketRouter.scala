package net.onplatforms.www.router

import akka.http.scaladsl.model.ws._
import akka.stream.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

class SocketRouter(
  env: {
    val materializer: ActorMaterializer
  }
) {
  implicit val materializer = env.materializer

  def handle = handleSocketIOMessages(handler)

  private def handler: Flow[Message, Message, Any] =
    Flow[Message].mapConcat {
      case tm: TextMessage =>
        TextMessage(Source.single("Hello ") ++ tm.textStream ++ Source.single("!")) :: Nil
      case bm: BinaryMessage =>
        bm.dataStream.runWith(Sink.ignore)
        Nil
    }
  private def handleSocketIOMessages(handler: Flow[Message, Message, Any]) = {
    // implement socket.io fallback logic
    handleWebSocketMessages(handler)
  }
}
