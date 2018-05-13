lazy val root = (project in file("."))
  .settings(
    name         := "core",
    organization := "org.discordscala",
    scalaVersion := "2.12.5",
    version := "0.1.0",
    libraryDependencies ++= Seq(
      "net.liftweb" %% "lift-json" % "3.2.0",
      "net.liftmodules" %% "json-extractor-ng_3.2" % "0.3.0",
      "com.typesafe.akka" %% "akka-actor" % "2.5.12",
      "com.typesafe.akka" %% "akka-stream" % "2.5.12",
      "org.typelevel" %% "spire" % "0.15.0",
      "com.softwaremill.sttp" %% "core" % "1.1.14",
      "com.softwaremill.sttp" %% "akka-http-backend" % "1.1.14",
      "org.clapper" %% "classutil" % "1.2.0",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    ),
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
  )
