package net.onplatforms.accounts.entity

case class Session(
  sid:       String,
  userId:    Option[String] = None,
  csrfToken: Option[String] = None) extends Serializable