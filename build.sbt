val liftJSON = "net.liftweb" %% "lift-json" % 3.1.1
val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.0.11"
val akkaActors = "com.typesafe.akka" %% "akka-actor" % "2.5.8"
val fastparse = "com.lihaoyi" %% "fastparse" % "1.0.0"

lazy val root = (project in file("."))
  .settings(
    name         := "core-api",
    organization := "github.discord-scala",
    scalaVersion := "2.12.4",
    version      := "0.1.0-SNAPSHOT"
    libraryDependencies ++= Seq(
        liftJson,
        akkaHttp,
        akkaActors,
        fastparse 
  )
