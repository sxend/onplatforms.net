package net.onplatforms.accounts.io
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.driver.MySQLDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.driver.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(AuthenticationProvider.schema, MultiProviderAuthenticated.schema, OwnedUsers.schema, SchemaVersion.schema, TwitterUsers.schema, Users.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /**
   * Entity class storing rows of table AuthenticationProvider
   *  @param id Database column id SqlType(VARCHAR), PrimaryKey, Length(32,true)
   */
  case class AuthenticationProviderRow(id: String)
  /** GetResult implicit for fetching AuthenticationProviderRow objects using plain SQL queries */
  implicit def GetResultAuthenticationProviderRow(implicit e0: GR[String]): GR[AuthenticationProviderRow] = GR {
    prs =>
      import prs._
      AuthenticationProviderRow(<<[String])
  }
  /** Table description of table authentication_provider. Objects of this class serve as prototypes for rows in queries. */
  class AuthenticationProvider(_tableTag: Tag) extends Table[AuthenticationProviderRow](_tableTag, "authentication_provider") {
    def * = id <> (AuthenticationProviderRow, AuthenticationProviderRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = Rep.Some(id).shaped.<>(r => r.map(_ => AuthenticationProviderRow(r.get)), (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(VARCHAR), PrimaryKey, Length(32,true) */
    val id: Rep[String] = column[String]("id", O.PrimaryKey, O.Length(32, varying = true))
  }
  /** Collection-like TableQuery object for table AuthenticationProvider */
  lazy val AuthenticationProvider = new TableQuery(tag => new AuthenticationProvider(tag))

  /**
   * Entity class storing rows of table MultiProviderAuthenticated
   *  @param userId Database column user_id SqlType(VARCHAR), Length(64,true)
   *  @param authenticationProviderId Database column authentication_provider_id SqlType(VARCHAR), Length(32,true)
   */
  case class MultiProviderAuthenticatedRow(userId: String, authenticationProviderId: String)
  /** GetResult implicit for fetching MultiProviderAuthenticatedRow objects using plain SQL queries */
  implicit def GetResultMultiProviderAuthenticatedRow(implicit e0: GR[String]): GR[MultiProviderAuthenticatedRow] = GR {
    prs =>
      import prs._
      MultiProviderAuthenticatedRow.tupled((<<[String], <<[String]))
  }
  /** Table description of table multi_provider_authenticated. Objects of this class serve as prototypes for rows in queries. */
  class MultiProviderAuthenticated(_tableTag: Tag) extends Table[MultiProviderAuthenticatedRow](_tableTag, "multi_provider_authenticated") {
    def * = (userId, authenticationProviderId) <> (MultiProviderAuthenticatedRow.tupled, MultiProviderAuthenticatedRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userId), Rep.Some(authenticationProviderId)).shaped.<>({ r => import r._; _1.map(_ => MultiProviderAuthenticatedRow.tupled((_1.get, _2.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column user_id SqlType(VARCHAR), Length(64,true) */
    val userId: Rep[String] = column[String]("user_id", O.Length(64, varying = true))
    /** Database column authentication_provider_id SqlType(VARCHAR), Length(32,true) */
    val authenticationProviderId: Rep[String] = column[String]("authentication_provider_id", O.Length(32, varying = true))

    /** Primary key of MultiProviderAuthenticated (database name multi_provider_authenticated_PK) */
    val pk = primaryKey("multi_provider_authenticated_PK", (userId, authenticationProviderId))

    /** Foreign key referencing AuthenticationProvider (database name multi_provider_authenticated_ibfk_2) */
    lazy val authenticationProviderFk = foreignKey("multi_provider_authenticated_ibfk_2", authenticationProviderId, AuthenticationProvider)(r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name multi_provider_authenticated_ibfk_1) */
    lazy val usersFk = foreignKey("multi_provider_authenticated_ibfk_1", userId, Users)(r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table MultiProviderAuthenticated */
  lazy val MultiProviderAuthenticated = new TableQuery(tag => new MultiProviderAuthenticated(tag))

  /**
   * Entity class storing rows of table OwnedUsers
   *  @param email Database column email SqlType(VARCHAR), PrimaryKey, Length(64,true)
   *  @param passwordHash Database column password_hash SqlType(VARCHAR), Length(128,true)
   *  @param userName Database column user_name SqlType(VARCHAR), Length(32,true)
   *  @param userId Database column user_id SqlType(VARCHAR), Length(64,true)
   */
  case class OwnedUsersRow(email: String, passwordHash: String, userName: String, userId: String)
  /** GetResult implicit for fetching OwnedUsersRow objects using plain SQL queries */
  implicit def GetResultOwnedUsersRow(implicit e0: GR[String]): GR[OwnedUsersRow] = GR {
    prs =>
      import prs._
      OwnedUsersRow.tupled((<<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table owned_users. Objects of this class serve as prototypes for rows in queries. */
  class OwnedUsers(_tableTag: Tag) extends Table[OwnedUsersRow](_tableTag, "owned_users") {
    def * = (email, passwordHash, userName, userId) <> (OwnedUsersRow.tupled, OwnedUsersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(email), Rep.Some(passwordHash), Rep.Some(userName), Rep.Some(userId)).shaped.<>({ r => import r._; _1.map(_ => OwnedUsersRow.tupled((_1.get, _2.get, _3.get, _4.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column email SqlType(VARCHAR), PrimaryKey, Length(64,true) */
    val email: Rep[String] = column[String]("email", O.PrimaryKey, O.Length(64, varying = true))
    /** Database column password_hash SqlType(VARCHAR), Length(128,true) */
    val passwordHash: Rep[String] = column[String]("password_hash", O.Length(128, varying = true))
    /** Database column user_name SqlType(VARCHAR), Length(32,true) */
    val userName: Rep[String] = column[String]("user_name", O.Length(32, varying = true))
    /** Database column user_id SqlType(VARCHAR), Length(64,true) */
    val userId: Rep[String] = column[String]("user_id", O.Length(64, varying = true))

    /** Foreign key referencing Users (database name owned_users_ibfk_1) */
    lazy val usersFk = foreignKey("owned_users_ibfk_1", userId, Users)(r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)

    /** Uniqueness Index over (userId) (database name user_id) */
    val index1 = index("user_id", userId, unique = true)
  }
  /** Collection-like TableQuery object for table OwnedUsers */
  lazy val OwnedUsers = new TableQuery(tag => new OwnedUsers(tag))

  /**
   * Entity class storing rows of table SchemaVersion
   *  @param installedRank Database column installed_rank SqlType(INT), PrimaryKey
   *  @param version Database column version SqlType(VARCHAR), Length(50,true), Default(None)
   *  @param description Database column description SqlType(VARCHAR), Length(200,true)
   *  @param `type` Database column type SqlType(VARCHAR), Length(20,true)
   *  @param script Database column script SqlType(VARCHAR), Length(1000,true)
   *  @param checksum Database column checksum SqlType(INT), Default(None)
   *  @param installedBy Database column installed_by SqlType(VARCHAR), Length(100,true)
   *  @param installedOn Database column installed_on SqlType(TIMESTAMP)
   *  @param executionTime Database column execution_time SqlType(INT)
   *  @param success Database column success SqlType(BIT)
   */
  case class SchemaVersionRow(installedRank: Int, version: Option[String] = None, description: String, `type`: String, script: String, checksum: Option[Int] = None, installedBy: String, installedOn: java.sql.Timestamp, executionTime: Int, success: Boolean)
  /** GetResult implicit for fetching SchemaVersionRow objects using plain SQL queries */
  implicit def GetResultSchemaVersionRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[String], e3: GR[Option[Int]], e4: GR[java.sql.Timestamp], e5: GR[Boolean]): GR[SchemaVersionRow] = GR {
    prs =>
      import prs._
      SchemaVersionRow.tupled((<<[Int], <<?[String], <<[String], <<[String], <<[String], <<?[Int], <<[String], <<[java.sql.Timestamp], <<[Int], <<[Boolean]))
  }
  /**
   * Table description of table schema_version. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type
   */
  class SchemaVersion(_tableTag: Tag) extends Table[SchemaVersionRow](_tableTag, "schema_version") {
    def * = (installedRank, version, description, `type`, script, checksum, installedBy, installedOn, executionTime, success) <> (SchemaVersionRow.tupled, SchemaVersionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(installedRank), version, Rep.Some(description), Rep.Some(`type`), Rep.Some(script), checksum, Rep.Some(installedBy), Rep.Some(installedOn), Rep.Some(executionTime), Rep.Some(success)).shaped.<>({ r => import r._; _1.map(_ => SchemaVersionRow.tupled((_1.get, _2, _3.get, _4.get, _5.get, _6, _7.get, _8.get, _9.get, _10.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column installed_rank SqlType(INT), PrimaryKey */
    val installedRank: Rep[Int] = column[Int]("installed_rank", O.PrimaryKey)
    /** Database column version SqlType(VARCHAR), Length(50,true), Default(None) */
    val version: Rep[Option[String]] = column[Option[String]]("version", O.Length(50, varying = true), O.Default(None))
    /** Database column description SqlType(VARCHAR), Length(200,true) */
    val description: Rep[String] = column[String]("description", O.Length(200, varying = true))
    /**
     * Database column type SqlType(VARCHAR), Length(20,true)
     *  NOTE: The name was escaped because it collided with a Scala keyword.
     */
    val `type`: Rep[String] = column[String]("type", O.Length(20, varying = true))
    /** Database column script SqlType(VARCHAR), Length(1000,true) */
    val script: Rep[String] = column[String]("script", O.Length(1000, varying = true))
    /** Database column checksum SqlType(INT), Default(None) */
    val checksum: Rep[Option[Int]] = column[Option[Int]]("checksum", O.Default(None))
    /** Database column installed_by SqlType(VARCHAR), Length(100,true) */
    val installedBy: Rep[String] = column[String]("installed_by", O.Length(100, varying = true))
    /** Database column installed_on SqlType(TIMESTAMP) */
    val installedOn: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("installed_on")
    /** Database column execution_time SqlType(INT) */
    val executionTime: Rep[Int] = column[Int]("execution_time")
    /** Database column success SqlType(BIT) */
    val success: Rep[Boolean] = column[Boolean]("success")

    /** Index over (success) (database name schema_version_s_idx) */
    val index1 = index("schema_version_s_idx", success)
  }
  /** Collection-like TableQuery object for table SchemaVersion */
  lazy val SchemaVersion = new TableQuery(tag => new SchemaVersion(tag))

  /**
   * Entity class storing rows of table TwitterUsers
   *  @param screenName Database column screen_name SqlType(VARCHAR), PrimaryKey, Length(32,true)
   *  @param userId Database column user_id SqlType(VARCHAR), Length(64,true)
   *  @param oauthToken Database column oauth_token SqlType(VARCHAR), Length(64,true)
   *  @param oauthTokenSecret Database column oauth_token_secret SqlType(VARCHAR), Length(64,true)
   */
  case class TwitterUsersRow(screenName: String, userId: String, oauthToken: String, oauthTokenSecret: String)
  /** GetResult implicit for fetching TwitterUsersRow objects using plain SQL queries */
  implicit def GetResultTwitterUsersRow(implicit e0: GR[String]): GR[TwitterUsersRow] = GR {
    prs =>
      import prs._
      TwitterUsersRow.tupled((<<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table twitter_users. Objects of this class serve as prototypes for rows in queries. */
  class TwitterUsers(_tableTag: Tag) extends Table[TwitterUsersRow](_tableTag, "twitter_users") {
    def * = (screenName, userId, oauthToken, oauthTokenSecret) <> (TwitterUsersRow.tupled, TwitterUsersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(screenName), Rep.Some(userId), Rep.Some(oauthToken), Rep.Some(oauthTokenSecret)).shaped.<>({ r => import r._; _1.map(_ => TwitterUsersRow.tupled((_1.get, _2.get, _3.get, _4.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column screen_name SqlType(VARCHAR), PrimaryKey, Length(32,true) */
    val screenName: Rep[String] = column[String]("screen_name", O.PrimaryKey, O.Length(32, varying = true))
    /** Database column user_id SqlType(VARCHAR), Length(64,true) */
    val userId: Rep[String] = column[String]("user_id", O.Length(64, varying = true))
    /** Database column oauth_token SqlType(VARCHAR), Length(64,true) */
    val oauthToken: Rep[String] = column[String]("oauth_token", O.Length(64, varying = true))
    /** Database column oauth_token_secret SqlType(VARCHAR), Length(64,true) */
    val oauthTokenSecret: Rep[String] = column[String]("oauth_token_secret", O.Length(64, varying = true))

    /** Foreign key referencing Users (database name twitter_users_ibfk_1) */
    lazy val usersFk = foreignKey("twitter_users_ibfk_1", userId, Users)(r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)

    /** Uniqueness Index over (userId) (database name user_id) */
    val index1 = index("user_id", userId, unique = true)
  }
  /** Collection-like TableQuery object for table TwitterUsers */
  lazy val TwitterUsers = new TableQuery(tag => new TwitterUsers(tag))

  /**
   * Entity class storing rows of table Users
   *  @param id Database column id SqlType(VARCHAR), PrimaryKey, Length(64,true)
   */
  case class UsersRow(id: String)
  /** GetResult implicit for fetching UsersRow objects using plain SQL queries */
  implicit def GetResultUsersRow(implicit e0: GR[String]): GR[UsersRow] = GR {
    prs =>
      import prs._
      UsersRow(<<[String])
  }
  /** Table description of table users. Objects of this class serve as prototypes for rows in queries. */
  class Users(_tableTag: Tag) extends Table[UsersRow](_tableTag, "users") {
    def * = id <> (UsersRow, UsersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = Rep.Some(id).shaped.<>(r => r.map(_ => UsersRow(r.get)), (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(VARCHAR), PrimaryKey, Length(64,true) */
    val id: Rep[String] = column[String]("id", O.PrimaryKey, O.Length(64, varying = true))
  }
  /** Collection-like TableQuery object for table Users */
  lazy val Users = new TableQuery(tag => new Users(tag))
}
