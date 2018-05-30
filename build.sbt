import Dependencies._

name := "play-database-test-helpers"
organization := "de.gccc"
scalaVersion := "2.12.6"
version := "0.1.0-SNAPSHOT"

lazy val root = (project in file(".")).settings(
  libraryDependencies ++= Seq(
    playJdbcApi % Provided,
    postgresql % Provided,
    scalaTest   % Test
  )
)
