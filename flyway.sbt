import org.flywaydb.sbt.FlywayPlugin._

val RDB_HOST = Option(System.getenv("RDB_HOST"))
  .orElse(Option(System.getProperty("sbt.RDB_HOST")))
  .getOrElse("0.0.0.0")

val RDB_PORT = Option(System.getenv("RDB_PORT"))
  .orElse(Option(System.getProperty("sbt.RDB_PORT")))
  .getOrElse("3306")


flywayUrl := s"jdbc:mysql://$RDB_HOST:$RDB_PORT?useSSL=false"
