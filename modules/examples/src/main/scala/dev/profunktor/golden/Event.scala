/*
 * Copyright 2020 ProfunKtor
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
