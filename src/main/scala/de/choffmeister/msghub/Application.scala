package de.choffmeister.msghub

import akka.actor._

import scala.concurrent.duration._

class Application extends Bootable {
  implicit val system = ActorSystem("msghub")
  implicit val executor = system.dispatcher

  def startup() = {
    val config = SmtpServer.Config.load()
    val server = system.actorOf(Props(new TcpServer(config.bind, conn ⇒ Props(new SmtpServer(conn, config)))))
    val client = system.actorOf(Props(new TcpClient(config.bind, conn ⇒ Props(new SmtpClient(conn)))))
  }

  def shutdown() = {
    system.shutdown()
    system.awaitTermination(3.seconds)
  }
}

object Application {
  def main(args: Array[String]) {
    val app = new Application()
    app.startup()
  }
}

trait Bootable {
  def startup(): Unit
  def shutdown(): Unit

  sys.ShutdownHookThread(shutdown())
}
