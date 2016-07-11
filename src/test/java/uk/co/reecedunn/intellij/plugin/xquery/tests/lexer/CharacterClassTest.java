/*
 * Copyright (C) 2016 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lexer;

import junit.framework.TestCase;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.CharacterClass;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CharacterClassTest extends TestCase {
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Char")
    @Specification(name="XML 1.0 5ed", reference="https://www.w3.org/TR/2008/REC-xml-20081126/#NT-Char")
    public void testChar() {
        // Excludes the ASCII control character codes

        assertThat(CharacterClass.getCharClass(0x00), is(CharacterClass.INVALID));
        assertThat(CharacterClass.getCharClass(0x01), is(CharacterClass.INVALID));
        assertThat(CharacterClass.getCharClass(0x1F), is(CharacterClass.INVALID));

        // ASCII

        assertThat(CharacterClass.getCharClass('\\'), is(CharacterClass.CHAR));
        assertThat(CharacterClass.getCharClass('^'), is(CharacterClass.CHAR));
        assertThat(CharacterClass.getCharClass('`'), is(CharacterClass.CHAR));
        assertThat(CharacterClass.getCharClass('~'), is(CharacterClass.CHAR));

        // Excludes the surrogate blocks [0xD800-0xDFFF]

        assertThat(CharacterClass.getCharClass(0xD800), is(CharacterClass.INVALID));
        assertThat(CharacterClass.getCharClass(0xD801), is(CharacterClass.INVALID));
        assertThat(CharacterClass.getCharClass(0xD92C), is(CharacterClass.INVALID));
        assertThat(CharacterClass.getCharClass(0xDFFE), is(CharacterClass.INVALID));
        assertThat(CharacterClass.getCharClass(0xDFFF), is(CharacterClass.INVALID));

        // Excludes 0xFFFE and 0xFFFF

        assertThat(CharacterClass.getCharClass(0xFFFE), is(CharacterClass.INVALID));
        assertThat(CharacterClass.getCharClass(0xFFFF), is(CharacterClass.INVALID));

        // Excludes non-Unicode characters

        assertThat(CharacterClass.getCharClass(0x10FFFF), is(CharacterClass.CHAR));
        assertThat(CharacterClass.getCharClass(0x110000), is(CharacterClass.INVALID));
        assertThat(CharacterClass.getCharClass(0x8FD347), is(CharacterClass.INVALID));
        assertThat(CharacterClass.getCharClass(0xFFFFFF), is(CharacterClass.INVALID));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-S")
    @Specification(name="XML 1.0 5ed", reference="https://www.w3.org/TR/2008/REC-xml-20081126/#NT-S")
    public void testWhitespace() {
        assertThat(CharacterClass.getCharClass('\t'), is(CharacterClass.WHITESPACE));
        assertThat(CharacterClass.getCharClass('\r'), is(CharacterClass.WHITESPACE));
        assertThat(CharacterClass.getCharClass('\n'), is(CharacterClass.WHITESPACE));
        assertThat(CharacterClass.getCharClass(' '), is(CharacterClass.WHITESPACE));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Digits")
    public void testDigit() {
        assertThat(CharacterClass.getCharClass('0'), is(CharacterClass.DIGIT));
        assertThat(CharacterClass.getCharClass('1'), is(CharacterClass.DIGIT));
        assertThat(CharacterClass.getCharClass('2'), is(CharacterClass.DIGIT));
        assertThat(CharacterClass.getCharClass('3'), is(CharacterClass.DIGIT));
        assertThat(CharacterClass.getCharClass('4'), is(CharacterClass.DIGIT));
        assertThat(CharacterClass.getCharClass('5'), is(CharacterClass.DIGIT));
        assertThat(CharacterClass.getCharClass('6'), is(CharacterClass.DIGIT));
        assertThat(CharacterClass.getCharClass('7'), is(CharacterClass.DIGIT));
        assertThat(CharacterClass.getCharClass('8'), is(CharacterClass.DIGIT));
        assertThat(CharacterClass.getCharClass('9'), is(CharacterClass.DIGIT));
    }

    @Specification(name="XML 1.0 5ed", reference="https://www.w3.org/TR/2008/REC-xml-20081126/#NT-NameStartChar")
    public void testNameStartChar() {
        // [a-z]

        assertThat(CharacterClass.getCharClass('a'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('b'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('c'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('d'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('e'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('f'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('g'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('h'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('i'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('j'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('k'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('l'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('m'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('n'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('o'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('p'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('q'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('r'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('s'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('t'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('u'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('v'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('w'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('x'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('y'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('z'), is(CharacterClass.NAME_START_CHAR));

        // [A-Z]

        assertThat(CharacterClass.getCharClass('A'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('B'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('C'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('D'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('E'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('F'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('G'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('H'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('I'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('J'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('K'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('L'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('M'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('N'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('O'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('P'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('Q'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('R'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('S'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('T'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('U'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('V'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('W'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('X'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('Y'), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass('Z'), is(CharacterClass.NAME_START_CHAR));

        // "_"

        assertThat(CharacterClass.getCharClass('_'), is(CharacterClass.NAME_START_CHAR));

        // [#xC0-#xD6] | [#xD8-#xF6] | [#xF8-#x2FF]

        assertThat(CharacterClass.getCharClass(0x00007F), is(CharacterClass.INVALID));
        assertThat(CharacterClass.getCharClass(0x000080), is(CharacterClass.CHAR));
        assertThat(CharacterClass.getCharClass(0x0000A3), is(CharacterClass.CHAR));
        assertThat(CharacterClass.getCharClass(0x0000BF), is(CharacterClass.CHAR));
        assertThat(CharacterClass.getCharClass(0x0000C0), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x0000C9), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x0000D6), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x0000D7), is(CharacterClass.CHAR));
        assertThat(CharacterClass.getCharClass(0x0000D8), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x0000E5), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x0000F6), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x0000F7), is(CharacterClass.CHAR));
        assertThat(CharacterClass.getCharClass(0x0000F8), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x00013C), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x0002FF), is(CharacterClass.NAME_START_CHAR));

        // [#x370-#x37D] | [#x37F-#x1FFF]

        assertThat(CharacterClass.getCharClass(0x000370), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x000378), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x00037D), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x00037E), is(CharacterClass.CHAR));
        assertThat(CharacterClass.getCharClass(0x00037F), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x000525), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x001FFF), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x002000), is(CharacterClass.CHAR));

        // [#x200C-#x200D]

        assertThat(CharacterClass.getCharClass(0x00200B), is(CharacterClass.CHAR));
        assertThat(CharacterClass.getCharClass(0x00200C), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x00200D), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x00200E), is(CharacterClass.CHAR));

        // [#x2070-#x218F]

        assertThat(CharacterClass.getCharClass(0x00206F), is(CharacterClass.CHAR));
        assertThat(CharacterClass.getCharClass(0x002070), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x002102), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x00218F), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x002190), is(CharacterClass.CHAR));

        // [#x2C00-#x2FEF]

        assertThat(CharacterClass.getCharClass(0x002BFF), is(CharacterClass.CHAR));
        assertThat(CharacterClass.getCharClass(0x002C00), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x002DA4), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x002FEF), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x002FF0), is(CharacterClass.CHAR));

        // [#x3001-#xD7FF]

        assertThat(CharacterClass.getCharClass(0x003000), is(CharacterClass.CHAR));
        assertThat(CharacterClass.getCharClass(0x003001), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x005F92), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x00D7FF), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x00D800), is(CharacterClass.INVALID));

        // [#xF900-#xFDCF]

        assertThat(CharacterClass.getCharClass(0x00F8FF), is(CharacterClass.CHAR));
        assertThat(CharacterClass.getCharClass(0x00F900), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x00FB04), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x00FDCF), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x00FDD0), is(CharacterClass.CHAR));

        // [#xFDF0-#xFFFD]

        assertThat(CharacterClass.getCharClass(0x00FDEF), is(CharacterClass.CHAR));
        assertThat(CharacterClass.getCharClass(0x00FDF0), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x00FE9C), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x00FFFD), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x00FFFE), is(CharacterClass.INVALID));

        // [#x10000-#xEFFFF]

        assertThat(CharacterClass.getCharClass(0x00FFFF), is(CharacterClass.INVALID));
        assertThat(CharacterClass.getCharClass(0x010000), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x0204FD), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x0EFFFF), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x0F0000), is(CharacterClass.CHAR));
    }

    @Specification(name="XML 1.0 5ed", reference="https://www.w3.org/TR/2008/REC-xml-20081126/#NT-NameChar")
    public void testNameChar() {
        // [#xB7]

        assertThat(CharacterClass.getCharClass(0x0000B7), is(CharacterClass.NAME_CHAR));

        // [#x0300-#x036F]

        assertThat(CharacterClass.getCharClass(0x0002FF), is(CharacterClass.NAME_START_CHAR));
        assertThat(CharacterClass.getCharClass(0x000300), is(CharacterClass.NAME_CHAR));
        assertThat(CharacterClass.getCharClass(0x000346), is(CharacterClass.NAME_CHAR));
        assertThat(CharacterClass.getCharClass(0x00036F), is(CharacterClass.NAME_CHAR));
        assertThat(CharacterClass.getCharClass(0x000370), is(CharacterClass.NAME_START_CHAR));
    }

    public void testDot() {
        assertThat(CharacterClass.getCharClass('.'), is(CharacterClass.DOT));
    }

    public void testQuote() {
        assertThat(CharacterClass.getCharClass('"'), is(CharacterClass.QUOTE));
    }

    public void testApostrophe() {
        assertThat(CharacterClass.getCharClass('\''), is(CharacterClass.APOSTROPHE));
    }

    public void testSemicolon() {
        assertThat(CharacterClass.getCharClass(';'), is(CharacterClass.SEMICOLON));
    }

    public void testColon() {
        assertThat(CharacterClass.getCharClass(':'), is(CharacterClass.COLON));
    }

    public void testHash() {
        assertThat(CharacterClass.getCharClass('#'), is(CharacterClass.HASH));
    }

    public void testHyphenMinus() {
        assertThat(CharacterClass.getCharClass('-'), is(CharacterClass.HYPHEN_MINUS));
    }

    public void testParenthesisOpen() {
        assertThat(CharacterClass.getCharClass('('), is(CharacterClass.PARENTHESIS_OPEN));
    }

    public void testParenthesisClose() {
        assertThat(CharacterClass.getCharClass(')'), is(CharacterClass.PARENTHESIS_CLOSE));
    }

    public void testExclamationMark() {
        assertThat(CharacterClass.getCharClass('!'), is(CharacterClass.EXCLAMATION_MARK));
    }

    public void testEqual() {
        assertThat(CharacterClass.getCharClass('='), is(CharacterClass.EQUAL));
    }

    public void testDollar() {
        assertThat(CharacterClass.getCharClass('$'), is(CharacterClass.DOLLAR));
    }

    public void testAsterisk() {
        assertThat(CharacterClass.getCharClass('*'), is(CharacterClass.ASTERISK));
    }

    public void testPlus() {
        assertThat(CharacterClass.getCharClass('+'), is(CharacterClass.PLUS));
    }

    public void testLessThan() {
        assertThat(CharacterClass.getCharClass('<'), is(CharacterClass.LESS_THAN));
    }

    public void testGreaterThan() {
        assertThat(CharacterClass.getCharClass('>'), is(CharacterClass.GREATER_THAN));
    }

    public void testComma() {
        assertThat(CharacterClass.getCharClass(','), is(CharacterClass.COMMA));
    }

    public void testCurlyBraceOpen() {
        assertThat(CharacterClass.getCharClass('{'), is(CharacterClass.CURLY_BRACE_OPEN));
    }

    public void testCurlyBraceClose() {
        assertThat(CharacterClass.getCharClass('}'), is(CharacterClass.CURLY_BRACE_CLOSE));
    }

    public void testQuestionMark() {
        assertThat(CharacterClass.getCharClass('?'), is(CharacterClass.QUESTION_MARK));
    }

    public void testForwardSlash() {
        assertThat(CharacterClass.getCharClass('/'), is(CharacterClass.FORWARD_SLASH));
    }

    public void testAtSign() {
        assertThat(CharacterClass.getCharClass('@'), is(CharacterClass.AT_SIGN));
    }

    public void testSquareBraceOpen() {
        assertThat(CharacterClass.getCharClass('['), is(CharacterClass.SQUARE_BRACE_OPEN));
    }

    public void testSquareBraceClose() {
        assertThat(CharacterClass.getCharClass(']'), is(CharacterClass.SQUARE_BRACE_CLOSE));
    }

    public void testVerticalBar() {
        assertThat(CharacterClass.getCharClass('|'), is(CharacterClass.VERTICAL_BAR));
    }

    public void testPercent() {
        assertThat(CharacterClass.getCharClass('%'), is(CharacterClass.PERCENT));
    }

    public void testAmpersand() {
        assertThat(CharacterClass.getCharClass('&'), is(CharacterClass.AMPERSAND));
    }
}
