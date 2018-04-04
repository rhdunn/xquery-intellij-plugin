/*
 * Copyright (C) 2016 Reece H. Dunn
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
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.xquery.annotation.QNameAnnotator
import uk.co.reecedunn.intellij.plugin.xquery.lexer.SyntaxHighlighter

class QNameAnnotatorTest : AnnotatorTestCase() {
    // region NCName

    fun testNCName() {
        val file = parseResource("tests/parser/xquery-1.0/OptionDecl.xq")
        val annotations = annotateTree(file, QNameAnnotator())
        assertThat(annotations.size, `is`(0))
    }

    fun testNCName_Keyword() {
        val file = parseResource("tests/parser/xquery-1.0/NCName_Keyword.xq")
        val annotations = annotateTree(file, QNameAnnotator())
        assertThat(annotations.size, `is`(2))

        assertThat(annotations[0].severity, `is`(HighlightSeverity.INFORMATION))
        assertThat(annotations[0].startOffset, `is`(15))
        assertThat(annotations[0].endOffset, `is`(24))
        assertThat(annotations[0].message, `is`(nullValue()))
        assertThat(annotations[0].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
        assertThat(annotations[0].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

        assertThat(annotations[1].severity, `is`(HighlightSeverity.INFORMATION))
        assertThat(annotations[1].startOffset, `is`(15))
        assertThat(annotations[1].endOffset, `is`(24))
        assertThat(annotations[1].message, `is`(nullValue()))
        assertThat(annotations[1].enforcedTextAttributes, `is`(nullValue()))
        assertThat(annotations[1].textAttributes, `is`(SyntaxHighlighter.IDENTIFIER))
    }

    // endregion
    // region QName

    fun testQName() {
        val file = parseResource("tests/parser/xquery-1.0/QName.xq")
        val annotations = annotateTree(file, QNameAnnotator())
        assertThat(annotations.size, `is`(2))

        assertThat(annotations[0].severity, `is`(HighlightSeverity.INFORMATION))
        assertThat(annotations[0].startOffset, `is`(15))
        assertThat(annotations[0].endOffset, `is`(18))
        assertThat(annotations[0].message, `is`(nullValue()))
        assertThat(annotations[0].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
        assertThat(annotations[0].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

        assertThat(annotations[1].severity, `is`(HighlightSeverity.INFORMATION))
        assertThat(annotations[1].startOffset, `is`(15))
        assertThat(annotations[1].endOffset, `is`(18))
        assertThat(annotations[1].message, `is`(nullValue()))
        assertThat(annotations[1].enforcedTextAttributes, `is`(nullValue()))
        assertThat(annotations[1].textAttributes, `is`(SyntaxHighlighter.NS_PREFIX))
    }

    fun testQName_KeywordPrefixPart() {
        val file = parseResource("tests/parser/xquery-1.0/QName_KeywordPrefixPart.xq")
        val annotations = annotateTree(file, QNameAnnotator())
        assertThat(annotations.size, `is`(2))

        assertThat(annotations[0].severity, `is`(HighlightSeverity.INFORMATION))
        assertThat(annotations[0].startOffset, `is`(15))
        assertThat(annotations[0].endOffset, `is`(20))
        assertThat(annotations[0].message, `is`(nullValue()))
        assertThat(annotations[0].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
        assertThat(annotations[0].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

        assertThat(annotations[1].severity, `is`(HighlightSeverity.INFORMATION))
        assertThat(annotations[1].startOffset, `is`(15))
        assertThat(annotations[1].endOffset, `is`(20))
        assertThat(annotations[1].message, `is`(nullValue()))
        assertThat(annotations[1].enforcedTextAttributes, `is`(nullValue()))
        assertThat(annotations[1].textAttributes, `is`(SyntaxHighlighter.NS_PREFIX))
    }

    fun testQName_MissingPrefixPart() {
        val file = parseResource("tests/parser/xquery-1.0/QName_MissingPrefixPart.xq")
        val annotations = annotateTree(file, QNameAnnotator())
        assertThat(annotations.size, `is`(0))
    }

    fun testQName_KeywordLocalPart() {
        val file = parseResource("tests/parser/xquery-1.0/QName_KeywordLocalPart.xq")
        val annotations = annotateTree(file, QNameAnnotator())
        assertThat(annotations.size, `is`(4))

        assertThat(annotations[0].severity, `is`(HighlightSeverity.INFORMATION))
        assertThat(annotations[0].startOffset, `is`(15))
        assertThat(annotations[0].endOffset, `is`(19))
        assertThat(annotations[0].message, `is`(nullValue()))
        assertThat(annotations[0].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
        assertThat(annotations[0].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

        assertThat(annotations[1].severity, `is`(HighlightSeverity.INFORMATION))
        assertThat(annotations[1].startOffset, `is`(15))
        assertThat(annotations[1].endOffset, `is`(19))
        assertThat(annotations[1].message, `is`(nullValue()))
        assertThat(annotations[1].enforcedTextAttributes, `is`(nullValue()))
        assertThat(annotations[1].textAttributes, `is`(SyntaxHighlighter.NS_PREFIX))

        assertThat(annotations[2].severity, `is`(HighlightSeverity.INFORMATION))
        assertThat(annotations[2].startOffset, `is`(20))
        assertThat(annotations[2].endOffset, `is`(25))
        assertThat(annotations[2].message, `is`(nullValue()))
        assertThat(annotations[2].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
        assertThat(annotations[2].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

        assertThat(annotations[3].severity, `is`(HighlightSeverity.INFORMATION))
        assertThat(annotations[3].startOffset, `is`(20))
        assertThat(annotations[3].endOffset, `is`(25))
        assertThat(annotations[3].message, `is`(nullValue()))
        assertThat(annotations[3].enforcedTextAttributes, `is`(nullValue()))
        assertThat(annotations[3].textAttributes, `is`(SyntaxHighlighter.IDENTIFIER))
    }

    fun testQName_MissingLocalPart() {
        val file = parseResource("tests/parser/xquery-1.0/QName_MissingLocalPart.xq")
        val annotations = annotateTree(file, QNameAnnotator())
        assertThat(annotations.size, `is`(0))
    }

    // endregion
    // region URIQualifiedName

    fun testURIQualifiedName() {
        val file = parseResource("tests/parser/xquery-3.0/BracedURILiteral.xq")
        val annotations = annotateTree(file, QNameAnnotator())
        assertThat(annotations.size, `is`(0))
    }

    fun testURIQualifiedName_Keyword() {
        val file = parseResource("tests/parser/xquery-3.0/BracedURILiteral_KeywordLocalName.xq")
        val annotations = annotateTree(file, QNameAnnotator())
        assertThat(annotations.size, `is`(2))

        assertThat(annotations[0].severity, `is`(HighlightSeverity.INFORMATION))
        assertThat(annotations[0].startOffset, `is`(21))
        assertThat(annotations[0].endOffset, `is`(25))
        assertThat(annotations[0].message, `is`(nullValue()))
        assertThat(annotations[0].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
        assertThat(annotations[0].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

        assertThat(annotations[1].severity, `is`(HighlightSeverity.INFORMATION))
        assertThat(annotations[1].startOffset, `is`(21))
        assertThat(annotations[1].endOffset, `is`(25))
        assertThat(annotations[1].message, `is`(nullValue()))
        assertThat(annotations[1].enforcedTextAttributes, `is`(nullValue()))
        assertThat(annotations[1].textAttributes, `is`(SyntaxHighlighter.IDENTIFIER))
    }

    // endregion
    // region DirAttributeList

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
        assertThat(annotations[1].textAttributes, `is`(SyntaxHighlighter.XML_TAG))

        assertThat(annotations[2].severity, `is`(HighlightSeverity.INFORMATION))
        assertThat(annotations[2].startOffset, `is`(1))
        assertThat(annotations[2].endOffset, `is`(2))
        assertThat(annotations[2].message, `is`(nullValue()))
        assertThat(annotations[2].enforcedTextAttributes, `is`(nullValue()))
        assertThat(annotations[2].textAttributes, `is`(SyntaxHighlighter.NS_PREFIX))

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
        assertThat(annotations[4].textAttributes, `is`(SyntaxHighlighter.XML_TAG))

        assertThat(annotations[5].severity, `is`(HighlightSeverity.INFORMATION))
        assertThat(annotations[5].startOffset, `is`(11))
        assertThat(annotations[5].endOffset, `is`(12))
        assertThat(annotations[5].message, `is`(nullValue()))
        assertThat(annotations[5].enforcedTextAttributes, `is`(nullValue()))
        assertThat(annotations[5].textAttributes, `is`(SyntaxHighlighter.NS_PREFIX))
    }

    // endregion
    // region Annotation

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
        assertThat(annotations[1].textAttributes, `is`(SyntaxHighlighter.ANNOTATION))
    }

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
        assertThat(annotations[1].textAttributes, `is`(SyntaxHighlighter.NS_PREFIX))

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
        assertThat(annotations[3].textAttributes, `is`(SyntaxHighlighter.ANNOTATION))
    }

    // endregion
}
