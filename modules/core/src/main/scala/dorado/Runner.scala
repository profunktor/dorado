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

package dorado

import java.nio.file.{ Files, Path, Paths }
import java.util.stream.Collectors

import scala.io.Source
import scala.jdk.CollectionConverters._
import scala.reflect.ClassTag

import dorado.internal.Check

object Runner {

  private def sources(path: String) =
    Files
      .walk(Paths.get(getClass().getResource(path).getPath()))
      .map(_.toAbsolutePath())
      .collect(Collectors.toList())
      .asScala
      .toList
      .filter(_.toFile().isFile())

  private def getJson(path: Path) =
    Source
      .fromFile(path.toUri())
      .getLines()
      .mkString
      .filterNot(_.isWhitespace)

  def testNameFor[A](implicit ct: ClassTag[A]): String =
    s"$ct roundtrip conversion"

  case class Run[T](
      assert: Boolean => String => T,
      same: String => String => T,
      failure: String => T
  )

  def run[A: Check.Checks, T](self: Dorado[A])(runner: Run[T]): T = {
    val check = Check[A]

    val branches: collection.mutable.Set[String] = collection.mutable.Set()
    val checks: collection.mutable.Set[check.Id] = collection.mutable.Set()

    sources(self.path).toList.map { path =>
      val json = getJson(path)

      self.jsonDecoder(json) match {
        case Left(e) => runner.failure(e)
        case Right(e) =>
          runner.same(self.jsonEncoder(e))(json)
          checks.add(check.register(e))
          branches += e.getClass().getCanonicalName()
      }
    }

    runner.assert(check.check(checks.toSet))(
      s"> Non-exhaustive matching. Processed branches: ${branches.map(b => s"\n  - $b").mkString}"
    )
  }

  def runWithoutCheck[A, T](self: Dorado[A])(runner: Run[T]): T =
    sources(self.path)
      .map { path =>
        val json = getJson(path)

        self.jsonDecoder(json) match {
          case Left(e)  => runner.failure(e)
          case Right(e) => runner.same(self.jsonEncoder(e))(json)
        }
      }
      .headOption
      .getOrElse(runner.failure(s"No files found in the given path: ${self.path}."))

}
