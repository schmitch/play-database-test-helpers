package de.gccc.test.database

import java.io.FileNotFoundException

import play.api.Environment

import scala.io.Source
import scala.util.control.NonFatal

/**
 * Defines Evolutions utilities functions.
 */
object Evolutions {

  private def statements(sql: String): Seq[String] = {
    // Regex matches on semicolons that neither precede nor follow other semicolons
    sql.split("(?<!;);(?!;)").map(_.trim.replace(";;", ";")).filter(_ != "")
  }

  private def directoryFiles(path: String, environment: Environment): List[String] = {
    environment
      .resourceAsStream(path)
      .map { in =>
        val sr = Source.fromInputStream(in)
        try {
          sr.getLines().toList.map(value => s"$path/$value")
        } finally {
          sr.close()
          in.close()
        }
      }
      .getOrElse(Nil)
  }

  private def fileContent(path: String, environment: Environment): String = {
    directoryFiles(path, environment).map { file =>
      environment
        .resourceAsStream(file)
        .map { in =>
          try {
            TestIO.readStreamAsString(in)
          } catch {
            case NonFatal(t) =>
              println(s"Fatal Error while Loading Evolution with name: $file")
              t
          } finally {
            in.close()
          }
        }
        .getOrElse(throw new FileNotFoundException())
    }.mkString
  }

  def load(path: String, environment: Environment): Seq[String] = {
    val content = fileContent(path, environment)
    statements(content)
  }

}
