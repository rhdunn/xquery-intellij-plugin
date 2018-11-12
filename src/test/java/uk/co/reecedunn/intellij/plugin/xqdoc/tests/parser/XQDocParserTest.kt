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
package uk.co.reecedunn.intellij.plugin.xqdoc.tests.parser

import com.intellij.psi.tree.IElementType
import com.intellij.util.Range
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xqdoc.parser.XQDocElementType
import uk.co.reecedunn.intellij.plugin.xqdoc.parser.XQDocParser

@DisplayName("xqDoc - Parser")
class XQDocParserTest {
    private fun matchStart(parser: XQDocParser, isXQDoc: Boolean) {
        assertThat(parser.isXQDoc, `is`(isXQDoc))
        assertThat(parser.elementType, `is`(nullValue()))
        assertThat(parser.text, `is`(nullValue()))
        assertThat(parser.textRange, `is`(nullValue()))
    }

    private fun match(parser: XQDocParser, elementType: IElementType, text: String, textRange: Range<Int>) {
        assertThat(parser.next(), `is`(true))
        assertThat(parser.elementType, `is`(elementType))
        assertThat(parser.text, `is`(text))
        assertThat(parser.textRange, `is`(textRange))
    }

    private fun matchEof(parser: XQDocParser) {
        assertThat(parser.next(), `is`(false))
        assertThat(parser.elementType, `is`(nullValue()))
        assertThat(parser.text, `is`(nullValue()))
        assertThat(parser.textRange, `is`(nullValue()))
    }

    @Nested
    @DisplayName("xquery comment")
    internal inner class XQueryComment {
        @Test
        @DisplayName("empty")
        fun empty() {
            val parser = XQDocParser("")
            matchStart(parser, false)
            matchEof(parser)
        }

        @Test
        @DisplayName("xquery comment")
        fun xqueryComment() {
            val parser = XQDocParser("Lorem ipsum dolor")
            matchStart(parser, false)
            matchEof(parser)
        }
    }

    @Nested
    @DisplayName("description")
    internal inner class Description {
        @Test
        @DisplayName("empty")
        fun empty() {
            val parser = XQDocParser("~")
            matchStart(parser, true)
            matchEof(parser)
        }

        @Test
        @DisplayName("single line")
        fun singleLine() {
            val parser = XQDocParser("~Lorem ipsum dolor")
            matchStart(parser, true)
            match(parser, XQDocElementType.DESCRIPTION_LINE, "Lorem ipsum dolor", Range(1, 18))
            matchEof(parser)
        }

        @Test
        @DisplayName("multiple lines; trim only")
        fun multipleLines_Trim() {
            val parser = XQDocParser("~Lorem ipsum dolor\n Alpha beta gamma\r\n :One two three")
            matchStart(parser, true)
            match(parser, XQDocElementType.DESCRIPTION_LINE, "Lorem ipsum dolor", Range(1, 18))
            match(parser, XQDocElementType.DESCRIPTION_LINE, "Alpha beta gamma", Range(20, 36))
            match(parser, XQDocElementType.DESCRIPTION_LINE, "One two three", Range(40, 53))
            matchEof(parser)
        }

        @Test
        @DisplayName("multiple lines; trim and whitespace")
        fun multipleLines_TrimAndWhitespace() {
            val parser = XQDocParser("~Lorem ipsum dolor\n : Alpha beta gamma\r\n : One two three")
            matchStart(parser, true)
            match(parser, XQDocElementType.DESCRIPTION_LINE, "Lorem ipsum dolor", Range(1, 18))
            match(parser, XQDocElementType.DESCRIPTION_LINE, "Alpha beta gamma", Range(22, 38))
            match(parser, XQDocElementType.DESCRIPTION_LINE, "One two three", Range(43, 56))
            matchEof(parser)
        }
    }
}
