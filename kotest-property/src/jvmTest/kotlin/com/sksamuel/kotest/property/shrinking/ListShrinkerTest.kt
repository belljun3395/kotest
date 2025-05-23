package com.sksamuel.kotest.property.shrinking

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.captureStandardOut
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.collections.shouldHaveAtMostSize
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyTesting
import io.kotest.property.ShrinkingMode
import io.kotest.property.arbitrary.ListShrinker
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import io.kotest.property.internal.doShrinking
import io.kotest.property.rtree

@EnabledIf(NotMacOnGithubCondition::class)
class ListShrinkerTest : FunSpec() {
   init {

      val state = PropertyTesting.shouldPrintShrinkSteps

      beforeSpec {
         PropertyTesting.shouldPrintShrinkSteps = false
      }

      afterSpec {
         PropertyTesting.shouldPrintShrinkSteps = state
      }

      test("ListShrinker should include bisected input") {
         checkAll(Arb.list(Arb.int(0..1000))) { list ->
            if (list.size > 1) {
               val candidates = ListShrinker<Int>(0..100).shrink(list)
               candidates.forAtLeastOne {
                  list.take(list.size / 2) shouldBe it
               }
            }
         }
      }

      test("ListShrinker should include input minus head") {
         checkAll(Arb.list(Arb.int(0..1000))) { list ->
            if (list.size > 1) {
               val candidates = ListShrinker<Int>(0..100).shrink(list)
               candidates.forAtLeastOne {
                  list.drop(1) shouldBe it
               }
            }
         }
      }

      test("ListShrinker should include input minus tail") {
         checkAll(Arb.list(Arb.int(0..1000))) { list ->
            if (list.size > 1) {
               val candidates = ListShrinker<Int>(0..100).shrink(list)
               candidates.forAtLeastOne {
                  list.dropLast(1) shouldBe it
               }
            }
         }
      }

      test("ListShrinker should shrink to expected value") {
         checkAll(Arb.list(Arb.int(0..1000))) { list ->
            if (list.isNotEmpty()) {
               val shrinks = ListShrinker<Int>(0..100).rtree(list)
               val shrunk = doShrinking(shrinks, ShrinkingMode.Unbounded) {
                  it shouldHaveSize 0
               }
               shrunk.shrink shouldHaveSize 1
            }
         }

         val input = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
         val shrinks = ListShrinker<Int>(0..100).rtree(input)
         val shrunk = doShrinking(shrinks, ShrinkingMode.Unbounded) {
            it shouldHaveAtMostSize 2
         }
         shrunk.shrink shouldHaveSize 3
      }

      test("ListShrinker should observe range") {

         checkAll(Arb.list(Arb.constant(0), range = 4..100)) { list ->
            if (list.isNotEmpty()) {
               val shrinks = ListShrinker<Int>(4..100).rtree(list)
               val shrunk = doShrinking(shrinks, ShrinkingMode.Unbounded) {
                  it shouldHaveSize 0
               }
               shrunk.shrink shouldHaveSize 4
            }
         }
      }

      test("ListShrinker in action") {
         val stdout = captureStandardOut {
            PropertyTesting.shouldPrintShrinkSteps = true
            shouldThrowAny {
               checkAll(PropTestConfig(seed = 123132), Arb.list(Arb.int(0..100))) { list ->
                  list.shouldHaveAtMostSize(3)
               }
            }
         }
         println(stdout)
         stdout.shouldContain("Shrink result (after 24 shrinks) => [1, 100, 96, 29")
      }
   }
}
