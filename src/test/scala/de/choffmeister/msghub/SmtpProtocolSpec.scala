package de.choffmeister.msghub

import akka.util.ByteString
import org.specs2.mutable.Specification

class SmtpProtocolSpec extends Specification {
  import de.choffmeister.msghub.SmtpProtocol._

  "SmtpProtocol" should {
    "apply to ByteString" in {
      Reply.apply(200, "") === ByteString("200\r\n")
      Reply.apply(200, "Hello World") === ByteString("200 Hello World\r\n")
    }

    "unapply from ByteString" in {
      Reply.unapply(ByteString("200\r\n")) === Some((200, ""))
      Reply.unapply(ByteString("200 Hello World\r\n")) === Some((200, "Hello World"))
    }
  }
}
