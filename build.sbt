val arooa = "uk.co.rgordon" % "arooa" % "1.5.0-SNAPSHOT"

val oddjob = "uk.co.rgordon" % "oddjob" % "1.5.0-SNAPSHOT"

val ojtools = "uk.co.rgordon" % "oddjob" % "1.5.0-SNAPSHOT" artifacts(Artifact("oj-tools", "jar", "jar"))

val junit = "junit" % "junit" % "4.7"

val hsql = "org.hsqldb" % "hsqldb" % "2.3.3"

lazy val root = (project in file(".")).
  settings(
    organization := "uk.co.rgordon",
    name := "dido",
    version := "1.0.0-SNAPSHOT",
    scalaVersion := "2.11.8",
    libraryDependencies ++= Seq(arooa, oddjob, ojtools, junit, hsql)
  )
