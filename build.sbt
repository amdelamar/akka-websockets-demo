libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"   % "10.1.11" withSources(),
  "com.typesafe.akka" %% "akka-stream" % "2.6.1"   withSources()
)
name := "akka-websockets-demo"
organization := "com.amdelamar"
scalaVersion := "2.13.1"
version := "1.1"
