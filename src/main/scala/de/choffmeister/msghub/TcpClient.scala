package de.choffmeister.msghub

import java.net.InetSocketAddress

import akka.actor._
import akka.io.Tcp._
import akka.io._

class TcpClient(connect: InetSocketAddress, handler: ActorRef => Props) extends Actor with ActorLogging {
  implicit val system = context.system
  IO(Tcp) ! Connect(connect)

  def receive = {
    case CommandFailed(_: Connect) =>
      log.error("Unable to connect to {}", connect)
      context.stop(self)

    case Connected(remote, local) =>
      log.debug("New connection to {}", remote)
      val connection = sender()
      context.actorOf(handler(connection))
  }
}
