import sbt.Keys._
import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

name := "tripletail"

val akkaVersion = "2.5.22"
val akkkHttpVersion = "10.1.8"
val quillVersion = "3.1.0"
val circeVersion =  "0.11.1"
val sttpVersion = "1.5.17"

val jsCompileMode = fastOptJS  // fullOptJS

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
  .settings(
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion
    )
  )
lazy val sharedJS = shared.js
lazy val sharedJVM = shared.jvm

lazy val js = (project in file("js"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % "0.6",
      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-generic" % circeVersion,
      "io.circe" %%% "circe-parser" % circeVersion
    )
  ) dependsOn sharedJS

lazy val sw = (project in file("sw"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "com.raquo" %%% "domtypes" % "0.9.4"
    )
  )

lazy val jvm = (project in file("jvm"))
  .configs(IntegrationTest)
  .settings(commonSettings)
  .settings(
    Defaults.itSettings,
    mainClass in reStart := Some("tripletail.PoolApp"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkkHttpVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "de.heikoseeberger" %% "akka-http-circe" % "1.25.2",
      "io.getquill" %% "quill-sql" % quillVersion,
      "io.getquill" %% "quill-async-postgres" % quillVersion,
      "com.github.cb372" %% "scalacache-caffeine" % "0.27.0",
      "com.typesafe" % "config" % "1.3.3",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.softwaremill.sttp" %% "akka-http-backend" % sttpVersion % "it,test",
      "com.softwaremill.sttp" %% "circe" % sttpVersion % "it,test",
      "org.scalatest" %% "scalatest" % "3.0.5" % "it,test"
    ),
    (resources in Compile) += (jsCompileMode in (sharedJS, Compile)).value.data,
    (resources in Compile) += (jsCompileMode in (js, Compile)).value.data,
    (resources in Compile) += (jsCompileMode in (sw, Compile)).value.data
  ) dependsOn(sharedJS, sharedJVM, js, sw)