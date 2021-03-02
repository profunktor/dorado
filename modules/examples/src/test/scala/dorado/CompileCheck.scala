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

import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.Uuid
import io.estatico.newtype.macros.newtype
import dorado.internal.Check
import scala.annotation.nowarn

object CompileCheck {

  /* Newtype of a refined type */
  @newtype case class MyId(value: String Refined Uuid)

  sealed trait Yay
  object Yay {
    final case class Foo(x: Int) extends Yay
    final case class Bar(y: MyId, z: String) extends Yay
  }

  Check[Yay]

  /* Type alias of a case class within a companion object */
  sealed trait Yay2
  object Yay2 {
    case class MyId(value: Int)
    type ID = MyId

    final case class Foo(x: Int) extends Yay2
    final case class Bar(y: MyId, z: String) extends Yay2
  }

  Check[Yay2]

  /* Enumeration object (ADT) within a companion object */
  sealed trait Yay3
  object Yay3 {
    sealed trait MyEnum
    object MyEnum {
      case object A extends MyEnum
      case object B extends MyEnum
    }

    final case class Foo(x: Int) extends Yay3
    final case class Bar(y: MyEnum, z: String) extends Yay3
  }

  Check[Yay3]

  /* Newtype over a class with private constructor */
  @nowarn
  final class FUUID private (private val uuid: java.util.UUID)
  @newtype case class MyUUID(value: FUUID)

  object MyUUID {
    def foo(): String = ""
  }

  sealed trait Yay4
  object Yay4 {
    final case class Foo(x: Int) extends Yay4
    final case class Bar(y: MyUUID) extends Yay4
  }

  Check[Yay4]

  /* Newtype with companion object over a case class with companion object */
  final case class MyLong(h: Long, l: Long)
  object MyLong {
    def bar(): MyLong = MyLong(0L, 0L)
  }

  @newtype case class MyLongLong(value: MyLong)
  object MyLongLong {
    def foo(): MyLongLong = MyLongLong(MyLong.bar())
  }

  sealed trait Yay5
  object Yay5 {
    final case class Foo(x: Int) extends Yay5
    final case class Bar(y: MyLongLong) extends Yay5
  }

  Check[Yay5]

  /* Case class over two newtypes */
  final case class Loc(country: Loc.Country, continent: Loc.Continent)
  object Loc {
    @newtype case class Country(value: String)
    @newtype case class Continent(value: String)
  }

  sealed trait Yay6
  object Yay6 {
    final case class Foo(x: Int) extends Yay6
    final case class Bar(y: Loc) extends Yay6
  }

  Check[Yay6]

  /* abstract type */
  type Lala

  sealed trait Yay7
  object Yay7 {
    case class Foo(x: Lala) extends Yay7
  }

  internal.Check[Yay7]

  /* abstract type within a companion object */
  sealed trait Yay8
  object Yay8 {
    type LOL
    case class Foo(x: Yay8.LOL) extends Yay8
  }

  internal.Check[Yay8]
}
