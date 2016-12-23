package net.onplatforms.accounts.service

import java.util.UUID

import AuthenticationService.Protocol._
import akka.actor.Actor
import akka.event.LoggingAdapter
import akka.pattern._
import com.typesafe.config.Config
import net.onplatforms.accounts.datasource.Tables
import net.onplatforms.lib.rdb.MySQL
import slick.driver.MySQLDriver.api._
import org.apache.commons.codec.digest.DigestUtils

import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}

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
  private val db = env.mysql.db

  def receive = {
    case Signup(userName, email, password) =>
      signup(userName, email, password).pipeTo(sender())
    case Signin(email, password) =>
      signin(email, password).pipeTo(sender())
  }
  private def signup(userName: String, email: String, password: String): Future[SignupResult] = {
    val action = for {
      signupUserOpt <- findOneSignupedUserAction(email)
      result <- signupUserOpt match {
        case Some(signupUser) => findUser(signupUser.userId)
        case _                => createUsers(email, password, userName)
      }
    } yield result

    db.run(action.transactionally)
  }

  private def signin(email: String, password: String): Future[SigninResult] = {
    val action = findOneSignupedUserAction(email).map {
      case Some(signupUser) if signupUser.passwordHash == passwordHashing(email, password) =>
        Success(signupUser.userId)
      case _ => Fail()
    }
    db.run(action.transactionally)
  }

  private def createUsers[A](email: String, password: String, userName: String) = {
    for {
      userId <- createNewUserAction
      _ <- createSignupUserAction(email, passwordHashing(email, password), userName, userId)
    } yield NewUser(userId, email, userName)
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
  private def createNewUserAction = {
    val id = UUID.randomUUID().toString
    (Tables.Users.map(_.id) += id).map(_ => id)
  }
  private def createSignupUserAction(email: String, passwordHash: String, userName: String, userId: String) =
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

    case class Signin(email: String, password: String)
    sealed trait SigninResult
    case class Success(userId: String) extends SigninResult
    case class Fail() extends SigninResult
  }
}
