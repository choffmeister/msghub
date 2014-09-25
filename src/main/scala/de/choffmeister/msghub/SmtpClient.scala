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
  case object State6 extends State

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

  private var pipeline = new DelimitedTcpPipeline(ByteString("\r\n")).compose(new LoggingTcpPipeline("CLIENT"))
  private var adapter = context.actorOf(Props(new TcpPipelineAdapter(connection, self, pipeline)))
  connection ! Register(adapter)

  when(State0) {
    case Event(Received(Reply(221, _)), _) ⇒
      adapter ! Close
      goto(State0)
  }

  when(State1) {
    case Event(Received(Reply(220, remoteName)), _) ⇒
      command("HELO", "localhost")
      goto(State2)
  }

  when(State2) {
    case Event(Received(Reply(250, _)), _) ⇒
      command("MAIL", "FROM:<user1@domain.com>")
      goto(State3)
  }

  when(State3) {
    case Event(Received(Reply(250, _)), _) ⇒
      command("RCPT", "TO:<user2@domain.com>")
      goto(State4)
  }

  when(State4) {
    case Event(Received(Reply(250, _)), _) ⇒
      command("DATA")
      goto(State5)
  }

  when(State4) {
    case Event(Received(Reply(250, _)), _) ⇒
      command("DATA")
      goto(State5)
  }

  when(State5) {
    case Event(Received(Reply(354, _)), _) ⇒
      adapter ! Write(ByteString("Hello\r"))
      adapter ! Write(ByteString("\nWorld\r\n.\r"))
      adapter ! Write(ByteString("\n"))
      goto(State6)
  }

  when(State6) {
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
      command("QUIT")
      goto(State0)
  }

  startWith(State1, Empty)
  initialize()

  def command(code: String, message: String = "") = adapter ! Write(Command(code, message))
}
