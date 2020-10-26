package dev.profunktor.golden

import cats.syntax.either._
import io.circe.parser.decode
import io.circe.syntax._

class EventGoldenSuite extends GoldenSuite[Event] {

  override def jsonDecoder: String => Either[String, Event] =
    decode[Event](_).leftMap(_.getMessage())

  override val jsonEncoder: Event => String = {
    case e: Event.One => (e: Event).asJson.noSpaces
    case e: Event.Two => (e: Event).asJson.noSpaces
  }

  override val path = "/event"

}
