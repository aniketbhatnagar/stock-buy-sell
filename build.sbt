name := "citadel-exercise"

version := "1.0"

scalaVersion := "2.11.7"

resolvers ++=  Seq(
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq (
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.4.4" excludeAll(
      ExclusionRule(organization = "org.scala-lang"),
      ExclusionRule(organization = "org.scalatest")
    ),
    "org.scalatest" %% "scalatest" % "2.2.5" % "test" excludeAll(
      ExclusionRule(organization = "org.scala-lang")
    )
)

enablePlugins(play.PlayScala)