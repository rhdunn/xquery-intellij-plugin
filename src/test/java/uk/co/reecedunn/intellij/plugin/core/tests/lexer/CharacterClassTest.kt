/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.lexer.CharacterClass
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification

class CharacterClassTest {
    @Test
    @Specification(name = "XQuery 1.0 2ed", reference = "https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Char")
    @Specification(name = "XML 1.0 5ed", reference = "https://www.w3.org/TR/2008/REC-xml-20081126/#NT-Char")
    fun testChar() {
        // Excludes the ASCII control character codes

        assertThat(CharacterClass.getCharClass(0x00), `is`(CharacterClass.INVALID))
        assertThat(CharacterClass.getCharClass(0x01), `is`(CharacterClass.INVALID))
        assertThat(CharacterClass.getCharClass(0x1F), `is`(CharacterClass.INVALID))

        // ASCII

        assertThat(CharacterClass.getCharClass('\\'.toInt()), `is`(CharacterClass.CHAR))
        assertThat(CharacterClass.getCharClass('^'.toInt()), `is`(CharacterClass.CHAR))
        assertThat(CharacterClass.getCharClass('~'.toInt()), `is`(CharacterClass.CHAR))

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
    @Specification(name = "XQuery 1.0 2ed", reference = "https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-S")
    @Specification(name = "XML 1.0 5ed", reference = "https://www.w3.org/TR/2008/REC-xml-20081126/#NT-S")
    fun testWhitespace() {
        assertThat(CharacterClass.getCharClass('\t'.toInt()), `is`(CharacterClass.WHITESPACE))
        assertThat(CharacterClass.getCharClass('\r'.toInt()), `is`(CharacterClass.WHITESPACE))
        assertThat(CharacterClass.getCharClass('\n'.toInt()), `is`(CharacterClass.WHITESPACE))
        assertThat(CharacterClass.getCharClass(' '.toInt()), `is`(CharacterClass.WHITESPACE))
    }

    @Test
    @Specification(name = "XQuery 1.0 2ed", reference = "https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Digits")
    fun testDigit() {
        assertThat(CharacterClass.getCharClass('0'.toInt()), `is`(CharacterClass.DIGIT))
        assertThat(CharacterClass.getCharClass('1'.toInt()), `is`(CharacterClass.DIGIT))
        assertThat(CharacterClass.getCharClass('2'.toInt()), `is`(CharacterClass.DIGIT))
        assertThat(CharacterClass.getCharClass('3'.toInt()), `is`(CharacterClass.DIGIT))
        assertThat(CharacterClass.getCharClass('4'.toInt()), `is`(CharacterClass.DIGIT))
        assertThat(CharacterClass.getCharClass('5'.toInt()), `is`(CharacterClass.DIGIT))
        assertThat(CharacterClass.getCharClass('6'.toInt()), `is`(CharacterClass.DIGIT))
        assertThat(CharacterClass.getCharClass('7'.toInt()), `is`(CharacterClass.DIGIT))
        assertThat(CharacterClass.getCharClass('8'.toInt()), `is`(CharacterClass.DIGIT))
        assertThat(CharacterClass.getCharClass('9'.toInt()), `is`(CharacterClass.DIGIT))
    }

    @Test
    @Specification(name = "XML 1.0 5ed", reference = "https://www.w3.org/TR/2008/REC-xml-20081126/#NT-NameStartChar")
    fun testNameStartChar() {
        // [a-z]

        assertThat(CharacterClass.getCharClass('a'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('b'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('c'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('d'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('e'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('f'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('g'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('h'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('i'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('j'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('k'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('l'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('m'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('n'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('o'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('p'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('q'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('r'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('s'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('t'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('u'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('v'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('w'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('x'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('y'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('z'.toInt()), `is`(CharacterClass.NAME_START_CHAR))

        // [A-Z]

        assertThat(CharacterClass.getCharClass('A'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('B'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('C'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('D'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('E'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('F'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('G'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('H'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('I'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('J'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('K'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('L'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('M'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('N'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('O'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('P'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('Q'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('R'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('S'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('T'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('U'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('V'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('W'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('X'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('Y'.toInt()), `is`(CharacterClass.NAME_START_CHAR))
        assertThat(CharacterClass.getCharClass('Z'.toInt()), `is`(CharacterClass.NAME_START_CHAR))

        // "_"

        assertThat(CharacterClass.getCharClass('_'.toInt()), `is`(CharacterClass.NAME_START_CHAR))

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
    @Specification(name = "XML 1.0 5ed", reference = "https://www.w3.org/TR/2008/REC-xml-20081126/#NT-NameChar")
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
    fun testDot() {
        assertThat(CharacterClass.getCharClass('.'.toInt()), `is`(CharacterClass.DOT))
    }

    @Test
    fun testQuote() {
        assertThat(CharacterClass.getCharClass('"'.toInt()), `is`(CharacterClass.QUOTE))
    }

    @Test
    fun testApostrophe() {
        assertThat(CharacterClass.getCharClass('\''.toInt()), `is`(CharacterClass.APOSTROPHE))
    }

    @Test
    fun testSemicolon() {
        assertThat(CharacterClass.getCharClass(';'.toInt()), `is`(CharacterClass.SEMICOLON))
    }

    @Test
    fun testColon() {
        assertThat(CharacterClass.getCharClass(':'.toInt()), `is`(CharacterClass.COLON))
    }

    @Test
    fun testHash() {
        assertThat(CharacterClass.getCharClass('#'.toInt()), `is`(CharacterClass.HASH))
    }

    @Test
    fun testHyphenMinus() {
        assertThat(CharacterClass.getCharClass('-'.toInt()), `is`(CharacterClass.HYPHEN_MINUS))
    }

    @Test
    fun testParenthesisOpen() {
        assertThat(CharacterClass.getCharClass('('.toInt()), `is`(CharacterClass.PARENTHESIS_OPEN))
    }

    @Test
    fun testParenthesisClose() {
        assertThat(CharacterClass.getCharClass(')'.toInt()), `is`(CharacterClass.PARENTHESIS_CLOSE))
    }

    @Test
    fun testExclamationMark() {
        assertThat(CharacterClass.getCharClass('!'.toInt()), `is`(CharacterClass.EXCLAMATION_MARK))
    }

    @Test
    fun testEqual() {
        assertThat(CharacterClass.getCharClass('='.toInt()), `is`(CharacterClass.EQUAL))
    }

    @Test
    fun testDollar() {
        assertThat(CharacterClass.getCharClass('$'.toInt()), `is`(CharacterClass.DOLLAR))
    }

    @Test
    fun testAsterisk() {
        assertThat(CharacterClass.getCharClass('*'.toInt()), `is`(CharacterClass.ASTERISK))
    }

    @Test
    fun testPlus() {
        assertThat(CharacterClass.getCharClass('+'.toInt()), `is`(CharacterClass.PLUS))
    }

    @Test
    fun testLessThan() {
        assertThat(CharacterClass.getCharClass('<'.toInt()), `is`(CharacterClass.LESS_THAN))
    }

    @Test
    fun testGreaterThan() {
        assertThat(CharacterClass.getCharClass('>'.toInt()), `is`(CharacterClass.GREATER_THAN))
    }

    @Test
    fun testComma() {
        assertThat(CharacterClass.getCharClass(','.toInt()), `is`(CharacterClass.COMMA))
    }

    @Test
    fun testCurlyBraceOpen() {
        assertThat(CharacterClass.getCharClass('{'.toInt()), `is`(CharacterClass.CURLY_BRACE_OPEN))
    }

    @Test
    fun testCurlyBraceClose() {
        assertThat(CharacterClass.getCharClass('}'.toInt()), `is`(CharacterClass.CURLY_BRACE_CLOSE))
    }

    @Test
    fun testQuestionMark() {
        assertThat(CharacterClass.getCharClass('?'.toInt()), `is`(CharacterClass.QUESTION_MARK))
    }

    @Test
    fun testForwardSlash() {
        assertThat(CharacterClass.getCharClass('/'.toInt()), `is`(CharacterClass.FORWARD_SLASH))
    }

    @Test
    fun testAtSign() {
        assertThat(CharacterClass.getCharClass('@'.toInt()), `is`(CharacterClass.AT_SIGN))
    }

    @Test
    fun testSquareBraceOpen() {
        assertThat(CharacterClass.getCharClass('['.toInt()), `is`(CharacterClass.SQUARE_BRACE_OPEN))
    }

    @Test
    fun testSquareBraceClose() {
        assertThat(CharacterClass.getCharClass(']'.toInt()), `is`(CharacterClass.SQUARE_BRACE_CLOSE))
    }

    @Test
    fun testVerticalBar() {
        assertThat(CharacterClass.getCharClass('|'.toInt()), `is`(CharacterClass.VERTICAL_BAR))
    }

    @Test
    fun testPercent() {
        assertThat(CharacterClass.getCharClass('%'.toInt()), `is`(CharacterClass.PERCENT))
    }

    @Test
    fun testAmpersand() {
        assertThat(CharacterClass.getCharClass('&'.toInt()), `is`(CharacterClass.AMPERSAND))
    }

    @Test
    fun testBackTick() {
        assertThat(CharacterClass.getCharClass('`'.toInt()), `is`(CharacterClass.BACK_TICK))
    }
}
