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
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase;
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryWithXQDocLexer;

public class XQueryLexerWithXQDocTest extends LexerTestCase {
    public void testXQueryTokens() {
        Lexer lexer = new XQueryWithXQDocLexer();

        matchSingleToken(lexer, "xquery",   XQueryTokenType.K_XQUERY);
        matchSingleToken(lexer, "version",  XQueryTokenType.K_VERSION);
        matchSingleToken(lexer, "encoding", XQueryTokenType.K_ENCODING);
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public void testXQueryComment() {
        Lexer lexer = new XQueryWithXQDocLexer();

        lexer.start("(: Test :)");
        matchToken(lexer, "(:",     0x00000000|0,  0,  2, XQueryTokenType.COMMENT_START_TAG);
        matchToken(lexer, " Test ", 0x70000000|0,  2,  8, XQDocTokenType.COMMENT_CONTENTS);
        matchToken(lexer, ":)",     0x00000000|4,  8, 10, XQueryTokenType.COMMENT_END_TAG);
        matchToken(lexer, "",       0x00000000|0, 10, 10, null);
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public void testXQDocComment() {
        Lexer lexer = new XQueryWithXQDocLexer();

        lexer.start("(:~\n@xqdoc comment:)");
        matchToken(lexer, "(:",       0x00000000|0,  0,  2, XQueryTokenType.COMMENT_START_TAG);
        matchToken(lexer, "~",        0x70000000|0,  2,  3, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "\n",       0x70000000|8,  3,  4, XQDocTokenType.TRIM);
        matchToken(lexer, "@",        0x70000000|8,  4,  5, XQDocTokenType.TAG_MARKER);
        matchToken(lexer, "xqdoc",    0x70000000|2,  5, 10, XQDocTokenType.TAG);
        matchToken(lexer, " comment", 0x70000000|2, 10, 18, XQDocTokenType.CONTENTS);
        matchToken(lexer, ":)",       0x00000000|4, 18, 20, XQueryTokenType.COMMENT_END_TAG);
        matchToken(lexer, "",         0x00000000|0, 20, 20, null);
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public void testXQueryStateRestore() {
        Lexer lexer = new XQueryWithXQDocLexer();

        lexer.start("Q{Hello World}", 2, 14, 0x00000000|26);
        matchToken(lexer, "Hello World", 26,  2, 13, XQueryTokenType.STRING_LITERAL_CONTENTS);
        matchToken(lexer, "}",           26, 13, 14, XQueryTokenType.BRACED_URI_LITERAL_END);
        matchToken(lexer, "",             0, 14, 14, null);
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public void testXQDocStateRestore() {
        Lexer lexer = new XQueryWithXQDocLexer();

        lexer.start("(:~\n@xqdoc comment:)", 5, 20, 0x70000000|2);
        matchToken(lexer, "xqdoc",    0x70000000|2,  5, 10, XQDocTokenType.TAG);
        matchToken(lexer, " comment", 0x70000000|2, 10, 18, XQDocTokenType.CONTENTS);
        matchToken(lexer, ":)",       0x00000000|4, 18, 20, XQueryTokenType.COMMENT_END_TAG);
        matchToken(lexer, "",         0x00000000|0, 20, 20, null);
    }
}
