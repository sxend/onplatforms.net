package net.onplatforms.accounts.service

import AuthenticationService.Protocol._
import akka.actor.Actor
import akka.pattern._
import net.onplatforms.accounts.entity.User
import net.onplatforms.lib.rdb.MySQL

import scala.concurrent.Future

class AuthenticationService(env: {
                              val mysql: MySQL
                            }) extends Actor {
  import context.dispatcher

  def receive = {
    case Owned(email, password) =>
      Future(User("user")).pipeTo(sender())
  }

}

object AuthenticationService {
  object Protocol {
    case class Owned(email: String, password: String)
  }
}
