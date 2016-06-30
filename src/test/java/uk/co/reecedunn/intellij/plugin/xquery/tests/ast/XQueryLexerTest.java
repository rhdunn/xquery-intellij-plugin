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
package uk.co.reecedunn.intellij.plugin.xquery.tests.ast;

import com.intellij.lexer.Lexer;
import com.intellij.psi.tree.IElementType;
import junit.framework.TestCase;
import uk.co.reecedunn.intellij.plugin.xquery.LanguageLevel;
import uk.co.reecedunn.intellij.plugin.xquery.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class XQueryLexerTest extends TestCase {
    private void matchToken(Lexer lexer, String text, int state, int start, int end, IElementType type) {
        assertThat(lexer.getTokenText(), is(text));
        assertThat(lexer.getState(), is(state));
        assertThat(lexer.getTokenStart(), is(start));
        assertThat(lexer.getTokenEnd(), is(end));
        assertThat(lexer.getTokenType(), is(type));
        lexer.advance();
    }

    public void testEmptyBuffer() {
        Lexer lexer = new XQueryLexer();

        lexer.start("");
        matchToken(lexer, "", 0, 0, 0, null);
    }

    // XQuery 1.0 -- A.2.1 [156] S
    // XQuery 3.0 -- A.2.1 [215] S
    // XQuery 3.1 -- A.2.1 [237] S

    public void testWhiteSpace() {
        Lexer lexer = new XQueryLexer();

        lexer.start(" ");
        matchToken(lexer, " ", 0, 0, 1, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "",  0, 1, 1, null);

        lexer.start("\t");
        matchToken(lexer, "\t", 0, 0, 1, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "",   0, 1, 1, null);

        lexer.start("\r");
        matchToken(lexer, "\r", 0, 0, 1, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "",   0, 1, 1, null);

        lexer.start("\n");
        matchToken(lexer, "\n", 0, 0, 1, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "",   0, 1, 1, null);

        lexer.start("   \t  \r\n ");
        matchToken(lexer, "   \t  \r\n ", 0, 0, 9, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "",             0, 9, 9, null);
    }

    // XQuery 1.0 -- A.2.1 [141] IntegerLiteral
    // XQuery 3.0 -- A.2.1 [197] IntegerLiteral
    // XQuery 3.1 -- A.2.1 [219] IntegerLiteral

    public void testIntegerLiteral() {
        Lexer lexer = new XQueryLexer();

        lexer.start("1234");
        matchToken(lexer, "1234", 0, 0, 4, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "",     0, 4, 4, null);
    }

    // XQuery 1.0 -- A.2.1 [142] DecimalLiteral
    // XQuery 3.0 -- A.2.1 [198] DecimalLiteral
    // XQuery 3.1 -- A.2.1 [220] DecimalLiteral

    public void testDecimalLiteral() {
        Lexer lexer = new XQueryLexer();

        lexer.start("47.");
        matchToken(lexer, "47.", 0, 0, 3, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "",    0, 3, 3, null);

        lexer.start("1.234");
        matchToken(lexer, "1.234", 0, 0, 5, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "",      0, 5, 5, null);

        lexer.start(".25");
        matchToken(lexer, ".25", 0, 0, 3, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "",    0, 3, 3, null);

        lexer.start(".1.2");
        matchToken(lexer, ".1", 0, 0, 2, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, ".2", 0, 2, 4, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "",   0, 4, 4, null);
    }

    // XQuery 1.0 -- A.2.1 [143] DoubleLiteral
    // XQuery 3.0 -- A.2.1 [199] DoubleLiteral
    // XQuery 3.1 -- A.2.1 [221] DoubleLiteral

    public void testDoubleLiteral() {
        Lexer lexer = new XQueryLexer();

        lexer.start("3e7 3e+7 3e-7");
        matchToken(lexer, "3e7",  0,  0,  3, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",    0,  3,  4, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "3e+7", 0,  4,  8, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",    0,  8,  9, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "3e-7", 0,  9, 13, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, "",     0, 13, 13, null);

        lexer.start("43E22 43E+22 43E-22");
        matchToken(lexer, "43E22",  0,  0,  5, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",      0,  5,  6, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "43E+22", 0,  6, 12, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",      0, 12, 13, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "43E-22", 0, 13, 19, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, "",       0, 19, 19, null);

        lexer.start("2.1e3 2.1e+3 2.1e-3");
        matchToken(lexer, "2.1e3",  0,  0,  5, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",      0,  5,  6, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "2.1e+3", 0,  6, 12, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",      0, 12, 13, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "2.1e-3", 0, 13, 19, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, "",       0, 19, 19, null);

        lexer.start("1.7E99 1.7E+99 1.7E-99");
        matchToken(lexer, "1.7E99",  0,  0,  6, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",       0,  6,  7, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "1.7E+99", 0,  7, 14, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",       0, 14, 15, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "1.7E-99", 0, 15, 22, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, "",        0, 22, 22, null);

        lexer.start(".22e42 .22e+42 .22e-42");
        matchToken(lexer, ".22e42",  0,  0,  6, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",       0,  6,  7, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ".22e+42", 0,  7, 14, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",       0, 14, 15, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ".22e-42", 0, 15, 22, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, "",        0, 22, 22, null);

        lexer.start(".8E2 .8E+2 .8E-2");
        matchToken(lexer, ".8E2",  0,  0,  4, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",     0,  4,  5, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ".8E+2", 0,  5, 10, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, " ",     0, 10, 11, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ".8E-2", 0, 11, 16, XQueryTokenType.DOUBLE_LITERAL);
        matchToken(lexer, "",      0, 16, 16, null);

        lexer.start("1e 1e+ 1e-");
        matchToken(lexer, "1", 0,  0,  1, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "e", 0,  1,  2, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, " ", 0,  2,  3, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "1", 0,  3,  4, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "e", 0,  4,  5, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "+", 0,  5,  6, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, " ", 0,  6,  7, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "1", 0,  7,  8, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "e", 0,  8,  9, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "-", 0,  9, 10, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "",  0, 10, 10, null);

        lexer.start("1E 1E+ 1E-");
        matchToken(lexer, "1", 0,  0,  1, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "E", 0,  1,  2, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, " ", 0,  2,  3, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "1", 0,  3,  4, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "E", 0,  4,  5, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "+", 0,  5,  6, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, " ", 0,  6,  7, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "1", 0,  7,  8, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "E", 0,  8,  9, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "-", 0,  9, 10, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "",  0, 10, 10, null);

        lexer.start("8.9e 8.9e+ 8.9e-");
        matchToken(lexer, "8.9", 0,  0,  3, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "e",   0,  3,  4, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, " ",   0,  4,  5, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "8.9", 0,  5,  8, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "e",   0,  8,  9, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "+",   0,  9, 10, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, " ",   0, 10, 11, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "8.9", 0, 11, 14, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "e",   0, 14, 15, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "-",   0, 15, 16, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "",    0, 16, 16, null);

        lexer.start("8.9E 8.9E+ 8.9E-");
        matchToken(lexer, "8.9", 0,  0,  3, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "E",   0,  3,  4, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, " ",   0,  4,  5, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "8.9", 0,  5,  8, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "E",   0,  8,  9, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "+",   0,  9, 10, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, " ",   0, 10, 11, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "8.9", 0, 11, 14, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "E",   0, 14, 15, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "-",   0, 15, 16, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "",    0, 16, 16, null);

        lexer.start(".4e .4e+ .4e-");
        matchToken(lexer, ".4", 0,  0,  2, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "e",  0,  2,  3, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, " ",  0,  3,  4, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ".4", 0,  4,  6, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "e",  0,  6,  7, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "+",  0,  7,  8, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, " ",  0,  8,  9, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ".4", 0,  9, 11, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "e",  0, 11, 12, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "-",  0, 12, 13, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "",   0, 13, 13, null);

        lexer.start(".4E .4E+ .4E-");
        matchToken(lexer, ".4", 0,  0,  2, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "E",  0,  2,  3, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, " ",  0,  3,  4, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ".4", 0,  4,  6, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "E",  0,  6,  7, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "+",  0,  7,  8, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, " ",  0,  8,  9, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ".4", 0,  9, 11, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "E",  0, 11, 12, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "-",  0, 12, 13, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "",   0, 13, 13, null);
    }
}

/**
 * References:
 *   XQuery 1.0 (2ed) -- https://www.w3.org/TR/2010/REC-xquery-20101214/
 *   XQuery 3.0       -- https://www.w3.org/TR/2014/REC-xquery-30-20140408/
 *   XQuery 3.1       -- https://www.w3.org/TR/2015/CR-xquery-31-20151217/
 */