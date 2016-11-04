val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.4.8"
val akkaStream = "com.typesafe.akka" %% "akka-stream" % "2.4.9-RC1"
val akkaHttp = "com.typesafe.akka" %% "akka-http-experimental" % "2.4.9-RC2"
val sprayJSON = "com.typesafe.akka" %% "akka-http-spray-json-experimental"  % "2.4.8"
val liftJSON = "net.liftweb" % "lift-json_2.11" % "3.0-M8"
val joda = "joda-time" % "joda-time" % "2.9.4"

lazy val commonSettings = Seq(
  organization := "io.booking",
  version := "0.1.0",
  scalaVersion := "2.11.8"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "BookingIO",
    libraryDependencies += akkaActor,
    libraryDependencies += akkaStream,
    libraryDependencies += akkaHttp,
    libraryDependencies += sprayJSON,
    libraryDependencies += liftJSON,
    libraryDependencies += joda
  )
