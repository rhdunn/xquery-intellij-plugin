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
import uk.co.reecedunn.intellij.plugin.xslt.intellij.lang.EQNamesOrHashedKeywords

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("Reformat")
@DisplayName("XSLT 3.0 - Schema Types - EQNames or hashed keywords")
private class XslEQNamesOrHashedKeywordsTest :
    ParserTestCase(EQNamesOrHashedKeywords.ParserDefinition(), XPathParserDefinition()) {

    fun parseResource(resource: String): XsltSchemaType {
        val file = ResourceVirtualFile.create(this::class.java.classLoader, resource)
        return file.toPsiFile(myProject) as XsltSchemaType
    }

    fun loadResource(resource: String): String? {
        return ResourceVirtualFile.create(this::class.java.classLoader, resource).decode()
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (117) URIQualifiedName")
    inner class URIQualifiedName {
        @Test
        @DisplayName("one")
        fun one() {
            val expected = loadResource("tests/parser/schema-type/eqnames/URIQualifiedName.txt")
            val actual = parseResource("tests/parser/schema-type/eqnames/URIQualifiedName.input")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/schema-type/eqnames/URIQualifiedName_List.txt")
            val actual = parseResource("tests/parser/schema-type/eqnames/URIQualifiedName_List.input")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (122) QName")
    inner class QName {
        @Test
        @DisplayName("one")
        fun one() {
            val expected = loadResource("tests/parser/schema-type/eqnames/QName.txt")
            val actual = parseResource("tests/parser/schema-type/eqnames/QName.input")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/schema-type/eqnames/QName_List.txt")
            val actual = parseResource("tests/parser/schema-type/eqnames/QName_List.input")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (123) NCName")
    inner class NCName {
        @Test
        @DisplayName("one")
        fun one() {
            val expected = loadResource("tests/parser/schema-type/eqnames/NCName.txt")
            val actual = parseResource("tests/parser/schema-type/eqnames/NCName.input")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/schema-type/eqnames/NCName_List.txt")
            val actual = parseResource("tests/parser/schema-type/eqnames/NCName_List.input")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("HashedKeyword")
    internal inner class HashedKeyword {
        @Test
        @DisplayName("#all [xsl:accumulator-names, xsl:modes, xsl:prefix-list-or-all]")
        fun all() {
            val expected = loadResource("tests/parser/schema-type/hashed-keyword/All.txt")
            val actual = parseResource("tests/parser/schema-type/hashed-keyword/All.input")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("#default [xsl:modes]")
        fun default() {
            val expected = loadResource("tests/parser/schema-type/hashed-keyword/Default.txt")
            val actual = parseResource("tests/parser/schema-type/hashed-keyword/Default.input")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
    internal inner class Comment {
        @Test
        @DisplayName("comment")
        fun comment() {
            val expected = loadResource("tests/parser/schema-type/comments/Comment.txt")
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
            val expected = loadResource("tests/parser/schema-type/comments/Comment_UnexpectedCommentEndTag.txt")
            val actual = parseResource("tests/parser/schema-type/comments/Comment_UnexpectedCommentEndTag.input")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }
}
