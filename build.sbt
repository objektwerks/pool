import sbt.Keys.version
import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

name := "tripletail"

val akkaVersion = "2.5.19"
val akkkHttpVersion = "10.1.7"
val quillVersion = "2.6.0"
val circeVersion =  "0.11.0"
val scalaJsDomVersion = "0.9.6"

lazy val commonSettings = Defaults.coreDefaultSettings ++ Seq(
  organization := "objektwerks",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.8"
)

lazy val root = project.in(file("."))
  .aggregate(sharedJS, sharedJVM, js, sw, jvm)
  .settings(commonSettings)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(commonSettings)

lazy val sharedJS = shared.js
lazy val sharedJVM = shared.jvm

lazy val js = (project in file("js"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % scalaJsDomVersion,
      "org.scala-js" %%% "scalajs-java-time" % "0.2.5",
      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-generic" % circeVersion
    )
  ) dependsOn sharedJS

lazy val sw = (project in file("sw"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % scalaJsDomVersion
    )
  )

lazy val jvm = (project in file("jvm"))
  .settings(commonSettings)
  .settings(
    mainClass in reStart := Some("tripletail.TripletailApp"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkkHttpVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "de.heikoseeberger" %% "akka-http-circe" % "1.23.0",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.getquill" %% "quill-sql" % quillVersion,
      "io.getquill" %% "quill-async-postgres" % quillVersion,
      "com.typesafe" % "config" % "1.3.3",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
      "com.typesafe.akka" %% "akka-http-testkit" % akkkHttpVersion % "test",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    ),
    (resources in Compile) += (fullOptJS in (sharedJS, Compile)).value.data,
    (resources in Compile) += (fullOptJS in (js, Compile)).value.data,
    (resources in Compile) += (fullOptJS in (sw, Compile)).value.data
  ) dependsOn(sharedJS, sharedJVM, js, sw)