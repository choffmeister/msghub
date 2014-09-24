import scalariform.formatter.preferences._

name := "msghub"

version := "0.0.1-SNAPSHOT"

organization := "de.choffmeister"

scalaVersion := "2.10.4"

scalacOptions := Seq("-encoding", "utf8")

libraryDependencies ++= {
  val akkaVersion = "2.3.5"
  val dependencies = Seq(
    "ch.qos.logback" % "logback-classic" % "1.0.13",
    "com.typesafe" % "config" % "1.2.0",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  )
  val testDependencies = Seq(
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    "org.specs2" %% "specs2" % "2.3.11"
  ).map(_ % "test")
  dependencies ++ testDependencies
}

packSettings

packMain := Map("msghub" -> "de.choffmeister.msghub.Application")

packExtraClasspath := Map("msghub" -> Seq("${PROG_HOME}/config"))

scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(RewriteArrowSymbols, true)
  .setPreference(SpacesWithinPatternBinders, true)
  .setPreference(CompactControlReadability, false)
