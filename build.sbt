import Dependencies._

ThisBuild / scalaVersion := "2.13.4"
ThisBuild / organization := "dev.profunktor"

lazy val commonSettings = List(
  scalafmtOnCompile := true,
  startYear := Some(2020),
  licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt")),
  headerLicense := Some(HeaderLicense.ALv2("2020-2021", "ProfunKtor")),
  ThisBuild / crossScalaVersions := Seq("2.13.4"),
  ThisBuild / homepage := Some(url("https://github.com/profunktor/dorado")),
  ThisBuild / organization := "dev.profunktor",
  ThisBuild / organizationName := "ProfunKtor",
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
  .aggregate(
    `dorado-core`,
    `dorado-munit-core`,
    `dorado-munit-circe`,
    `dorado-weaver-core`,
    `dorado-weaver-circe`,
    examples
  )

lazy val `dorado-core` = (project in file("modules/core"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= List(
          CompilerPlugins.kindProjector,
          Libraries.shapeless
        )
  )

lazy val `dorado-munit-core` = (project in file("modules/munit-core"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= List(
          CompilerPlugins.kindProjector,
          Libraries.munitCore
        )
  )
  .dependsOn(`dorado-core`)

lazy val `dorado-munit-circe` = (project in file("modules/munit-circe"))
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
  .dependsOn(`dorado-munit-core`)

lazy val `dorado-weaver-core` = (project in file("modules/weaver-core"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= List(
          CompilerPlugins.kindProjector,
          Libraries.weaverCats
        )
  )
  .dependsOn(`dorado-core`)

lazy val `dorado-weaver-circe` = (project in file("modules/weaver-circe"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= List(
          CompilerPlugins.kindProjector,
          Libraries.circeCore,
          Libraries.circeParser,
          Libraries.weaverCats
        )
  )
  .dependsOn(`dorado-weaver-core`)

lazy val examples = (project in file("modules/examples"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(noPublish)
  .settings(
    scalacOptions += "-Ymacro-annotations",
    testFrameworks += new TestFramework("munit.Framework"),
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    libraryDependencies ++= List(
          CompilerPlugins.kindProjector,
          Libraries.cats,
          Libraries.catsEffect,
          Libraries.circeGeneric,
          Libraries.newtype,
          Libraries.refined,
          Libraries.munitCore  % Test,
          Libraries.weaverCats % Test
        )
  )
  .dependsOn(`dorado-munit-circe`, `dorado-weaver-circe`)
