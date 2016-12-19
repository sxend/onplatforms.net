
organization := "net.onplatforms"

name := "zero"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.8"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
)

val dependencies =  {
  val akkaHttpVersion = "10.0.0"
  val spec2Version = "3.8.4"
  val slickVersion = "3.1.1"
  val log4jVersion = "2.7"
  Seq(
    "com.typesafe.slick" %% "slick" % slickVersion,
    "com.typesafe.slick" %% "slick-hikaricp" % slickVersion excludeAll ExclusionRule(organization = "com.zaxxer", name = "HikariCP-java6"),
    "com.typesafe.slick" %% "slick-codegen" % slickVersion,
    "joda-time" % "joda-time" % "2.7",
    "org.joda" % "joda-convert" % "1.7",
    "mysql" % "mysql-connector-java" % "6.0.5",
    "com.zaxxer" % "HikariCP" % "2.5.1",
    "org.apache.commons" % "commons-lang3" % "3.5",
    "commons-codec" % "commons-codec" % "1.10",
    "com.bionicspirit" %% "shade" % "1.7.4",
    "com.mitchellbosecke" % "pebble" % "2.2.3",
    "com.iheart" %% "ficus" % "1.3.2",
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-slf4j" % "2.4.14",
    "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jVersion,
    "org.apache.logging.log4j" % "log4j-core" % log4jVersion,
    "org.apache.logging.log4j" % "log4j-api" % log4jVersion,
    "com.lmax" % "disruptor" % "3.3.6",
    "org.specs2" %% "specs2-html" % spec2Version % "test",
    "org.specs2" %% "specs2-junit" % spec2Version % "test",
    "org.specs2" %% "specs2-core" % spec2Version % "test",
    "org.scalariform" %% "scalariform" % "0.1.8" % "compile"
  )
}

val RDB_USER = Option(System.getenv("RDB_USER"))
  .orElse(Option(System.getProperty("sbt.RDB_USER"))).getOrElse("0.0.0.0")

val RDB_PASS = Option(System.getenv("RDB_PASS"))
  .orElse(Option(System.getProperty("sbt.RDB_PASS"))).getOrElse("3306")


lazy val slickCodeGenTask = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
  val outputDir = new File("src/main/scala").absolutePath
  val url = "jdbc:mysql://localhost:3306/accounts.onplatforms.net?useSSL=false&nullNamePatternMatchesAll=true"
  val jdbcDriver = "com.mysql.cj.jdbc.Driver"
  val slickDriver = "slick.driver.MySQLDriver"
  val pkg = "net.onplatforms.accounts.io"
  r.run("slick.codegen.SourceCodeGenerator", cp.files, Array(slickDriver, jdbcDriver, url, outputDir, pkg, RDB_USER, RDB_PASS), Logger.Null)
  val fname = outputDir + "/Tables.scala"
  Seq(file(fname))
}

lazy val mainProject = Project(
  id="zero",
  base=file("."),
  settings = Defaults.coreDefaultSettings ++ Seq(
    scalaVersion := "2.11.8",
    libraryDependencies ++= dependencies,
    slick := { slickCodeGenTask.value } // register manual sbt command
  )
)

lazy val slick = TaskKey[Seq[File]]("slick-gen")


publishMavenStyle := false

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

javacOptions ++= Seq("-source", "1.8")

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:reflectiveCalls",
  "-language:postfixOps"
)

assemblyMergeStrategy in assembly := {
  case PathList(ps @ _*) if ps.last endsWith "bnd.bnd" => MergeStrategy.first
  case x => (assemblyMergeStrategy in assembly).value(x)
}

testOptions in Test += Tests.Argument(TestFrameworks.Specs2, "junitxml", "html", "console")

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/ScalaTest-reports/html")

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-u", "target/ScalaTest-reports/unit")

assemblyJarName in assembly := s"zero.jar"
