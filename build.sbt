import scalariform.formatter.preferences._

name := "msghub"

version := "0.0.1-SNAPSHOT"

organization := "de.choffmeister"

scalaVersion := "2.10.4"

scalacOptions := Seq("-encoding", "utf8")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.5",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.5" % "test",
  "org.specs2" %% "specs2" % "2.3.11" % "test"
)

packSettings

packMain := Map("msghub" -> "de.choffmeister.msghub.Application")

scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(RewriteArrowSymbols, true)
  .setPreference(SpacesWithinPatternBinders, true)
  .setPreference(CompactControlReadability, false)
