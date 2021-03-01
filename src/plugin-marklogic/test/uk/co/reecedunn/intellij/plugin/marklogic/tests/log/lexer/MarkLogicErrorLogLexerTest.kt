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
import com.intellij.psi.tree.IElementType
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

    private fun simpleMessage(level: String, token: IElementType) {
        tokenize("2001-01-10 12:34:56.789 $level: Lorem ipsum dolor") {
            token("2001-01-10", MarkLogicErrorLogTokenType.DATE)
            state(1)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("12:34:56.789", MarkLogicErrorLogTokenType.TIME)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token(level, token)
            state(2)
            token(":", MarkLogicErrorLogTokenType.COLON)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("Lorem ipsum dolor", MarkLogicErrorLogTokenType.MESSAGE)
        }
    }

    @Test
    @DisplayName("simple message (MarkLogic >= 9.0)")
    fun simpleMessage() {
        simpleMessage("Finest", MarkLogicErrorLogTokenType.LogLevel.FINEST)
        simpleMessage("Finer", MarkLogicErrorLogTokenType.LogLevel.FINER)
        simpleMessage("Fine", MarkLogicErrorLogTokenType.LogLevel.FINE)
        simpleMessage("Debug", MarkLogicErrorLogTokenType.LogLevel.DEBUG)
        simpleMessage("Config", MarkLogicErrorLogTokenType.LogLevel.CONFIG)
        simpleMessage("Info", MarkLogicErrorLogTokenType.LogLevel.INFO)
        simpleMessage("Notice", MarkLogicErrorLogTokenType.LogLevel.NOTICE)
        simpleMessage("Warning", MarkLogicErrorLogTokenType.LogLevel.WARNING)
        simpleMessage("Error", MarkLogicErrorLogTokenType.LogLevel.ERROR)
        simpleMessage("Critical", MarkLogicErrorLogTokenType.LogLevel.CRITICAL)
        simpleMessage("Alert", MarkLogicErrorLogTokenType.LogLevel.ALERT)
        simpleMessage("Emergency", MarkLogicErrorLogTokenType.LogLevel.EMERGENCY)
        simpleMessage("Unknown", MarkLogicErrorLogTokenType.LOG_LEVEL)
    }

    private fun messageWithTaskServer(level: String, token: IElementType) {
        tokenize("2001-01-10 12:34:56.789 $level: TaskServer: Lorem ipsum dolor") {
            token("2001-01-10", MarkLogicErrorLogTokenType.DATE)
            state(1)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("12:34:56.789", MarkLogicErrorLogTokenType.TIME)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token(level, token)
            state(2)
            token(":", MarkLogicErrorLogTokenType.COLON)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("TaskServer", MarkLogicErrorLogTokenType.SERVER)
            token(":", MarkLogicErrorLogTokenType.COLON)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("Lorem ipsum dolor", MarkLogicErrorLogTokenType.MESSAGE)
        }
    }

    @Test
    @DisplayName("message with TaskServer (MarkLogic <= 8.0)")
    fun messageWithTaskServer() {
        messageWithTaskServer("Finest", MarkLogicErrorLogTokenType.LogLevel.FINEST)
        messageWithTaskServer("Finer", MarkLogicErrorLogTokenType.LogLevel.FINER)
        messageWithTaskServer("Fine", MarkLogicErrorLogTokenType.LogLevel.FINE)
        messageWithTaskServer("Debug", MarkLogicErrorLogTokenType.LogLevel.DEBUG)
        messageWithTaskServer("Config", MarkLogicErrorLogTokenType.LogLevel.CONFIG)
        messageWithTaskServer("Info", MarkLogicErrorLogTokenType.LogLevel.INFO)
        messageWithTaskServer("Notice", MarkLogicErrorLogTokenType.LogLevel.NOTICE)
        messageWithTaskServer("Warning", MarkLogicErrorLogTokenType.LogLevel.WARNING)
        messageWithTaskServer("Error", MarkLogicErrorLogTokenType.LogLevel.ERROR)
        messageWithTaskServer("Critical", MarkLogicErrorLogTokenType.LogLevel.CRITICAL)
        messageWithTaskServer("Alert", MarkLogicErrorLogTokenType.LogLevel.ALERT)
        messageWithTaskServer("Emergency", MarkLogicErrorLogTokenType.LogLevel.EMERGENCY)
        messageWithTaskServer("Unknown", MarkLogicErrorLogTokenType.LOG_LEVEL)
    }

    private fun messageWithAppServer(level: String, token: IElementType) {
        tokenize("2001-01-10 12:34:56.789 $level: abc-2d_3e: Lorem ipsum dolor") {
            token("2001-01-10", MarkLogicErrorLogTokenType.DATE)
            state(1)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("12:34:56.789", MarkLogicErrorLogTokenType.TIME)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token(level, token)
            state(2)
            token(":", MarkLogicErrorLogTokenType.COLON)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("abc-2d_3e", MarkLogicErrorLogTokenType.SERVER)
            token(":", MarkLogicErrorLogTokenType.COLON)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("Lorem ipsum dolor", MarkLogicErrorLogTokenType.MESSAGE)
        }
    }

    @Test
    @DisplayName("message with AppServer (MarkLogic <= 8.0)")
    fun messageWithAppServer() {
        messageWithAppServer("Finest", MarkLogicErrorLogTokenType.LogLevel.FINEST)
        messageWithAppServer("Finer", MarkLogicErrorLogTokenType.LogLevel.FINER)
        messageWithAppServer("Fine", MarkLogicErrorLogTokenType.LogLevel.FINE)
        messageWithAppServer("Debug", MarkLogicErrorLogTokenType.LogLevel.DEBUG)
        messageWithAppServer("Config", MarkLogicErrorLogTokenType.LogLevel.CONFIG)
        messageWithAppServer("Info", MarkLogicErrorLogTokenType.LogLevel.INFO)
        messageWithAppServer("Notice", MarkLogicErrorLogTokenType.LogLevel.NOTICE)
        messageWithAppServer("Warning", MarkLogicErrorLogTokenType.LogLevel.WARNING)
        messageWithAppServer("Error", MarkLogicErrorLogTokenType.LogLevel.ERROR)
        messageWithAppServer("Critical", MarkLogicErrorLogTokenType.LogLevel.CRITICAL)
        messageWithAppServer("Alert", MarkLogicErrorLogTokenType.LogLevel.ALERT)
        messageWithAppServer("Emergency", MarkLogicErrorLogTokenType.LogLevel.EMERGENCY)
        messageWithAppServer("Unknown", MarkLogicErrorLogTokenType.LOG_LEVEL)
    }

    private fun messageContinuation(level: String, token: IElementType) {
        tokenize("2001-01-10 12:34:56.789 $level: abc-2d_3e: Lorem ipsum dolor") {
            token("2001-01-10", MarkLogicErrorLogTokenType.DATE)
            state(1)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("12:34:56.789", MarkLogicErrorLogTokenType.TIME)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token(level, token)
            state(2)
            token(":", MarkLogicErrorLogTokenType.COLON)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("abc-2d_3e", MarkLogicErrorLogTokenType.SERVER)
            token(":", MarkLogicErrorLogTokenType.COLON)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("Lorem ipsum dolor", MarkLogicErrorLogTokenType.MESSAGE)
        }
    }

    @Test
    @DisplayName("message continuation (MarkLogic >= 9.0)")
    fun messageContinuation() {
        messageContinuation("Finest", MarkLogicErrorLogTokenType.LogLevel.FINEST)
        messageContinuation("Finer", MarkLogicErrorLogTokenType.LogLevel.FINER)
        messageContinuation("Fine", MarkLogicErrorLogTokenType.LogLevel.FINE)
        messageContinuation("Debug", MarkLogicErrorLogTokenType.LogLevel.DEBUG)
        messageContinuation("Config", MarkLogicErrorLogTokenType.LogLevel.CONFIG)
        messageContinuation("Info", MarkLogicErrorLogTokenType.LogLevel.INFO)
        messageContinuation("Notice", MarkLogicErrorLogTokenType.LogLevel.NOTICE)
        messageContinuation("Warning", MarkLogicErrorLogTokenType.LogLevel.WARNING)
        messageContinuation("Error", MarkLogicErrorLogTokenType.LogLevel.ERROR)
        messageContinuation("Critical", MarkLogicErrorLogTokenType.LogLevel.CRITICAL)
        messageContinuation("Alert", MarkLogicErrorLogTokenType.LogLevel.ALERT)
        messageContinuation("Emergency", MarkLogicErrorLogTokenType.LogLevel.EMERGENCY)
        messageContinuation("Unknown", MarkLogicErrorLogTokenType.LOG_LEVEL)
    }
}
