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
package munit

import _root_.munit.FunSuite

import scala.reflect.ClassTag

// Temporary suite that does not check for exhaustiveness until https://github.com/profunktor/munit-golden/issues/4 is fixed.
abstract class SimpleMunitDoradoSuite[A: ClassTag] extends FunSuite with Dorado[A] {

  test(Runner.testNameFor[A]) {
    val runner = Runner.Run(
      assert = b => msg => assert(b, msg),
      same = a => b => assertEquals(a, b),
      failure = e => fail(e)
    )
    Runner.runWithoutCheck(this)(runner)
  }

}
