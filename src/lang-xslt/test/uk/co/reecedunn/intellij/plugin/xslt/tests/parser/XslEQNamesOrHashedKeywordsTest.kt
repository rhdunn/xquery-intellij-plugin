// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xslt.tests.parser

import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.psi.toPsiTreeString
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.requiresIFileElementTypeParseContents
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresPsiFileGetChildren
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresVirtualFileToPsiFile
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.vfs.requiresVirtualFileGetCharset
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFileSystem
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xslt.ast.schema.XsltSchemaType
import uk.co.reecedunn.intellij.plugin.xslt.lang.EQNamesOrHashedKeywords

@Suppress("Reformat", "RedundantVisibilityModifier")
@DisplayName("XSLT 3.0 - Schema Types - EQNames or hashed keywords")
class XslEQNamesOrHashedKeywordsTest : IdeaPlatformTestCase(), LanguageTestCase {
    override val pluginId: PluginId = PluginId.getId("XslEQNamesOrHashedKeywordsTest")
    override val language: Language = EQNamesOrHashedKeywords

    override fun registerServicesAndExtensions() {
        requiresVirtualFileToPsiFile()
        requiresIFileElementTypeParseContents()
        requiresVirtualFileGetCharset()
        requiresPsiFileGetChildren()

        EQNamesOrHashedKeywords.ParserDefinition().registerExtension(project)
        EQNamesOrHashedKeywords.FileType.registerFileType()

        XPathParserDefinition().registerExtension(project)
    }

    private val res = ResourceVirtualFileSystem(this::class.java.classLoader)

    fun parseResource(resource: String): XsltSchemaType = res.toPsiFile(resource, project)

    fun loadResource(resource: String): String? = res.findFileByPath(resource)!!.decode()

    @Nested
    @DisplayName("XPath 3.1 EBNF (117) URIQualifiedName")
    inner class URIQualifiedName {
        @Test
        @DisplayName("one")
        fun one() {
            val expected = loadResource("tests/parser/schema-type/eqnames/URIQualifiedName.txt")
            val actual = parseResource("tests/parser/schema-type/eqnames/URIQualifiedName.input")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/schema-type/eqnames/URIQualifiedName_List.txt")
            val actual = parseResource("tests/parser/schema-type/eqnames/URIQualifiedName_List.input")
            assertThat(actual.toPsiTreeString(), `is`(expected))
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
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/schema-type/eqnames/QName_List.txt")
            val actual = parseResource("tests/parser/schema-type/eqnames/QName_List.input")
            assertThat(actual.toPsiTreeString(), `is`(expected))
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
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/schema-type/eqnames/NCName_List.txt")
            val actual = parseResource("tests/parser/schema-type/eqnames/NCName_List.input")
            assertThat(actual.toPsiTreeString(), `is`(expected))
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
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("#current [xsl:mode]")
        fun current() {
            val expected = loadResource("tests/parser/schema-type/hashed-keyword/Current.txt")
            val actual = parseResource("tests/parser/schema-type/hashed-keyword/Current.input")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("#default [xsl:mode, xsl:modes, xsl:prefix, xsl:prefix-or-default, xsl:prefix-list, xsl:prefix-list-or-all]")
        fun default() {
            val expected = loadResource("tests/parser/schema-type/hashed-keyword/Default.txt")
            val actual = parseResource("tests/parser/schema-type/hashed-keyword/Default.input")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("#unnamed [xsl:default-mode-type, xsl:mode, xsl:modes]")
        fun unnamed() {
            val expected = loadResource("tests/parser/schema-type/hashed-keyword/Unnamed.txt")
            val actual = parseResource("tests/parser/schema-type/hashed-keyword/Unnamed.input")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing NCName")
        fun missingNCName() {
            val expected = loadResource("tests/parser/schema-type/hashed-keyword/MissingNCName.txt")
            val actual = parseResource("tests/parser/schema-type/hashed-keyword/MissingNCName.input")
            assertThat(actual.toPsiTreeString(), `is`(expected))
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
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("unclosed comment")
        fun unclosedComment() {
            val expected = loadResource("tests/parser/schema-type/comments/Comment_UnclosedComment.txt")
            val actual = parseResource("tests/parser/schema-type/comments/Comment_UnclosedComment.input")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("comment end tag without comment start tag")
        fun unexpectedCommentEndTag() {
            val expected = loadResource("tests/parser/schema-type/comments/Comment_UnexpectedCommentEndTag.txt")
            val actual = parseResource("tests/parser/schema-type/comments/Comment_UnexpectedCommentEndTag.input")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }
}
