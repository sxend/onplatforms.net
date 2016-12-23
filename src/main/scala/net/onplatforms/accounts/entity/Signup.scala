package net.onplatforms.accounts.entity

case class Signup(
  userName: String,
  email:    String,
  password: String
) {
  require(password.length >= 8)
}

case class SignupResponse(location: String)