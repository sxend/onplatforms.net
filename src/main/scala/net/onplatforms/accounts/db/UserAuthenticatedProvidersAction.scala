package net.onplatforms.accounts.db

import net.onplatforms.accounts.datasource.Tables

trait UserAuthenticatedProvidersAction extends ActionSupport {
  import slick.driver.MySQLDriver.api._
  def provideUser(userId: String) =
    Tables.UserAuthenticatedProviders.map(u =>
      (u.userId, u.authenticationProviderId)
    ) += (userId, "signup")
}

object UserAuthenticatedProvidersAction extends UserAuthenticatedProvidersAction