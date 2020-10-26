package dev.profunktor.golden

import java.util.UUID

import cats.effect.Sync
import io.circe._
import io.circe.generic.semiauto._
import io.estatico.newtype.macros.newtype

sealed trait Event
object Event {
  @newtype case class Id(value: UUID)
  object Id {
    def make[F[_]: Sync]: F[Id] = F.delay(Id(UUID.randomUUID()))

    implicit val jsonEncoder: Encoder[Id] = deriving
    implicit val jsonDecoder: Decoder[Id] = deriving
  }

  final case class One(id: Id, foo: String, createdAt: Timestamp) extends Event
  final case class Two(id: Id, bar: Int, createdAt: Timestamp) extends Event

  implicit val jsonEncoder: Encoder[Event] = deriveEncoder
  implicit val jsonDecoder: Decoder[Event] = deriveDecoder
}
