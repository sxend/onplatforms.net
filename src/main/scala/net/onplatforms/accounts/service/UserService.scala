package net.onplatforms.accounts.service

import akka.actor.Actor
import akka.pattern._
import net.onplatforms.accounts.datasource.Tables.SignupUsersRow
import slick.driver.MySQLDriver.api._
import net.onplatforms.lib.rdb.MySQL
import net.onplatforms.accounts.db.Actions._
import net.onplatforms.accounts.datasource.Tables._

class UserService(env: {
                    val mysql: MySQL
                  }) extends Actor {
  val db = env.mysql.db
  import context.dispatcher
  import UserService.Protocol._
  def receive = {
    case find: FindProfileByUserId =>
      val action = for {
        su <- findOneSignupUserByUserId(find.userId).map(maskPasswordHash)
      } yield Profile(find.userId, su)
      db.run(action).pipeTo(sender)
  }

  private def maskPasswordHash(suOpt: Option[SignupUsersRow]) =
    suOpt.map(_.copy(passwordHash = ""))
}

object UserService {
  object Protocol {
    case class FindProfileByUserId(userId: String)
    sealed trait FindProfileResult
    case class Profile(userId: String, singupUser: Option[SignupUsersRow]) extends FindProfileResult
  }
}