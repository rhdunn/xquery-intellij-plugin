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
package uk.co.reecedunn.intellij.plugin.xqdoc.tests.lexer;

import com.intellij.lexer.Lexer;
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase;
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocLexer;
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocTokenType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.expectThrows;

public class XQDocLexerTest extends LexerTestCase {
    // region Lexer :: Invalid State

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public void testInvalidState() {
        Lexer lexer = new XQDocLexer();

        AssertionError e = expectThrows(AssertionError.class, () -> lexer.start("123", 0, 3, -1));
        assertThat(e.getMessage(), is("Invalid state: -1"));
    }

    // endregion
    // region Lexer :: Empty Buffer

    public void testEmptyBuffer() {
        Lexer lexer = new XQDocLexer();

        lexer.start("");
        matchToken(lexer, "", 0, 0, 0, null);
    }

    // endregion
    // region xqDoc :: CommentContents

    public void testCommentContents() {
        Lexer lexer = new XQDocLexer();

        lexer.start("Lorem ipsum dolor.");
        matchToken(lexer, "Lorem ipsum dolor.", 0,  0, 18, XQDocTokenType.COMMENT_CONTENTS);
        matchToken(lexer, "",                   0, 18, 18, null);
    }

    // endregion
    // region xqDoc :: Contents

    public void testContents() {
        Lexer lexer = new XQDocLexer();

        lexer.start("~Lorem ipsum dolor.");
        matchToken(lexer, "~",                  0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "Lorem ipsum dolor.", 1,  1, 19, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",                   1, 19, 19, null);
    }

    // endregion
    // region xqDoc :: Trim

    public void testTrim() {
        Lexer lexer = new XQDocLexer();

        lexer.start("~a\nb\nc");
        matchToken(lexer, "~",  0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "a",  1, 1, 2, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\n", 1, 2, 3, XQDocTokenType.TRIM);
        matchToken(lexer, "b",  1, 3, 4, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\n", 1, 4, 5, XQDocTokenType.TRIM);
        matchToken(lexer, "c",  1, 5, 6, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",   1, 6, 6, null);

        lexer.start("~a\n \tb\n\t c");
        matchToken(lexer, "~",     0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "a",     1,  1,  2, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\n \t", 1,  2,  5, XQDocTokenType.TRIM);
        matchToken(lexer, "b",     1,  5,  6, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\n\t ", 1,  6,  9, XQDocTokenType.TRIM);
        matchToken(lexer, "c",     1,  9, 10, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",      1, 10, 10, null);

        lexer.start("~a\n : b\n\t:\tc");
        matchToken(lexer, "~",     0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "a",     1,  1,  2, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\n :",  1,  2,  5, XQDocTokenType.TRIM);
        matchToken(lexer, " b",    1,  5,  7, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\n\t:", 1,  7, 10, XQDocTokenType.TRIM);
        matchToken(lexer, "\tc",   1, 10, 12, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",      1, 12, 12, null);
    }

    // endregion
}
