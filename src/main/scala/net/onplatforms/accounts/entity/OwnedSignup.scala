package net.onplatforms.accounts.entity

case class OwnedSignup(
  userName: String,
  email:    String,
  password: String
) {
  require(password.length >= 8)
}

case class OwnedSignupResult(id: String)