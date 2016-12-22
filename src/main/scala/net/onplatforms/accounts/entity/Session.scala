package net.onplatforms.accounts.entity

case class Session(sid: String, csrfToken: Option[String] = None) extends Serializable