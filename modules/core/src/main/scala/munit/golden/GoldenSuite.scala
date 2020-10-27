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

import munit.FunSuite
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

import scala.collection.immutable.SortedSet
import scala.io.Source
import scala.jdk.CollectionConverters._
import scala.reflect.ClassTag

abstract class GoldenSuite[A: ClassTag] extends FunSuite {

  /**
    * The JSON decoder function.
    */
  def jsonDecoder: String => Either[String, A]

  /**
    * The JSON encoder function.
    */
  def jsonEncoder: A => String

  /**
    * The path of the directory under the test/resources folder.
    */
  def path: String

  /**
    * The expected types. e.g. the names of the branches of an ADT.
    */
  def expectedTypes: SortedSet[String]

  private var obtainedTypes: SortedSet[String] = SortedSet.empty
  private val parentType: String               = implicitly[ClassTag[A]].toString()

  test(s"$parentType roundtrip conversion") {
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
          case Left(e) => fail(e)
          case Right(e) =>
            obtainedTypes = obtainedTypes + e
                    .getClass()
                    .getCanonicalName()
                    .drop(parentType.length + 1)
            assertEquals(jsonEncoder(e), json)
        }
      }
    assertEquals(expectedTypes, obtainedTypes)
  }

}
