package net.onplatforms.accounts.entity

case class Signin(email: String, password: String)

case class SigninResponse(location: String)
