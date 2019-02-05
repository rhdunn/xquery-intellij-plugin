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
package uk.co.reecedunn.intellij.plugin.xpath.tests.annotation

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.markup.TextAttributes
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.lexer.XPathSyntaxHighlighterColors
import uk.co.reecedunn.intellij.plugin.xpath.annotation.QNameAnnotator
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPath

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("IntelliJ - Custom Language Support - Syntax Highlighting - XPath QNameAnnotator")
private class QNameAnnotatorTest : AnnotatorTestCase() {
    @Nested
    @DisplayName("XPath 3.1 EBNF (123) NCName")
    internal inner class NCName {
        @Test
        @DisplayName("identifier")
        fun testNCName() {
            val file = parse<XPath>("lorem-ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(0))
        }

        @Test
        @DisplayName("keyword")
        fun testNCName_Keyword() {
            val file = parse<XPath>("cast")[0]
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(2))

            assertThat(annotations[0].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[0].startOffset, `is`(0))
            assertThat(annotations[0].endOffset, `is`(4))
            assertThat(annotations[0].message, `is`(nullValue()))
            assertThat(annotations[0].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
            assertThat(annotations[0].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

            assertThat(annotations[1].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[1].startOffset, `is`(0))
            assertThat(annotations[1].endOffset, `is`(4))
            assertThat(annotations[1].message, `is`(nullValue()))
            assertThat(annotations[1].enforcedTextAttributes, `is`(nullValue()))
            assertThat(annotations[1].textAttributes, `is`(XPathSyntaxHighlighterColors.IDENTIFIER))
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (122) QName")
    internal inner class QName {
        @Test
        @DisplayName("prefix: identifier; local name: identifier")
        fun testQName() {
            val file = parse<XPath>("lorem:ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(2))

            assertThat(annotations[0].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[0].startOffset, `is`(0))
            assertThat(annotations[0].endOffset, `is`(5))
            assertThat(annotations[0].message, `is`(nullValue()))
            assertThat(annotations[0].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
            assertThat(annotations[0].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

            assertThat(annotations[1].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[1].startOffset, `is`(0))
            assertThat(annotations[1].endOffset, `is`(5))
            assertThat(annotations[1].message, `is`(nullValue()))
            assertThat(annotations[1].enforcedTextAttributes, `is`(nullValue()))
            assertThat(annotations[1].textAttributes, `is`(XPathSyntaxHighlighterColors.NS_PREFIX))
        }

        @Test
        @DisplayName("prefix: keyword")
        fun testQName_KeywordPrefixPart() {
            val file = parse<XPath>("cast:ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(2))

            assertThat(annotations[0].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[0].startOffset, `is`(0))
            assertThat(annotations[0].endOffset, `is`(4))
            assertThat(annotations[0].message, `is`(nullValue()))
            assertThat(annotations[0].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
            assertThat(annotations[0].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

            assertThat(annotations[1].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[1].startOffset, `is`(0))
            assertThat(annotations[1].endOffset, `is`(4))
            assertThat(annotations[1].message, `is`(nullValue()))
            assertThat(annotations[1].enforcedTextAttributes, `is`(nullValue()))
            assertThat(annotations[1].textAttributes, `is`(XPathSyntaxHighlighterColors.NS_PREFIX))
        }

        @Test
        @DisplayName("prefix: missing")
        fun testQName_MissingPrefixPart() {
            val file = parse<XPath>(":ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(0))
        }

        @Test
        @DisplayName("local name: keyword")
        fun testQName_KeywordLocalPart() {
            val file = parse<XPath>("lorem:cast")[0]
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(4))

            assertThat(annotations[0].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[0].startOffset, `is`(0))
            assertThat(annotations[0].endOffset, `is`(5))
            assertThat(annotations[0].message, `is`(nullValue()))
            assertThat(annotations[0].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
            assertThat(annotations[0].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

            assertThat(annotations[1].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[1].startOffset, `is`(0))
            assertThat(annotations[1].endOffset, `is`(5))
            assertThat(annotations[1].message, `is`(nullValue()))
            assertThat(annotations[1].enforcedTextAttributes, `is`(nullValue()))
            assertThat(annotations[1].textAttributes, `is`(XPathSyntaxHighlighterColors.NS_PREFIX))

            assertThat(annotations[2].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[2].startOffset, `is`(6))
            assertThat(annotations[2].endOffset, `is`(10))
            assertThat(annotations[2].message, `is`(nullValue()))
            assertThat(annotations[2].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
            assertThat(annotations[2].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

            assertThat(annotations[3].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[3].startOffset, `is`(6))
            assertThat(annotations[3].endOffset, `is`(10))
            assertThat(annotations[3].message, `is`(nullValue()))
            assertThat(annotations[3].enforcedTextAttributes, `is`(nullValue()))
            assertThat(annotations[3].textAttributes, `is`(XPathSyntaxHighlighterColors.IDENTIFIER))
        }

        @Test
        @DisplayName("local name: missing")
        fun testQName_MissingLocalPart() {
            val file = parse<XPath>("lorem:")[0]
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(2))

            assertThat(annotations[0].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[0].startOffset, `is`(0))
            assertThat(annotations[0].endOffset, `is`(5))
            assertThat(annotations[0].message, `is`(nullValue()))
            assertThat(annotations[0].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
            assertThat(annotations[0].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

            assertThat(annotations[1].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[1].startOffset, `is`(0))
            assertThat(annotations[1].endOffset, `is`(5))
            assertThat(annotations[1].message, `is`(nullValue()))
            assertThat(annotations[1].enforcedTextAttributes, `is`(nullValue()))
            assertThat(annotations[1].textAttributes, `is`(XPathSyntaxHighlighterColors.NS_PREFIX))
        }

        @Test
        @DisplayName("whitespace in QName; before ':'")
        fun whitespaceInQName_beforeColon() {
            val file = parse<XPath>("lorem :ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(2))

            assertThat(annotations[0].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[0].startOffset, `is`(0))
            assertThat(annotations[0].endOffset, `is`(5))
            assertThat(annotations[0].message, `is`(nullValue()))
            assertThat(annotations[0].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
            assertThat(annotations[0].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

            assertThat(annotations[1].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[1].startOffset, `is`(0))
            assertThat(annotations[1].endOffset, `is`(5))
            assertThat(annotations[1].message, `is`(nullValue()))
            assertThat(annotations[1].enforcedTextAttributes, `is`(nullValue()))
            assertThat(annotations[1].textAttributes, `is`(XPathSyntaxHighlighterColors.NS_PREFIX))
        }

        @Test
        @DisplayName("whitespace in QName; after ':'")
        fun whitespaceInQName_afterColon() {
            val file = parse<XPath>("lorem: ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(2))

            assertThat(annotations[0].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[0].startOffset, `is`(0))
            assertThat(annotations[0].endOffset, `is`(5))
            assertThat(annotations[0].message, `is`(nullValue()))
            assertThat(annotations[0].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
            assertThat(annotations[0].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

            assertThat(annotations[1].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[1].startOffset, `is`(0))
            assertThat(annotations[1].endOffset, `is`(5))
            assertThat(annotations[1].message, `is`(nullValue()))
            assertThat(annotations[1].enforcedTextAttributes, `is`(nullValue()))
            assertThat(annotations[1].textAttributes, `is`(XPathSyntaxHighlighterColors.NS_PREFIX))
        }

        @Test
        @DisplayName("whitespace in QName; before and after ':'")
        fun whitespaceInQName_beforeAndAfterColon() {
            val file = parse<XPath>("lorem : ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(2))

            assertThat(annotations[0].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[0].startOffset, `is`(0))
            assertThat(annotations[0].endOffset, `is`(5))
            assertThat(annotations[0].message, `is`(nullValue()))
            assertThat(annotations[0].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
            assertThat(annotations[0].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

            assertThat(annotations[1].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[1].startOffset, `is`(0))
            assertThat(annotations[1].endOffset, `is`(5))
            assertThat(annotations[1].message, `is`(nullValue()))
            assertThat(annotations[1].enforcedTextAttributes, `is`(nullValue()))
            assertThat(annotations[1].textAttributes, `is`(XPathSyntaxHighlighterColors.NS_PREFIX))
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (117) URIQualifiedName")
    internal inner class URIQualifiedName {
        @Test
        @DisplayName("local name: identifier")
        fun testURIQualifiedName() {
            val file = parse<XPath>("Q{http://www.example.com/test#}lorem-ipsum")[0]
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(0))
        }

        @Test
        @DisplayName("local name: keyword")
        fun testURIQualifiedName_Keyword() {
            val file = parse<XPath>("Q{http://www.example.com/test#}let")[0]
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(2))

            assertThat(annotations[0].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[0].startOffset, `is`(31))
            assertThat(annotations[0].endOffset, `is`(34))
            assertThat(annotations[0].message, `is`(nullValue()))
            assertThat(annotations[0].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
            assertThat(annotations[0].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

            assertThat(annotations[1].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[1].startOffset, `is`(31))
            assertThat(annotations[1].endOffset, `is`(34))
            assertThat(annotations[1].message, `is`(nullValue()))
            assertThat(annotations[1].enforcedTextAttributes, `is`(nullValue()))
            assertThat(annotations[1].textAttributes, `is`(XPathSyntaxHighlighterColors.IDENTIFIER))
        }
    }
}
