/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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

        @Test
        @DisplayName("whitespace in QName; before ':'")
        fun whitespaceInQName_beforeColon() {
            val file = parse<XQueryModule>("lorem :*")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(2))
            info(annotations[0], 0, 5, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
            info(annotations[1], 0, 5, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
        }

        @Test
        @DisplayName("whitespace in QName; after ':'")
        fun whitespaceInQName_afterColon() {
            val file = parse<XQueryModule>("lorem: *")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(2))
            info(annotations[0], 0, 5, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
            info(annotations[1], 0, 5, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
        }

        @Test
        @DisplayName("whitespace in QName; before and after ':'")
        fun whitespaceInQName_beforeAndAfterColon() {
            val file = parse<XQueryModule>("lorem : *")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(2))
            info(annotations[0], 0, 5, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
            info(annotations[1], 0, 5, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
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

        @Test
        @DisplayName("whitespace in QName; before ':'")
        fun whitespaceInQName_beforeColon() {
            val file = parse<XQueryModule>("lorem :ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(2))
            info(annotations[0], 0, 5, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
            info(annotations[1], 0, 5, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
        }

        @Test
        @DisplayName("whitespace in QName; after ':'")
        fun whitespaceInQName_afterColon() {
            val file = parse<XQueryModule>("lorem: ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(2))
            info(annotations[0], 0, 5, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
            info(annotations[1], 0, 5, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
        }

        @Test
        @DisplayName("whitespace in QName; before and after ':'")
        fun whitespaceInQName_beforeAndAfterColon() {
            val file = parse<XQueryModule>("lorem : ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(2))
            info(annotations[0], 0, 5, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
            info(annotations[1], 0, 5, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
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
    @DisplayName("XQuery 3.1 EBNF (27) Annotation")
    internal inner class AnnotationXQuery {
        @Test
        @DisplayName("ncname")
        fun testAnnotation() {
            val file = parse<XQueryModule>("declare % private function test ( ) external ;")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(2))
            info(annotations[0], 10, 17, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
            info(annotations[1], 10, 17, null, XQuerySyntaxHighlighterColors.ANNOTATION)
        }

        @Test
        @DisplayName("qname")
        fun testAnnotation_QName() {
            val file = parse<XQueryModule>("declare % xs:string function test ( ) external ;")[0]
            val annotations = annotateTree(file, QNameAnnotator())

            assertThat(annotations.size, `is`(4))
            info(annotations[0], 10, 12, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
            info(annotations[1], 10, 12, null, XQuerySyntaxHighlighterColors.NS_PREFIX)
            info(annotations[2], 13, 19, TextAttributes.ERASE_MARKER, HighlighterColors.NO_HIGHLIGHTING)
            info(annotations[3], 13, 19, null, XQuerySyntaxHighlighterColors.ANNOTATION)
        }

        @Test
        @DisplayName("xpath annotator")
        fun xpathAnnotator() {
            val file = parse<XQueryModule>("declare % xs:string function test ( ) external ;")[0]
            val annotations = annotateTree(file, XPathQNameAnnotator())
            assertThat(annotations.size, `is`(0))
        }
    }
}
