package arimitsu.sf.platform.interceptor

import akka.event.Logging
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

class LoggingInterceptor(env: {
}) {
  def intercept(underlying: => Route) =
    logRequestResult("access-log", Logging.InfoLevel)(underlying)
}
