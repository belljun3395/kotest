package io.kotest.assertions.nondeterministic

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@EnabledIf(NotMacOnGithubCondition::class)
class FibonacciIntervalTest : FunSpec() {

   init {
      test("fib correctness") {
         fibonacci(0) shouldBe 0
         fibonacci(1) shouldBe 1
         fibonacci(2) shouldBe 1
         fibonacci(3) shouldBe 2
         fibonacci(4) shouldBe 3
         fibonacci(5) shouldBe 5
         fibonacci(6) shouldBe 8
         fibonacci(7) shouldBe 13
      }

      test("fibonacci interval should have a reasonable default max") {
         val max = FibonacciIntervalFn.defaultMax
         val default = 10.minutes.fibonacci()
         val unbounded = 10.minutes.fibonacci(2.hours)

         val first = 0
         val last = 20

         unbounded.next(first) shouldBeLessThan max
         unbounded.next(last) shouldBeGreaterThan max

         for (i in first..last) {
            val u = unbounded.next(i)
            val d = default.next(i)

            if (u < max) {
               d shouldBe u
            } else {
               d shouldBe max
               u shouldBeGreaterThan max
            }
         }
      }

      test("fibonacci interval should respect user specified max") {
         val max = 15.minutes
         val bounded = 10.minutes.fibonacci(max)
         val unbounded = 10.minutes.fibonacci(1.hours)

         val first = 0
         val last = 20

         unbounded.next(first) shouldBeLessThan max
         unbounded.next(last) shouldBeGreaterThan max

         for (i in first..last) {
            val u = unbounded.next(i)
            val b = bounded.next(i)

            if (u < max) {
               b shouldBe u
            } else {
               b shouldBe max
               u shouldBeGreaterThan max
            }
         }
      }
   }
}
