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
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCaseEx
import uk.co.reecedunn.intellij.plugin.marklogic.log.lexer.MarkLogicErrorLogLexer
import uk.co.reecedunn.intellij.plugin.marklogic.log.lexer.MarkLogicErrorLogTokenType

@Suppress("ClassName", "Reformat")
@DisplayName("MarkLogic ErrorLog - Lexer")
class MarkLogicErrorLogLexerTest : LexerTestCaseEx() {
    override val lexer: Lexer = MarkLogicErrorLogLexer()

    @Nested
    @DisplayName("Lexer")
    internal inner class LexerTest {
        @Test
        @DisplayName("invalid state")
        fun invalidState() {
            val e = assertThrows(AssertionError::class.java) { lexer.start("123", 0, 3, 4096) }
            assertThat(e.message, `is`("Invalid state: 4096"))
        }

        @Test
        @DisplayName("empty buffer")
        fun emptyBuffer() {
            tokenize("")
        }
    }

    @Test
    @DisplayName("Java exception")
    fun javaException() {
        tokenize(
            "WARNING: JNI local refs: zu, exceeds capacity: zu",
            "\tat java.lang.System.initProperties(Native Method)",
            "\tat java.lang.System.initializeSystemClass(System.java:1166)"
        ) {
            token("WARNING: JNI local refs: zu, exceeds capacity: zu", MarkLogicErrorLogTokenType.MESSAGE)
            token("\n", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("\tat java.lang.System.initProperties(Native Method)", MarkLogicErrorLogTokenType.MESSAGE)
            token("\n", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("\tat java.lang.System.initializeSystemClass(System.java:1166)", MarkLogicErrorLogTokenType.MESSAGE)
        }
    }

    @Test
    @DisplayName("date only")
    fun dateOnly() {
        tokenize("2001-01-10", "2001-01-11", "2001-01-12") {
            token("2001-01-10", MarkLogicErrorLogTokenType.DATE)
            state(1)
            token("\n", MarkLogicErrorLogTokenType.WHITE_SPACE)
            state(0)
            token("2001-01-11", MarkLogicErrorLogTokenType.DATE)
            state(1)
            token("\n", MarkLogicErrorLogTokenType.WHITE_SPACE)
            state(0)
            token("2001-01-12", MarkLogicErrorLogTokenType.DATE)
            state(1)
        }
    }

    @Test
    @DisplayName("date and time only")
    fun dateAndTimeOnly() {
        tokenize("2001-01-10 12:34:56.789", "2001-01-11 12:34:56.789", "2001-01-12 12:34:56.789") {
            token("2001-01-10", MarkLogicErrorLogTokenType.DATE)
            state(1)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("12:34:56.789", MarkLogicErrorLogTokenType.TIME)
            token("\n", MarkLogicErrorLogTokenType.WHITE_SPACE)
            state(0)
            token("2001-01-11", MarkLogicErrorLogTokenType.DATE)
            state(1)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("12:34:56.789", MarkLogicErrorLogTokenType.TIME)
            token("\n", MarkLogicErrorLogTokenType.WHITE_SPACE)
            state(0)
            token("2001-01-12", MarkLogicErrorLogTokenType.DATE)
            state(1)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("12:34:56.789", MarkLogicErrorLogTokenType.TIME)
        }
    }

    @Test
    @DisplayName("simple message (MarkLogic >= 9.0)")
    fun simpleMessage() {
        tokenize("2001-01-10 12:34:56.789 Info: Lorem ipsum dolor") {
            token("2001-01-10", MarkLogicErrorLogTokenType.DATE)
            state(1)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("12:34:56.789", MarkLogicErrorLogTokenType.TIME)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("Info: Lorem ipsum dolor", MarkLogicErrorLogTokenType.MESSAGE)
        }
    }
}
