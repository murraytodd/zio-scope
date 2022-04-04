ThisBuild / organization := "com.murraywilliams"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.6"

lazy val oldVersion = "2.0.0-RC2"
lazy val newVersion = "2.0.0-RC4"

lazy val oldzio = (project in file("old"))
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % oldVersion,
      "dev.zio" %% "zio-streams" % oldVersion,
      "dev.zio" %% "zio-test-sbt" % oldVersion % Test,
      "dev.zio" %% "zio-test" % oldVersion % Test
    )
  )

lazy val newzio = (project in file("new"))
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % newVersion,
      "dev.zio" %% "zio-streams" % newVersion,
      "dev.zio" %% "zio-test-sbt" % newVersion % Test,
      "dev.zio" %% "zio-test" % newVersion % Test
    )
  )