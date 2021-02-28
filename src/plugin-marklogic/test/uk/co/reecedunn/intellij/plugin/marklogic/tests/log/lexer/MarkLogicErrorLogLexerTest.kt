/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.tests.log.lexer

import com.intellij.lexer.Lexer
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase
import uk.co.reecedunn.intellij.plugin.marklogic.log.lexer.MarkLogicErrorLogLexer

@Suppress("ClassName", "Reformat")
@DisplayName("MarkLogic ErrorLog - Lexer")
class MarkLogicErrorLogLexerTest : LexerTestCase() {
    private fun createLexer(): Lexer = MarkLogicErrorLogLexer()

    @Nested
    @DisplayName("Lexer")
    internal inner class LexerTest {
        @Test
        @DisplayName("invalid state")
        fun testInvalidState() {
            val lexer = createLexer()

            val e = assertThrows(AssertionError::class.java) { lexer.start("123", 0, 3, 4096) }
            assertThat(e.message, `is`("Invalid state: 4096"))
        }

        @Test
        @DisplayName("empty buffer")
        fun testEmptyBuffer() {
            val lexer = createLexer()

            lexer.start("")
            matchToken(lexer, "", 0, 0, 0, null)
        }
    }
}
