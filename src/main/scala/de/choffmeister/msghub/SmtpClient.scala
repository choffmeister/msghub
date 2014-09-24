package de.choffmeister.msghub

import java.net.InetSocketAddress

import akka.actor._
import akka.io.Tcp._
import akka.util.ByteString
import com.typesafe.config.ConfigFactory

object SmtpClient {
  sealed trait State
  case object State0 extends State
  case object State1 extends State
  case object State2 extends State
  case object State3 extends State
  case object State4 extends State
  case object State5 extends State

  sealed trait Data
  case object Empty extends Data
  case class Envelope(from: Option[String] = None, to: List[String] = Nil, body: ByteString = ByteString.empty) extends Data
}

/**
 * See http://tools.ietf.org/html/rfc5321
 */
class SmtpClient(connection: ActorRef) extends FSM[SmtpClient.State, SmtpClient.Data] {
  import de.choffmeister.msghub.SmtpProtocol._
  import de.choffmeister.msghub.SmtpClient._

  connection ! Register(self)

  when(State0) {
    case Event(Received(Reply(221, _)), _) ⇒
      connection ! Close
      goto(State0)
  }

  when(State1) {
    case Event(Received(Reply(220, remoteName)), _) ⇒
      command("HELO", "localhost")
      goto(State2)
  }

  when(State2) {
    case Event(Received(Reply(250, _)), _) ⇒
      command("QUIT")
      goto(State0)
  }

  whenUnhandled {
    case Event(_: ConnectionClosed, _) ⇒
      log.debug("Connection closed")
      context.stop(self)
      goto(State0)

    case Event(e, s) ⇒
      log.warning("received unhandled request {} in state {}/{}", e, stateName, s)
      stay()
  }

  startWith(State1, Empty)
  initialize()

  def command(code: String, message: String = "") = connection ! Write(Command(code, message))
}
