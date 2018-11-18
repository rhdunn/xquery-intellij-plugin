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

    @Test
    @DisplayName("XPath 2.0 EBNF (72) DecimalLiteral")
    fun decimalLiteral() {
        val lexer = createLexer()

        lexer.start("47.")
        matchToken(lexer, "47.", 0, 0, 3, XPathTokenType.DECIMAL_LITERAL)
        matchToken(lexer, "", 0, 3, 3, null)

        lexer.start("1.234")
        matchToken(lexer, "1.234", 0, 0, 5, XPathTokenType.DECIMAL_LITERAL)
        matchToken(lexer, "", 0, 5, 5, null)

        lexer.start(".25")
        matchToken(lexer, ".25", 0, 0, 3, XPathTokenType.DECIMAL_LITERAL)
        matchToken(lexer, "", 0, 3, 3, null)

        lexer.start(".1.2")
        matchToken(lexer, ".1", 0, 0, 2, XPathTokenType.DECIMAL_LITERAL)
        matchToken(lexer, ".2", 0, 2, 4, XPathTokenType.DECIMAL_LITERAL)
        matchToken(lexer, "", 0, 4, 4, null)
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (73) DoubleLiteral")
    internal inner class DoubleLiteral {
        @Test
        @DisplayName("double literal")
        fun doubleLiteral() {
            val lexer = createLexer()

            lexer.start("3e7 3e+7 3e-7")
            matchToken(lexer, "3e7", 0, 0, 3, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 3, 4, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "3e+7", 0, 4, 8, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 8, 9, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "3e-7", 0, 9, 13, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, "", 0, 13, 13, null)

            lexer.start("43E22 43E+22 43E-22")
            matchToken(lexer, "43E22", 0, 0, 5, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 5, 6, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "43E+22", 0, 6, 12, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 12, 13, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "43E-22", 0, 13, 19, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, "", 0, 19, 19, null)

            lexer.start("2.1e3 2.1e+3 2.1e-3")
            matchToken(lexer, "2.1e3", 0, 0, 5, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 5, 6, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "2.1e+3", 0, 6, 12, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 12, 13, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "2.1e-3", 0, 13, 19, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, "", 0, 19, 19, null)

            lexer.start("1.7E99 1.7E+99 1.7E-99")
            matchToken(lexer, "1.7E99", 0, 0, 6, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 6, 7, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "1.7E+99", 0, 7, 14, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 14, 15, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "1.7E-99", 0, 15, 22, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, "", 0, 22, 22, null)

            lexer.start(".22e42 .22e+42 .22e-42")
            matchToken(lexer, ".22e42", 0, 0, 6, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 6, 7, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, ".22e+42", 0, 7, 14, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 14, 15, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, ".22e-42", 0, 15, 22, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, "", 0, 22, 22, null)

            lexer.start(".8E2 .8E+2 .8E-2")
            matchToken(lexer, ".8E2", 0, 0, 4, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 4, 5, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, ".8E+2", 0, 5, 10, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 10, 11, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, ".8E-2", 0, 11, 16, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, "", 0, 16, 16, null)

            lexer.start("1e 1e+ 1e-")
            matchToken(lexer, "1", 0, 0, 1, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "e", 3, 1, 2, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 2, 3, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "1", 0, 3, 4, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "e+", 3, 4, 6, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 6, 7, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "1", 0, 7, 8, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "e-", 3, 8, 10, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, "", 0, 10, 10, null)

            lexer.start("1E 1E+ 1E-")
            matchToken(lexer, "1", 0, 0, 1, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "E", 3, 1, 2, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 2, 3, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "1", 0, 3, 4, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "E+", 3, 4, 6, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 6, 7, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "1", 0, 7, 8, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "E-", 3, 8, 10, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, "", 0, 10, 10, null)

            lexer.start("8.9e 8.9e+ 8.9e-")
            matchToken(lexer, "8.9", 0, 0, 3, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "e", 3, 3, 4, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 4, 5, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "8.9", 0, 5, 8, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "e+", 3, 8, 10, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 10, 11, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "8.9", 0, 11, 14, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "e-", 3, 14, 16, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, "", 0, 16, 16, null)

            lexer.start("8.9E 8.9E+ 8.9E-")
            matchToken(lexer, "8.9", 0, 0, 3, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "E", 3, 3, 4, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 4, 5, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "8.9", 0, 5, 8, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "E+", 3, 8, 10, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 10, 11, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "8.9", 0, 11, 14, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "E-", 3, 14, 16, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, "", 0, 16, 16, null)

            lexer.start(".4e .4e+ .4e-")
            matchToken(lexer, ".4", 0, 0, 2, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "e", 3, 2, 3, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 3, 4, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, ".4", 0, 4, 6, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "e+", 3, 6, 8, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 8, 9, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, ".4", 0, 9, 11, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "e-", 3, 11, 13, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, "", 0, 13, 13, null)

            lexer.start(".4E .4E+ .4E-")
            matchToken(lexer, ".4", 0, 0, 2, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "E", 3, 2, 3, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 3, 4, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, ".4", 0, 4, 6, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "E+", 3, 6, 8, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 8, 9, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, ".4", 0, 9, 11, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "E-", 3, 11, 13, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, "", 0, 13, 13, null)
        }

        @Test
        @DisplayName("initial state")
        fun initialState() {
            val lexer = createLexer()

            lexer.start("1e", 1, 2, 3)
            matchToken(lexer, "e", 3, 1, 2, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, "", 0, 2, 2, null)
        }
    }

    @Nested
    @DisplayName("XPath 1.0 EBNF (74) StringLiteral")
    internal inner class StringLiteral {
        @Test
        @DisplayName("string literal")
        fun stringLiteral() {
            val lexer = createLexer()

            lexer.start("\"")
            matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
            matchToken(lexer, "", 1, 1, 1, null)

            lexer.start("\"Hello World\"")
            matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
            matchToken(lexer, "Hello World", 1, 1, 12, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "\"", 1, 12, 13, XPathTokenType.STRING_LITERAL_END)
            matchToken(lexer, "", 0, 13, 13, null)

            lexer.start("'")
            matchToken(lexer, "'", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
            matchToken(lexer, "", 2, 1, 1, null)

            lexer.start("'Hello World'")
            matchToken(lexer, "'", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
            matchToken(lexer, "Hello World", 2, 1, 12, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "'", 2, 12, 13, XPathTokenType.STRING_LITERAL_END)
            matchToken(lexer, "", 0, 13, 13, null)
        }

        @Test
        @DisplayName("initial state")
        fun initialState() {
            val lexer = createLexer()

            lexer.start("\"Hello World\"", 1, 13, 1)
            matchToken(lexer, "Hello World", 1, 1, 12, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "\"", 1, 12, 13, XPathTokenType.STRING_LITERAL_END)
            matchToken(lexer, "", 0, 13, 13, null)

            lexer.start("'Hello World'", 1, 13, 2)
            matchToken(lexer, "Hello World", 2, 1, 12, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "'", 2, 12, 13, XPathTokenType.STRING_LITERAL_END)
            matchToken(lexer, "", 0, 13, 13, null)
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
    internal inner class Comment {
        @Test
        @DisplayName("comment")
        fun comment() {
            val lexer = createLexer()

            matchSingleToken(lexer, "(:", 4, XPathTokenType.COMMENT_START_TAG)
            matchSingleToken(lexer, ":)", 0, XPathTokenType.COMMENT_END_TAG)

            lexer.start("(: Test :")
            matchToken(lexer, "(:", 0, 0, 2, XPathTokenType.COMMENT_START_TAG)
            matchToken(lexer, " Test :", 4, 2, 9, XPathTokenType.COMMENT)
            matchToken(lexer, "", 6, 9, 9, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            matchToken(lexer, "", 0, 9, 9, null)

            lexer.start("(: Test :)")
            matchToken(lexer, "(:", 0, 0, 2, XPathTokenType.COMMENT_START_TAG)
            matchToken(lexer, " Test ", 4, 2, 8, XPathTokenType.COMMENT)
            matchToken(lexer, ":)", 4, 8, 10, XPathTokenType.COMMENT_END_TAG)
            matchToken(lexer, "", 0, 10, 10, null)

            lexer.start("(::Test::)")
            matchToken(lexer, "(:", 0, 0, 2, XPathTokenType.COMMENT_START_TAG)
            matchToken(lexer, ":Test:", 4, 2, 8, XPathTokenType.COMMENT)
            matchToken(lexer, ":)", 4, 8, 10, XPathTokenType.COMMENT_END_TAG)
            matchToken(lexer, "", 0, 10, 10, null)

            lexer.start("(:\nMultiline\nComment\n:)")
            matchToken(lexer, "(:", 0, 0, 2, XPathTokenType.COMMENT_START_TAG)
            matchToken(lexer, "\nMultiline\nComment\n", 4, 2, 21, XPathTokenType.COMMENT)
            matchToken(lexer, ":)", 4, 21, 23, XPathTokenType.COMMENT_END_TAG)
            matchToken(lexer, "", 0, 23, 23, null)

            lexer.start("(: Outer (: Inner :) Outer :)")
            matchToken(lexer, "(:", 0, 0, 2, XPathTokenType.COMMENT_START_TAG)
            matchToken(lexer, " Outer (: Inner :) Outer ", 4, 2, 27, XPathTokenType.COMMENT)
            matchToken(lexer, ":)", 4, 27, 29, XPathTokenType.COMMENT_END_TAG)
            matchToken(lexer, "", 0, 29, 29, null)

            lexer.start("(: Outer ( : Inner :) Outer :)")
            matchToken(lexer, "(:", 0, 0, 2, XPathTokenType.COMMENT_START_TAG)
            matchToken(lexer, " Outer ( : Inner ", 4, 2, 19, XPathTokenType.COMMENT)
            matchToken(lexer, ":)", 4, 19, 21, XPathTokenType.COMMENT_END_TAG)
            matchToken(lexer, " ", 0, 21, 22, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "Outer", 0, 22, 27, XPathTokenType.NCNAME)
            matchToken(lexer, " ", 0, 27, 28, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, ":)", 0, 28, 30, XPathTokenType.COMMENT_END_TAG)
            matchToken(lexer, "", 0, 30, 30, null)
        }

        @Test
        @DisplayName("initial state")
        fun initialState() {
            val lexer = createLexer()

            lexer.start("(: Test :", 2, 9, 4)
            matchToken(lexer, " Test :", 4, 2, 9, XPathTokenType.COMMENT)
            matchToken(lexer, "", 6, 9, 9, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            matchToken(lexer, "", 0, 9, 9, null)

            lexer.start("(: Test :)", 2, 10, 4)
            matchToken(lexer, " Test ", 4, 2, 8, XPathTokenType.COMMENT)
            matchToken(lexer, ":)", 4, 8, 10, XPathTokenType.COMMENT_END_TAG)
            matchToken(lexer, "", 0, 10, 10, null)
        }
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (78) QName ; Namespaces in XML 1.0 EBNF (7) QName")
    fun qname() {
        val lexer = createLexer()

        lexer.start("one:two")
        matchToken(lexer, "one", 0, 0, 3, XPathTokenType.NCNAME)
        matchToken(lexer, ":", 0, 3, 4, XPathTokenType.QNAME_SEPARATOR)
        matchToken(lexer, "two", 0, 4, 7, XPathTokenType.NCNAME)
        matchToken(lexer, "", 0, 7, 7, null)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (79) NCName ; Namespaces in XML 1.0 EBNF (4) NCName")
    fun ncname() {
        val lexer = createLexer()

        lexer.start("test x b2b F.G a-b g\u0330d")
        matchToken(lexer, "test", 0, 0, 4, XPathTokenType.NCNAME)
        matchToken(lexer, " ", 0, 4, 5, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "x", 0, 5, 6, XPathTokenType.NCNAME)
        matchToken(lexer, " ", 0, 6, 7, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "b2b", 0, 7, 10, XPathTokenType.NCNAME)
        matchToken(lexer, " ", 0, 10, 11, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "F.G", 0, 11, 14, XPathTokenType.NCNAME)
        matchToken(lexer, " ", 0, 14, 15, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "a-b", 0, 15, 18, XPathTokenType.NCNAME)
        matchToken(lexer, " ", 0, 18, 19, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "g\u0330d", 0, 19, 22, XPathTokenType.NCNAME)
        matchToken(lexer, "", 0, 22, 22, null)
    }
}
