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
    public void testEndOfBuffer() {
        assertThat(CharacterClass.getCharClass(0), is(CharacterClass.END_OF_BUFFER));
    }

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Char")
    @Specification(name="XML 1.0 5ed", reference="https://www.w3.org/TR/2008/REC-xml-20081126/#NT-Char")
    public void testInvalidXmlChar() {
        // 1. excludes the ASCII control character codes

        assertThat(CharacterClass.getCharClass(0x01), is(CharacterClass.INVALID));
        assertThat(CharacterClass.getCharClass(0x1F), is(CharacterClass.INVALID));
        assertThat(CharacterClass.getCharClass(0x7F), is(CharacterClass.INVALID));

        // 2. excludes the surrogate blocks [0xD800-0xDFFF]

        assertThat(CharacterClass.getCharClass(0xD800), is(CharacterClass.INVALID));
        assertThat(CharacterClass.getCharClass(0xD801), is(CharacterClass.INVALID));
        assertThat(CharacterClass.getCharClass(0xD92C), is(CharacterClass.INVALID));
        assertThat(CharacterClass.getCharClass(0xDFFE), is(CharacterClass.INVALID));
        assertThat(CharacterClass.getCharClass(0xDFFF), is(CharacterClass.INVALID));

        // 3. excludes 0xFFFE and 0xFFFF

        assertThat(CharacterClass.getCharClass(0xFFFE), is(CharacterClass.INVALID));
        assertThat(CharacterClass.getCharClass(0xFFFF), is(CharacterClass.INVALID));
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
    public void testLetter() {
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

    public void testHash() {
        assertThat(CharacterClass.getCharClass('#'), is(CharacterClass.HASH));
    }
}
