package de.choffmeister.msghub

import akka.actor._

import scala.concurrent.duration._

class Application extends Bootable {
  implicit val system = ActorSystem("msghub")
  implicit val executor = system.dispatcher

  def startup() = {
    val smtpServer = system.actorOf(Props(new SmtpServer("0.0.0.0", 2525)), "smtpserver")
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
