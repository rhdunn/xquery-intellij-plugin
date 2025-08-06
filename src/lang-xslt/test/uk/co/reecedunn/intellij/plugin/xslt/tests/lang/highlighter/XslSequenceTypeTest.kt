// Copyright (C) 2016-2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xslt.tests.lang.highlighter

import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiFile
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.lang.annotation.AnnotationTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parse
import uk.co.reecedunn.intellij.plugin.core.tests.lang.requiresIFileElementTypeParseContents
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresPsiFileGetChildren
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xslt.ast.schema.XsltSchemaType
import uk.co.reecedunn.intellij.plugin.xslt.lang.SequenceType
import uk.co.reecedunn.intellij.plugin.xslt.lang.highlighter.SchemaTypeAnnotator
import uk.co.reecedunn.intellij.plugin.xslt.schema.XsltSchemaTypes

@Suppress("Reformat", "RedundantVisibilityModifier")
@DisplayName("IntelliJ - Custom Language Support - Syntax Highlighting - SequenceType Schema Type Annotator")
class XslSequenceTypeTest : IdeaPlatformTestCase(), LanguageParserTestCase<PsiFile>, AnnotationTestCase {
    override val pluginId: PluginId = PluginId.getId("XslSequenceTypeTest")
    override val language: Language = SequenceType

    override fun registerServicesAndExtensions() {
        registerPsiFileFactory()
        requiresIFileElementTypeParseContents()
        requiresPsiFileGetChildren()

        SequenceType.ParserDefinition().registerExtension(project)
        SequenceType.FileType.registerFileType()

        XPathParserDefinition().registerExtension(project)
    }

    @Nested
    @DisplayName("xsl:item-type")
    internal inner class ItemTypeTest {
        val annotator = SchemaTypeAnnotator(XsltSchemaTypes.XslItemType)

        @Test
        @DisplayName("XPath 3.1 EBNF (81) ItemType")
        fun itemType() {
            val file = parse<XsltSchemaType>("item()")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (79) SequenceType")
        internal inner class SequenceTypeTest {
            @Test
            @DisplayName("zero or more")
            fun zeroOrMore() {
                val file = parse<XsltSchemaType>("item()*")[0]
                val annotations = annotateTree(file, annotator).prettyPrint()
                assertThat(
                    annotations, `is`(
                        """
                        ERROR (0:7) "SequenceType is not supported for the xsl:item-type schema type."
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("one or more")
            fun oneOrMore() {
                val file = parse<XsltSchemaType>("item()+")[0]
                val annotations = annotateTree(file, annotator).prettyPrint()
                assertThat(
                    annotations, `is`(
                        """
                        ERROR (0:7) "SequenceType is not supported for the xsl:item-type schema type."
                        """.trimIndent()
                    )
                )
            }

            @Test
            @DisplayName("zero or one")
            fun zeroOrOne() {
                val file = parse<XsltSchemaType>("item()?")[0]
                val annotations = annotateTree(file, annotator).prettyPrint()
                assertThat(
                    annotations, `is`(
                        """
                        ERROR (0:7) "SequenceType is not supported for the xsl:item-type schema type."
                        """.trimIndent()
                    )
                )
            }
        }
    }

    @Nested
    @DisplayName("xsl:sequence-type")
    internal inner class SequenceTypeTest {
        val annotator = SchemaTypeAnnotator(XsltSchemaTypes.XslSequenceType)

        @Test
        @DisplayName("XPath 3.1 EBNF (81) ItemType")
        fun itemType() {
            val file = parse<XsltSchemaType>("item()")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (79) SequenceType")
        internal inner class SequenceTypeTest {
            @Test
            @DisplayName("zero or more")
            fun zeroOrMore() {
                val file = parse<XsltSchemaType>("item()*")[0]
                val annotations = annotateTree(file, annotator).prettyPrint()
                assertThat(annotations, `is`(""))
            }

            @Test
            @DisplayName("one or more")
            fun oneOrMore() {
                val file = parse<XsltSchemaType>("item()+")[0]
                val annotations = annotateTree(file, annotator).prettyPrint()
                assertThat(annotations, `is`(""))
            }

            @Test
            @DisplayName("zero or one")
            fun zeroOrOne() {
                val file = parse<XsltSchemaType>("item()?")[0]
                val annotations = annotateTree(file, annotator).prettyPrint()
                assertThat(annotations, `is`(""))
            }
        }
    }
}
