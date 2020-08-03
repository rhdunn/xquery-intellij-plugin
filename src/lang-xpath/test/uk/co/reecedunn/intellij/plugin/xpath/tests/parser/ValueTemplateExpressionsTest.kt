/*
 * Copyright (C) 2020 Reece H. Dunn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.reecedunn.intellij.plugin.xpath.tests.parser

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.parser.valueTemplateExpressions

@Suppress("ClassName")
@DisplayName("IntelliJ - Custom Language Support - XPath Language Injection - Value Templates")
class ValueTemplateExpressionsTest {
    @Test
    @DisplayName("empty")
    fun empty() {
        val expressions = "".valueTemplateExpressions()
        assertThat(expressions.size, `is`(0))
    }

    @Test
    @DisplayName("no expressions")
    fun noExpressions() {
        val expressions = "Lorem ipsum dolor.".valueTemplateExpressions()
        assertThat(expressions.size, `is`(0))
    }

    @Nested
    @DisplayName("single expression; no nested braces")
    inner class SingleExpression {
        @Test
        @DisplayName("spanning the entire text")
        fun span() {
            val expressions = "{1234}".valueTemplateExpressions()
            assertThat(expressions.size, `is`(1))

            assertThat(expressions[0].startOffset, `is`(0))
            assertThat(expressions[0].endOffset, `is`(6))
        }

        @Test
        @DisplayName("text before")
        fun textBefore() {
            val expressions = "Results: {1234}".valueTemplateExpressions()
            assertThat(expressions.size, `is`(1))

            assertThat(expressions[0].startOffset, `is`(9))
            assertThat(expressions[0].endOffset, `is`(15))
        }

        @Test
        @DisplayName("text after")
        fun textAfter() {
            val expressions = "{1234} is the value of x.".valueTemplateExpressions()
            assertThat(expressions.size, `is`(1))

            assertThat(expressions[0].startOffset, `is`(0))
            assertThat(expressions[0].endOffset, `is`(6))
        }

        @Test
        @DisplayName("missing closing brace")
        fun missingClosingBrace() {
            val expressions = "{1234".valueTemplateExpressions()
            assertThat(expressions.size, `is`(1))

            assertThat(expressions[0].startOffset, `is`(0))
            assertThat(expressions[0].endOffset, `is`(5))
        }
    }

    @Nested
    @DisplayName("single expression; nested braces")
    inner class SingleExpression_Nested {
        @Test
        @DisplayName("spanning the entire text")
        fun span() {
            val expressions = "{array {1, 2, 3}}".valueTemplateExpressions()
            assertThat(expressions.size, `is`(1))

            assertThat(expressions[0].startOffset, `is`(0))
            assertThat(expressions[0].endOffset, `is`(17))
        }

        @Test
        @DisplayName("missing closing brace")
        fun missingClosingBrace() {
            val expressions = "{array {1, 2, 3}".valueTemplateExpressions()
            assertThat(expressions.size, `is`(1))

            assertThat(expressions[0].startOffset, `is`(0))
            assertThat(expressions[0].endOffset, `is`(16))
        }
    }
}
