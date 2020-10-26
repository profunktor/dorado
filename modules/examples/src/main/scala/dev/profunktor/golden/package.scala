package dev.profunktor

import java.time.Instant

import cats.effect.Sync
import io.circe._
import io.estatico.newtype.macros.newtype

package object golden {

  @newtype case class Timestamp(value: Instant)
  object Timestamp {
    def make[F[_]: Sync]: F[Timestamp] = F.delay(Timestamp(Instant.now()))

    implicit val jsonEncoder: Encoder[Timestamp] = deriving
    implicit val jsonDecoder: Decoder[Timestamp] = deriving
  }

}
