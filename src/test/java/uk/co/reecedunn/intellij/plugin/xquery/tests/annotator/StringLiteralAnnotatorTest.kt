/*
 * Copyright (C) 2018 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xquery.annotation.StringLiteralAnnotator
import uk.co.reecedunn.intellij.plugin.xquery.lexer.SyntaxHighlighter

class StringLiteralAnnotatorTest : AnnotatorTestCase() {
    // region PITest

    fun testPITest_NCName() {
        val file = parseText("processing-instruction(xml-stylesheet)")
        val annotations = annotateTree(file, StringLiteralAnnotator())
        assertThat(annotations.size, `is`(0))
    }

    fun testPITest_StringLiteral_NCName() {
        val file = parseText("processing-instruction(\"xml-stylesheet\")")
        val annotations = annotateTree(file, StringLiteralAnnotator())
        assertThat(annotations.size, `is`(2))

        assertThat(annotations[0].severity, `is`(HighlightSeverity.INFORMATION))
        assertThat(annotations[0].startOffset, `is`(24))
        assertThat(annotations[0].endOffset, `is`(38))
        assertThat(annotations[0].message, `is`(nullValue()))
        assertThat(annotations[0].enforcedTextAttributes, `is`(TextAttributes.ERASE_MARKER))
        assertThat(annotations[0].textAttributes, `is`(HighlighterColors.NO_HIGHLIGHTING))

        assertThat(annotations[1].severity, `is`(HighlightSeverity.INFORMATION))
        assertThat(annotations[1].startOffset, `is`(24))
        assertThat(annotations[1].endOffset, `is`(38))
        assertThat(annotations[1].message, `is`(nullValue()))
        assertThat(annotations[1].enforcedTextAttributes, `is`(nullValue()))
        assertThat(annotations[1].textAttributes, `is`(SyntaxHighlighter.IDENTIFIER))
    }

    // endregion
}
