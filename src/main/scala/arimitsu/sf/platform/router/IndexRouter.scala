package arimitsu.sf.platform.router

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._

class IndexRouter(env: {
}) {
  def handle = complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, html))
  private val html =
    """
      |<h1>Hello, World.</h1>
    """.stripMargin
}

object IndexRouter {
  def apply(env: {}) = new IndexRouter(env)
}