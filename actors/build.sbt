lazy val akkaHttpVersion = "10.0.11"
lazy val akkaVersion    = "2.5.11"

oneJarSettings

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.nlpagentsystem",
      scalaVersion    := "2.12.4"
    )),
    name := "actors",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,
      "org.mongodb.scala" %% "mongo-scala-driver"   % "2.3.0",

      "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.1"         % Test,

      "commons-lang" % "commons-lang" % "2.6"
    )
  )
