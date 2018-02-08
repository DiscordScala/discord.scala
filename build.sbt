lazy val root = (project in file("."))
  .settings(
    name         := "core",
    organization := "github.discordscala",
    scalaVersion := "2.12.4",
    version      := "0.1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      "net.liftweb" %% "lift-json" % "3.2.0",
      "net.liftmodules" %% "json-extractor-ng_3.2" % "0.3.0",
      "com.typesafe.akka" %% "akka-actor" % "2.5.9",
      "com.typesafe.akka" %% "akka-stream" % "2.5.9",
      "com.lihaoyi" %% "fastparse" % "1.0.0",
      "org.typelevel" %% "spire" % "0.14.1",
      "com.softwaremill.sttp" %% "core" % "1.1.5",
      "com.softwaremill.sttp" %% "akka-http-backend" % "1.1.5",
      "org.clapper" %% "classutil" % "1.2.0"
    )
  )
