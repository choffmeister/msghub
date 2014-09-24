package de.choffmeister.msghub

import akka.actor._
import akka.testkit._
import org.specs2.execute._
import org.specs2.mutable._

class TestActorSystem extends TestKit(ActorSystem()) with ImplicitSender with After {
  def after = system.shutdown()
}
