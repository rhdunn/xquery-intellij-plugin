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
import static org.junit.jupiter.api.Assertions.assertThrows;

public class XQDocLexerTest extends LexerTestCase {
    // region Lexer :: Invalid State

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public void testInvalidState() {
        Lexer lexer = new XQDocLexer();

        AssertionError e = assertThrows(AssertionError.class, () -> lexer.start("123", 0, 3, -1));
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
    // region xqDoc :: CommentContents + Comment

    public void testCommentContents() {
        Lexer lexer = new XQDocLexer();

        lexer.start("Lorem ipsum dolor.");
        matchToken(lexer, "Lorem ipsum dolor.", 0,  0, 18, XQDocTokenType.COMMENT_CONTENTS);
        matchToken(lexer, "",                   0, 18, 18, null);
    }

    // endregion
    // region xqDoc :: Contents + XQDocComment

    public void testContents() {
        Lexer lexer = new XQDocLexer();

        lexer.start("~Lorem ipsum dolor.");
        matchToken(lexer, "~",                  0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "Lorem ipsum dolor.", 1,  1, 19, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",                   1, 19, 19, null);
    }

    // endregion
    // region xqDoc :: DirAttrConstructor + DirAttributeValue

    public void testDirAttrConstructor_Quot() {
        Lexer lexer = new XQDocLexer();

        lexer.start("~one <two three = \"four\" />");
        matchToken(lexer, "~",     0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "one ",  1,  1,  5, XQDocTokenType.CONTENTS);
        matchToken(lexer, "<",     1,  5,  6, XQDocTokenType.OPEN_XML_TAG);
        matchToken(lexer, "two",   3,  6,  9, XQDocTokenType.XML_TAG);
        matchToken(lexer, " ",     3,  9, 10, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, "three", 3, 10, 15, XQDocTokenType.XML_TAG);
        matchToken(lexer, " ",     3, 15, 16, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, "=",     3, 16, 17, XQDocTokenType.XML_EQUAL);
        matchToken(lexer, " ",     3, 17, 18, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, "\"",    3, 18, 19, XQDocTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "four",  6, 19, 23, XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "\"",    6, 23, 24, XQDocTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, " ",     3, 24, 25, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, "/>",    3, 25, 27, XQDocTokenType.SELF_CLOSING_XML_TAG);
        matchToken(lexer, "",      1, 27, 27, null);

        lexer.start("~one <two three = \"four");
        matchToken(lexer, "~",     0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "one ",  1,  1,  5, XQDocTokenType.CONTENTS);
        matchToken(lexer, "<",     1,  5,  6, XQDocTokenType.OPEN_XML_TAG);
        matchToken(lexer, "two",   3,  6,  9, XQDocTokenType.XML_TAG);
        matchToken(lexer, " ",     3,  9, 10, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, "three", 3, 10, 15, XQDocTokenType.XML_TAG);
        matchToken(lexer, " ",     3, 15, 16, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, "=",     3, 16, 17, XQDocTokenType.XML_EQUAL);
        matchToken(lexer, " ",     3, 17, 18, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, "\"",    3, 18, 19, XQDocTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "four",  6, 19, 23, XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "",      6, 23, 23, null);
    }

    public void testDirAttrConstructor_Apos() {
        Lexer lexer = new XQDocLexer();

        lexer.start("~one <two three = 'four' />");
        matchToken(lexer, "~",     0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "one ",  1,  1,  5, XQDocTokenType.CONTENTS);
        matchToken(lexer, "<",     1,  5,  6, XQDocTokenType.OPEN_XML_TAG);
        matchToken(lexer, "two",   3,  6,  9, XQDocTokenType.XML_TAG);
        matchToken(lexer, " ",     3,  9, 10, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, "three", 3, 10, 15, XQDocTokenType.XML_TAG);
        matchToken(lexer, " ",     3, 15, 16, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, "=",     3, 16, 17, XQDocTokenType.XML_EQUAL);
        matchToken(lexer, " ",     3, 17, 18, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, "'",     3, 18, 19, XQDocTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "four",  7, 19, 23, XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "'",     7, 23, 24, XQDocTokenType.XML_ATTRIBUTE_VALUE_END);
        matchToken(lexer, " ",     3, 24, 25, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, "/>",    3, 25, 27, XQDocTokenType.SELF_CLOSING_XML_TAG);
        matchToken(lexer, "",      1, 27, 27, null);

        lexer.start("~one <two three = 'four");
        matchToken(lexer, "~",     0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "one ",  1,  1,  5, XQDocTokenType.CONTENTS);
        matchToken(lexer, "<",     1,  5,  6, XQDocTokenType.OPEN_XML_TAG);
        matchToken(lexer, "two",   3,  6,  9, XQDocTokenType.XML_TAG);
        matchToken(lexer, " ",     3,  9, 10, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, "three", 3, 10, 15, XQDocTokenType.XML_TAG);
        matchToken(lexer, " ",     3, 15, 16, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, "=",     3, 16, 17, XQDocTokenType.XML_EQUAL);
        matchToken(lexer, " ",     3, 17, 18, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, "'",     3, 18, 19, XQDocTokenType.XML_ATTRIBUTE_VALUE_START);
        matchToken(lexer, "four",  7, 19, 23, XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS);
        matchToken(lexer, "",      7, 23, 23, null);
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

        lexer.start("~one <two#>three</two#> four");
        matchToken(lexer, "~",     0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "one ",  1,  1,  5, XQDocTokenType.CONTENTS);
        matchToken(lexer, "<",     1,  5,  6, XQDocTokenType.OPEN_XML_TAG);
        matchToken(lexer, "two",   3,  6,  9, XQDocTokenType.XML_TAG);
        matchToken(lexer, "#",     3,  9, 10, XQDocTokenType.INVALID);
        matchToken(lexer, ">",     3, 10, 11, XQDocTokenType.END_XML_TAG);
        matchToken(lexer, "three", 4, 11, 16, XQDocTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "</",    4, 16, 18, XQDocTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "two",   5, 18, 21, XQDocTokenType.XML_TAG);
        matchToken(lexer, "#",     5, 21, 22, XQDocTokenType.INVALID);
        matchToken(lexer, ">",     5, 22, 23, XQDocTokenType.END_XML_TAG);
        matchToken(lexer, " four", 1, 23, 28, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",      1, 28, 28, null);
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

        lexer.start("~a <b#/> c");
        matchToken(lexer, "~",  0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "a ", 1,  1,  3, XQDocTokenType.CONTENTS);
        matchToken(lexer, "<",  1,  3,  4, XQDocTokenType.OPEN_XML_TAG);
        matchToken(lexer, "b",  3,  4,  5, XQDocTokenType.XML_TAG);
        matchToken(lexer, "#",  3,  5,  6, XQDocTokenType.INVALID);
        matchToken(lexer, "/>", 3,  6,  8, XQDocTokenType.SELF_CLOSING_XML_TAG);
        matchToken(lexer, " c", 1,  8, 10, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",   1, 10, 10, null);
    }

    public void testDirElemConstructor_Nested() {
        Lexer lexer = new XQDocLexer();

        lexer.start("~a<b>c<d>e</d>f</b>g");
        matchToken(lexer, "~",  0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "a",  1,  1,  2, XQDocTokenType.CONTENTS);
        matchToken(lexer, "<",  1,  2,  3, XQDocTokenType.OPEN_XML_TAG);
        matchToken(lexer, "b",  3,  3,  4, XQDocTokenType.XML_TAG);
        matchToken(lexer, ">",  3,  4,  5, XQDocTokenType.END_XML_TAG);
        matchToken(lexer, "c",  4,  5,  6, XQDocTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "<",  4,  6,  7, XQDocTokenType.OPEN_XML_TAG);
        matchToken(lexer, "d",  3,  7,  8, XQDocTokenType.XML_TAG);
        matchToken(lexer, ">",  3,  8,  9, XQDocTokenType.END_XML_TAG);
        matchToken(lexer, "e",  4,  9, 10, XQDocTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "</", 4, 10, 12, XQDocTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "d",  5, 12, 13, XQDocTokenType.XML_TAG);
        matchToken(lexer, ">",  5, 13, 14, XQDocTokenType.END_XML_TAG);
        matchToken(lexer, "f",  4, 14, 15, XQDocTokenType.XML_ELEMENT_CONTENTS);
        matchToken(lexer, "</", 4, 15, 17, XQDocTokenType.CLOSE_XML_TAG);
        matchToken(lexer, "b",  5, 17, 18, XQDocTokenType.XML_TAG);
        matchToken(lexer, ">",  5, 18, 19, XQDocTokenType.END_XML_TAG);
        matchToken(lexer, "g",  1, 19, 20, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",   1, 20, 20, null);
    }

    // endregion
    // region xqDoc :: TaggedContents

    public void testTaggedContents() {
        Lexer lexer = new XQDocLexer();

        lexer.start("~Lorem\n@ipsum dolor.");
        matchToken(lexer, "~",       0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "Lorem",   1,  1,  6, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\n",      8,  6,  7, XQDocTokenType.TRIM);
        matchToken(lexer, "@",       8,  7,  8, XQDocTokenType.TAG_MARKER);
        matchToken(lexer, "ipsum",   2,  8, 13, XQDocTokenType.TAG);
        matchToken(lexer, " dolor.", 2, 13, 20, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",        1, 20, 20, null);

        lexer.start("~Lorem\n@IPSUM dolor.");
        matchToken(lexer, "~",       0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "Lorem",   1,  1,  6, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\n",      8,  6,  7, XQDocTokenType.TRIM);
        matchToken(lexer, "@",       8,  7,  8, XQDocTokenType.TAG_MARKER);
        matchToken(lexer, "IPSUM",   2,  8, 13, XQDocTokenType.TAG);
        matchToken(lexer, " dolor.", 2, 13, 20, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",        1, 20, 20, null);

        lexer.start("~Lorem\n@12345 dolor.");
        matchToken(lexer, "~",       0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "Lorem",   1,  1,  6, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\n",      8,  6,  7, XQDocTokenType.TRIM);
        matchToken(lexer, "@",       8,  7,  8, XQDocTokenType.TAG_MARKER);
        matchToken(lexer, "12345",   2,  8, 13, XQDocTokenType.TAG);
        matchToken(lexer, " dolor.", 2, 13, 20, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",        1, 20, 20, null);

        lexer.start("~Lorem\n@# dolor.");
        matchToken(lexer, "~",        0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "Lorem",    1,  1,  6, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\n",       8,  6,  7, XQDocTokenType.TRIM);
        matchToken(lexer, "@",        8,  7,  8, XQDocTokenType.TAG_MARKER);
        matchToken(lexer, "# dolor.", 2,  8, 16, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",         1, 16, 16, null);

        lexer.start("~@lorem ipsum.");
        matchToken(lexer, "~",       0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "@",       8,  1,  2, XQDocTokenType.TAG_MARKER);
        matchToken(lexer, "lorem",   2,  2,  7, XQDocTokenType.TAG);
        matchToken(lexer, " ipsum.", 2,  7, 14, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",        1, 14, 14, null);
    }

    public void testTaggedContents_AtSignInContents() {
        Lexer lexer = new XQDocLexer();

        lexer.start("~Lorem\n@ipsum ab@cd.");
        matchToken(lexer, "~",       0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "Lorem",   1,  1,  6, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\n",      8,  6,  7, XQDocTokenType.TRIM);
        matchToken(lexer, "@",       8,  7,  8, XQDocTokenType.TAG_MARKER);
        matchToken(lexer, "ipsum",   2,  8, 13, XQDocTokenType.TAG);
        matchToken(lexer, " ab@cd.", 2, 13, 20, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",        1, 20, 20, null);
    }

    // endregion
    // region xqDoc :: TaggedContents :: @author

    public void testTaggedContents_Author() {
        Lexer lexer = new XQDocLexer();

        lexer.start("~\n@author John Doe");
        matchToken(lexer, "~",         0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "\n",        8,  1,  2, XQDocTokenType.TRIM);
        matchToken(lexer, "@",         8,  2,  3, XQDocTokenType.TAG_MARKER);
        matchToken(lexer, "author",    2,  3,  9, XQDocTokenType.T_AUTHOR);
        matchToken(lexer, " John Doe", 2,  9, 18, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",          1, 18, 18, null);
    }

    // endregion
    // region xqDoc :: TaggedContents :: @see

    public void testTaggedContents_See() {
        Lexer lexer = new XQDocLexer();

        lexer.start("~\n@see http://www.example.com");
        matchToken(lexer, "~",                       0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "\n",                      8,  1,  2, XQDocTokenType.TRIM);
        matchToken(lexer, "@",                       8,  2,  3, XQDocTokenType.TAG_MARKER);
        matchToken(lexer, "see",                     2,  3,  6, XQDocTokenType.T_SEE);
        matchToken(lexer, " http://www.example.com", 2,  6, 29, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",                        1, 29, 29, null);
    }

    // endregion
    // region xqDoc :: TaggedContents :: @since

    public void testTaggedContents_Since() {
        Lexer lexer = new XQDocLexer();

        lexer.start("~\n@since 1.2");
        matchToken(lexer, "~",       0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "\n",      8,  1,  2, XQDocTokenType.TRIM);
        matchToken(lexer, "@",       8,  2,  3, XQDocTokenType.TAG_MARKER);
        matchToken(lexer, "since",   2,  3,  8, XQDocTokenType.T_SINCE);
        matchToken(lexer, " 1.2",    2,  8, 12, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",        1, 12, 12, null);
    }

    // endregion
    // region xqDoc :: TaggedContents :: @version

    public void testTaggedContents_Version() {
        Lexer lexer = new XQDocLexer();

        lexer.start("~\n@version 1.2");
        matchToken(lexer, "~",         0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "\n",        8,  1,  2, XQDocTokenType.TRIM);
        matchToken(lexer, "@",         8,  2,  3, XQDocTokenType.TAG_MARKER);
        matchToken(lexer, "version",   2,  3, 10, XQDocTokenType.T_VERSION);
        matchToken(lexer, " 1.2",      2, 10, 14, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",          1, 14, 14, null);
    }

    // endregion
    // region xqDoc :: Trim

    public void testTrim_Linux() {
        Lexer lexer = new XQDocLexer();

        lexer.start("~a\nb\nc");
        matchToken(lexer, "~",  0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "a",  1, 1, 2, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\n", 8, 2, 3, XQDocTokenType.TRIM);
        matchToken(lexer, "b",  1, 3, 4, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\n", 8, 4, 5, XQDocTokenType.TRIM);
        matchToken(lexer, "c",  1, 5, 6, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",   1, 6, 6, null);

        lexer.start("~a\n \tb\n\t c");
        matchToken(lexer, "~",     0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "a",     1,  1,  2, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\n \t", 8,  2,  5, XQDocTokenType.TRIM);
        matchToken(lexer, "b",     1,  5,  6, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\n\t ", 8,  6,  9, XQDocTokenType.TRIM);
        matchToken(lexer, "c",     1,  9, 10, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",      1, 10, 10, null);

        lexer.start("~\n\n");
        matchToken(lexer, "~",  0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "\n", 8, 1, 2, XQDocTokenType.TRIM);
        matchToken(lexer, "\n", 8, 2, 3, XQDocTokenType.TRIM);
        matchToken(lexer, "",   8, 3, 3, null);
    }

    public void testTrim_Mac() {
        Lexer lexer = new XQDocLexer();

        // The xqDoc grammar does not support Mac line endings ('\r'), but XQuery/XML
        // line ending normalisation rules do.

        lexer.start("~a\rb\rc");
        matchToken(lexer, "~",  0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "a",  1, 1, 2, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\r", 8, 2, 3, XQDocTokenType.TRIM);
        matchToken(lexer, "b",  1, 3, 4, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\r", 8, 4, 5, XQDocTokenType.TRIM);
        matchToken(lexer, "c",  1, 5, 6, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",   1, 6, 6, null);

        lexer.start("~a\r \tb\r\t c");
        matchToken(lexer, "~",     0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "a",     1,  1,  2, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\r \t", 8,  2,  5, XQDocTokenType.TRIM);
        matchToken(lexer, "b",     1,  5,  6, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\r\t ", 8,  6,  9, XQDocTokenType.TRIM);
        matchToken(lexer, "c",     1,  9, 10, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",      1, 10, 10, null);

        lexer.start("~\r\r");
        matchToken(lexer, "~",  0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "\r", 8, 1, 2, XQDocTokenType.TRIM);
        matchToken(lexer, "\r", 8, 2, 3, XQDocTokenType.TRIM);
        matchToken(lexer, "",   8, 3, 3, null);
    }

    public void testTrim_Windows() {
        Lexer lexer = new XQDocLexer();

        // The xqDoc grammar does not support Windows line endings ('\r\n'), but XQuery/XML
        // line ending normalisation rules do.

        lexer.start("~a\r\nb\r\nc");
        matchToken(lexer, "~",    0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "a",    1, 1, 2, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\r\n", 8, 2, 4, XQDocTokenType.TRIM);
        matchToken(lexer, "b",    1, 4, 5, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\r\n", 8, 5, 7, XQDocTokenType.TRIM);
        matchToken(lexer, "c",    1, 7, 8, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",     1, 8, 8, null);

        lexer.start("~a\r\n \tb\r\n\t c");
        matchToken(lexer, "~",       0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "a",       1,  1,  2, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\r\n \t", 8,  2,  6, XQDocTokenType.TRIM);
        matchToken(lexer, "b",       1,  6,  7, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\r\n\t ", 8,  7, 11, XQDocTokenType.TRIM);
        matchToken(lexer, "c",       1, 11, 12, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",        1, 12, 12, null);

        lexer.start("~\r\n\r\n");
        matchToken(lexer, "~",    0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "\r\n", 8, 1, 3, XQDocTokenType.TRIM);
        matchToken(lexer, "\r\n", 8, 3, 5, XQDocTokenType.TRIM);
        matchToken(lexer, "",     8, 5, 5, null);
    }

    public void testTrim_WhitespaceAfterTrim() {
        Lexer lexer = new XQDocLexer();

        // This is different to the xqDoc grammar, but is necessary to support treating
        // '@' characters within the line as part of the Contents token. The xqDoc
        // processor collates these in the parser phase, but the syntax highlighter
        // needs to highlight those as comment, not document tag, tokens.

        lexer.start("~\n : \t@lorem ipsum.");
        matchToken(lexer, "~",       0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "\n :",    8,  1,  4, XQDocTokenType.TRIM);
        matchToken(lexer, " \t",     8,  4,  6, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, "@",       8,  6,  7, XQDocTokenType.TAG_MARKER);
        matchToken(lexer, "lorem",   2,  7, 12, XQDocTokenType.TAG);
        matchToken(lexer, " ipsum.", 2, 12, 19, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",        1, 19, 19, null);

        lexer.start("~\n :\t @lorem ipsum.");
        matchToken(lexer, "~",       0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "\n :",    8,  1,  4, XQDocTokenType.TRIM);
        matchToken(lexer, "\t ",     8,  4,  6, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, "@",       8,  6,  7, XQDocTokenType.TAG_MARKER);
        matchToken(lexer, "lorem",   2,  7, 12, XQDocTokenType.TAG);
        matchToken(lexer, " ipsum.", 2, 12, 19, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",        1, 19, 19, null);

        lexer.start("~\n : @lorem ipsum\n : @dolor sed emit");
        matchToken(lexer, "~",         0,  0,  1, XQDocTokenType.XQDOC_COMMENT_MARKER);
        matchToken(lexer, "\n :",      8,  1,  4, XQDocTokenType.TRIM);
        matchToken(lexer, " ",         8,  4,  5, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, "@",         8,  5,  6, XQDocTokenType.TAG_MARKER);
        matchToken(lexer, "lorem",     2,  6, 11, XQDocTokenType.TAG);
        matchToken(lexer, " ipsum",    2, 11, 17, XQDocTokenType.CONTENTS);
        matchToken(lexer, "\n :",      8, 17, 20, XQDocTokenType.TRIM);
        matchToken(lexer, " ",         8, 20, 21, XQDocTokenType.WHITE_SPACE);
        matchToken(lexer, "@",         8, 21, 22, XQDocTokenType.TAG_MARKER);
        matchToken(lexer, "dolor",     2, 22, 27, XQDocTokenType.TAG);
        matchToken(lexer, " sed emit", 2, 27, 36, XQDocTokenType.CONTENTS);
        matchToken(lexer, "",          1, 36, 36, null);
    }

    // endregion
}
