/*
 * Copyright (C) 2016-2018, 2020, 2022 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.tests.lexer

import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.lexer.CharacterClass
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat

@DisplayName("IntelliJ - Custom Language Support - Implementing Lexer - CharacterClass")
class CharacterClassTest {
    @Test
    @DisplayName("XQuery 1.0 EBNF (157) Char ; XML 1.0 EBNF (2) Char")
    fun testChar() {
        // Excludes the ASCII control character codes

        assertThat(CharacterClass.getCharClass(0x00), `is`(CharacterClass.INVALID))
        assertThat(CharacterClass.getCharClass(0x01), `is`(CharacterClass.INVALID))
        assertThat(CharacterClass.getCharClass(0x1F), `is`(CharacterClass.INVALID))

        // ASCII

        assertThat(CharacterClass.getCharClass('\\'.code), `is`(CharacterClass.CHAR))
        assertThat(CharacterClass.getCharClass('^'.code), `is`(CharacterClass.CHAR))

        // Excludes the surrogate blocks [0xD800-0xDFFF]

        assertThat(CharacterClass.getCharClass(0xD800), `is`(CharacterClass.INVALID))
        assertThat(CharacterClass.getCharClass(0xD801), `is`(CharacterClass.INVALID))
        assertThat(CharacterClass.getCharClass(0xD92C), `is`(CharacterClass.INVALID))
        assertThat(CharacterClass.getCharClass(0xDFFE), `is`(CharacterClass.INVALID))
        assertThat(CharacterClass.getCharClass(0xDFFF), `is`(CharacterClass.INVALID))

        // Excludes 0xFFFE and 0xFFFF

        assertThat(CharacterClass.getCharClass(0xFFFE), `is`(CharacterClass.INVALID))
        assertThat(CharacterClass.getCharClass(0xFFFF), `is`(CharacterClass.INVALID))

        // Excludes non-Unicode characters

        assertThat(CharacterClass.getCharClass(0x10FFFF), `is`(CharacterClass.CHAR))
        assertThat(CharacterClass.getCharClass(0x110000), `is`(CharacterClass.INVALID))
        assertThat(CharacterClass.getCharClass(0x8FD347), `is`(CharacterClass.INVALID))
        assertThat(CharacterClass.getCharClass(0xFFFFFF), `is`(CharacterClass.INVALID))
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (156) S ; XML 1.0 EBNF (3) S")
    fun testWhitespace() {
        assertThat(CharacterClass.getCharClass('\t'.code), `is`(CharacterClass.WHITESPACE))
        assertThat(CharacterClass.getCharClass('\r'.code), `is`(CharacterClass.WHITESPACE))
        assertThat(CharacterClass.getCharClass('\n'.code), `is`(CharacterClass.WHITESPACE))
        assertThat(CharacterClass.getCharClass(' '.code), `is`(CharacterClass.WHITESPACE))
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (158) Digits")
    fun testDigit() {
        assertThat(CharacterClass.getCharClass('0'.code), `is`(CharacterClass.DIGIT))
        assertThat(CharacterClass.getCharClass('1'.code), `is`(CharacterClass.DIGIT))
        assertThat(CharacterClass.getCharClass('2'.code), `is`(CharacterClass.DIGIT))
        assertThat(CharacterClass.getCharClass('3'.code), `is`(CharacterClass.DIGIT))
        assertThat(CharacterClass.getCharClass('4'.code), `is`(CharacterClass.DIGIT))
        assertThat(CharacterClass.getCharClass('5'.code), `is`(CharacterClass.DIGIT))
        assertThat(CharacterClass.getCharClass('6'.code), `is`(CharacterClass.DIGIT))
        assertThat(CharacterClass.getCharClass('7'.code), `is`(CharacterClass.DIGIT))
        assertThat(CharacterClass.getCharClass('8'.code), `is`(CharacterClass.DIGIT))
        assertThat(CharacterClass.getCharClass('9'.code), `is`(CharacterClass.DIGIT))
    }

    @Test
    @DisplayName("XML 1.0 EBNF (4) NameStartChar")
    fun testNameStartChar() {
        // [a-z]

        assertThat(CharacterClass.getCharClass('a'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('b'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('c'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('d'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('e'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('f'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('g'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('h'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('i'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('j'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('k'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('l'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('m'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('n'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('o'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('p'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('q'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('r'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('s'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('t'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('u'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('v'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('w'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('x'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('y'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('z'.code), `is`(CharacterClass.NAME_START_CHAR))

        // [A-Z]

        assertThat(CharacterClass.getCharClass('A'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('B'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('C'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('D'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('E'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('F'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('G'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('H'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('I'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('J'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('K'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('L'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('M'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('N'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('O'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('P'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('Q'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('R'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('S'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('T'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('U'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('V'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('W'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('X'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('Y'.code), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('Z'.code), `is`(CharacterClass.NAME_START_CHAR))

        // "_"

        assertThat(CharacterClass.getCharClass('_'.code), `is`(CharacterClass.NAME_START_CHAR))

        // [#xC0-#xD6] | [#xD8-#xF6] | [#xF8-#x2FF]

        assertThat(CharacterClass.getCharClass(0x00007F), `is`(CharacterClass.INVALID))
        assertThat(CharacterClass.getCharClass(0x000080), `is`(CharacterClass.CHAR))
        assertThat(CharacterClass.getCharClass(0x0000A3), `is`(CharacterClass.CHAR))
        assertThat(CharacterClass.getCharClass(0x0000BF), `is`(CharacterClass.CHAR))
        assertThat(CharacterClass.getCharClass(0x0000C0), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x0000C9), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x0000D6), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x0000D7), `is`(CharacterClass.CHAR))
        assertThat(CharacterClass.getCharClass(0x0000D8), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x0000E5), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x0000F6), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x0000F7), `is`(CharacterClass.CHAR))
        assertThat(CharacterClass.getCharClass(0x0000F8), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x00013C), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x0002FF), `is`(CharacterClass.NAME_START_CHAR))

        // [#x370-#x37D] | [#x37F-#x1FFF]

        assertThat(CharacterClass.getCharClass(0x000370), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x000378), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x00037D), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x00037E), `is`(CharacterClass.CHAR))
        assertThat(CharacterClass.getCharClass(0x00037F), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x000525), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x001FFF), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x002000), `is`(CharacterClass.CHAR))

        // [#x200C-#x200D]

        assertThat(CharacterClass.getCharClass(0x00200B), `is`(CharacterClass.CHAR))
        assertThat(CharacterClass.getCharClass(0x00200C), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x00200D), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x00200E), `is`(CharacterClass.CHAR))

        // [#x2070-#x218F]

        assertThat(CharacterClass.getCharClass(0x00206F), `is`(CharacterClass.CHAR))
        assertThat(CharacterClass.getCharClass(0x002070), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x002102), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x00218F), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x002190), `is`(CharacterClass.CHAR))

        // [#x2C00-#x2FEF]

        assertThat(CharacterClass.getCharClass(0x002BFF), `is`(CharacterClass.CHAR))
        assertThat(CharacterClass.getCharClass(0x002C00), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x002DA4), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x002FEF), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x002FF0), `is`(CharacterClass.CHAR))

        // [#x3001-#xD7FF]

        assertThat(CharacterClass.getCharClass(0x003000), `is`(CharacterClass.CHAR))
        assertThat(CharacterClass.getCharClass(0x003001), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x005F92), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x00D7FF), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x00D800), `is`(CharacterClass.INVALID))

        // [#xF900-#xFDCF]

        assertThat(CharacterClass.getCharClass(0x00F8FF), `is`(CharacterClass.CHAR))
        assertThat(CharacterClass.getCharClass(0x00F900), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x00FB04), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x00FDCF), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x00FDD0), `is`(CharacterClass.CHAR))

        // [#xFDF0-#xFFFD]

        assertThat(CharacterClass.getCharClass(0x00FDEF), `is`(CharacterClass.CHAR))
        assertThat(CharacterClass.getCharClass(0x00FDF0), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x00FE9C), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x00FFFD), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x00FFFE), `is`(CharacterClass.INVALID))

        // [#x10000-#xEFFFF]

        assertThat(CharacterClass.getCharClass(0x00FFFF), `is`(CharacterClass.INVALID))
        assertThat(CharacterClass.getCharClass(0x010000), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x0204FD), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x0EFFFF), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x0F0000), `is`(CharacterClass.CHAR))
    }

    @Test
    @DisplayName("XML 1.0 EBNF (4a) NameChar")
    fun testNameChar() {
        // [#xB7]

        assertThat(CharacterClass.getCharClass(0x0000B7), `is`(CharacterClass.NAME_CHAR))

        // [#x0300-#x036F]

        assertThat(CharacterClass.getCharClass(0x0002FF), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass(0x000300), `is`(CharacterClass.NAME_CHAR))
        assertThat(CharacterClass.getCharClass(0x000346), `is`(CharacterClass.NAME_CHAR))
        assertThat(CharacterClass.getCharClass(0x00036F), `is`(CharacterClass.NAME_CHAR))
        assertThat(CharacterClass.getCharClass(0x000370), `is`(CharacterClass.NAME_START_CHAR))
    }

    @Test
    @DisplayName("U+002E FULL STOP")
    fun testDot() {
        assertThat(CharacterClass.getCharClass('.'.code), `is`(CharacterClass.DOT))
    }

    @Test
    @DisplayName("U+0022 QUOTATION MARK")
    fun testQuote() {
        assertThat(CharacterClass.getCharClass('"'.code), `is`(CharacterClass.QUOTE))
    }

    @Test
    @DisplayName("U+0027 APOSTROPHE")
    fun testApostrophe() {
        assertThat(CharacterClass.getCharClass('\''.code), `is`(CharacterClass.APOSTROPHE))
    }

    @Test
    @DisplayName("U+003B SEMICOLON")
    fun testSemicolon() {
        assertThat(CharacterClass.getCharClass(';'.code), `is`(CharacterClass.SEMICOLON))
    }

    @Test
    @DisplayName("U+003A COLON")
    fun testColon() {
        assertThat(CharacterClass.getCharClass(':'.code), `is`(CharacterClass.COLON))
    }

    @Test
    @DisplayName("U+0023 NUMBER SIGN")
    fun testHash() {
        assertThat(CharacterClass.getCharClass('#'.code), `is`(CharacterClass.HASH))
    }

    @Test
    @DisplayName("U+002D HYPHEN-MINUS")
    fun testHyphenMinus() {
        assertThat(CharacterClass.getCharClass('-'.code), `is`(CharacterClass.HYPHEN_MINUS))
    }

    @Test
    @DisplayName("U+0028 LEFT PARENTHESIS")
    fun testParenthesisOpen() {
        assertThat(CharacterClass.getCharClass('('.code), `is`(CharacterClass.PARENTHESIS_OPEN))
    }

    @Test
    @DisplayName("U+0029 RIGHT PARENTHESIS")
    fun testParenthesisClose() {
        assertThat(CharacterClass.getCharClass(')'.code), `is`(CharacterClass.PARENTHESIS_CLOSE))
    }

    @Test
    @DisplayName("U+0021 EXCLAMATION MARK")
    fun testExclamationMark() {
        assertThat(CharacterClass.getCharClass('!'.code), `is`(CharacterClass.EXCLAMATION_MARK))
    }

    @Test
    @DisplayName("U+003D EQUALS SIGN")
    fun testEqual() {
        assertThat(CharacterClass.getCharClass('='.code), `is`(CharacterClass.EQUAL))
    }

    @Test
    @DisplayName("U+0024 DOLLAR SIGN")
    fun testDollar() {
        assertThat(CharacterClass.getCharClass('$'.code), `is`(CharacterClass.DOLLAR))
    }

    @Test
    @DisplayName("U+002A ASTERISK")
    fun testAsterisk() {
        assertThat(CharacterClass.getCharClass('*'.code), `is`(CharacterClass.ASTERISK))
    }

    @Test
    @DisplayName("U+002B PLUS SIGN")
    fun testPlus() {
        assertThat(CharacterClass.getCharClass('+'.code), `is`(CharacterClass.PLUS))
    }

    @Test
    @DisplayName("U+003C LESS-THAN SIGN")
    fun testLessThan() {
        assertThat(CharacterClass.getCharClass('<'.code), `is`(CharacterClass.LESS_THAN))
    }

    @Test
    @DisplayName("U+003E GREATER-THAN SIGN")
    fun testGreaterThan() {
        assertThat(CharacterClass.getCharClass('>'.code), `is`(CharacterClass.GREATER_THAN))
    }

    @Test
    @DisplayName("U+002C COMMA")
    fun testComma() {
        assertThat(CharacterClass.getCharClass(','.code), `is`(CharacterClass.COMMA))
    }

    @Test
    @DisplayName("U+007B LEFT CURLY BRACE")
    fun testCurlyBraceOpen() {
        assertThat(CharacterClass.getCharClass('{'.code), `is`(CharacterClass.CURLY_BRACE_OPEN))
    }

    @Test
    @DisplayName("U+007D RIGHT CURLY BRACE")
    fun testCurlyBraceClose() {
        assertThat(CharacterClass.getCharClass('}'.code), `is`(CharacterClass.CURLY_BRACE_CLOSE))
    }

    @Test
    @DisplayName("U+003F QUESTION MARK")
    fun testQuestionMark() {
        assertThat(CharacterClass.getCharClass('?'.code), `is`(CharacterClass.QUESTION_MARK))
    }

    @Test
    @DisplayName("U+002F SOLIDUS")
    fun testForwardSlash() {
        assertThat(CharacterClass.getCharClass('/'.code), `is`(CharacterClass.FORWARD_SLASH))
    }

    @Test
    @DisplayName("U+0040 COMMERCIAL AT")
    fun testAtSign() {
        assertThat(CharacterClass.getCharClass('@'.code), `is`(CharacterClass.AT_SIGN))
    }

    @Test
    @DisplayName("U+005B LEFT SQUARE BRACE")
    fun testSquareBraceOpen() {
        assertThat(CharacterClass.getCharClass('['.code), `is`(CharacterClass.SQUARE_BRACE_OPEN))
    }

    @Test
    @DisplayName("U+005D RIGHT SQUARE BRACE")
    fun testSquareBraceClose() {
        assertThat(CharacterClass.getCharClass(']'.code), `is`(CharacterClass.SQUARE_BRACE_CLOSE))
    }

    @Test
    @DisplayName("U+007C VERTICAL LINE")
    fun testVerticalBar() {
        assertThat(CharacterClass.getCharClass('|'.code), `is`(CharacterClass.VERTICAL_BAR))
    }

    @Test
    @DisplayName("U+0025 PERCENT SIGN")
    fun testPercent() {
        assertThat(CharacterClass.getCharClass('%'.code), `is`(CharacterClass.PERCENT))
    }

    @Test
    @DisplayName("U+0026 AMPERSAND")
    fun testAmpersand() {
        assertThat(CharacterClass.getCharClass('&'.code), `is`(CharacterClass.AMPERSAND))
    }

    @Test
    @DisplayName("U+0060 GRAVE ACCENT")
    fun testBackTick() {
        assertThat(CharacterClass.getCharClass('`'.code), `is`(CharacterClass.BACK_TICK))
    }

    @Test
    @DisplayName("U+007E TILDE")
    fun testTilde() {
        assertThat(CharacterClass.getCharClass('~'.code), `is`(CharacterClass.TILDE))
    }
}
