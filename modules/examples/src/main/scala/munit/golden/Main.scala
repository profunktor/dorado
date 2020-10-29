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

package munit.golden

import cats.effect._
import cats.implicits._
import io.circe.syntax._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    (EventId.make[IO], Timestamp.make[IO]).parTupled
      .flatMap {
        case (id, ts) =>
          val e1: Event = Event.One(id, "test1", ts)
          val e2: Event = Event.Two(id, 123, ts)
          IO(println(e1.asJson)) >> IO(println(e2.asJson))
      }
      .as(ExitCode.Success)

}
