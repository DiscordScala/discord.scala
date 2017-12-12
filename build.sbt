lazy val root = (project in file("."))
  .settings(
    name         := "core",
    organization := "github.discordscala",
    scalaVersion := "2.12.4",
    version      := "0.1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      "net.liftweb" %% "lift-json" % "3.1.1",
    	"com.typesafe.akka" %% "akka-http" % "10.0.11",
    	"com.typesafe.akka" %% "akka-actor" % "2.5.8",
    	"com.lihaoyi" %% "fastparse" % "1.0.0",
    )
  )
