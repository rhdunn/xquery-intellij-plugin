/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.tests.lexer

import com.intellij.lexer.Lexer
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

@DisplayName("XPath 3.1 - Lexer")
class XPathLexerTest : LexerTestCase() {
    private fun createLexer(): Lexer = XPathLexer()

    @Nested
    @DisplayName("Lexer")
    internal inner class LexerTest {
        @Test
        @DisplayName("invalid state")
        fun invalidState() {
            val lexer = createLexer()

            val e = assertThrows(AssertionError::class.java) { lexer.start("123", 0, 3, 4096) }
            assertThat(e.message, `is`("Invalid state: 4096"))
        }

        @Test
        @DisplayName("empty buffer")
        fun emptyBuffer() {
            val lexer = createLexer()

            lexer.start("")
            matchToken(lexer, "", 0, 0, 0, null)
        }

        @Test
        @DisplayName("bad characters")
        fun badCharacters() {
            val lexer = createLexer()

            lexer.start("~\uFFFE\u0000\uFFFF")
            matchToken(lexer, "~", 0, 0, 1, XPathTokenType.BAD_CHARACTER)
            matchToken(lexer, "\uFFFE", 0, 1, 2, XPathTokenType.BAD_CHARACTER)
            matchToken(lexer, "\u0000", 0, 2, 3, XPathTokenType.BAD_CHARACTER)
            matchToken(lexer, "\uFFFF", 0, 3, 4, XPathTokenType.BAD_CHARACTER)
            matchToken(lexer, "", 0, 4, 4, null)
        }
    }

    @Test
    @DisplayName("XML 1.0 EBNF (3) S")
    fun s() {
        val lexer = createLexer()

        lexer.start(" ")
        matchToken(lexer, " ", 0, 0, 1, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "", 0, 1, 1, null)

        lexer.start("\t")
        matchToken(lexer, "\t", 0, 0, 1, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "", 0, 1, 1, null)

        lexer.start("\r")
        matchToken(lexer, "\r", 0, 0, 1, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "", 0, 1, 1, null)

        lexer.start("\n")
        matchToken(lexer, "\n", 0, 0, 1, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "", 0, 1, 1, null)

        lexer.start("   \t  \r\n ")
        matchToken(lexer, "   \t  \r\n ", 0, 0, 9, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "", 0, 9, 9, null)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (71) IntegerLiteral")
    fun integerLiteral() {
        val lexer = createLexer()

        lexer.start("1234")
        matchToken(lexer, "1234", 0, 0, 4, XPathTokenType.INTEGER_LITERAL)
        matchToken(lexer, "", 0, 4, 4, null)
    }
}
