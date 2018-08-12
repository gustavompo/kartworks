name := """kart"""

organization := "com.kartworks"

lazy val root = (project in file("."))
    .enablePlugins(PlayScala, BuildInfoPlugin)
    .settings(
      buildInfoKeys := Seq[BuildInfoKey](name, version),
      buildInfoPackage := "com.kartworks",
    )

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  ws,
  guice,
  filters,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "org.mockito" % "mockito-core" % "2.8.9" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "com.github.cb372" %% "scalacache-caffeine" % "0.22.0",
  "net.codingwell" %% "scala-guice" % "4.2.1",
  "com.danielasfregola" %% "random-data-generator" % "2.5"
)

resolvers += Resolver.typesafeRepo("releases")
resolvers += Resolver.sonatypeRepo("public")


parallelExecution in Test := false

logBuffered in Test := false


javaOptions in Test ++= Seq(s"-Dconfig.file=./conf/application.test.conf")
