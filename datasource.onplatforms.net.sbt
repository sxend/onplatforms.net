
val VERSION = Option(System.getenv("CI")) match {
  case Some("true") => scala.io.Source.fromURL("https://s3-ap-northeast-1.amazonaws.com/public.onplatforms.net/onplatforms.net/datasource/latest.version").getLines.toList.head
  case _ => "SNAPSHOT"
}

libraryDependencies += "net.onplatforms" %% "datasource" % s"0.0.1-$VERSION"