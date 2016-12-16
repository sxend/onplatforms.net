package net.onplatforms.accounts.entity

case class Signup(
  ownedSignupOpt:   Option[OwnedSignup],
  twitterSignupOpt: Option[TwitterSighup]
)
