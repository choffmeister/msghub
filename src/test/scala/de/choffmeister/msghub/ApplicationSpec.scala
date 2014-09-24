package de.choffmeister.msghub

import org.specs2.mutable._

class ApplicationSpec extends Specification {
  "Application" should {
    "run" in {
      val app = new Application()
      app.run(Array.empty[String])

      ok
    }
  }
}
