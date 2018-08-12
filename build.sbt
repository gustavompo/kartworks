name := """kart"""

organization := "com.kartworks"

git.formattedShaVersion := git.gitHeadCommit.value map (_.substring(0, 7))

lazy val slickVersion = "3.0.1"

lazy val root = (project in file("."))
    .enablePlugins(PlayScala, BuildInfoPlugin, DockerPlugin, GitVersioning)
    .settings(
      buildInfoKeys := Seq[BuildInfoKey](name, version),
      buildInfoPackage := "com.kartworks",
      dockerRepository := Some("581737776948.dkr.ecr.us-east-1.amazonaws.com"),
      dockerBaseImage := "99taxis/mini-java8:1.1",
      dockerExposedPorts := Seq(9000)
    )

scalaVersion := "2.12.6"

libraryDependencies += guice

libraryDependencies ++= Seq(
  ws,
  filters,
  evolutions,
  "com.typesafe.play" %% "play-slick" % slickVersion,
  "com.typesafe.play" %% "play-slick-evolutions" % slickVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "com.github.tminglei" %% "slick-pg" % "0.15.0",
  "com.github.tminglei" %% "slick-pg_play-json" % "0.15.0",
  "org.postgresql" % "postgresql" % "9.4.1212",
  "org.mockito" % "mockito-core" % "2.8.9" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "com.github.cb372" %% "scalacache-caffeine" % "0.22.0",
  "net.codingwell" %% "scala-guice" % "4.2.1",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.14"
)


resolvers += Resolver.typesafeRepo("releases")
resolvers += Resolver.sonatypeRepo("public")


parallelExecution in Test := false

logBuffered in Test := false


javaOptions in Test ++= Seq(s"-Dconfig.file=./conf/application.test.conf")
