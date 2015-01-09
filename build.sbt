name := "casecsv"

version := "0.0.1"

scalaVersion := "2.11.4"

scalacOptions ++= Seq("-feature", "-deprecation","-Xlog-implicits")

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.0.0",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)
