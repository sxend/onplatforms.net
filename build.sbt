
organization := "net.onplatforms"

name := "zero"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.8"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/",
  Resolver.bintrayRepo("iheartradio","maven")
)

libraryDependencies ++= {
  val akkaHttpVersion = "10.0.0"
  val shapelessVersion = "2.3.1"
  val spec2Version = "3.8.4"
  val scalaTestVersion = "2.2.6"
  Seq(
    "org.apache.commons" % "commons-lang3" % "3.5",
    "com.bionicspirit" %% "shade" % "1.7.4",
    "org.twitter4j" % "twitter4j-async" % "4.0.5",
    "com.mitchellbosecke" % "pebble" % "2.2.3",
    "com.iheart" %% "ficus" % "1.3.2",
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-slf4j" % "2.4.14",
    "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.7",
    "org.apache.logging.log4j" % "log4j-core" % "2.7",
    "org.apache.logging.log4j" % "log4j-api" % "2.7",
    "com.chuusai" %% "shapeless" % shapelessVersion,
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "org.specs2" %% "specs2-html" % spec2Version % "test",
    "org.specs2" %% "specs2-junit" % spec2Version % "test",
    "org.specs2" %% "specs2-core" % spec2Version % "test",
    "org.scalariform" %% "scalariform" % "0.1.8" % "compile"
  )
}

publishMavenStyle := false

bintrayRepository := {
  if (version.value.matches("^[0-9]+\\.[0-9]+\\.[0-9]+$")) "releases" else "snapshots"
}

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
