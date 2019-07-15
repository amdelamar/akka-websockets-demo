name := "akka-websockets-demo"
organization := "com.amdelamar"
version := "1.0"
scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.8",
  "com.typesafe.akka" %% "akka-stream" % "2.5.19"
)

lazy val root = (project in file("."))
