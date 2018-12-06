/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.tests.parser

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.io.decode
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPath

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XPath 3.1 - Parser")
private class XPathParserTest : ParserTestCase() {
    fun parseResource(resource: String): XPath {
        val file = ResourceVirtualFile(XPathParserTest::class.java.classLoader, resource)
        return file.toPsiFile(myProject)!!
    }

    fun loadResource(resource: String): String? {
        val file = ResourceVirtualFile(XPathParserTest::class.java.classLoader, resource)
        return file.inputStream?.decode()
    }

    @Nested
    @DisplayName("Parser")
    internal inner class Parser {
        @Test
        @DisplayName("empty buffer")
        fun emptyBuffer() {
            val expected = "XPathImpl[FILE(0:0)]\n"

            assertThat(prettyPrintASTNode(parseText("")), `is`(expected))
        }

        @Test
        @DisplayName("bad characters")
        fun badCharacters() {
            val expected =
                "XPathImpl[FILE(0:3)]\n" +
                "   PsiErrorElementImpl[ERROR_ELEMENT(0:0)]('XPST0003: Missing expression.')\n" +
                "   PsiErrorElementImpl[ERROR_ELEMENT(0:1)]('XPST0003: Unexpected token.')\n" +
                "      LeafPsiElement[BAD_CHARACTER(0:1)]('~')\n" +
                "   LeafPsiElement[BAD_CHARACTER(1:2)]('\uFFFE')\n" +
                "   LeafPsiElement[BAD_CHARACTER(2:3)]('\uFFFF')\n"

            assertThat(prettyPrintASTNode(parseText("~\uFFFE\uFFFF")), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 1.0 EBNF (41) StringLiteral")
    internal inner class StringLiteral_XPath10 {
        @Test
        @DisplayName("string literal")
        fun stringLiteral() {
            val expected = loadResource("tests/parser/xpath-1.0/StringLiteral.txt")
            val actual = parseResource("tests/parser/xpath-1.0/StringLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("unclosed string literal")
        fun unclosedString() {
            val expected = loadResource("tests/parser/xpath-1.0/StringLiteral_UnclosedString.txt")
            val actual = parseResource("tests/parser/xpath-1.0/StringLiteral_UnclosedString.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (42) IntegerLiteral")
    fun integerLiteral() {
        val expected = loadResource("tests/parser/xpath-1.0/IntegerLiteral.txt")
        val actual = parseResource("tests/parser/xpath-1.0/IntegerLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (43) DecimalLiteral")
    fun decimalLiteral() {
        val expected = loadResource("tests/parser/xpath-1.0/DecimalLiteral.txt")
        val actual = parseResource("tests/parser/xpath-1.0/DecimalLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (44) S ; XML 1.0 EBNF (3) S")
    fun s() {
        val expected = loadResource("tests/parser/xpath-1.0/S.txt")
        val actual = parseResource("tests/parser/xpath-1.0/S.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (73) DoubleLiteral")
    internal inner class DoubleLiteral {
        @Test
        @DisplayName("double literal")
        fun doubleLiteral() {
            val expected = loadResource("tests/parser/xpath-2.0/DoubleLiteral.txt")
            val actual = parseResource("tests/parser/xpath-2.0/DoubleLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("double literal with incomplete exponent")
        fun incompleteExponent() {
            val expected = loadResource("tests/parser/xpath-2.0/DoubleLiteral_IncompleteExponent.txt")
            val actual = parseResource("tests/parser/xpath-2.0/DoubleLiteral_IncompleteExponent.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (74) StringLiteral")
    internal inner class StringLiteral_XPath20 {
        @Test
        @DisplayName("XPath 1.0 EBNF (75) EscapeQuot")
        fun escapeQuot() {
            val expected = loadResource("tests/parser/xpath-2.0/StringLiteral_EscapeQuot.txt")
            val actual = parseResource("tests/parser/xpath-2.0/StringLiteral_EscapeQuot.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("XPath 2.0 EBNF (76) EscapeApos")
        fun escapeApos() {
            val expected = loadResource("tests/parser/xpath-2.0/StringLiteral_EscapeApos.txt")
            val actual = parseResource("tests/parser/xpath-2.0/StringLiteral_EscapeApos.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
    internal inner class Comment {
        @Test
        @DisplayName("comment")
        fun comment() {
            val expected = loadResource("tests/parser/xpath-2.0/Comment.txt")
            val actual = parseResource("tests/parser/xpath-2.0/Comment.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("unclosed comment")
        fun unclosedComment() {
            val expected = loadResource("tests/parser/xpath-2.0/Comment_UnclosedComment.txt")
            val actual = parseResource("tests/parser/xpath-2.0/Comment_UnclosedComment.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("comment end tag without comment start tag")
        fun unexpectedCommentEndTag() {
            val expected = loadResource("tests/parser/xpath-2.0/Comment_UnexpectedCommentEndTag.txt")
            val actual = parseResource("tests/parser/xpath-2.0/Comment_UnexpectedCommentEndTag.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }
}
