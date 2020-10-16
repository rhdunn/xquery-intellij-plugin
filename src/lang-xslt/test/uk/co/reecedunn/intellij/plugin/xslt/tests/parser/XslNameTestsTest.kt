/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.tests.parser

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xslt.ast.schema.XsltSchemaType
import uk.co.reecedunn.intellij.plugin.xslt.intellij.lang.NameTests

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("Reformat")
@DisplayName("XSLT 3.0 - Schema Types - xsl:nametests")
private class XslNameTestsTest : ParserTestCase(NameTests.ParserDefinition(), XPathParserDefinition()) {
    fun parseResource(resource: String): XsltSchemaType {
        val file = ResourceVirtualFile.create(this::class.java.classLoader, resource)
        return file.toPsiFile(myProject) as XsltSchemaType
    }

    fun loadResource(resource: String): String? {
        return ResourceVirtualFile.create(this::class.java.classLoader, resource).decode()
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (48) Wildcard")
    inner class Wildcard {
        @Test
        @DisplayName("one")
        fun one() {
            val expected = loadResource("tests/parser/schema-type/nametests/Wildcard.txt")
            val actual = parseResource("tests/parser/schema-type/nametests/Wildcard.input")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/schema-type/nametests/Wildcard_List.txt")
            val actual = parseResource("tests/parser/schema-type/nametests/Wildcard_List.input")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (122) QName")
    inner class QName {
        @Test
        @DisplayName("one")
        fun one() {
            val expected = loadResource("tests/parser/schema-type/nametests/QName.txt")
            val actual = parseResource("tests/parser/schema-type/qname/QName.input")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/schema-type/nametests/QName_List.txt")
            val actual = parseResource("tests/parser/schema-type/qnames/QName_List.input")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (123) NCName")
    inner class NCName {
        @Test
        @DisplayName("one")
        fun one() {
            val expected = loadResource("tests/parser/schema-type/nametests/NCName.txt")
            val actual = parseResource("tests/parser/schema-type/qname/NCName.input")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/schema-type/nametests/NCName_List.txt")
            val actual = parseResource("tests/parser/schema-type/prefixes/NCName_List.input")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
    internal inner class Comment {
        @Test
        @DisplayName("comment")
        fun comment() {
            val expected = loadResource("tests/parser/schema-type/nametests/Comment.txt")
            val actual = parseResource("tests/parser/schema-type/comments/Comment.input")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("unclosed comment")
        fun unclosedComment() {
            val expected = loadResource("tests/parser/schema-type/comments/Comment_UnclosedComment.txt")
            val actual = parseResource("tests/parser/schema-type/comments/Comment_UnclosedComment.input")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("comment end tag without comment start tag")
        fun unexpectedCommentEndTag() {
            val expected = loadResource("tests/parser/schema-type/nametests/Comment_UnexpectedCommentEndTag.txt")
            val actual = parseResource("tests/parser/schema-type/comments/Comment_UnexpectedCommentEndTag.input")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }
}
