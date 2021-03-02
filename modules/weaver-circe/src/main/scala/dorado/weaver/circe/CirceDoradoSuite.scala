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

package dorado.weaver
package circe

import dorado.internal.Check
import io.circe.{ Decoder, Encoder }
import io.circe.parser.decode
import io.circe.syntax._

import scala.reflect.ClassTag

abstract class CirceDoradoSuite[A: Check.Checks: ClassTag: Decoder: Encoder](
    val path: String
) extends WeaverDoradoSuite[A] {

  override def jsonDecoder: String => Either[String, A] =
    json =>
      decode[A](json) match {
        case Left(e)  => Left(s"Error: ${e.getMessage}. Input: $json")
        case Right(x) => Right(x)
      }

  override val jsonEncoder: A => String = _.asJson.noSpaces

}
