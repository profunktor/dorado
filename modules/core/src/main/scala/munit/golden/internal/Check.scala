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

package munit.golden.internal

import shapeless._
import scala.annotation.nowarn

/* All credits goes to Fabio Labella for this implementation: https://gist.github.com/SystemFw/1038a0ba297760efca946bbe5c1650bd */
trait Check[T] {
  type Id
  def register(t: T): Id
  def check(registered: Set[Id]): Boolean
}

object Check {
  private[golden] def apply[T](implicit ev: Checks[T]): Check[T] = new Check[T] {
    type Id = Int
    def register(t: T): Id = ev.checks.indexWhere(_.apply(t))
    def check(registered: Set[Id]): Boolean =
      ev.checks.indices.forall(registered.contains)
  }

  trait Checks[T] {
    def checks: Vector[Any => Boolean]
  }

  implicit def base: Checks[CNil] =
    new Checks[CNil] {
      def checks = Vector.empty
    }

  implicit def inductive[H, T <: Coproduct](
      implicit next: Checks[T],
      castH: Typeable[H]
  ): Checks[H :+: T] =
    new Checks[H :+: T] {
      def checks = (castH.cast(_: Any).isDefined) +: next.checks
    }

  @nowarn
  implicit def generic[T, R <: Coproduct](
      implicit ev: Generic.Aux[T, R],
      check: Checks[R]
  ): Checks[T] = new Checks[T] {
    def checks = check.checks
  }
}
