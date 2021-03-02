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

package dorado
package weaver

import _root_.weaver.SimpleIOSuite

import scala.reflect.ClassTag

// Temporary suite that does not check for exhaustiveness until https://github.com/profunktor/munit-golden/issues/4 is fixed.
abstract class SimpleWeaverDoradoSuite[A: ClassTag]
    extends SimpleIOSuite
    with Weaver
    with Dorado[A] {

  pureTest(Runner.testNameFor[A]) {
    Runner.runWithoutCheck(this)(runner)
  }

}
