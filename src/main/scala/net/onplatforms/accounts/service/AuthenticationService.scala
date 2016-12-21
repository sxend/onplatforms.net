package net.onplatforms.accounts.service

import java.util.UUID

import AuthenticationService.Protocol._
import akka.actor.Actor
import akka.event.LoggingAdapter
import akka.pattern._
import com.typesafe.config.Config
import net.onplatforms.accounts.entity.User
import net.onplatforms.accounts.io.rdb.Tables
import net.onplatforms.lib.rdb.MySQL
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
  private val SALT: String = env.config.getString(s"${env.namespace}.authentication.owned.password-hash-salt")
  private val db = env.mysql.DB

  def receive = {
    case Owned(userName, email, password) =>
      registerOwnedUser(userName, email, password).pipeTo(sender())
  }
  private def checkUserExists(email: String, password: String) = {

  }
  private def registerOwnedUser(userName: String, email: String, password: String): Future[User] = {
    val session = db.createSession()
    session.conn.setAutoCommit(false)
    Future {
      val user: User = {

        val id = UUID.randomUUID().toString
        val passwordHash = passwordHashing(email, password)
        val userStmt = session.prepareInsertStatement("insert into users(id) values (?)")
        userStmt.setString(1, id)
        env.logger.info(s"insert users: ${userStmt.executeUpdate()}")
        val ownedUserStmt = session.prepareInsertStatement("insert into owned_users(email, password_hash, user_name, user_id) values (?, ?, ?, ?)")
        ownedUserStmt.setString(1, email)
        ownedUserStmt.setString(2, passwordHash)
        ownedUserStmt.setString(3, userName)
        ownedUserStmt.setString(4, id)
        env.logger.info(s"insert owned_users: ${ownedUserStmt.executeUpdate()}")
        env.logger.info(s"register user: $id, $email, $passwordHash, $userName")
        User(id)
      }
      user
    }(env.blockingContext).andThen {
      case Success(user) =>
        session.conn.commit()
        user
      case Failure(t) =>
        env.logger.error(t, t.getMessage)
        session.conn.rollback()
        throw t
    }(env.blockingContext)
  }

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
    case class Owned(userName: String, email: String, password: String)
  }
}
