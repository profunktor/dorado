/*
 * Copyright 2020-2021 ProfunKtor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.time.Instant
import java.util.UUID

import cats.effect.Sync
import io.circe._
import io.estatico.newtype.macros.newtype

package object dorado {

  @newtype case class EventId(value: UUID)
  object EventId {
    def make[F[_]: Sync]: F[EventId] = Sync[F].delay(EventId(UUID.randomUUID()))

    implicit val jsonEncoder: Encoder[EventId] = deriving
    implicit val jsonDecoder: Decoder[EventId] = deriving
  }

  @newtype case class Timestamp(value: Instant)
  object Timestamp {
    def make[F[_]: Sync]: F[Timestamp] = Sync[F].delay(Timestamp(Instant.now()))

    implicit val jsonEncoder: Encoder[Timestamp] = deriving
    implicit val jsonDecoder: Decoder[Timestamp] = deriving
  }

}
