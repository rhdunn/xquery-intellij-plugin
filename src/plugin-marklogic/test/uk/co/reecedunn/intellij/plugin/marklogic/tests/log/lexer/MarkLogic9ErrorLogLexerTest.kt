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
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase
import uk.co.reecedunn.intellij.plugin.marklogic.log.lang.MarkLogicErrorLogFormat
import uk.co.reecedunn.intellij.plugin.marklogic.log.lexer.ILogLevelElementType
import uk.co.reecedunn.intellij.plugin.marklogic.log.lexer.MarkLogicErrorLogLexer
import uk.co.reecedunn.intellij.plugin.marklogic.log.lexer.MarkLogicErrorLogTokenType

@Suppress("ClassName", "Reformat")
@DisplayName("MarkLogic 9.0 ErrorLog - Lexer")
class MarkLogic9ErrorLogLexerTest : LexerTestCase() {
    override val lexer: Lexer = MarkLogicErrorLogLexer(MarkLogicErrorLogFormat.MARKLOGIC_9)

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
        fun emptyBuffer() = tokenize("") {
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
            token("WARNING", MarkLogicErrorLogTokenType.LogLevel.WARNING)
            state(4)
            token(":", MarkLogicErrorLogTokenType.COLON)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("JNI local refs: zu, exceeds capacity: zu", MarkLogicErrorLogTokenType.MESSAGE)
            token("\n", MarkLogicErrorLogTokenType.WHITE_SPACE)
            state(0)
            token("\tat java.lang.System.initProperties(Native Method)", MarkLogicErrorLogTokenType.MESSAGE)
            state(5)
            token("\n", MarkLogicErrorLogTokenType.WHITE_SPACE)
            state(0)
            token("\tat java.lang.System.initializeSystemClass(System.java:1166)", MarkLogicErrorLogTokenType.MESSAGE)
            state(5)
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
            state(2)
            token("\n", MarkLogicErrorLogTokenType.WHITE_SPACE)
            state(0)
            token("2001-01-11", MarkLogicErrorLogTokenType.DATE)
            state(1)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("12:34:56.789", MarkLogicErrorLogTokenType.TIME)
            state(2)
            token("\n", MarkLogicErrorLogTokenType.WHITE_SPACE)
            state(0)
            token("2001-01-12", MarkLogicErrorLogTokenType.DATE)
            state(1)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("12:34:56.789", MarkLogicErrorLogTokenType.TIME)
            state(2)
        }
    }

    private fun simpleMessage(logLevel: ILogLevelElementType) {
        tokenize("2001-01-10 12:34:56.789 ${logLevel.name}: Lorem ipsum dolor") {
            token("2001-01-10", MarkLogicErrorLogTokenType.DATE)
            state(1)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("12:34:56.789", MarkLogicErrorLogTokenType.TIME)
            state(2)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token(logLevel.name, logLevel)
            state(4)
            token(":", MarkLogicErrorLogTokenType.COLON)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("Lorem ipsum dolor", MarkLogicErrorLogTokenType.MESSAGE)
        }
    }

    @Test
    @DisplayName("simple message (MarkLogic >= 9.0)")
    fun simpleMessage() {
        simpleMessage(MarkLogicErrorLogTokenType.LogLevel.FINEST)
        simpleMessage(MarkLogicErrorLogTokenType.LogLevel.FINER)
        simpleMessage(MarkLogicErrorLogTokenType.LogLevel.FINE)
        simpleMessage(MarkLogicErrorLogTokenType.LogLevel.DEBUG)
        simpleMessage(MarkLogicErrorLogTokenType.LogLevel.CONFIG)
        simpleMessage(MarkLogicErrorLogTokenType.LogLevel.INFO)
        simpleMessage(MarkLogicErrorLogTokenType.LogLevel.NOTICE)
        simpleMessage(MarkLogicErrorLogTokenType.LogLevel.WARNING)
        simpleMessage(MarkLogicErrorLogTokenType.LogLevel.ERROR)
        simpleMessage(MarkLogicErrorLogTokenType.LogLevel.CRITICAL)
        simpleMessage(MarkLogicErrorLogTokenType.LogLevel.ALERT)
        simpleMessage(MarkLogicErrorLogTokenType.LogLevel.EMERGENCY)
        simpleMessage(MarkLogicErrorLogTokenType.LogLevel.UNKNOWN)
    }

    private fun messageWithTaskServer(logLevel: ILogLevelElementType) {
        tokenize("2001-01-10 12:34:56.789 ${logLevel.name}: TaskServer: Lorem ipsum dolor") {
            token("2001-01-10", MarkLogicErrorLogTokenType.DATE)
            state(1)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("12:34:56.789", MarkLogicErrorLogTokenType.TIME)
            state(2)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token(logLevel.name, logLevel)
            state(4)
            token(":", MarkLogicErrorLogTokenType.COLON)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("TaskServer: Lorem ipsum dolor", MarkLogicErrorLogTokenType.MESSAGE)
        }
    }

    @Test
    @DisplayName("message with TaskServer (MarkLogic <= 8.0)")
    fun messageWithTaskServer() {
        messageWithTaskServer(MarkLogicErrorLogTokenType.LogLevel.FINEST)
        messageWithTaskServer(MarkLogicErrorLogTokenType.LogLevel.FINER)
        messageWithTaskServer(MarkLogicErrorLogTokenType.LogLevel.FINE)
        messageWithTaskServer(MarkLogicErrorLogTokenType.LogLevel.DEBUG)
        messageWithTaskServer(MarkLogicErrorLogTokenType.LogLevel.CONFIG)
        messageWithTaskServer(MarkLogicErrorLogTokenType.LogLevel.INFO)
        messageWithTaskServer(MarkLogicErrorLogTokenType.LogLevel.NOTICE)
        messageWithTaskServer(MarkLogicErrorLogTokenType.LogLevel.WARNING)
        messageWithTaskServer(MarkLogicErrorLogTokenType.LogLevel.ERROR)
        messageWithTaskServer(MarkLogicErrorLogTokenType.LogLevel.CRITICAL)
        messageWithTaskServer(MarkLogicErrorLogTokenType.LogLevel.ALERT)
        messageWithTaskServer(MarkLogicErrorLogTokenType.LogLevel.EMERGENCY)
        messageWithTaskServer(MarkLogicErrorLogTokenType.LogLevel.UNKNOWN)
    }

    private fun messageWithAppServer(logLevel: ILogLevelElementType) {
        tokenize("2001-01-10 12:34:56.789 ${logLevel.name}: abc-2d_3e: Lorem ipsum dolor") {
            token("2001-01-10", MarkLogicErrorLogTokenType.DATE)
            state(1)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("12:34:56.789", MarkLogicErrorLogTokenType.TIME)
            state(2)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token(logLevel.name, logLevel)
            state(4)
            token(":", MarkLogicErrorLogTokenType.COLON)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("abc-2d_3e: Lorem ipsum dolor", MarkLogicErrorLogTokenType.MESSAGE)
        }
    }

    @Test
    @DisplayName("message with AppServer (MarkLogic <= 8.0)")
    fun messageWithAppServer() {
        messageWithAppServer(MarkLogicErrorLogTokenType.LogLevel.FINEST)
        messageWithAppServer(MarkLogicErrorLogTokenType.LogLevel.FINER)
        messageWithAppServer(MarkLogicErrorLogTokenType.LogLevel.FINE)
        messageWithAppServer(MarkLogicErrorLogTokenType.LogLevel.DEBUG)
        messageWithAppServer(MarkLogicErrorLogTokenType.LogLevel.CONFIG)
        messageWithAppServer(MarkLogicErrorLogTokenType.LogLevel.INFO)
        messageWithAppServer(MarkLogicErrorLogTokenType.LogLevel.NOTICE)
        messageWithAppServer(MarkLogicErrorLogTokenType.LogLevel.WARNING)
        messageWithAppServer(MarkLogicErrorLogTokenType.LogLevel.ERROR)
        messageWithAppServer(MarkLogicErrorLogTokenType.LogLevel.CRITICAL)
        messageWithAppServer(MarkLogicErrorLogTokenType.LogLevel.ALERT)
        messageWithAppServer(MarkLogicErrorLogTokenType.LogLevel.EMERGENCY)
        messageWithAppServer(MarkLogicErrorLogTokenType.LogLevel.UNKNOWN)
    }

    private fun messageWithAppServerLikeNameAfterAppServer(logLevel: ILogLevelElementType) {
        tokenize("2001-01-10 12:34:56.789 ${logLevel.name}: lorem: ipsum: Lorem ipsum dolor") {
            token("2001-01-10", MarkLogicErrorLogTokenType.DATE)
            state(1)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("12:34:56.789", MarkLogicErrorLogTokenType.TIME)
            state(2)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token(logLevel.name, logLevel)
            state(4)
            token(":", MarkLogicErrorLogTokenType.COLON)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("lorem: ipsum: Lorem ipsum dolor", MarkLogicErrorLogTokenType.MESSAGE)
        }
    }

    @Test
    @DisplayName("message with AppServer-like name after AppServer (MarkLogic <= 8.0)")
    fun messageWithAppServerLikeNameAfterAppServer() {
        messageWithAppServerLikeNameAfterAppServer(MarkLogicErrorLogTokenType.LogLevel.FINEST)
        messageWithAppServerLikeNameAfterAppServer(MarkLogicErrorLogTokenType.LogLevel.FINER)
        messageWithAppServerLikeNameAfterAppServer(MarkLogicErrorLogTokenType.LogLevel.FINE)
        messageWithAppServerLikeNameAfterAppServer(MarkLogicErrorLogTokenType.LogLevel.DEBUG)
        messageWithAppServerLikeNameAfterAppServer(MarkLogicErrorLogTokenType.LogLevel.CONFIG)
        messageWithAppServerLikeNameAfterAppServer(MarkLogicErrorLogTokenType.LogLevel.INFO)
        messageWithAppServerLikeNameAfterAppServer(MarkLogicErrorLogTokenType.LogLevel.NOTICE)
        messageWithAppServerLikeNameAfterAppServer(MarkLogicErrorLogTokenType.LogLevel.WARNING)
        messageWithAppServerLikeNameAfterAppServer(MarkLogicErrorLogTokenType.LogLevel.ERROR)
        messageWithAppServerLikeNameAfterAppServer(MarkLogicErrorLogTokenType.LogLevel.CRITICAL)
        messageWithAppServerLikeNameAfterAppServer(MarkLogicErrorLogTokenType.LogLevel.ALERT)
        messageWithAppServerLikeNameAfterAppServer(MarkLogicErrorLogTokenType.LogLevel.EMERGENCY)
        messageWithAppServerLikeNameAfterAppServer(MarkLogicErrorLogTokenType.LogLevel.UNKNOWN)
    }

    private fun messageContinuation(logLevel: ILogLevelElementType) {
        tokenize("2001-01-10 12:34:56.789 ${logLevel.name}:+Lorem ipsum dolor") {
            token("2001-01-10", MarkLogicErrorLogTokenType.DATE)
            state(1)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("12:34:56.789", MarkLogicErrorLogTokenType.TIME)
            state(2)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token(logLevel.name, logLevel)
            state(4)
            token(":", MarkLogicErrorLogTokenType.COLON)
            token("+", MarkLogicErrorLogTokenType.CONTINUATION)
            token("Lorem ipsum dolor", MarkLogicErrorLogTokenType.MESSAGE)
        }
    }

    @Test
    @DisplayName("message continuation (MarkLogic >= 9.0)")
    fun messageContinuation() {
        messageContinuation(MarkLogicErrorLogTokenType.LogLevel.FINEST)
        messageContinuation(MarkLogicErrorLogTokenType.LogLevel.FINER)
        messageContinuation(MarkLogicErrorLogTokenType.LogLevel.FINE)
        messageContinuation(MarkLogicErrorLogTokenType.LogLevel.DEBUG)
        messageContinuation(MarkLogicErrorLogTokenType.LogLevel.CONFIG)
        messageContinuation(MarkLogicErrorLogTokenType.LogLevel.INFO)
        messageContinuation(MarkLogicErrorLogTokenType.LogLevel.NOTICE)
        messageContinuation(MarkLogicErrorLogTokenType.LogLevel.WARNING)
        messageContinuation(MarkLogicErrorLogTokenType.LogLevel.ERROR)
        messageContinuation(MarkLogicErrorLogTokenType.LogLevel.CRITICAL)
        messageContinuation(MarkLogicErrorLogTokenType.LogLevel.ALERT)
        messageContinuation(MarkLogicErrorLogTokenType.LogLevel.EMERGENCY)
        messageContinuation(MarkLogicErrorLogTokenType.LogLevel.UNKNOWN)
    }

    private fun logLevelWithoutSpace(logLevel: ILogLevelElementType) {
        tokenize("2001-01-10 12:34:56.789 ${logLevel.name}:Lorem ipsum dolor") {
            token("2001-01-10", MarkLogicErrorLogTokenType.DATE)
            state(1)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("12:34:56.789", MarkLogicErrorLogTokenType.TIME)
            state(2)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("${logLevel.name}:Lorem ipsum dolor", MarkLogicErrorLogTokenType.MESSAGE)
        }
    }

    @Test
    @DisplayName("log level without space")
    fun logLevelWithoutSpace() {
        logLevelWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.FINEST)
        logLevelWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.FINER)
        logLevelWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.FINE)
        logLevelWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.DEBUG)
        logLevelWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.CONFIG)
        logLevelWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.INFO)
        logLevelWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.NOTICE)
        logLevelWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.WARNING)
        logLevelWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.ERROR)
        logLevelWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.CRITICAL)
        logLevelWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.ALERT)
        logLevelWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.EMERGENCY)
        logLevelWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.UNKNOWN)
    }

    private fun appServerWithoutSpace(logLevel: ILogLevelElementType) {
        tokenize("2001-01-10 12:34:56.789 ${logLevel.name}: server:Lorem ipsum dolor") {
            token("2001-01-10", MarkLogicErrorLogTokenType.DATE)
            state(1)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("12:34:56.789", MarkLogicErrorLogTokenType.TIME)
            state(2)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token(logLevel.name, logLevel)
            state(4)
            token(":", MarkLogicErrorLogTokenType.COLON)
            token(" ", MarkLogicErrorLogTokenType.WHITE_SPACE)
            token("server:Lorem ipsum dolor", MarkLogicErrorLogTokenType.MESSAGE)
        }
    }

    @Test
    @DisplayName("log level without space")
    fun appServerWithoutSpace() {
        appServerWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.FINEST)
        appServerWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.FINER)
        appServerWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.FINE)
        appServerWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.DEBUG)
        appServerWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.CONFIG)
        appServerWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.INFO)
        appServerWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.NOTICE)
        appServerWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.WARNING)
        appServerWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.ERROR)
        appServerWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.CRITICAL)
        appServerWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.ALERT)
        appServerWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.EMERGENCY)
        appServerWithoutSpace(MarkLogicErrorLogTokenType.LogLevel.UNKNOWN)
    }
}
