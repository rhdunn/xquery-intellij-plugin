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

@DisplayName("IntelliJ - Custom Language Support - XPath Language Injection - Text Value Templates")
class ValueTemplateExpressionsTest {
    @Test
    @DisplayName("empty")
    fun empty() {
        val blocks = "".valueTemplateExpressions()
        assertThat(blocks.size, `is`(0))
    }

    @Test
    @DisplayName("no blocks")
    fun noBlocks() {
        val blocks = "Lorem ipsum dolor.".valueTemplateExpressions()
        assertThat(blocks.size, `is`(0))
    }

    @Nested
    @DisplayName("single block; no nested braces")
    inner class SingleBlock {
        @Test
        @DisplayName("spanning the entire text block")
        fun span() {
            val blocks = "{1234}".valueTemplateExpressions()
            assertThat(blocks.size, `is`(1))

            assertThat(blocks[0].startOffset, `is`(0))
            assertThat(blocks[0].endOffset, `is`(6))
        }
    }
}
