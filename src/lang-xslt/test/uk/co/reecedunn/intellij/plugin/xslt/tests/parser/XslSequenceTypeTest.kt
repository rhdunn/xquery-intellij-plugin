// Copyright (C) 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xslt.tests.parser

import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiFile
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.psi.toPsiTreeString
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.parser.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFileSystem
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xslt.ast.schema.XsltSchemaType
import uk.co.reecedunn.intellij.plugin.xslt.lang.SequenceType

@Suppress("Reformat", "RedundantVisibilityModifier")
@DisplayName("XSLT 3.0 - Schema Types - xsl:sequence-type")
class XslSequenceTypeTest : IdeaPlatformTestCase(), LanguageParserTestCase<PsiFile> {
    override val pluginId: PluginId = PluginId.getId("XslSequenceTypeTest")
    override val language: Language = SequenceType

    override fun registerServicesAndExtensions() {
        registerPsiFileFactory()
        registerPsiTreeWalker()

        SequenceType.ParserDefinition().registerExtension(project)
        SequenceType.FileType.registerFileType()

        XPathParserDefinition().registerExtension(project)
    }

    private val res = ResourceVirtualFileSystem(this::class.java.classLoader)

    fun parseResource(resource: String): XsltSchemaType = res.toPsiFile(resource, project)

    fun loadResource(resource: String): String? = res.findFileByPath(resource)!!.decode()

    @Test
    @DisplayName("XPath 3.1 EBNF (79) SequenceType")
    fun sequenceType() {
        val expected = loadResource("tests/parser/schema-type/sequence-type/SequenceType.txt")
        val actual = parseResource("tests/parser/schema-type/sequence-type/SequenceType.input")
        assertThat(actual.toPsiTreeString(), `is`(expected))
    }

    @Test
    @DisplayName("XPath 3.1 EBNF (81) ItemType")
    fun itemType() {
        val expected = loadResource("tests/parser/schema-type/sequence-type/ItemType.txt")
        val actual = parseResource("tests/parser/schema-type/sequence-type/ItemType.input")
        assertThat(actual.toPsiTreeString(), `is`(expected))
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
    internal inner class Comment {
        @Test
        @DisplayName("comment")
        fun comment() {
            val expected = loadResource("tests/parser/schema-type/sequence-type/Comment.txt")
            val actual = parseResource("tests/parser/schema-type/comments/Comment.input")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("unclosed comment")
        fun unclosedComment() {
            val expected = loadResource("tests/parser/schema-type/sequence-type/Comment_UnclosedComment.txt")
            val actual = parseResource("tests/parser/schema-type/comments/Comment_UnclosedComment.input")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("comment end tag without comment start tag")
        fun unexpectedCommentEndTag() {
            val expected = loadResource("tests/parser/schema-type/sequence-type/Comment_UnexpectedCommentEndTag.txt")
            val actual = parseResource("tests/parser/schema-type/comments/Comment_UnexpectedCommentEndTag.input")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }
}
