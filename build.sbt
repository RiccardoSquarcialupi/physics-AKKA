ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.3"

lazy val root = (project in file("."))
  .settings(
    name := "physics-AKKA"
  )

lazy val akkaVersion = "2.6.19"
lazy val akkaGroup = "com.typesafe.akka"
libraryDependencies ++= Seq(
  akkaGroup %% "akka-actor-typed" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  akkaGroup %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.2.11" % Test
)