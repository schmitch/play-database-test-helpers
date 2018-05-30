import sbt._

object Dependencies {
  lazy val postgresql = "org.postgresql" % "postgresql" % "42.2.2"
  lazy val playJdbcApi = "com.typesafe.play" %% "play-jdbc-api" % "2.6.15"
  lazy val scalaTest   = "org.scalatest"     %% "scalatest"     % "3.0.5"
}
