ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

lazy val root = (project in file("."))
  .settings(
    name := "sso",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-core"         % "1.6.0",
      "com.softwaremill.sttp.tapir" %% "tapir-json-tethys"  % "1.6.0",
      "org.typelevel"               %% "cats-core"          % "2.9.0",
      "io.github.jmcardon"          %% "tsec-common"        % "0.4.0",
      "io.github.jmcardon"          %% "tsec-password"      % "0.4.0",
      "io.github.jmcardon"          %% "tsec-mac"           % "0.4.0",
      "io.github.jmcardon"          %% "tsec-signatures"    % "0.4.0",
      "io.github.jmcardon"          %% "tsec-jwt-mac"       % "0.4.0",
      "io.github.jmcardon"          %% "tsec-jwt-sig"       % "0.4.0",
      "dev.profunktor"              %% "redis4cats-effects" % "1.4.3",
      "tf.tofu"                     %% "derevo-cats"        % "0.13.0",
      "tf.tofu"                     %% "derevo-circe"       % "0.13.0",
      "tf.tofu"                     %% "derevo-tethys"       % "0.13.0",
      "io.circe"                    %% "circe-core"         % "0.13.0",
      "io.circe"                    %% "circe-parser"       % "0.13.0"
    ),
    scalacOptions += "-Ymacro-annotations"
  )
