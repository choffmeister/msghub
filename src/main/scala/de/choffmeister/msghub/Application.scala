package de.choffmeister.msghub

class Application {
  def run(args: Array[String]) {
    println("Hello World")
  }
}

object Application {
  def main(args: Array[String]) {
    val app = new Application()
    app.run(args)
  }
}
