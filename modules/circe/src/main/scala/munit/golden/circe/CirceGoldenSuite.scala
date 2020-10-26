package munit.golden
package circe

import io.circe.{ Decoder, Encoder }
import io.circe.parser.decode
import io.circe.syntax._

import scala.reflect.ClassTag

abstract class CirceGoldenSuite[A: ClassTag: Decoder: Encoder](
    val path: String
) extends GoldenSuite[A] {

  override def jsonDecoder: String => Either[String, A] =
    json =>
      decode[A](json) match {
        case Left(e)  => Left(s"Error: ${e.getMessage}. Input: $json")
        case Right(x) => Right(x)
      }

  override val jsonEncoder: A => String = _.asJson.noSpaces

}
