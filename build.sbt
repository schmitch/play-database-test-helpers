import Dependencies._

name := "database-test-helpers"
organization := "de.gccc.play"
scalaVersion := "2.12.6"

lazy val root = (project in file(".")).settings(
  libraryDependencies ++= Seq(
    playJdbcApi % Provided,
    postgresql  % Provided
  )
)

sonatypeProfileName := "de.gccc"
publishTo := sonatypePublishTo.value
publishMavenStyle := true
licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/schmitch/play-database-test-helpers"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/schmitch/play-database-test-helpers"),
    "scm:git@github.com:schmitch/play-database-test-helpers.git"
  )
)
developers := List(
  Developer(
    id = "schmitch",
    name = "Christian Schmitt",
    email = "c.schmitt@briefdomain.de",
    url = url("http://gccc.de")
  )
)

import ReleaseTransformations._
releaseCrossBuild := true // true if you cross-build the project for multiple Scala versions
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  // For non cross-build projects, use releaseStepCommand("publishSigned")
  releaseStepCommandAndRemaining("+publishSigned"),
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)
