package com.sksamuel.kotest.property.exhaustive

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.boolean

@EnabledIf(NotMacOnGithubCondition::class)
class BooleanTest : FunSpec({
   test("should return all booleans") {
      Exhaustive.boolean().values shouldBe listOf(true, false)
   }
})
