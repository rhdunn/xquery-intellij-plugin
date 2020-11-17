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
package uk.co.reecedunn.intellij.plugin.xslt.tests.annotation

import com.intellij.openapi.extensions.PluginId
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.parser.prettyPrint
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xslt.annotation.SchemaTypeAnnotator
import uk.co.reecedunn.intellij.plugin.xslt.ast.schema.XsltSchemaType
import uk.co.reecedunn.intellij.plugin.xslt.intellij.lang.SequenceType
import uk.co.reecedunn.intellij.plugin.xslt.schema.XslItemType
import uk.co.reecedunn.intellij.plugin.xslt.schema.XslSequenceType

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("Reformat")
@DisplayName("IntelliJ - Custom Language Support - Syntax Highlighting - SequenceType Schema Type Annotator")
private class XslSequenceTypeTest : AnnotatorTestCase(SequenceType.ParserDefinition(), XPathParserDefinition()) {
    override val pluginId: PluginId = PluginId.getId("XslSequenceTypeTest")

    @Nested
    @DisplayName("xsl:item-type")
    internal inner class ItemTypeTest {
        val annotator = SchemaTypeAnnotator(XslItemType)

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
        val annotator = SchemaTypeAnnotator(XslSequenceType)

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
