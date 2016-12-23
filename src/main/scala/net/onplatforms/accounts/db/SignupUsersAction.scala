package net.onplatforms.accounts.db

import net.onplatforms.accounts.datasource.Tables

import scala.concurrent.ExecutionContext

trait SignupUsersAction extends ActionSupport {
  import slick.driver.MySQLDriver.api._

  def findOneSignupUser(email: String)(implicit ec: ExecutionContext) =
    Tables.SignupUsers.filter(_.email === email).result.map(_.headOption)
  def findOneSignupUserByUserId(userId: String)(implicit ec: ExecutionContext) =
    Tables.SignupUsers.filter(_.userId === userId).result.map(_.headOption)

  def createSignupUser(
    email:        String,
    passwordHash: String,
    userName:     String,
    userId:       String)(implicit ec: ExecutionContext) =
    Tables.SignupUsers.map(u =>
      (u.email, u.passwordHash, u.userName, u.userId)
    ) += (email, passwordHash, userName, userId)

}

object SignupUsersAction extends SignupUsersAction