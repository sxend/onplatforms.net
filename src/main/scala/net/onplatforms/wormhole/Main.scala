package net.onplatforms.wormhole

import java.util.concurrent.atomic.{AtomicInteger, AtomicReference}

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.event.Logging._
import akka.http.scaladsl._
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.{ExecutionContext, Future, Promise}

object Main {
  val config: Config = ConfigFactory.load
  val namespace = "net.onplatforms.wormhole"
  def withNamespace(suffix: String) = s"$namespace.$suffix"
  def getConfigInNamespace(suffix: String): Config = config.getConfig(withNamespace(suffix))
  val systemConfig: Config = getConfigInNamespace("system")
  val env = new {
    val config: Config = Main.config
    val namespace: String = Main.namespace
    val version: String = systemConfig.getString("version")
    implicit val system: ActorSystem = ActorSystem("accounts-system", this.config)
    val logger: LoggingAdapter = Logging(system.eventStream, getClass)
    implicit val materializer = ActorMaterializer()
    val blockingContext: ExecutionContext =
      system.dispatchers.lookup(withNamespace("dispatchers.blocking-io-dispatcher"))
  }
  def main(args: Array[String]): Unit = {
    import env._
    val server = new {} with Runnable {
      override def run(): Unit = {
        val mapping = {
          path("collect")(complete(""))
        } ~ reject
        Http().bindAndHandle(mapping, systemConfig.getString("listen-address"), systemConfig.getInt("listen-port"))
      }
    }
    server.run()
  }

  private def singleton[A](creator: => A): () => A = {
    var actor: Option[A] = None // non thread-safe!!!
    () => {
      if (actor.isEmpty) {
        actor = Option(creator)
      }
      actor.get
    }
  }
  object ActorNames {
    class Generator(basename: String) {
      private val seqNr = new AtomicInteger(0)
      def name: String = s"${basename}_${seqNr.incrementAndGet()}"
    }
    class SingletonGenerator(basename: String) extends Generator(basename) {
      override def name: String = basename
    }
  }
}
