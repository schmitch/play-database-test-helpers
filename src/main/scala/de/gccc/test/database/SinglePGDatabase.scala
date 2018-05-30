package de.gccc.test.database

import java.io.PrintWriter
import java.sql.Savepoint
import java.util.logging.Logger
import java.util.{ Properties, UUID }

import javax.sql.DataSource
import org.postgresql.PGConnection
import org.postgresql.jdbc.AutoSave
import play.api.db.Database

import scala.util.control.NonFatal

class SinglePGDatabase(override val url: String, username: String, password: String) extends Database {

  import java.sql.{ Connection, DriverManager }

  def uuid: String = UUID.randomUUID().toString.replace("-", "_").toUpperCase().substring(0, 10)

  private val connection: Connection = {
    val props = new Properties()
    props.setProperty("user", username)
    props.setProperty("password", password)
    val conn = DriverManager.getConnection(url, props)
    conn.setAutoCommit(false)
    // we use autosave to actually rollback the pg transaction
    // if any statement failed
    conn.unwrap(classOf[PGConnection]).setAutosave(AutoSave.ALWAYS)
    conn
  }

  def internalConnection: Connection = connection

  def internalDataSource(noCommit: Boolean): DataSource = {
    new DataSource {
      override def getConnection: Connection = new ConnectionNoClose(internalConnection, noCommit)
      override def getConnection(username: String, password: String): Connection =
        new ConnectionNoClose(internalConnection, noCommit)
      override def unwrap[T](iface: Class[T]): T          = ???
      override def isWrapperFor(iface: Class[_]): Boolean = false
      override def setLoginTimeout(seconds: Int): Unit    = ()
      override def setLogWriter(out: PrintWriter): Unit   = ()
      override def getParentLogger: Logger                = ???
      override def getLoginTimeout: Int                   = ???
      override def getLogWriter: PrintWriter              = ???
    }
  }

  override def name: String = "default"
  override def dataSource: DataSource = {
    new DataSource {
      override def getConnection: Connection                                     = FakeConnectionProxy.wrap(connection)
      override def getConnection(username: String, password: String): Connection = FakeConnectionProxy.wrap(connection)
      override def isWrapperFor(iface: Class[_]): Boolean                        = false
      override def setLoginTimeout(seconds: Int): Unit                           = ()
      override def setLogWriter(out: PrintWriter): Unit                          = ()
      override def unwrap[T](iface: Class[T]): T                                 = ???
      override def getParentLogger: Logger                                       = ???
      override def getLoginTimeout: Int                                          = ???
      override def getLogWriter: PrintWriter                                     = ???
    }
  }
  override def getConnection(): Connection = {
    FakeConnectionProxy.wrap(connection)
  }
  override def getConnection(autocommit: Boolean): Connection = getConnection()
  override def withConnection[A](block: Connection => A): A = {
    val fc = FakeConnectionProxy.wrap(connection)
    try {
      block(fc)
    } catch {
      case NonFatal(t) =>
        throw t
    }
  }
  override def withConnection[A](autocommit: Boolean)(block: Connection => A): A = {
    withConnection(block)
  }
  override def withTransaction[A](block: Connection => A): A = {
    withConnection(block)
  }

  override def shutdown(): Unit = {
    // we never shutdown the single connection
    println("database connection close")
    ()
  }

  def createSavepoint(savepoint: String): Savepoint = {
    internalConnection.setSavepoint(savepoint)
  }

  def rollback(savepoint: Savepoint): Unit = {
    internalConnection.rollback(savepoint)
  }

  def close(): Unit = {
    println("database connection close")
    connection.close()
  }

}
