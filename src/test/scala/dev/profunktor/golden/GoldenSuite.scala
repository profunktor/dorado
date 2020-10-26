package dev.profunktor.golden

import munit.FunSuite
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

import scala.io.Source
import scala.jdk.CollectionConverters._
import scala.reflect.ClassTag

abstract class GoldenSuite[A: ClassTag] extends FunSuite {

  def jsonDecoder: String => Either[String, A]
  def jsonEncoder: A => String
  def path: String

  test(s"${implicitly[ClassTag[A]]} roundtrip conversion") {
    Files
      .walk(Paths.get(getClass().getResource(path).getPath()))
      .map(_.toAbsolutePath())
      .collect(Collectors.toList())
      .asScala
      .toList
      .filter(_.toFile().isFile())
      .foreach { path =>
        val json =
          Source.fromFile(path.toUri()).getLines().mkString.filterNot(_.isWhitespace)

        jsonDecoder(json) match {
          case Left(e)  => fail(e)
          case Right(e) => assertEquals(jsonEncoder(e), json)
        }
      }
  }
}
