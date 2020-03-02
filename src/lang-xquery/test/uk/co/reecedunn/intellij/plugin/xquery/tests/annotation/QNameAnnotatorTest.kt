/*
 * Copyright (C) 2016-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.annotation

import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.markup.TextAttributes
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.lexer.XQuerySyntaxHighlighterColors
import uk.co.reecedunn.intellij.plugin.xpath.annotation.QNameAnnotator as XPathQNameAnnotator
import uk.co.reecedunn.intellij.plugin.xquery.annotation.QNameAnnotator
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("IntelliJ - Custom Language Support - Syntax Highlighting - XQuery QNameAnnotator")
private class QNameAnnotatorTest : AnnotatorTestCase() {
    @Nested
    @DisplayName("XQuery 3.1 EBNF (120) Wildcard")
    internal inner class Wildcard {
        @Test
        @DisplayName("any")
        fun any() {
            val file = parse<XQueryModule>("*")[0]
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(0))
        }

        @Test
        @DisplayName("prefix: identifier")
        fun wildcard() {
            val file = parse<XQueryModule>("lorem:*")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(2))
            info(annotations[0], 0, 5, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
            info(annotations[1], 0, 5, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
        }

        @Test
        @DisplayName("prefix: keyword")
        fun keywordPrefixPart() {
            val file = parse<XQueryModule>("cast:*")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(2))
            info(annotations[0], 0, 4, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
            info(annotations[1], 0, 4, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
        }

        @Test
        @DisplayName("prefix: missing")
        fun missingPrefixPart() {
            val file = parse<XQueryModule>(":*")[0]
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(0))
        }

        @Test
        @DisplayName("local name: keyword")
        fun keywordLocalPart() {
            val file = parse<XQueryModule>("*:cast")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(2))
            info(annotations[0], 2, 6, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
            info(annotations[1], 2, 6, null, XQuerySyntaxHighlighterColors.IDENTIFIER)
        }

        @Test
        @DisplayName("local name: missing")
        fun missingLocalPart() {
            val file = parse<XQueryModule>("*:")[0]
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(0))
        }

        @Nested
        @DisplayName("whitespace in QName")
        internal inner class WhitespaceInQName {
            @Test
            @DisplayName("before ':'")
            fun beforeColon() {
                val file = parse<XQueryModule>("lorem :*")[0]
                val annotations = annotateTree(file, QNameAnnotator())

                assertThat(annotations.size, `is`(3))
                info(annotations[0], 0, 5, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
                info(annotations[1], 0, 5, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
                error(annotations[2], 5, 6, "XPST0003: Whitespace is not allowed before ':' in a wildcard.")
            }

            @Test
            @DisplayName("after ':'")
            fun afterColon() {
                val file = parse<XQueryModule>("lorem: *")[0]
                val annotations = annotateTree(file, QNameAnnotator())

                assertThat(annotations.size, `is`(3))
                info(annotations[0], 0, 5, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
                info(annotations[1], 0, 5, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
                error(annotations[2], 6, 7, "XPST0003: Whitespace is not allowed after ':' in a wildcard.")
            }

            @Test
            @DisplayName("before and after ':'")
            fun beforeAndAfterColon() {
                val file = parse<XQueryModule>("lorem : *")[0]
                val annotations = annotateTree(file, QNameAnnotator())

                assertThat(annotations.size, `is`(4))
                info(annotations[0], 0, 5, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
                info(annotations[1], 0, 5, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
                error(annotations[2], 5, 6, "XPST0003: Whitespace is not allowed before ':' in a wildcard.")
                error(annotations[3], 7, 8, "XPST0003: Whitespace is not allowed after ':' in a wildcard.")
            }
        }

        @Test
        @DisplayName("URIQualifiedName wildcard")
        fun uriQualifiedName() {
            val file = parse<XQueryModule>("Q{http://www.example.com/test#}*")[0]
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(0))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (235) NCName")
    internal inner class NCName {
        @Test
        @DisplayName("identifier")
        fun testNCName() {
            val file = parse<XQueryModule>("lorem-ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(0))
        }

        @Test
        @DisplayName("keyword")
        fun testNCName_Keyword() {
            val file = parse<XQueryModule>("cast")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(2))
            info(annotations[0], 0, 4, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
            info(annotations[1], 0, 4, null, XQuerySyntaxHighlighterColors.IDENTIFIER)
        }

        @Test
        @DisplayName("xpath annotator")
        fun xpathAnnotator() {
            val file = parse<XQueryModule>("cast")[0]
            val annotations = annotateTree(file, XPathQNameAnnotator())
            assertThat(annotations.size, `is`(0))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (234) QName")
    internal inner class QName {
        @Test
        @DisplayName("prefix: identifier; local name: identifier")
        fun testQName() {
            val file = parse<XQueryModule>("lorem:ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(2))
            info(annotations[0], 0, 5, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
            info(annotations[1], 0, 5, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
        }

        @Test
        @DisplayName("prefix: keyword")
        fun testQName_KeywordPrefixPart() {
            val file = parse<XQueryModule>("cast:ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(2))
            info(annotations[0], 0, 4, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
            info(annotations[1], 0, 4, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
        }

        @Test
        @DisplayName("prefix: missing")
        fun testQName_MissingPrefixPart() {
            val file = parse<XQueryModule>(":ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(0))
        }

        @Test
        @DisplayName("local name: keyword")
        fun testQName_KeywordLocalPart() {
            val file = parse<XQueryModule>("lorem:cast")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(4))
            info(annotations[0], 0, 5, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
            info(annotations[1], 0, 5, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
            info(annotations[2], 6, 10, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
            info(annotations[3], 6, 10, null, XQuerySyntaxHighlighterColors.IDENTIFIER)
        }

        @Test
        @DisplayName("local name: missing")
        fun testQName_MissingLocalPart() {
            val file = parse<XQueryModule>("lorem:")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(2))
            info(annotations[0], 0, 5, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
            info(annotations[1], 0, 5, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
        }

        @Nested
        @DisplayName("whitespace in QName")
        internal inner class WhitespaceInQName {
            @Test
            @DisplayName("before ':'")
            fun beforeColon() {
                val file = parse<XQueryModule>("lorem :ipsum")[0]
                val annotations = annotateTree(file, QNameAnnotator())

                assertThat(annotations.size, `is`(3))
                info(annotations[0], 0, 5, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
                info(annotations[1], 0, 5, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
                error(annotations[2], 5, 6, "XPST0003: Whitespace is not allowed before ':' in a qualified name.")
            }

            @Test
            @DisplayName("after ':'")
            fun afterColon() {
                val file = parse<XQueryModule>("lorem: ipsum")[0]
                val annotations = annotateTree(file, QNameAnnotator())

                assertThat(annotations.size, `is`(3))
                info(annotations[0], 0, 5, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
                info(annotations[1], 0, 5, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
                error(annotations[2], 6, 7, "XPST0003: Whitespace is not allowed after ':' in a qualified name.")
            }

            @Test
            @DisplayName("before and after ':'")
            fun beforeAndAfterColon() {
                val file = parse<XQueryModule>("lorem : ipsum")[0]
                val annotations = annotateTree(file, QNameAnnotator())

                assertThat(annotations.size, `is`(4))
                info(annotations[0], 0, 5, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
                info(annotations[1], 0, 5, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
                error(annotations[2], 5, 6, "XPST0003: Whitespace is not allowed before ':' in a qualified name.")
                error(annotations[3], 7, 8, "XPST0003: Whitespace is not allowed after ':' in a qualified name.")
            }
        }

        @Test
        @DisplayName("xpath annotator")
        fun xpathAnnotator() {
            val file = parse<XQueryModule>("cast:as")[0]
            val annotations = annotateTree(file, XPathQNameAnnotator())
            assertThat(annotations.size, `is`(0))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (223) URIQualifiedName")
    internal inner class URIQualifiedName {
        @Test
        @DisplayName("local name: identifier")
        fun testURIQualifiedName() {
            val file = parse<XQueryModule>("Q{http://www.example.com/test#}lorem-ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(0))
        }

        @Test
        @DisplayName("local name: keyword")
        fun testURIQualifiedName_Keyword() {
            val file = parse<XQueryModule>("Q{http://www.example.com/test#}let")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(2))
            info(annotations[0], 31, 34, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
            info(annotations[1], 31, 34, null, XQuerySyntaxHighlighterColors.IDENTIFIER)
        }

        @Test
        @DisplayName("xpath annotator")
        fun xpathAnnotator() {
            val file = parse<XQueryModule>("Q{http://www.example.com/test#}let")[0]
            val annotations = annotateTree(file, XPathQNameAnnotator())
            assertThat(annotations.size, `is`(0))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList")
    internal inner class DirAttributeList {
        @Test
        @DisplayName("xmlns:prefix")
        fun testDirAttributeList_XmlnsAttribute() {
            val file = parse<XQueryModule>("<a:b xmlns:a=\"http://www.example.com/a\"/>")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(6))
            info(annotations[0], 1, 2, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
            info(annotations[1], 1, 2, null, XQuerySyntaxHighlighterColors.XML_TAG)
            info(annotations[2], 1, 2, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
            info(annotations[3], 11, 12, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
            info(annotations[4], 11, 12, null, XQuerySyntaxHighlighterColors.XML_TAG)
            info(annotations[5], 11, 12, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
        }

        @Test
        @DisplayName("xpath annotator")
        fun xpathAnnotator() {
            val file = parse<XQueryModule>("<a:b xmlns:a=\"http://www.example.com/a\"/>")[0]
            val annotations = annotateTree(file, XPathQNameAnnotator())
            assertThat(annotations.size, `is`(0))
        }
    }

    @Nested
    @DisplayName("Usage Type: Annotation")
    internal inner class UsageType_Annotation {
        @Test
        @DisplayName("XQuery 3.1 EBNF (27) Annotation")
        fun annotation() {
            val file = parse<XQueryModule>(
                """
                |declare %private function test() external;
                |declare %xs:string function test() external;
                """.trimIndent())[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(6))
            info(annotations[1], 10, 17, null, XQuerySyntaxHighlighterColors.ANNOTATION)
            info(annotations[3], 54 , 56, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
            info(annotations[5], 57, 63, null, XQuerySyntaxHighlighterColors.ANNOTATION)
        }
    }

    @Nested
    @DisplayName("Usage Type: Attribute")
    internal inner class UsageType_Attribute {
        @Test
        @DisplayName("XQuery 3.1 EBNF (113) ForwardAxis")
        fun forwardAxis() {
            val file = parse<XQueryModule>("attribute::test, attribute::ns:test, attribute::Q{}test, attribute::*")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(10))
            info(annotations[1], 11, 15, null, XQuerySyntaxHighlighterColors.ATTRIBUTE)
            info(annotations[3], 28, 30, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
            info(annotations[5], 31, 35, null, XQuerySyntaxHighlighterColors.ATTRIBUTE)
            info(annotations[7], 51, 55, null, XQuerySyntaxHighlighterColors.ATTRIBUTE)
            info(annotations[9], 68, 69, null, XQuerySyntaxHighlighterColors.ATTRIBUTE)
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (114) AbbrevForwardStep")
        fun abbrevForwardStep() {
            val file = parse<XQueryModule>("@test, @ns:test, @Q{}test, @*")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(10))
            info(annotations[1], 1, 5, null, XQuerySyntaxHighlighterColors.ATTRIBUTE)
            info(annotations[3], 8, 10, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
            info(annotations[5], 11, 15, null, XQuerySyntaxHighlighterColors.ATTRIBUTE)
            info(annotations[7], 21, 25, null, XQuerySyntaxHighlighterColors.ATTRIBUTE)
            info(annotations[9], 28, 29, null, XQuerySyntaxHighlighterColors.ATTRIBUTE)
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList")
        fun dirAttributeList() {
            val file = parse<XQueryModule>("""<a test="one" ns:test="two"/>""")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(9))
            info(annotations[2], 3, 7, null, XQuerySyntaxHighlighterColors.ATTRIBUTE)
            info(annotations[5], 14, 16, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
            info(annotations[8], 17, 21, null, XQuerySyntaxHighlighterColors.ATTRIBUTE)
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor")
        fun compAttrConstructor() {
            val file = parse<XQueryModule>("attribute test {}, attribute ns:test {}, attribute Q{}test {}")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(8))
            info(annotations[1], 10, 14, null, XQuerySyntaxHighlighterColors.ATTRIBUTE)
            info(annotations[3], 29, 31, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
            info(annotations[5], 32, 36, null, XQuerySyntaxHighlighterColors.ATTRIBUTE)
            info(annotations[7], 54, 58, null, XQuerySyntaxHighlighterColors.ATTRIBUTE)
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (198) AttributeDeclaration")
        fun attributeDeclaration() {
            val file = parse<XQueryModule>(
                """
                |() instance of schema-attribute(test),
                |() instance of schema-attribute(ns:test),
                |() instance of schema-attribute(Q{}test)
                """.trimMargin())[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(8))
            info(annotations[1], 32, 36, null, XQuerySyntaxHighlighterColors.ATTRIBUTE)
            info(annotations[3], 71, 73, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
            info(annotations[5], 74, 78, null, XQuerySyntaxHighlighterColors.ATTRIBUTE)
            info(annotations[7], 116, 120, null, XQuerySyntaxHighlighterColors.ATTRIBUTE)
        }

        @Test
        @DisplayName("XQuery 3.1 EBNF (203) AttributeName")
        fun attributeName() {
            val file = parse<XQueryModule>(
                """
                |() instance of attribute(test),
                |() instance of attribute(ns:test),
                |() instance of attribute(Q{}test),
                |() instance of attribute(*)
                """.trimMargin())[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(8))
            info(annotations[1], 25, 29, null, XQuerySyntaxHighlighterColors.ATTRIBUTE)
            info(annotations[3], 57, 59, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
            info(annotations[5], 60, 64, null, XQuerySyntaxHighlighterColors.ATTRIBUTE)
            info(annotations[7], 95, 99, null, XQuerySyntaxHighlighterColors.ATTRIBUTE)
        }
    }

    @Nested
    @DisplayName("Usage Type: Decimal Format")
    internal inner class UsageType_DecimalFormat {
        @Test
        @DisplayName("XQuery 3.1 EBNF (18) DecimalFormatDecl")
        fun decimalFormatDecl() {
            val file = parse<XQueryModule>(
                """
                |declare decimal-format test;
                |declare decimal-format ns:test;
                """.trimIndent())[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(6))
            info(annotations[1], 24, 28, null, XQuerySyntaxHighlighterColors.DECIMAL_FORMAT)
            info(annotations[3], 54 , 56, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
            info(annotations[5], 57, 61, null, XQuerySyntaxHighlighterColors.DECIMAL_FORMAT)
        }
    }
}
