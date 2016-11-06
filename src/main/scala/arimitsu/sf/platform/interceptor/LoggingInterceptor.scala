package arimitsu.sf.platform.interceptor

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.{ Route, RouteResult }
import akka.http.scaladsl.server.directives.{ DebuggingDirectives, LoggingMagnet }
import scala.collection.JavaConverters._

class LoggingInterceptor(env: {
}) {
  def intercept(underlying: => Route) = logging(underlying)
  private def outputLog(logger: LoggingAdapter) = (req: HttpRequest) => (res: RouteResult) => {
    res match {
      case complete: RouteResult.Complete =>
        logger.info(s"Completed: Request: ${req.method.value} ${req.uri.toString} ${complete.getResponse.status}")
      case rejected: RouteResult.Rejected =>
        logger.info(s"Rejected: Request: ${req.method.value} ${req.uri.toString} ${rejected.getRejections.asScala.toList}")
      case _ =>
    }
  }
  private val logging =
    DebuggingDirectives.logRequestResult(LoggingMagnet(logger => outputLog(logger)))
}
