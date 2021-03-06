import sbt._

object Dependencies {
  lazy val postgresql = "org.postgresql" % "postgresql" % "42.2.6"
  lazy val playJdbcApi = "com.typesafe.play" %% "play-jdbc-api" % "2.7.3"
}
