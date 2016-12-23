package net.onplatforms.accounts.db

import java.util.UUID

import net.onplatforms.accounts.datasource.Tables

import scala.concurrent.ExecutionContext

trait UsersAction extends ActionSupport {
  import slick.driver.MySQLDriver.api._

  def findOneUser(id: String)(implicit ec: ExecutionContext) =
    Tables.Users.filter(_.id === id).result.map(_.headOption)

  def createNewUser(implicit ec: ExecutionContext) = {
    val id = UUID.randomUUID().toString
    (Tables.Users.map(_.id) += id).map(_ => id)
  }

}

object UsersAction extends UsersAction