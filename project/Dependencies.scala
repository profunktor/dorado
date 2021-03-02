import sbt._

object Dependencies {
  object V {
    val cats       = "2.3.1"
    val catsEffect = "2.3.1"
    val circe      = "0.13.0"
    val newtype    = "0.4.4"
    val refined    = "0.9.20"
    val shapeless  = "2.3.3"

    val munit  = "0.7.20"
    val weaver = "0.6.0-M6"

    val kindProjector = "0.11.2"
  }

  object Libraries {
    val cats       = "org.typelevel" %% "cats-core"   % V.cats
    val catsEffect = "org.typelevel" %% "cats-effect" % V.catsEffect
    val newtype    = "io.estatico"   %% "newtype"     % V.newtype
    val refined    = "eu.timepit"    %% "refined"     % V.refined
    val shapeless  = "com.chuusai"   %% "shapeless"   % V.shapeless

    val circeCore    = "io.circe" %% "circe-core"    % V.circe
    val circeGeneric = "io.circe" %% "circe-generic" % V.circe
    val circeParser  = "io.circe" %% "circe-parser"  % V.circe

    // Testing
    val munitCore  = "org.scalameta"       %% "munit"       % V.munit
    val weaverCats = "com.disneystreaming" %% "weaver-cats" % V.weaver
  }

  object CompilerPlugins {
    val kindProjector = compilerPlugin(
      "org.typelevel" %% "kind-projector" % V.kindProjector cross CrossVersion.full
    )
  }

}
