package net.onplatforms.accounts.service

import java.util.UUID

import AuthenticationService.Protocol._
import akka.actor.Actor
import akka.event.LoggingAdapter
import akka.pattern._
import com.typesafe.config.Config
import net.onplatforms.accounts.entity.User
import net.onplatforms.accounts.io.rdb.Tables
import net.onplatforms.accounts.io.rdb.Tables._
import net.onplatforms.lib.rdb.MySQL
import slick.driver.MySQLDriver.api._
import org.apache.commons.codec.digest.DigestUtils

import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class AuthenticationService(
  env: {
    val logger: LoggingAdapter
    val config: Config
    val namespace: String
    val blockingContext: ExecutionContext
    val mysql: MySQL
  }
) extends Actor {
  import context.dispatcher
  private val SALT: String = env.config.getString(s"${env.namespace}.authentication.signup.password-hash-salt")
  private val db = env.mysql.DB

  def receive = {
    case Signup(userName, email, password) =>
      signup(userName, email, password).pipeTo(sender())
  }
  private def signup(userName: String, email: String, password: String): Future[SignupResult] = {
    val action = findOneSignupedUserAction(email).flatMap {
      case Some(signupUser) => findUser(signupUser.userId)
      case _ =>
        registerUsers(UUID.randomUUID().toString, email, password, userName)
    }
    db.run(action.transactionally)
  }
  private def registerUsers(id: String, email: String, password: String, userName: String) =
    DBIO.seq(
      registerUserAction(id),
      registerSignupUserAction(email, passwordHashing(email, password), userName, id)
    ).map {
        _ => NewUser(id, email, userName)
      }
  private def findUser(id: String) =
    findOneUserAction(id).map {
      case Some(user) => AlreadyExists()
      case _          => throw new RuntimeException(s"user not found.")
    }
  private def findOneSignupedUserAction(email: String) =
    Tables.SignupUsers.filter(_.email === email).result.map(_.headOption)
  private def findOneUserAction(id: String) =
    Tables.Users.filter(_.id === id).result.map(_.headOption)
  private def registerUserAction(id: String) =
    Tables.Users.map(_.id) += id
  private def registerSignupUserAction(email: String, passwordHash: String, userName: String, userId: String) =
    Tables.SignupUsers.map(u => (u.email, u.passwordHash, u.userName, u.userId)) += (email, passwordHash, userName, userId)

  @tailrec
  private def passwordHashing(email: String, password: String, count: Int = 0): String = {
    if (count >= AuthenticationService.STRETCHING_NUM) {
      password
    } else {
      passwordHashing(email, DigestUtils.sha512Hex(s"${email}_${password}_$SALT"), count + 1)
    }
  }
}

object AuthenticationService {
  private val STRETCHING_NUM = 10000
  object Protocol {
    case class Signup(userName: String, email: String, password: String)
    sealed trait SignupResult
    case class AlreadyExists() extends SignupResult
    case class NewUser(id: String, email: String, userName: String) extends SignupResult
  }
}
