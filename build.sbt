import Dependencies._

ThisBuild / scalaVersion := "2.13.4"
ThisBuild / organization := "dev.profunktor"

lazy val commonSettings = List(
  //scalacOptions -= "-Xfatal-warnings",
  scalafmtOnCompile := true,
  ThisBuild / crossScalaVersions := Seq("2.13.4"),
  ThisBuild / homepage := Some(url("https://github.com/profunktor/munit-golden")),
  ThisBuild / organization := "dev.profunktor",
  ThisBuild / organizationName := "ProfunKtor",
  ThisBuild / startYear := Some(2020),
  ThisBuild / licenses := List(
    "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
  ),
  ThisBuild / headerLicenseStyle := HeaderLicenseStyle.SpdxSyntax,
  ThisBuild / developers := List(
    Developer(
      "gvolpe",
      "Gabriel Volpe",
      "hello@gvolpe.com",
      url("https://gvolpe.com")
    )
  )
)

lazy val noPublish = {
  skip in publish := true
}

lazy val root = (project in file("."))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(noPublish)
  .aggregate(`munit-golden-core`, `munit-golden-circe`, examples)

lazy val `munit-golden-core` = (project in file("modules/core"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= List(
      CompilerPlugins.kindProjector,
      Libraries.munitCore,
      Libraries.shapeless
    )
  )

lazy val `munit-golden-circe` = (project in file("modules/circe"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= List(
      CompilerPlugins.kindProjector,
      Libraries.circeCore,
      Libraries.circeParser,
      Libraries.munitCore
    )
  )
  .dependsOn(`munit-golden-core`)

lazy val examples = (project in file("modules/examples"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(noPublish)
  .settings(
    scalacOptions += "-Ymacro-annotations",
    testFrameworks += new TestFramework("munit.Framework"),
    libraryDependencies ++= List(
      CompilerPlugins.kindProjector,
      Libraries.cats,
      Libraries.catsEffect,
      Libraries.circeGeneric,
      Libraries.newtype,
      Libraries.refined,
      Libraries.munitCore % Test
    )
  )
  .dependsOn(`munit-golden-circe`)
