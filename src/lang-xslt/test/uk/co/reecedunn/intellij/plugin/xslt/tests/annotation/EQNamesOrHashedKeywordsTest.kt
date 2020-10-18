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

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.parser.prettyPrint
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xslt.annotation.SchemaTypeAnnotator
import uk.co.reecedunn.intellij.plugin.xslt.ast.schema.XsltSchemaType
import uk.co.reecedunn.intellij.plugin.xslt.intellij.lang.EQNamesOrHashedKeywords
import uk.co.reecedunn.intellij.plugin.xslt.schema.XslAccumulatorNames
import uk.co.reecedunn.intellij.plugin.xslt.schema.XslModes
import uk.co.reecedunn.intellij.plugin.xslt.schema.XslPrefixList

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("Reformat")
@DisplayName("IntelliJ - Custom Language Support - Syntax Highlighting - EQNames-or-hashed-keywords Schema Type Annotator")
private class EQNamesOrHashedKeywordsTest :
    AnnotatorTestCase(EQNamesOrHashedKeywords.ParserDefinition(), XPathParserDefinition()) {

    @Nested
    @DisplayName("xsl:accumulator-names")
    inner class AccumulatorNamesTest {
        val annotator = SchemaTypeAnnotator(XslAccumulatorNames)

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
                    ERROR (5:13) "Keyword '#current' is not supported for the xsl:accumulator-names schema type."
                    ERROR (14:22) "Keyword '#default' is not supported for the xsl:accumulator-names schema type."
                    ERROR (23:31) "Keyword '#unnamed' is not supported for the xsl:accumulator-names schema type."
                    ERROR (32:40) "Keyword '#unknown' is not supported for the xsl:accumulator-names schema type."
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("xsl:modes")
    inner class ModesTest {
        val annotator = SchemaTypeAnnotator(XslModes)

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
                    ERROR (5:13) "Keyword '#current' is not supported for the xsl:modes schema type."
                    ERROR (32:40) "Keyword '#unknown' is not supported for the xsl:modes schema type."
                    """.trimIndent()
                )
            )
        }
    }

    @Nested
    @DisplayName("xsl:prefix-list")
    inner class PrefixListTest {
        val annotator = SchemaTypeAnnotator(XslPrefixList)

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
                    ERROR (0:4) "Keyword '#all' is not supported for the xsl:prefix-list schema type."
                    ERROR (5:13) "Keyword '#current' is not supported for the xsl:prefix-list schema type."
                    ERROR (23:31) "Keyword '#unnamed' is not supported for the xsl:prefix-list schema type."
                    ERROR (32:40) "Keyword '#unknown' is not supported for the xsl:prefix-list schema type."
                    """.trimIndent()
                )
            )
        }
    }
}
