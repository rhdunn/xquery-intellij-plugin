/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.annotator

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.markup.TextAttributes
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpath.annotation.QNameAnnotator
import uk.co.reecedunn.intellij.plugin.intellij.lexer.XQuerySyntaxHighlighter
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("IntelliJ - Custom Language Support - Syntax Highlighting - QNameAnnotator")
private class QNameAnnotatorTest : AnnotatorTestCase() {
    fun parseResource(resource: String): XQueryModule {
        val file = ResourceVirtualFile(QNameAnnotatorTest::class.java.classLoader, resource)
        return file.toPsiFile(myProject)!!
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (123) NCName")
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
            assertThat(annotations[1].textAttributes, `is`(XQuerySyntaxHighlighter.IDENTIFIER))
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (122) QName")
    internal inner class QName {
        @Test
        @DisplayName("prefix: identifier; local name: identifier")
        fun testQName() {
            val file = parse<XQueryModule>("lorem:ipsum")[0]
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
            assertThat(annotations[1].textAttributes, `is`(XQuerySyntaxHighlighter.NS_PREFIX))
        }

        @Test
        @DisplayName("prefix: keyword")
        fun testQName_KeywordPrefixPart() {
            val file = parse<XQueryModule>("cast:ipsum")[0]
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
            assertThat(annotations[1].textAttributes, `is`(XQuerySyntaxHighlighter.NS_PREFIX))
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
            assertThat(annotations[1].textAttributes, `is`(XQuerySyntaxHighlighter.NS_PREFIX))

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
            assertThat(annotations[3].textAttributes, `is`(XQuerySyntaxHighlighter.IDENTIFIER))
        }

        @Test
        @DisplayName("local name: missing")
        fun testQName_MissingLocalPart() {
            val file = parse<XQueryModule>("lorem:")[0]
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
            assertThat(annotations[1].textAttributes, `is`(XQuerySyntaxHighlighter.NS_PREFIX))
        }

        @Test
        @DisplayName("whitespace in QName; before ':'")
        fun whitespaceInQName_beforeColon() {
            val file = parse<XQueryModule>("lorem :ipsum")[0]
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
            assertThat(annotations[1].textAttributes, `is`(XQuerySyntaxHighlighter.NS_PREFIX))
        }

        @Test
        @DisplayName("whitespace in QName; after ':'")
        fun whitespaceInQName_afterColon() {
            val file = parse<XQueryModule>("lorem: ipsum")[0]
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
            assertThat(annotations[1].textAttributes, `is`(XQuerySyntaxHighlighter.NS_PREFIX))
        }

        @Test
        @DisplayName("whitespace in QName; before and after ':'")
        fun whitespaceInQName_beforeAndAfterColon() {
            val file = parse<XQueryModule>("lorem : ipsum")[0]
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
            assertThat(annotations[1].textAttributes, `is`(XQuerySyntaxHighlighter.NS_PREFIX))
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (117) URIQualifiedName")
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
            assertThat(annotations[1].textAttributes, `is`(XQuerySyntaxHighlighter.IDENTIFIER))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList")
    internal inner class DirAttributeList {
        @Test
        @DisplayName("xmlns:prefix")
        fun testDirAttributeList_XmlnsAttribute() {
            val file = parseResource("tests/psi/xquery-1.0/DirAttributeList_XmlnsAttribute.xq")
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(6))

            assertThat(annotations[0].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[0].startOffset, `is`(1))
            assertThat(annotations[0].endOffset, `is`(2))
            assertThat(annotations[0].message, `is`(nullValue()))
            assertThat(annotations[0].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
            assertThat(annotations[0].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

            assertThat(annotations[1].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[1].startOffset, `is`(1))
            assertThat(annotations[1].endOffset, `is`(2))
            assertThat(annotations[1].message, `is`(nullValue()))
            assertThat(annotations[1].enforcedTextAttributes, `is`(nullValue()))
            assertThat(annotations[1].textAttributes, `is`(XQuerySyntaxHighlighter.XML_TAG))

            assertThat(annotations[2].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[2].startOffset, `is`(1))
            assertThat(annotations[2].endOffset, `is`(2))
            assertThat(annotations[2].message, `is`(nullValue()))
            assertThat(annotations[2].enforcedTextAttributes, `is`(nullValue()))
            assertThat(annotations[2].textAttributes, `is`(XQuerySyntaxHighlighter.NS_PREFIX))

            assertThat(annotations[3].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[3].startOffset, `is`(11))
            assertThat(annotations[3].endOffset, `is`(12))
            assertThat(annotations[3].message, `is`(nullValue()))
            assertThat(annotations[3].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
            assertThat(annotations[3].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

            assertThat(annotations[4].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[4].startOffset, `is`(11))
            assertThat(annotations[4].endOffset, `is`(12))
            assertThat(annotations[4].message, `is`(nullValue()))
            assertThat(annotations[4].enforcedTextAttributes, `is`(nullValue()))
            assertThat(annotations[4].textAttributes, `is`(XQuerySyntaxHighlighter.XML_TAG))

            assertThat(annotations[5].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[5].startOffset, `is`(11))
            assertThat(annotations[5].endOffset, `is`(12))
            assertThat(annotations[5].message, `is`(nullValue()))
            assertThat(annotations[5].enforcedTextAttributes, `is`(nullValue()))
            assertThat(annotations[5].textAttributes, `is`(XQuerySyntaxHighlighter.NS_PREFIX))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (27) Annotation")
    internal inner class Annotation {
        @Test
        @DisplayName("ncname")
        fun testAnnotation() {
            val file = parseResource("tests/parser/xquery-3.0/Annotation.xq")
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(2))

            assertThat(annotations[0].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[0].startOffset, `is`(10))
            assertThat(annotations[0].endOffset, `is`(17))
            assertThat(annotations[0].message, `is`(nullValue()))
            assertThat(annotations[0].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
            assertThat(annotations[0].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

            assertThat(annotations[1].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[1].startOffset, `is`(10))
            assertThat(annotations[1].endOffset, `is`(17))
            assertThat(annotations[1].message, `is`(nullValue()))
            assertThat(annotations[1].enforcedTextAttributes, `is`(nullValue()))
            assertThat(annotations[1].textAttributes, `is`(XQuerySyntaxHighlighter.ANNOTATION))
        }

        @Test
        @DisplayName("qname")
        fun testAnnotation_QName() {
            val file = parseResource("tests/psi/xquery-3.0/Annotation_QName.xq")
            val annotations = annotateTree(file, QNameAnnotator())
            assertThat(annotations.size, `is`(4))

            assertThat(annotations[0].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[0].startOffset, `is`(10))
            assertThat(annotations[0].endOffset, `is`(12))
            assertThat(annotations[0].message, `is`(nullValue()))
            assertThat(annotations[0].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
            assertThat(annotations[0].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

            assertThat(annotations[1].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[1].startOffset, `is`(10))
            assertThat(annotations[1].endOffset, `is`(12))
            assertThat(annotations[1].message, `is`(nullValue()))
            assertThat(annotations[1].enforcedTextAttributes, `is`(nullValue()))
            assertThat(annotations[1].textAttributes, `is`(XQuerySyntaxHighlighter.NS_PREFIX))

            assertThat(annotations[2].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[2].startOffset, `is`(13))
            assertThat(annotations[2].endOffset, `is`(19))
            assertThat(annotations[2].message, `is`(nullValue()))
            assertThat(annotations[2].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
            assertThat(annotations[2].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

            assertThat(annotations[3].severity, `is`(HighlightSeverity.INFORMATION))
            assertThat(annotations[3].startOffset, `is`(13))
            assertThat(annotations[3].endOffset, `is`(19))
            assertThat(annotations[3].message, `is`(nullValue()))
            assertThat(annotations[3].enforcedTextAttributes, `is`(nullValue()))
            assertThat(annotations[3].textAttributes, `is`(XQuerySyntaxHighlighter.ANNOTATION))
        }
    }
}
