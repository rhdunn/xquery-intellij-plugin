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

import com.intellij.lexer.Lexer;
import com.intellij.psi.tree.IElementType;
import junit.framework.TestCase;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class XQueryLexerTest extends TestCase {
    // region Lexer Test Helpers

    private void matchToken(Lexer lexer, String text, int state, int start, int end, IElementType type) {
        assertThat(lexer.getTokenText(), is(text));
        assertThat(lexer.getState(), is(state));
        assertThat(lexer.getTokenStart(), is(start));
        assertThat(lexer.getTokenEnd(), is(end));
        assertThat(lexer.getTokenType(), is(type));

        if (lexer.getTokenType() == null) {
            assertThat(lexer.getBufferEnd(), is(start));
            assertThat(lexer.getBufferEnd(), is(end));
        }

        lexer.advance();
    }

    private void matchSingleToken(Lexer lexer, String text, int state, IElementType type) {
        final int length = text.length();
        lexer.start(text);
        matchToken(lexer, text, 0,     0,      length, type);
        matchToken(lexer, "",   state, length, length, null);
    }

    private void matchSingleToken(Lexer lexer, String text, IElementType type) {
        matchSingleToken(lexer, text, 0, type);
    }

    // endregion
    // region Basic Lexer Tests

    public void testEmptyBuffer() {
        Lexer lexer = new XQueryLexer();

        lexer.start("");
        matchToken(lexer, "", 0, 0, 0, null);
    }

    public void testBadCharacters() {
        Lexer lexer = new XQueryLexer();

        lexer.start("~\uFFFE\u0000\uFFFF");
        matchToken(lexer, "~",      0, 0, 1, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "\uFFFE", 0, 1, 2, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "\u0000", 0, 2, 3, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "\uFFFF", 0, 3, 4, XQueryTokenType.BAD_CHARACTER);
        matchToken(lexer, "",       0, 4, 4, null);
    }

    // endregion
    // region A.2.1 Terminal Symbols

    // region IntegerLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-IntegerLiteral")
    public void testIntegerLiteral() {
        Lexer lexer = new XQueryLexer();

        lexer.start("1234");
        matchToken(lexer, "1234", 0, 0, 4, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "",     0, 4, 4, null);
    }

    // endregion
    // region DecimalLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DecimalLiteral")
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

    // endregion
    // region DoubleLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DoubleLiteral")
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
        matchToken(lexer, "1",  0,  0,  1, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "e",  3,  1,  2, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",  0,  2,  3, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "1",  0,  3,  4, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "e+", 3,  4,  6, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",  0,  6,  7, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "1",  0,  7,  8, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "e-", 3,  8, 10, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, "",   0, 10, 10, null);

        lexer.start("1E 1E+ 1E-");
        matchToken(lexer, "1",  0,  0,  1, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "E",  3,  1,  2, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",  0,  2,  3, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "1",  0,  3,  4, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "E+", 3,  4,  6, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",  0,  6,  7, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "1",  0,  7,  8, XQueryTokenType.INTEGER_LITERAL);
        matchToken(lexer, "E-", 3,  8, 10, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, "",   0, 10, 10, null);

        lexer.start("8.9e 8.9e+ 8.9e-");
        matchToken(lexer, "8.9", 0,  0,  3, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "e",   3,  3,  4, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",   0,  4,  5, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "8.9", 0,  5,  8, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "e+",  3,  8, 10, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",   0, 10, 11, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "8.9", 0, 11, 14, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "e-",  3, 14, 16, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, "",    0, 16, 16, null);

        lexer.start("8.9E 8.9E+ 8.9E-");
        matchToken(lexer, "8.9", 0,  0,  3, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "E",   3,  3,  4, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",   0,  4,  5, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "8.9", 0,  5,  8, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "E+",  3,  8, 10, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",   0, 10, 11, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "8.9", 0, 11, 14, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "E-",  3, 14, 16, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, "",    0, 16, 16, null);

        lexer.start(".4e .4e+ .4e-");
        matchToken(lexer, ".4", 0,  0,  2, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "e",  3,  2,  3, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",  0,  3,  4, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ".4", 0,  4,  6, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "e+", 3,  6,  8, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",  0,  8,  9, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ".4", 0,  9, 11, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "e-", 3, 11, 13, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, "",   0, 13, 13, null);

        lexer.start(".4E .4E+ .4E-");
        matchToken(lexer, ".4", 0,  0,  2, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "E",  3,  2,  3, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",  0,  3,  4, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ".4", 0,  4,  6, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "E+", 3,  6,  8, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, " ",  0,  8,  9, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ".4", 0,  9, 11, XQueryTokenType.DECIMAL_LITERAL);
        matchToken(lexer, "E-", 3, 11, 13, XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT);
        matchToken(lexer, "",   0, 13, 13, null);
    }

    // endregion
    // region StringLiteral

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    public void testStringLiteral() {
        Lexer lexer = new XQueryLexer();

        lexer.start("\"");
        matchToken(lexer, "\"", 0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "",   1, 1, 1, null);

        lexer.start("\"Hello World\"");
        matchToken(lexer, "\"",          0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "Hello World", 1,  1, 12, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"",          1, 12, 13, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",            0, 13, 13, null);

        lexer.start("'");
        matchToken(lexer, "'", 0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "",  2, 1, 1, null);

        lexer.start("'Hello World'");
        matchToken(lexer, "'",           0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "Hello World", 2,  1, 12, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "'",           2, 12, 13, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",            0, 13, 13, null);
    }

    // endregion
    // region PredefinedEntityRef

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-PredefinedEntityRef")
    public void testStringLiteral_PredefinedEntityRef() {
        Lexer lexer = new XQueryLexer();

        // NOTE: The predefined entity reference names are not validated by the lexer, as some
        // XQuery processors support HTML predefined entities. Shifting the name validation to
        // the parser allows proper validation errors to be generated.

        lexer.start("\"One&abc;&aBc;&Abc;&ABC;&a4;&a;Two\"");
        matchToken(lexer, "\"",    0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "One",   1,  1,  4, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&abc;", 1,  4,  9, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&aBc;", 1,  9, 14, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&Abc;", 1, 14, 19, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&ABC;", 1, 19, 24, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&a4;",  1, 24, 28, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&a;",   1, 28, 31, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "Two",   1, 31, 34, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"",    1, 34, 35, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",      0, 35, 35, null);

        lexer.start("'One&abc;&aBc;&Abc;&ABC;&a4;&a;Two'");
        matchToken(lexer, "'",     0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "One",   2,  1,  4, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&abc;", 2,  4,  9, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&aBc;", 2,  9, 14, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&Abc;", 2, 14, 19, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&ABC;", 2, 19, 24, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&a4;",  2, 24, 28, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "&a;",   2, 28, 31, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE);
        matchToken(lexer, "Two",   2, 31, 34, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "'",     2, 34, 35, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",      0, 35, 35, null);

        lexer.start("\"&\"");
        matchToken(lexer, "\"", 0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&",  1, 1, 2, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "\"", 1, 2, 3, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",   0, 3, 3, null);

        lexer.start("\"&abc!\"");
        matchToken(lexer, "\"",   0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&abc", 1, 1, 5, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "!",    1, 5, 6, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"",   1, 6, 7, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",     0, 7, 7, null);

        lexer.start("\"& \"");
        matchToken(lexer, "\"", 0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&",  1, 1, 2, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, " ",  1, 2, 3, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"", 1, 3, 4, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",   0, 4, 4, null);

        lexer.start("\"&");
        matchToken(lexer, "\"", 0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&",  1, 1, 2, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",   1, 2, 2, null);

        lexer.start("\"&abc");
        matchToken(lexer, "\"",   0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&abc", 1, 1, 5, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",     1, 5, 5, null);

        lexer.start("\"&;\"");
        matchToken(lexer, "\"", 0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&;", 1, 1, 3, XQueryTokenType.EMPTY_ENTITY_REFERENCE);
        matchToken(lexer, "\"", 1, 3, 4, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",   0, 4, 4, null);

        lexer.start("&");
        matchToken(lexer, "&",  0, 0, 1, XQueryTokenType.ENTITY_REFERENCE_NOT_IN_STRING);
        matchToken(lexer, "",   0, 1, 1, null);
    }

    // endregion
    // region EscapeQuot

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EscapeQuot")
    public void testStringLiteral_EscapeQuot() {
        Lexer lexer = new XQueryLexer();

        lexer.start("\"One\"\"Two\"");
        matchToken(lexer, "\"",   0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "One",  1,  1,  4, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"\"", 1,  4,  6, XQueryTokenType.STRING_LITERAL_ESCAPED_CHARACTER);
        matchToken(lexer, "Two",  1,  6,  9, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"",   1,  9, 10, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",     0, 10, 10, null);
    }

    // endregion
    // region EscapeApos

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-EscapeApos")
    public void testStringLiteral_EscapeApos() {
        Lexer lexer = new XQueryLexer();

        lexer.start("'One''Two'");
        matchToken(lexer, "'",    0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "One",  2,  1,  4, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "''",   2,  4,  6, XQueryTokenType.STRING_LITERAL_ESCAPED_CHARACTER);
        matchToken(lexer, "Two",  2,  6,  9, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "'",    2,  9, 10, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",     0, 10, 10, null);
    }

    // endregion
    // region Comment

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-Comment")
    public void testComment() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "(:", 4, XQueryTokenType.COMMENT_START_TAG);
        matchSingleToken(lexer, ":)", 0, XQueryTokenType.COMMENT_END_TAG);

        lexer.start("(: Test :");
        matchToken(lexer, "(:",      0, 0, 2, XQueryTokenType.COMMENT_START_TAG);
        matchToken(lexer, " Test :", 4, 2, 9, XQueryTokenType.COMMENT);
        matchToken(lexer, "",        6, 9, 9, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",        0, 9, 9, null);

        lexer.start("(: Test :)");
        matchToken(lexer, "(:",     0,  0,  2, XQueryTokenType.COMMENT_START_TAG);
        matchToken(lexer, " Test ", 4,  2,  8, XQueryTokenType.COMMENT);
        matchToken(lexer, ":)",     4,  8, 10, XQueryTokenType.COMMENT_END_TAG);
        matchToken(lexer, "",       0, 10, 10, null);

        lexer.start("(::Test::)");
        matchToken(lexer, "(:",     0,  0,  2, XQueryTokenType.COMMENT_START_TAG);
        matchToken(lexer, ":Test:", 4,  2,  8, XQueryTokenType.COMMENT);
        matchToken(lexer, ":)",     4,  8, 10, XQueryTokenType.COMMENT_END_TAG);
        matchToken(lexer, "",       0, 10, 10, null);

        lexer.start("(:\nMultiline\nComment\n:)");
        matchToken(lexer, "(:",                     0,  0,  2, XQueryTokenType.COMMENT_START_TAG);
        matchToken(lexer, "\nMultiline\nComment\n", 4,  2, 21, XQueryTokenType.COMMENT);
        matchToken(lexer, ":)",                     4, 21, 23, XQueryTokenType.COMMENT_END_TAG);
        matchToken(lexer, "",                       0, 23, 23, null);

        lexer.start("(: Outer (: Inner :) Outer :)");
        matchToken(lexer, "(:",                        0,  0,  2, XQueryTokenType.COMMENT_START_TAG);
        matchToken(lexer, " Outer (: Inner :) Outer ", 4,  2, 27, XQueryTokenType.COMMENT);
        matchToken(lexer, ":)",                        4, 27, 29, XQueryTokenType.COMMENT_END_TAG);
        matchToken(lexer, "",                          0, 29, 29, null);

        lexer.start("(: Outer ( : Inner :) Outer :)");
        matchToken(lexer, "(:",                0,  0,  2, XQueryTokenType.COMMENT_START_TAG);
        matchToken(lexer, " Outer ( : Inner ", 4,  2, 19, XQueryTokenType.COMMENT);
        matchToken(lexer, ":)",                4, 19, 21, XQueryTokenType.COMMENT_END_TAG);
        matchToken(lexer, " ",                 0, 21, 22, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "Outer",             0, 22, 27, XQueryTokenType.NCNAME);
        matchToken(lexer, " ",                 0, 27, 28, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, ":)",                0, 28, 30, XQueryTokenType.COMMENT_END_TAG);
        matchToken(lexer, "",                  0, 30, 30, null);
    }

    // endregion
    // region CharRef

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-StringLiteral")
    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-CharRef")
    @Specification(name="XML 1.0 5ed", reference="https://www.w3.org/TR/2008/REC-xml-20081126/#NT-CharRef")
    public void testStringLiteral_CharRef() {
        Lexer lexer = new XQueryLexer();

        lexer.start("\"One&#20;&#xA0;Two\"");
        matchToken(lexer, "\"",     0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "One",    1,  1,  4, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&#20;",  1,  4,  9, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "&#xA0;", 1,  9, 15, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "Two",    1, 15, 18, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"",     1, 18, 19, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",       0, 19, 19, null);

        lexer.start("'One&#20;&#xA0;Two'");
        matchToken(lexer, "'",      0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "One",    2,  1,  4, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&#20;",  2,  4,  9, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "&#xA0;", 2,  9, 15, XQueryTokenType.CHARACTER_REFERENCE);
        matchToken(lexer, "Two",    2, 15, 18, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "'",      2, 18, 19, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",       0, 19, 19, null);

        lexer.start("\"&#\"");
        matchToken(lexer, "\"", 0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&#", 1, 1, 3, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "\"", 1, 3, 4, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",   0, 4, 4, null);

        lexer.start("\"&# \"");
        matchToken(lexer, "\"", 0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&#", 1, 1, 3, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, " ",  1, 3, 4, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"", 1, 4, 5, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",   0, 5, 5, null);

        lexer.start("\"&#");
        matchToken(lexer, "\"", 0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&#", 1, 1, 3, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",   1, 3, 3, null);

        lexer.start("\"&#12");
        matchToken(lexer, "\"",   0, 0, 1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&#12", 1, 1, 5, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "",     1, 5, 5, null);

        lexer.start("\"&#;&#x;&#x2G;&#x2g;\"");
        matchToken(lexer, "\"",   0,  0,  1, XQueryTokenType.STRING_LITERAL_START);
        matchToken(lexer, "&#;",  1,  1,  4, XQueryTokenType.EMPTY_ENTITY_REFERENCE);
        matchToken(lexer, "&#x;", 1,  4,  8, XQueryTokenType.EMPTY_ENTITY_REFERENCE);
        matchToken(lexer, "&#x2", 1,  8, 12, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "G;",   1, 12, 14, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "&#x2", 1, 14, 18, XQueryTokenType.PARTIAL_ENTITY_REFERENCE);
        matchToken(lexer, "g;",   1, 18, 20, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "\"",   1, 20, 21, XQueryTokenType.STRING_LITERAL_END);
        matchToken(lexer, "",     0, 21, 21, null);
    }

    // endregion
    // region QName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-QName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-QName")
    public void testQName() {
        Lexer lexer = new XQueryLexer();

        lexer.start("one:two");
        matchToken(lexer, "one", 0, 0, 3, XQueryTokenType.NCNAME);
        matchToken(lexer, ":",   0, 3, 4, XQueryTokenType.QNAME_SEPARATOR);
        matchToken(lexer, "two", 0, 4, 7, XQueryTokenType.NCNAME);
        matchToken(lexer, "",    0, 7, 7, null);
    }

    // endregion
    // region NCName

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-NCName")
    @Specification(name="Namespaces in XML 1.0 3ed", reference="https://www.w3.org/TR/2009/REC-xml-names-20091208/#NT-NCName")
    public void testNCName() {
        Lexer lexer = new XQueryLexer();

        lexer.start("test x b2b F.G a-b g\u0330d");
        matchToken(lexer, "test",     0,  0,  4, XQueryTokenType.NCNAME);
        matchToken(lexer, " ",        0,  4,  5, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "x",        0,  5,  6, XQueryTokenType.NCNAME);
        matchToken(lexer, " ",        0,  6,  7, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "b2b",      0,  7, 10, XQueryTokenType.NCNAME);
        matchToken(lexer, " ",        0, 10, 11, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "F.G",      0, 11, 14, XQueryTokenType.NCNAME);
        matchToken(lexer, " ",        0, 14, 15, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "a-b",      0, 15, 18, XQueryTokenType.NCNAME);
        matchToken(lexer, " ",        0, 18, 19, XQueryTokenType.WHITE_SPACE);
        matchToken(lexer, "g\u0330d", 0, 19, 22, XQueryTokenType.NCNAME);
        matchToken(lexer, "",         0, 22, 22, null);
    }

    // endregion
    // region S

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-S")
    @Specification(name="XML 1.0 5ed", reference="https://www.w3.org/TR/2008/REC-xml-20081126/#NT-S")
    public void testS() {
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

    // endregion

    // endregion
    // region A.2.2 Terminal Delimitation

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#id-terminal-delimitation")
    public void testDelimitingTerminalSymbols() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR);
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN);
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE);
        matchSingleToken(lexer, "*", XQueryTokenType.STAR);
        matchSingleToken(lexer, "+", XQueryTokenType.PLUS);
        matchSingleToken(lexer, ":", XQueryTokenType.QNAME_SEPARATOR);
        matchSingleToken(lexer, "|", XQueryTokenType.UNION);
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA);
        matchSingleToken(lexer, "-", XQueryTokenType.MINUS);
        matchSingleToken(lexer, ".", XQueryTokenType.DOT);
        matchSingleToken(lexer, ";", XQueryTokenType.SEMICOLON);
        matchSingleToken(lexer, "=", XQueryTokenType.EQUAL);
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN);
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE);
        matchSingleToken(lexer, "<", XQueryTokenType.LESS_THAN);
        matchSingleToken(lexer, ">", XQueryTokenType.GREATER_THAN);
        matchSingleToken(lexer, "?", XQueryTokenType.QUESTION);
        matchSingleToken(lexer, "/", XQueryTokenType.DIRECT_DESCENDANTS_PATH);
        matchSingleToken(lexer, "@", XQueryTokenType.ATTRIBUTE_SELECTOR);
        matchSingleToken(lexer, "[", XQueryTokenType.PREDICATE_BEGIN);
        matchSingleToken(lexer, "]", XQueryTokenType.PREDICATE_END);

        matchSingleToken(lexer, "!=", XQueryTokenType.NOT_EQUAL);
        matchSingleToken(lexer, ":=", XQueryTokenType.ASSIGN_EQUAL);
        matchSingleToken(lexer, "(#", XQueryTokenType.PRAGMA_BEGIN);
        matchSingleToken(lexer, "#)", XQueryTokenType.PRAGMA_END);
        matchSingleToken(lexer, "::", XQueryTokenType.AXIS_SEPARATOR);
        matchSingleToken(lexer, "..", XQueryTokenType.PARENT_SELECTOR);
        matchSingleToken(lexer, "//", XQueryTokenType.ALL_DESCENDANTS_PATH);
        matchSingleToken(lexer, "</", XQueryTokenType.CLOSE_XML_TAG);
        matchSingleToken(lexer, "/>", XQueryTokenType.SELF_CLOSING_XML_TAG);
        matchSingleToken(lexer, "<<", XQueryTokenType.NODE_BEFORE);
        matchSingleToken(lexer, ">>", XQueryTokenType.NODE_AFTER);
        matchSingleToken(lexer, "<=", XQueryTokenType.LESS_THAN_OR_EQUAL);
        matchSingleToken(lexer, ">=", XQueryTokenType.GREATER_THAN_OR_EQUAL);
        matchSingleToken(lexer, "<?", XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN);
        matchSingleToken(lexer, "?>", XQueryTokenType.PROCESSING_INSTRUCTION_END);
    }

    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#id-terminal-delimitation")
    public void testDelimitingTerminalSymbols_XQuery30() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "!", XQueryTokenType.MAP_OPERATOR);
        matchSingleToken(lexer, "#", XQueryTokenType.FUNCTION_REF_OPERATOR);
        matchSingleToken(lexer, "%", XQueryTokenType.ANNOTATION_INDICATOR);
    }

    // endregion
    // region A.1 EBNF

    // region VersionDecl

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#doc-xquery-VersionDecl")
    public void testVersionDecl() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "xquery",   XQueryTokenType.K_XQUERY);
        matchSingleToken(lexer, "version",  XQueryTokenType.K_VERSION);
        matchSingleToken(lexer, "encoding", XQueryTokenType.K_ENCODING);
    }

    // endregion
    // region DirCommentConstructor

    @Specification(name="XQuery 1.0 2ed", reference="https://www.w3.org/TR/2010/REC-xquery-20101214/#prod-xquery-DirCommentConstructor")
    public void testDirCommentConstructor() {
        Lexer lexer = new XQueryLexer();

        matchSingleToken(lexer, "<!",   0, XQueryTokenType.INVALID);
        matchSingleToken(lexer, "<!-",  0, XQueryTokenType.INVALID);
        matchSingleToken(lexer, "<!--", 5, XQueryTokenType.XML_COMMENT_START_TAG);

        matchSingleToken(lexer, "--",  XQueryTokenType.INVALID);
        matchSingleToken(lexer, "-->", XQueryTokenType.XML_COMMENT_END_TAG);

        lexer.start("<!-- Test");
        matchToken(lexer, "<!--",  0, 0, 4, XQueryTokenType.XML_COMMENT_START_TAG);
        matchToken(lexer, " Test", 5, 4, 9, XQueryTokenType.XML_COMMENT);
        matchToken(lexer, "",      6, 9, 9, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",      0, 9, 9, null);

        lexer.start("<!-- Test --");
        matchToken(lexer, "<!--",     0,  0,  4, XQueryTokenType.XML_COMMENT_START_TAG);
        matchToken(lexer, " Test --", 5,  4, 12, XQueryTokenType.XML_COMMENT);
        matchToken(lexer, "",         6, 12, 12, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",         0, 12, 12, null);

        lexer.start("<!-- Test -->");
        matchToken(lexer, "<!--",   0,  0,  4, XQueryTokenType.XML_COMMENT_START_TAG);
        matchToken(lexer, " Test ", 5,  4, 10, XQueryTokenType.XML_COMMENT);
        matchToken(lexer, "-->",    5, 10, 13, XQueryTokenType.XML_COMMENT_END_TAG);
        matchToken(lexer, "",       0, 13, 13, null);

        lexer.start("<!--\nMultiline\nComment\n-->");
        matchToken(lexer, "<!--",                   0,  0,  4, XQueryTokenType.XML_COMMENT_START_TAG);
        matchToken(lexer, "\nMultiline\nComment\n", 5,  4, 23, XQueryTokenType.XML_COMMENT);
        matchToken(lexer, "-->",                    5, 23, 26, XQueryTokenType.XML_COMMENT_END_TAG);
        matchToken(lexer, "",                       0, 26, 26, null);

        lexer.start("<!---");
        matchToken(lexer, "<!--",  0, 0, 4, XQueryTokenType.XML_COMMENT_START_TAG);
        matchToken(lexer, "-",     5, 4, 5, XQueryTokenType.XML_COMMENT);
        matchToken(lexer, "",      6, 5, 5, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",      0, 5, 5, null);

        lexer.start("<!----");
        matchToken(lexer, "<!--",  0, 0, 4, XQueryTokenType.XML_COMMENT_START_TAG);
        matchToken(lexer, "--",    5, 4, 6, XQueryTokenType.XML_COMMENT);
        matchToken(lexer, "",      6, 6, 6, XQueryTokenType.UNEXPECTED_END_OF_BLOCK);
        matchToken(lexer, "",      0, 6, 6, null);
    }

    // endregion

    // endregion
}
