import Dependencies._

ThisBuild / scalaVersion := "2.13.3"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / organization := "dev.profunktor"

lazy val root = (project in file("."))
  .aggregate(`munit-golden-core`, `munit-golden-circe`, examples)

lazy val `munit-golden-core` = (project in file("modules/core"))
  .settings(
    licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt")),
    libraryDependencies ++= List(
      CompilerPlugins.betterMonadicFor,
      CompilerPlugins.contextApplied,
      CompilerPlugins.kindProjector,
      Libraries.munitCore
    )
  )

lazy val `munit-golden-circe` = (project in file("modules/circe"))
  .settings(
    licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt")),
    libraryDependencies ++= List(
      CompilerPlugins.betterMonadicFor,
      CompilerPlugins.contextApplied,
      CompilerPlugins.kindProjector,
      Libraries.circeCore,
      Libraries.circeParser,
      Libraries.munitCore
    )
  )
  .dependsOn(`munit-golden-core`)

lazy val examples = (project in file("modules/examples"))
  .settings(
    licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt")),
    scalacOptions += "-Ymacro-annotations",
    testFrameworks += new TestFramework("munit.Framework"),
    libraryDependencies ++= List(
      CompilerPlugins.betterMonadicFor,
      CompilerPlugins.contextApplied,
      CompilerPlugins.kindProjector,
      Libraries.cats,
      Libraries.catsEffect,
      Libraries.circeGeneric,
      Libraries.newtype,
      Libraries.munitCore % Test
    )
  )
  .dependsOn(`munit-golden-circe`)
