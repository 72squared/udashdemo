// The simplest possible sbt build file is just one line:

scalaVersion := "2.13.1"


val udashVersion: String = "0.9.0-M2" // appropriate version of Udash here
val jettyVersion: String = "9.4.30.v20200611" // appropriate version of Jetty here

libraryDependencies ++= Seq(
  "io.udash" %% "udash-rest" % udashVersion,
  "org.eclipse.jetty" % "jetty-server" % jettyVersion,
  "org.eclipse.jetty" % "jetty-servlet" % jettyVersion,
  "com.softwaremill.sttp.client" %% "core" % "2.2.3",
  "joda-time" % "joda-time" % "2.10.6",
)
