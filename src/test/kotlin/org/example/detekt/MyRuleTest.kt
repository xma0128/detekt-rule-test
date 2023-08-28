package org.example.detekt

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.kotest.matchers.collections.shouldHaveSize
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
internal class MyRuleTest(private val env: KotlinCoreEnvironment) {

    @Test
    fun `reports inner classes`() {
        val code = """
        class A {
          inner class B
        }
        """
        val findings = MyRule(Config.empty).compileAndLintWithContext(env, code)
        findings shouldHaveSize 1
    }

    @Test
    fun `doesn't report inner classes`() {
        val code = """
        class A {
          class B
        }
        """
        val findings = MyRule(Config.empty).compileAndLintWithContext(env, code)
        findings shouldHaveSize 0
    }

    @Test
    fun `test rule`() {
        val code = """
            class DynamicValueController {
              companion object {
                // Cleanup: http://www.jira.com/issues/mxplat-1234
                private const val AABCED = "AABCD"
                // bbcde
                private const val BBCDE = "BBCDE"
              }
            }
        """
        val findings = TestRule(Config.empty).compileAndLintWithContext(env, code)
        findings shouldHaveSize 1

        val code2 = """
            class A {
                private const val AABCED
                companion object {
                    private const val BBCDE = "BBCDE"
                }
            }
        """
        val findings2 = TestRule(Config.empty).compileAndLintWithContext(env, code2)
        findings2 shouldHaveSize 0
    }
}
