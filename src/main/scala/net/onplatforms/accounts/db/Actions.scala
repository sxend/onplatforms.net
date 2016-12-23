package net.onplatforms.accounts.db

trait Actions extends AnyRef
  with UsersAction
  with SignupUsersAction
  with UserAuthenticatedProvidersAction {

}

object Actions extends Actions

trait ActionSupport