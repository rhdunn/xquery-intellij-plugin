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
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xslt.ast.schema.XsltSchemaType
import uk.co.reecedunn.intellij.plugin.xslt.lang.EQNamesOrHashedKeywords
import uk.co.reecedunn.intellij.plugin.xslt.lang.highlighter.SchemaTypeAnnotator
import uk.co.reecedunn.intellij.plugin.xslt.schema.XsltSchemaTypes

@Suppress("Reformat")
@DisplayName("IntelliJ - Custom Language Support - Syntax Highlighting - EQNames-or-hashed-keywords Schema Type Annotator")
class EQNamesOrHashedKeywordsTest : IdeaPlatformTestCase(), LanguageParserTestCase<PsiFile>, AnnotationTestCase {
    override val pluginId: PluginId = PluginId.getId("EQNamesOrHashedKeywordsTest")
    override val language: Language = EQNamesOrHashedKeywords

    override fun registerServicesAndExtensions() {
        registerPsiFileFactory()
        registerPsiTreeWalker()

        EQNamesOrHashedKeywords.ParserDefinition().registerExtension(project)
        EQNamesOrHashedKeywords.FileType.registerFileType()

        XPathParserDefinition().registerExtension(project)
    }

    @Nested
    @DisplayName("xsl:accumulator-names")
    inner class AccumulatorNamesTest {
        val annotator = SchemaTypeAnnotator(XsltSchemaTypes.XslAccumulatorNames)

        @Test
        @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
        fun comment() {
            val file = parse<XsltSchemaType>("lorem (: ipsum :)")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) URIQualifiedName")
        fun uriQualifiedName() {
            val file = parse<XsltSchemaType>("Q{http://www.example.co.uk}one Q{http://www.example.co.uk}two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) QName")
        fun qname() {
            val file = parse<XsltSchemaType>("lorem:one lorem:two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (123) NCName")
        fun ncname() {
            val file = parse<XsltSchemaType>("lorem ipsum")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("hashed keywords")
        fun hashedKeywords() {
            val file = parse<XsltSchemaType>("#all #current #default #unnamed #unknown")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) XML_ATTRIBUTE_VALUE
                    INFORMATION (5:13) XML_ATTRIBUTE_VALUE
                    ERROR (5:13) "Keyword '#current' is not supported for the xsl:accumulator-names schema type."
                    INFORMATION (14:22) XML_ATTRIBUTE_VALUE
                    ERROR (14:22) "Keyword '#default' is not supported for the xsl:accumulator-names schema type."
                    INFORMATION (23:31) XML_ATTRIBUTE_VALUE
                    ERROR (23:31) "Keyword '#unnamed' is not supported for the xsl:accumulator-names schema type."
                    INFORMATION (32:40) XML_ATTRIBUTE_VALUE
                    ERROR (32:40) "Keyword '#unknown' is not supported for the xsl:accumulator-names schema type."
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("xsl:default-mode-type")
    inner class DefaultModeTypeTest {
        val annotator = SchemaTypeAnnotator(XsltSchemaTypes.XslDefaultModeType)

        @Test
        @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
        fun comment() {
            val file = parse<XsltSchemaType>("lorem (: ipsum :)")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) URIQualifiedName")
        fun uriQualifiedName() {
            val file = parse<XsltSchemaType>("Q{http://www.example.co.uk}one Q{http://www.example.co.uk}two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (31:61) "The xsl:default-mode-type schema type only supports a single item."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) QName")
        fun qname() {
            val file = parse<XsltSchemaType>("lorem:one lorem:two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (10:19) "The xsl:default-mode-type schema type only supports a single item."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (123) NCName")
        fun ncname() {
            val file = parse<XsltSchemaType>("lorem ipsum")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (6:11) "The xsl:default-mode-type schema type only supports a single item."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("hashed keywords")
        fun hashedKeywords() {
            val file = parse<XsltSchemaType>("#all #current #default #unnamed #unknown")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) XML_ATTRIBUTE_VALUE
                    ERROR (0:4) "Keyword '#all' is not supported for the xsl:default-mode-type schema type."
                    INFORMATION (5:13) XML_ATTRIBUTE_VALUE
                    ERROR (5:13) "Keyword '#current' is not supported for the xsl:default-mode-type schema type."
                    INFORMATION (14:22) XML_ATTRIBUTE_VALUE
                    ERROR (14:22) "Keyword '#default' is not supported for the xsl:default-mode-type schema type."
                    INFORMATION (23:31) XML_ATTRIBUTE_VALUE
                    INFORMATION (32:40) XML_ATTRIBUTE_VALUE
                    ERROR (32:40) "Keyword '#unknown' is not supported for the xsl:default-mode-type schema type."
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("xsl:EQName")
    inner class EQNameTest {
        val annotator = SchemaTypeAnnotator(XsltSchemaTypes.XslEQName)

        @Test
        @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
        fun comment() {
            val file = parse<XsltSchemaType>("lorem (: ipsum :)")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) URIQualifiedName")
        fun uriQualifiedName() {
            val file = parse<XsltSchemaType>("Q{http://www.example.co.uk}one Q{http://www.example.co.uk}two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (31:61) "The xsl:EQName schema type only supports a single item."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) QName")
        fun qname() {
            val file = parse<XsltSchemaType>("lorem:one lorem:two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (10:19) "The xsl:EQName schema type only supports a single item."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (123) NCName")
        fun ncname() {
            val file = parse<XsltSchemaType>("lorem ipsum")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (6:11) "The xsl:EQName schema type only supports a single item."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("hashed keywords")
        fun hashedKeywords() {
            val file = parse<XsltSchemaType>("#all #current #default #unnamed #unknown")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) XML_ATTRIBUTE_VALUE
                    ERROR (0:4) "Keyword '#all' is not supported for the xsl:EQName schema type."
                    INFORMATION (5:13) XML_ATTRIBUTE_VALUE
                    ERROR (5:13) "Keyword '#current' is not supported for the xsl:EQName schema type."
                    INFORMATION (14:22) XML_ATTRIBUTE_VALUE
                    ERROR (14:22) "Keyword '#default' is not supported for the xsl:EQName schema type."
                    INFORMATION (23:31) XML_ATTRIBUTE_VALUE
                    ERROR (23:31) "Keyword '#unnamed' is not supported for the xsl:EQName schema type."
                    INFORMATION (32:40) XML_ATTRIBUTE_VALUE
                    ERROR (32:40) "Keyword '#unknown' is not supported for the xsl:EQName schema type."
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("xsl:EQName-in-namespace")
    inner class EQNameInNamespaceTest {
        val annotator = SchemaTypeAnnotator(XsltSchemaTypes.XslEQNameInNamespace)

        @Test
        @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
        fun comment() {
            val file = parse<XsltSchemaType>("lorem (: ipsum :)")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) URIQualifiedName")
        fun uriQualifiedName() {
            val file = parse<XsltSchemaType>("Q{http://www.example.co.uk}one Q{http://www.example.co.uk}two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (31:61) "The xsl:EQName-in-namespace schema type only supports a single item."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) QName")
        fun qname() {
            val file = parse<XsltSchemaType>("lorem:one lorem:two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (10:19) "The xsl:EQName-in-namespace schema type only supports a single item."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (123) NCName")
        fun ncname() {
            val file = parse<XsltSchemaType>("lorem ipsum")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (6:11) "The xsl:EQName-in-namespace schema type only supports a single item."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("hashed keywords")
        fun hashedKeywords() {
            val file = parse<XsltSchemaType>("#all #current #default #unnamed #unknown")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) XML_ATTRIBUTE_VALUE
                    ERROR (0:4) "Keyword '#all' is not supported for the xsl:EQName-in-namespace schema type."
                    INFORMATION (5:13) XML_ATTRIBUTE_VALUE
                    ERROR (5:13) "Keyword '#current' is not supported for the xsl:EQName-in-namespace schema type."
                    INFORMATION (14:22) XML_ATTRIBUTE_VALUE
                    ERROR (14:22) "Keyword '#default' is not supported for the xsl:EQName-in-namespace schema type."
                    INFORMATION (23:31) XML_ATTRIBUTE_VALUE
                    ERROR (23:31) "Keyword '#unnamed' is not supported for the xsl:EQName-in-namespace schema type."
                    INFORMATION (32:40) XML_ATTRIBUTE_VALUE
                    ERROR (32:40) "Keyword '#unknown' is not supported for the xsl:EQName-in-namespace schema type."
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("xsl:EQNames")
    inner class EQNamesTest {
        val annotator = SchemaTypeAnnotator(XsltSchemaTypes.XslEQNames)

        @Test
        @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
        fun comment() {
            val file = parse<XsltSchemaType>("lorem (: ipsum :)")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) URIQualifiedName")
        fun uriQualifiedName() {
            val file = parse<XsltSchemaType>("Q{http://www.example.co.uk}one Q{http://www.example.co.uk}two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) QName")
        fun qname() {
            val file = parse<XsltSchemaType>("lorem:one lorem:two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (123) NCName")
        fun ncname() {
            val file = parse<XsltSchemaType>("lorem ipsum")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("hashed keywords")
        fun hashedKeywords() {
            val file = parse<XsltSchemaType>("#all #current #default #unnamed #unknown")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) XML_ATTRIBUTE_VALUE
                    ERROR (0:4) "Keyword '#all' is not supported for the xsl:EQNames schema type."
                    INFORMATION (5:13) XML_ATTRIBUTE_VALUE
                    ERROR (5:13) "Keyword '#current' is not supported for the xsl:EQNames schema type."
                    INFORMATION (14:22) XML_ATTRIBUTE_VALUE
                    ERROR (14:22) "Keyword '#default' is not supported for the xsl:EQNames schema type."
                    INFORMATION (23:31) XML_ATTRIBUTE_VALUE
                    ERROR (23:31) "Keyword '#unnamed' is not supported for the xsl:EQNames schema type."
                    INFORMATION (32:40) XML_ATTRIBUTE_VALUE
                    ERROR (32:40) "Keyword '#unknown' is not supported for the xsl:EQNames schema type."
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("xsl:method")
    inner class MethodTest {
        val annotator = SchemaTypeAnnotator(XsltSchemaTypes.XslMethod)

        @Test
        @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
        fun comment() {
            val file = parse<XsltSchemaType>("lorem (: ipsum :)")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) URIQualifiedName")
        fun uriQualifiedName() {
            val file = parse<XsltSchemaType>("Q{http://www.example.co.uk}one Q{http://www.example.co.uk}two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (0:30) "URIQualifiedName is not supported for the xsl:method schema type."
                    ERROR (31:61) "URIQualifiedName is not supported for the xsl:method schema type."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) QName")
        fun qname() {
            val file = parse<XsltSchemaType>("lorem:one lorem:two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (10:19) "The xsl:method schema type only supports a single item."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (123) NCName")
        fun ncname() {
            val file = parse<XsltSchemaType>("lorem ipsum")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (6:11) "The xsl:method schema type only supports a single item."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("hashed keywords")
        fun hashedKeywords() {
            val file = parse<XsltSchemaType>("#all #current #default #unnamed #unknown")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) XML_ATTRIBUTE_VALUE
                    ERROR (0:4) "Keyword '#all' is not supported for the xsl:method schema type."
                    INFORMATION (5:13) XML_ATTRIBUTE_VALUE
                    ERROR (5:13) "Keyword '#current' is not supported for the xsl:method schema type."
                    INFORMATION (14:22) XML_ATTRIBUTE_VALUE
                    ERROR (14:22) "Keyword '#default' is not supported for the xsl:method schema type."
                    INFORMATION (23:31) XML_ATTRIBUTE_VALUE
                    ERROR (23:31) "Keyword '#unnamed' is not supported for the xsl:method schema type."
                    INFORMATION (32:40) XML_ATTRIBUTE_VALUE
                    ERROR (32:40) "Keyword '#unknown' is not supported for the xsl:method schema type."
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("xsl:mode")
    inner class ModeTest {
        val annotator = SchemaTypeAnnotator(XsltSchemaTypes.XslMode)

        @Test
        @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
        fun comment() {
            val file = parse<XsltSchemaType>("lorem (: ipsum :)")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) URIQualifiedName")
        fun uriQualifiedName() {
            val file = parse<XsltSchemaType>("Q{http://www.example.co.uk}one Q{http://www.example.co.uk}two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (31:61) "The xsl:mode schema type only supports a single item."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) QName")
        fun qname() {
            val file = parse<XsltSchemaType>("lorem:one lorem:two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (10:19) "The xsl:mode schema type only supports a single item."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (123) NCName")
        fun ncname() {
            val file = parse<XsltSchemaType>("lorem ipsum")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (6:11) "The xsl:mode schema type only supports a single item."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("hashed keywords")
        fun hashedKeywords() {
            val file = parse<XsltSchemaType>("#all #current #default #unnamed #unknown")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) XML_ATTRIBUTE_VALUE
                    ERROR (0:4) "Keyword '#all' is not supported for the xsl:mode schema type."
                    INFORMATION (5:13) XML_ATTRIBUTE_VALUE
                    INFORMATION (14:22) XML_ATTRIBUTE_VALUE
                    ERROR (14:22) "The xsl:mode schema type only supports a single item."
                    INFORMATION (23:31) XML_ATTRIBUTE_VALUE
                    ERROR (23:31) "The xsl:mode schema type only supports a single item."
                    INFORMATION (32:40) XML_ATTRIBUTE_VALUE
                    ERROR (32:40) "Keyword '#unknown' is not supported for the xsl:mode schema type."
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("xsl:modes")
    inner class ModesTest {
        val annotator = SchemaTypeAnnotator(XsltSchemaTypes.XslModes)

        @Test
        @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
        fun comment() {
            val file = parse<XsltSchemaType>("lorem (: ipsum :)")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) URIQualifiedName")
        fun uriQualifiedName() {
            val file = parse<XsltSchemaType>("Q{http://www.example.co.uk}one Q{http://www.example.co.uk}two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) QName")
        fun qname() {
            val file = parse<XsltSchemaType>("lorem:one lorem:two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (123) NCName")
        fun ncname() {
            val file = parse<XsltSchemaType>("lorem ipsum")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("hashed keywords")
        fun hashedKeywords() {
            val file = parse<XsltSchemaType>("#all #current #default #unnamed #unknown")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) XML_ATTRIBUTE_VALUE
                    INFORMATION (5:13) XML_ATTRIBUTE_VALUE
                    ERROR (5:13) "Keyword '#current' is not supported for the xsl:modes schema type."
                    INFORMATION (14:22) XML_ATTRIBUTE_VALUE
                    INFORMATION (23:31) XML_ATTRIBUTE_VALUE
                    INFORMATION (32:40) XML_ATTRIBUTE_VALUE
                    ERROR (32:40) "Keyword '#unknown' is not supported for the xsl:modes schema type."
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("xsl:prefix")
    inner class PrefixTest {
        val annotator = SchemaTypeAnnotator(XsltSchemaTypes.XslPrefix)

        @Test
        @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
        fun comment() {
            val file = parse<XsltSchemaType>("lorem (: ipsum :)")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) URIQualifiedName")
        fun uriQualifiedName() {
            val file = parse<XsltSchemaType>("Q{http://www.example.co.uk}one Q{http://www.example.co.uk}two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (0:30) "URIQualifiedName is not supported for the xsl:prefix schema type."
                    ERROR (31:61) "URIQualifiedName is not supported for the xsl:prefix schema type."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) QName")
        fun qname() {
            val file = parse<XsltSchemaType>("lorem:one lorem:two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (0:9) "QName is not supported for the xsl:prefix schema type."
                    ERROR (10:19) "QName is not supported for the xsl:prefix schema type."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (123) NCName")
        fun ncname() {
            val file = parse<XsltSchemaType>("lorem ipsum")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (6:11) "The xsl:prefix schema type only supports a single item."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("hashed keywords")
        fun hashedKeywords() {
            val file = parse<XsltSchemaType>("#all #current #default #unnamed #unknown")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) XML_ATTRIBUTE_VALUE
                    ERROR (0:4) "Keyword '#all' is not supported for the xsl:prefix schema type."
                    INFORMATION (5:13) XML_ATTRIBUTE_VALUE
                    ERROR (5:13) "Keyword '#current' is not supported for the xsl:prefix schema type."
                    INFORMATION (14:22) XML_ATTRIBUTE_VALUE
                    INFORMATION (23:31) XML_ATTRIBUTE_VALUE
                    ERROR (23:31) "Keyword '#unnamed' is not supported for the xsl:prefix schema type."
                    INFORMATION (32:40) XML_ATTRIBUTE_VALUE
                    ERROR (32:40) "Keyword '#unknown' is not supported for the xsl:prefix schema type."
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("xsl:prefixes")
    inner class PrefixesTest {
        val annotator = SchemaTypeAnnotator(XsltSchemaTypes.XslPrefixes)

        @Test
        @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
        fun comment() {
            val file = parse<XsltSchemaType>("lorem (: ipsum :)")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) URIQualifiedName")
        fun uriQualifiedName() {
            val file = parse<XsltSchemaType>("Q{http://www.example.co.uk}one Q{http://www.example.co.uk}two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (0:30) "URIQualifiedName is not supported for the xsl:prefixes schema type."
                    ERROR (31:61) "URIQualifiedName is not supported for the xsl:prefixes schema type."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) QName")
        fun qname() {
            val file = parse<XsltSchemaType>("lorem:one lorem:two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (0:9) "QName is not supported for the xsl:prefixes schema type."
                    ERROR (10:19) "QName is not supported for the xsl:prefixes schema type."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (123) NCName")
        fun ncname() {
            val file = parse<XsltSchemaType>("lorem ipsum")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("hashed keywords")
        fun hashedKeywords() {
            val file = parse<XsltSchemaType>("#all #current #default #unnamed #unknown")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) XML_ATTRIBUTE_VALUE
                    ERROR (0:4) "Keyword '#all' is not supported for the xsl:prefixes schema type."
                    INFORMATION (5:13) XML_ATTRIBUTE_VALUE
                    ERROR (5:13) "Keyword '#current' is not supported for the xsl:prefixes schema type."
                    INFORMATION (14:22) XML_ATTRIBUTE_VALUE
                    ERROR (14:22) "Keyword '#default' is not supported for the xsl:prefixes schema type."
                    INFORMATION (23:31) XML_ATTRIBUTE_VALUE
                    ERROR (23:31) "Keyword '#unnamed' is not supported for the xsl:prefixes schema type."
                    INFORMATION (32:40) XML_ATTRIBUTE_VALUE
                    ERROR (32:40) "Keyword '#unknown' is not supported for the xsl:prefixes schema type."
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("xsl:prefix-list")
    inner class PrefixListTest {
        val annotator = SchemaTypeAnnotator(XsltSchemaTypes.XslPrefixList)

        @Test
        @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
        fun comment() {
            val file = parse<XsltSchemaType>("lorem (: ipsum :)")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) URIQualifiedName")
        fun uriQualifiedName() {
            val file = parse<XsltSchemaType>("Q{http://www.example.co.uk}one Q{http://www.example.co.uk}two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (0:30) "URIQualifiedName is not supported for the xsl:prefix-list schema type."
                    ERROR (31:61) "URIQualifiedName is not supported for the xsl:prefix-list schema type."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) QName")
        fun qname() {
            val file = parse<XsltSchemaType>("lorem:one lorem:two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (0:9) "QName is not supported for the xsl:prefix-list schema type."
                    ERROR (10:19) "QName is not supported for the xsl:prefix-list schema type."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (123) NCName")
        fun ncname() {
            val file = parse<XsltSchemaType>("lorem ipsum")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("hashed keywords")
        fun hashedKeywords() {
            val file = parse<XsltSchemaType>("#all #current #default #unnamed #unknown")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) XML_ATTRIBUTE_VALUE
                    ERROR (0:4) "Keyword '#all' is not supported for the xsl:prefix-list schema type."
                    INFORMATION (5:13) XML_ATTRIBUTE_VALUE
                    ERROR (5:13) "Keyword '#current' is not supported for the xsl:prefix-list schema type."
                    INFORMATION (14:22) XML_ATTRIBUTE_VALUE
                    INFORMATION (23:31) XML_ATTRIBUTE_VALUE
                    ERROR (23:31) "Keyword '#unnamed' is not supported for the xsl:prefix-list schema type."
                    INFORMATION (32:40) XML_ATTRIBUTE_VALUE
                    ERROR (32:40) "Keyword '#unknown' is not supported for the xsl:prefix-list schema type."
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("xsl:prefix-list-or-all")
    inner class PrefixListOrAllTest {
        val annotator = SchemaTypeAnnotator(XsltSchemaTypes.XslPrefixListOrAll)

        @Test
        @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
        fun comment() {
            val file = parse<XsltSchemaType>("lorem (: ipsum :)")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) URIQualifiedName")
        fun uriQualifiedName() {
            val file = parse<XsltSchemaType>("Q{http://www.example.co.uk}one Q{http://www.example.co.uk}two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (0:30) "URIQualifiedName is not supported for the xsl:prefix-list-or-all schema type."
                    ERROR (31:61) "URIQualifiedName is not supported for the xsl:prefix-list-or-all schema type."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) QName")
        fun qname() {
            val file = parse<XsltSchemaType>("lorem:one lorem:two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (0:9) "QName is not supported for the xsl:prefix-list-or-all schema type."
                    ERROR (10:19) "QName is not supported for the xsl:prefix-list-or-all schema type."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (123) NCName")
        fun ncname() {
            val file = parse<XsltSchemaType>("lorem ipsum")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("hashed keywords")
        fun hashedKeywords() {
            val file = parse<XsltSchemaType>("#all #current #default #unnamed #unknown")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) XML_ATTRIBUTE_VALUE
                    INFORMATION (5:13) XML_ATTRIBUTE_VALUE
                    ERROR (5:13) "Keyword '#current' is not supported for the xsl:prefix-list-or-all schema type."
                    INFORMATION (14:22) XML_ATTRIBUTE_VALUE
                    INFORMATION (23:31) XML_ATTRIBUTE_VALUE
                    ERROR (23:31) "Keyword '#unnamed' is not supported for the xsl:prefix-list-or-all schema type."
                    INFORMATION (32:40) XML_ATTRIBUTE_VALUE
                    ERROR (32:40) "Keyword '#unknown' is not supported for the xsl:prefix-list-or-all schema type."
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("xsl:prefix-or-default")
    inner class PrefixOrDefaultTest {
        val annotator = SchemaTypeAnnotator(XsltSchemaTypes.XslPrefixOrDefault)

        @Test
        @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
        fun comment() {
            val file = parse<XsltSchemaType>("lorem (: ipsum :)")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) URIQualifiedName")
        fun uriQualifiedName() {
            val file = parse<XsltSchemaType>("Q{http://www.example.co.uk}one Q{http://www.example.co.uk}two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (0:30) "URIQualifiedName is not supported for the xsl:prefix-or-default schema type."
                    ERROR (31:61) "URIQualifiedName is not supported for the xsl:prefix-or-default schema type."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) QName")
        fun qname() {
            val file = parse<XsltSchemaType>("lorem:one lorem:two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (0:9) "QName is not supported for the xsl:prefix-or-default schema type."
                    ERROR (10:19) "QName is not supported for the xsl:prefix-or-default schema type."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (123) NCName")
        fun ncname() {
            val file = parse<XsltSchemaType>("lorem ipsum")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (6:11) "The xsl:prefix-or-default schema type only supports a single item."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("hashed keywords")
        fun hashedKeywords() {
            val file = parse<XsltSchemaType>("#all #current #default #unnamed #unknown")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) XML_ATTRIBUTE_VALUE
                    ERROR (0:4) "Keyword '#all' is not supported for the xsl:prefix-or-default schema type."
                    INFORMATION (5:13) XML_ATTRIBUTE_VALUE
                    ERROR (5:13) "Keyword '#current' is not supported for the xsl:prefix-or-default schema type."
                    INFORMATION (14:22) XML_ATTRIBUTE_VALUE
                    INFORMATION (23:31) XML_ATTRIBUTE_VALUE
                    ERROR (23:31) "Keyword '#unnamed' is not supported for the xsl:prefix-or-default schema type."
                    INFORMATION (32:40) XML_ATTRIBUTE_VALUE
                    ERROR (32:40) "Keyword '#unknown' is not supported for the xsl:prefix-or-default schema type."
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("xsl:QName")
    inner class QNameTest {
        val annotator = SchemaTypeAnnotator(XsltSchemaTypes.XslQName)

        @Test
        @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
        fun comment() {
            val file = parse<XsltSchemaType>("lorem (: ipsum :)")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) URIQualifiedName")
        fun uriQualifiedName() {
            val file = parse<XsltSchemaType>("Q{http://www.example.co.uk}one Q{http://www.example.co.uk}two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (0:30) "URIQualifiedName is not supported for the xsl:QName schema type."
                    ERROR (31:61) "URIQualifiedName is not supported for the xsl:QName schema type."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) QName")
        fun qname() {
            val file = parse<XsltSchemaType>("lorem:one lorem:two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (10:19) "The xsl:QName schema type only supports a single item."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (123) NCName")
        fun ncname() {
            val file = parse<XsltSchemaType>("lorem ipsum")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (6:11) "The xsl:QName schema type only supports a single item."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("hashed keywords")
        fun hashedKeywords() {
            val file = parse<XsltSchemaType>("#all #current #default #unnamed #unknown")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) XML_ATTRIBUTE_VALUE
                    ERROR (0:4) "Keyword '#all' is not supported for the xsl:QName schema type."
                    INFORMATION (5:13) XML_ATTRIBUTE_VALUE
                    ERROR (5:13) "Keyword '#current' is not supported for the xsl:QName schema type."
                    INFORMATION (14:22) XML_ATTRIBUTE_VALUE
                    ERROR (14:22) "Keyword '#default' is not supported for the xsl:QName schema type."
                    INFORMATION (23:31) XML_ATTRIBUTE_VALUE
                    ERROR (23:31) "Keyword '#unnamed' is not supported for the xsl:QName schema type."
                    INFORMATION (32:40) XML_ATTRIBUTE_VALUE
                    ERROR (32:40) "Keyword '#unknown' is not supported for the xsl:QName schema type."
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("xsl:QNames")
    inner class QNamesTest {
        val annotator = SchemaTypeAnnotator(XsltSchemaTypes.XslQNames)

        @Test
        @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
        fun comment() {
            val file = parse<XsltSchemaType>("lorem (: ipsum :)")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) URIQualifiedName")
        fun uriQualifiedName() {
            val file = parse<XsltSchemaType>("Q{http://www.example.co.uk}one Q{http://www.example.co.uk}two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (0:30) "URIQualifiedName is not supported for the xsl:QNames schema type."
                    ERROR (31:61) "URIQualifiedName is not supported for the xsl:QNames schema type."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) QName")
        fun qname() {
            val file = parse<XsltSchemaType>("lorem:one lorem:two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (123) NCName")
        fun ncname() {
            val file = parse<XsltSchemaType>("lorem ipsum")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("hashed keywords")
        fun hashedKeywords() {
            val file = parse<XsltSchemaType>("#all #current #default #unnamed #unknown")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) XML_ATTRIBUTE_VALUE
                    ERROR (0:4) "Keyword '#all' is not supported for the xsl:QNames schema type."
                    INFORMATION (5:13) XML_ATTRIBUTE_VALUE
                    ERROR (5:13) "Keyword '#current' is not supported for the xsl:QNames schema type."
                    INFORMATION (14:22) XML_ATTRIBUTE_VALUE
                    ERROR (14:22) "Keyword '#default' is not supported for the xsl:QNames schema type."
                    INFORMATION (23:31) XML_ATTRIBUTE_VALUE
                    ERROR (23:31) "Keyword '#unnamed' is not supported for the xsl:QNames schema type."
                    INFORMATION (32:40) XML_ATTRIBUTE_VALUE
                    ERROR (32:40) "Keyword '#unknown' is not supported for the xsl:QNames schema type."
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("xsl:streamability-type")
    inner class StreamabilityTypeTest {
        val annotator = SchemaTypeAnnotator(XsltSchemaTypes.XslStreamabilityType)

        @Test
        @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
        fun comment() {
            val file = parse<XsltSchemaType>("lorem (: ipsum :)")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) URIQualifiedName")
        fun uriQualifiedName() {
            val file = parse<XsltSchemaType>("Q{http://www.example.co.uk}one Q{http://www.example.co.uk}two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (31:61) "The xsl:streamability-type schema type only supports a single item."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) QName")
        fun qname() {
            val file = parse<XsltSchemaType>("lorem:one lorem:two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (10:19) "The xsl:streamability-type schema type only supports a single item."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (123) NCName")
        fun ncname() {
            val file = parse<XsltSchemaType>("lorem ipsum")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (6:11) "The xsl:streamability-type schema type only supports a single item."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("hashed keywords")
        fun hashedKeywords() {
            val file = parse<XsltSchemaType>("#all #current #default #unnamed #unknown")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) XML_ATTRIBUTE_VALUE
                    ERROR (0:4) "Keyword '#all' is not supported for the xsl:streamability-type schema type."
                    INFORMATION (5:13) XML_ATTRIBUTE_VALUE
                    ERROR (5:13) "Keyword '#current' is not supported for the xsl:streamability-type schema type."
                    INFORMATION (14:22) XML_ATTRIBUTE_VALUE
                    ERROR (14:22) "Keyword '#default' is not supported for the xsl:streamability-type schema type."
                    INFORMATION (23:31) XML_ATTRIBUTE_VALUE
                    ERROR (23:31) "Keyword '#unnamed' is not supported for the xsl:streamability-type schema type."
                    INFORMATION (32:40) XML_ATTRIBUTE_VALUE
                    ERROR (32:40) "Keyword '#unknown' is not supported for the xsl:streamability-type schema type."
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("xsl:tokens")
    inner class TokensTest {
        val annotator = SchemaTypeAnnotator(XsltSchemaTypes.XslTokens)

        @Test
        @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
        fun comment() {
            val file = parse<XsltSchemaType>("lorem (: ipsum :)")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) URIQualifiedName")
        fun uriQualifiedName() {
            val file = parse<XsltSchemaType>("Q{http://www.example.co.uk}one Q{http://www.example.co.uk}two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (0:30) "URIQualifiedName is not supported for the xsl:tokens schema type."
                    ERROR (31:61) "URIQualifiedName is not supported for the xsl:tokens schema type."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (122) QName")
        fun qname() {
            val file = parse<XsltSchemaType>("lorem:one lorem:two")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    ERROR (0:9) "QName is not supported for the xsl:tokens schema type."
                    ERROR (10:19) "QName is not supported for the xsl:tokens schema type."
                    """.trimIndent()
                )
            )
        }

        @Test
        @DisplayName("XPath 3.1 EBNF (123) NCName")
        fun ncname() {
            val file = parse<XsltSchemaType>("lorem ipsum")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(annotations, `is`(""))
        }

        @Test
        @DisplayName("hashed keywords")
        fun hashedKeywords() {
            val file = parse<XsltSchemaType>("#all #current #default #unnamed #unknown")[0]
            val annotations = annotateTree(file, annotator).prettyPrint()
            assertThat(
                annotations, `is`(
                    """
                    INFORMATION (0:4) XML_ATTRIBUTE_VALUE
                    ERROR (0:4) "Keyword '#all' is not supported for the xsl:tokens schema type."
                    INFORMATION (5:13) XML_ATTRIBUTE_VALUE
                    ERROR (5:13) "Keyword '#current' is not supported for the xsl:tokens schema type."
                    INFORMATION (14:22) XML_ATTRIBUTE_VALUE
                    ERROR (14:22) "Keyword '#default' is not supported for the xsl:tokens schema type."
                    INFORMATION (23:31) XML_ATTRIBUTE_VALUE
                    ERROR (23:31) "Keyword '#unnamed' is not supported for the xsl:tokens schema type."
                    INFORMATION (32:40) XML_ATTRIBUTE_VALUE
                    ERROR (32:40) "Keyword '#unknown' is not supported for the xsl:tokens schema type."
                    """.trimIndent()
                )
            )
        }
    }
}
