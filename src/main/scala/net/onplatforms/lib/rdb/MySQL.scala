package net.onplatforms.lib.rdb

import slick.driver.MySQLDriver.api._

class MySQL(
  env: {
  }
) {

  val db: slick.driver.MySQLDriver.backend.Database =
    Database.forConfig("net.onplatforms.lib.rdb.mysql")

}

