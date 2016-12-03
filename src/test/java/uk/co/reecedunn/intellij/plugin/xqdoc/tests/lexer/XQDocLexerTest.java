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
    // region xqDoc :: DirElemConstructor

    public void testDirElemConstructor() {
        Lexer lexer = new XQDocLexer();

        lexer.start("~one <two >three</two > four");
        matchToken(lexer, "~",     0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "one ",  1,  1,  5, XQDocTokenType.CONTENTS);
        matchToken(lexer, "<",     1,  5,  6, XQDocTokenType.OPEN_XML_TAG);
        matchToken(lexer, "two",   3,  6,  9, XQDocTokenType.XML_TAG);
        matchToken(lexer, " ",     3,  9, 10, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, ">",     3, 10, 11, XQDocTokenType.END_XML_TAG);
        matchToken(lexer, "three", 4, 11, 16, XQDocTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "</",    4, 16, 18, XQDocTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "two",   5, 18, 21, XQDocTokenType.XML_TAG);
        matchToken(lexer, " ",     5, 21, 22, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, ">",     5, 22, 23, XQDocTokenType.END_XML_TAG);
        matchToken(lexer, " four", 1, 23, 28, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",      1, 28, 28, null);

        lexer.start("~one <two >three");
        matchToken(lexer, "~",     0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "one ",  1,  1,  5, XQDocTokenType.CONTENTS);
        matchToken(lexer, "<",     1,  5,  6, XQDocTokenType.OPEN_XML_TAG);
        matchToken(lexer, "two",   3,  6,  9, XQDocTokenType.XML_TAG);
        matchToken(lexer, " ",     3,  9, 10, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, ">",     3, 10, 11, XQDocTokenType.END_XML_TAG);
        matchToken(lexer, "three", 4, 11, 16, XQDocTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "",      4, 16, 16, null);
    }

    public void testDirElemConstructor_SelfClosing() {
        Lexer lexer = new XQDocLexer();

        lexer.start("~a <b /> c");
        matchToken(lexer, "~",  0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "a ", 1,  1,  3, XQDocTokenType.CONTENTS);
        matchToken(lexer, "<",  1,  3,  4, XQDocTokenType.OPEN_XML_TAG);
        matchToken(lexer, "b",  3,  4,  5, XQDocTokenType.XML_TAG);
        matchToken(lexer, " ",  3,  5,  6, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, "/>", 3,  6,  8, XQDocTokenType.SELF_CLOSING_XML_TAG);
        matchToken(lexer, " c", 1,  8, 10, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",   1, 10, 10, null);

        lexer.start("~a <b/");
        matchToken(lexer, "~",  0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "a ", 1, 1, 3, XQDocTokenType.CONTENTS);
        matchToken(lexer, "<",  1, 3, 4, XQDocTokenType.OPEN_XML_TAG);
        matchToken(lexer, "b",  3, 4, 5, XQDocTokenType.XML_TAG);
        matchToken(lexer, "/",  3, 5, 6, XQDocTokenType.INVALID);
        matchToken(lexer, "",   3, 6, 6, null);
    }

    // endregion
    // region xqDoc :: TaggedContents

    public void testTaggedContents() {
        Lexer lexer = new XQDocLexer();

        lexer.start("~Lorem\n@ipsum dolor.");
        matchToken(lexer, "~",       0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "Lorem",   1,  1,  6, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\n",      1,  6,  7, XQDocTokenType.TRIM);
        matchToken(lexer, "@",       1,  7,  8, XQDocTokenType.TAG_MARKER);
        matchToken(lexer, "ipsum",   2,  8, 13, XQDocTokenType.TAG);
        matchToken(lexer, " dolor.", 2, 13, 20, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",        1, 20, 20, null);

        lexer.start("~Lorem\n@IPSUM dolor.");
        matchToken(lexer, "~",       0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "Lorem",   1,  1,  6, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\n",      1,  6,  7, XQDocTokenType.TRIM);
        matchToken(lexer, "@",       1,  7,  8, XQDocTokenType.TAG_MARKER);
        matchToken(lexer, "IPSUM",   2,  8, 13, XQDocTokenType.TAG);
        matchToken(lexer, " dolor.", 2, 13, 20, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",        1, 20, 20, null);

        lexer.start("~Lorem\n@12345 dolor.");
        matchToken(lexer, "~",       0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "Lorem",   1,  1,  6, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\n",      1,  6,  7, XQDocTokenType.TRIM);
        matchToken(lexer, "@",       1,  7,  8, XQDocTokenType.TAG_MARKER);
        matchToken(lexer, "12345",   2,  8, 13, XQDocTokenType.TAG);
        matchToken(lexer, " dolor.", 2, 13, 20, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",        1, 20, 20, null);

        lexer.start("~Lorem\n@# dolor.");
        matchToken(lexer, "~",        0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "Lorem",    1,  1,  6, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\n",       1,  6,  7, XQDocTokenType.TRIM);
        matchToken(lexer, "@",        1,  7,  8, XQDocTokenType.TAG_MARKER);
        matchToken(lexer, "# dolor.", 2,  8, 16, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",         1, 16, 16, null);
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
