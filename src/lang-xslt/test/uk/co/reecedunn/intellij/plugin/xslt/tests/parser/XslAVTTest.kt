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
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.requiresIFileElementTypeParseContents
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresPsiFileGetChildren
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.vfs.requiresVirtualFileGetCharset
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFileSystem
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xslt.ast.schema.XsltSchemaType
import uk.co.reecedunn.intellij.plugin.xslt.lang.ValueTemplate

@Suppress("Reformat")
@DisplayName("XSLT 3.0 - Schema Types - xsl:avt")
class XslAVTTest : IdeaPlatformTestCase(), LanguageParserTestCase<PsiFile> {
    override val pluginId: PluginId = PluginId.getId("XslAVTTest")
    override val language: Language = ValueTemplate

    override fun registerServicesAndExtensions() {
        registerPsiFileFactory()
        requiresIFileElementTypeParseContents()
        requiresVirtualFileGetCharset()
        requiresPsiFileGetChildren()

        ValueTemplate.ParserDefinition().registerExtension(project)
        ValueTemplate.FileType.registerFileType()

        XPathParserDefinition().registerExtension(project)
    }

    private val res = ResourceVirtualFileSystem(this::class.java.classLoader)

    fun parseResource(resource: String): XsltSchemaType = res.toPsiFile(resource, project)

    fun loadResource(resource: String): String? = res.findFileByPath(resource)!!.decode()

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XSLT EBNF (1) ValueTemplate")
    inner class ValueTemplateTest {
        @Test
        @DisplayName("escaped characters")
        fun escapedCharacters() {
            val expected = loadResource("tests/parser/schema-type/avt/AttributeValueTemplate_EscapedChar.txt")
            val actual = parseResource("tests/parser/schema-type/avt/AttributeValueTemplate_EscapedChar.input")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("unpaired right brace")
        fun unpairedRightBrace() {
            val expected = loadResource("tests/parser/schema-type/avt/AttributeValueTemplate_UnpairedRightBrace.txt")
            val actual = parseResource("tests/parser/schema-type/avt/AttributeValueTemplate_UnpairedRightBrace.input")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin XSLT EBNF (2) ValueContentChar")
    fun valueContentChar() {
        val expected = loadResource("tests/parser/schema-type/avt/AttrContentChar.txt")
        val actual = parseResource("tests/parser/schema-type/avt/AttrContentChar.input")
        assertThat(actual.toPsiTreeString(), `is`(expected))
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (5) EnclosedExpr")
    inner class EnclosedExpr {
        @Test
        @DisplayName("after text")
        fun afterText() {
            val expected = loadResource("tests/parser/schema-type/avt/EnclosedExpr.txt")
            val actual = parseResource("tests/parser/schema-type/avt/EnclosedExpr.input")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("EnclosedExpr only")
        fun enclosedExprOnly() {
            val expected = loadResource("tests/parser/schema-type/avt/EnclosedExpr_Only.txt")
            val actual = parseResource("tests/parser/schema-type/avt/EnclosedExpr_Only.input")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }
}
