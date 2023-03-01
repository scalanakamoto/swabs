ThisBuild / scalaVersion := "2.13.10"

// import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

val Http4s                 = "1.0.0-M30"
val CatsVersion            = "3.4.8"
val LogbackVersion         = "1.4.5"
val CatsCore               = "2.9.0"
val MunitVersion           = "0.7.29"
val MunitCatsEffectVersion = "1.0.7"
val Redis4cats             = "1.4.0"
val Circe                  = "0.14.4"
val PlayJson               = "2.9.4"

lazy val root = (project in file("."))
  .settings(
    organization := "org.swabs",
    name := "swabs-app",
    version := "1.0-SNAPSHOT",
    reStart / mainClass := Some("org.swabs.Server"),
    Compile / scalacOptions ++= Seq("-deprecation", "-explaintypes", "-feature", "-unchecked"),
    scalacOptions ++= Seq(
      "-encoding", "utf8", // Specify character encoding used by source files.
      "-language:implicitConversions", // Allow definition of implicit functions called views
      "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
      "-unchecked" // Enable additional warnings where generated code depends on assumptions.
    ),
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.4.2",
      "org.typelevel" %% "cats-effect" % CatsVersion,

      "org.http4s" %% "http4s-ember-server" % Http4s,
      "org.http4s" %% "http4s-dsl" % Http4s,
      "org.http4s" %% "http4s-play-json" % Http4s,

      "com.typesafe.play" %% "play-json" % PlayJson,
      "io.circe" %% "circe-generic" % Circe,

      "dev.profunktor" %% "redis4cats-core" % Redis4cats,
      "dev.profunktor" %% "redis4cats-effects" % Redis4cats,
      "dev.profunktor" %% "redis4cats-log4cats" % Redis4cats,

      "org.bouncycastle" % "bcprov-jdk15on" % "1.69",
      "org.bouncycastle" % "bcpkix-jdk15on" % "1.69",

      "org.typelevel" %% "log4cats-slf4j" % "2.5.0",
      "ch.qos.logback" % "logback-classic" % LogbackVersion % Runtime,

      "org.mockito" %% "mockito-scala-scalatest" % "1.17.12" % Test,
      "org.mockito" % "mockito-inline" % "5.1.1" % Test,
      "org.scalacheck" %% "scalacheck" % "1.17.0" % Test,
      "org.scalatestplus" %% "scalacheck-1-14" % "3.2.2.0" % Test,
      "com.danielasfregola" %% "random-data-generator" % "2.9" % Test,

      "org.scalameta" %% "svm-subs" % "20.2.0"
    ),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full)
  )

Test / fork := true // @see https://github.com/sbt/sbt/issues/3022
Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oSD")
