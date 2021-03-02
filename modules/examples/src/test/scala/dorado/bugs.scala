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

object bugs {

  sealed trait Yay
  object Yay {
    @newtype case class MyId(value: Int)

    case class Foo(x: Int) extends Yay
    case class Bar(y: MyId, z: String) extends Yay
  }

  //internal.Check[Yay]

  @newtype case class MyInt(value: Int)

  sealed trait Yay2
  object Yay2 {
    type ID = MyInt
    case class Foo2(x: Int) extends Yay2
    case class Bar2(y: ID, z: String) extends Yay2
  }

  //internal.Check[Yay2]

  sealed trait Yay3
  object Yay3 {
    type MyId = String Refined Uuid

    case class Foo(x: Int) extends Yay3
    case class Bar(y: MyId, z: String) extends Yay3
  }

  //internal.Check[Yay3]

  sealed trait Yay4
  object Yay4 {
    type LOL
    case class Foo(x: LOL) extends Yay4 //this does not compile
    //case class Foo(x: Yay4.LOL) extends Yay4 //this compiles
  }

  //shapeless.Typeable[Yay4.Foo] // class type required but munit.golden.bugs.Yay4.LOL found
  //internal.Check[Yay4]
}
