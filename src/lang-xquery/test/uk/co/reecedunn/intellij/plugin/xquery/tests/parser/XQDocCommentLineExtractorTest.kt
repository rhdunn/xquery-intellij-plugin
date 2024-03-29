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
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser

import com.intellij.util.Range
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQDocCommentLineExtractor

@DisplayName("IntelliJ - Custom Language Support - Code Folding - XQueryCommentLineExtractor")
class XQDocCommentLineExtractorTest {
    private fun matchStart(parser: XQDocCommentLineExtractor, isXQDoc: Boolean) {
        assertThat(parser.isXQDoc, `is`(isXQDoc))
        assertThat(parser.text, `is`(nullValue()))
        assertThat(parser.textRange, `is`(nullValue()))
    }

    private fun match(parser: XQDocCommentLineExtractor, text: String, textRange: Range<Int>) {
        assertThat(parser.next(), `is`(true))
        assertThat(parser.text, `is`(text))
        assertThat(parser.textRange, `is`(textRange))
    }

    private fun matchEof(parser: XQDocCommentLineExtractor) {
        assertThat(parser.next(), `is`(false))
        assertThat(parser.text, `is`(nullValue()))
        assertThat(parser.textRange, `is`(nullValue()))
    }

    @Nested
    @DisplayName("xquery comment")
    internal inner class XQueryComment {
        @Test
        @DisplayName("empty")
        fun empty() {
            val parser = XQDocCommentLineExtractor("")
            matchStart(parser, false)
            matchEof(parser)
        }

        @Test
        @DisplayName("single line")
        fun singleLine() {
            val parser = XQDocCommentLineExtractor("Lorem ipsum dolor")
            matchStart(parser, false)
            match(parser, "Lorem ipsum dolor", Range(0, 17))
            matchEof(parser)
        }

        @Test
        @DisplayName("multiple lines; trim only")
        fun multipleLines_Trim() {
            val parser = XQDocCommentLineExtractor("Lorem ipsum dolor\n Alpha beta gamma\r\n :One two three")
            matchStart(parser, false)
            match(parser, "Lorem ipsum dolor", Range(0, 17))
            match(parser, "Alpha beta gamma", Range(19, 35))
            match(parser, "One two three", Range(39, 52))
            matchEof(parser)
        }

        @Test
        @DisplayName("multiple lines; trim and whitespace")
        fun multipleLines_TrimAndWhitespace() {
            val parser = XQDocCommentLineExtractor("Lorem ipsum dolor\n : Alpha beta gamma\r\n : One two three")
            matchStart(parser, false)
            match(parser, "Lorem ipsum dolor", Range(0, 17))
            match(parser, "Alpha beta gamma", Range(21, 37))
            match(parser, "One two three", Range(42, 55))
            matchEof(parser)
        }

        @Test
        @DisplayName("blank line; trim only")
        fun blankLine_Trim() {
            val parser = XQDocCommentLineExtractor("Lorem ipsum dolor\n \n Alpha beta gamma")
            matchStart(parser, false)
            match(parser, "Lorem ipsum dolor", Range(0, 17))
            match(parser, "", Range(19, 19))
            match(parser, "Alpha beta gamma", Range(21, 37))
            matchEof(parser)
        }

        @Test
        @DisplayName("blank line; trim and whitespace")
        fun blankLine_TrimAndWhitespace() {
            val parser = XQDocCommentLineExtractor("Lorem ipsum dolor\n : \n : Alpha beta gamma")
            matchStart(parser, false)
            match(parser, "Lorem ipsum dolor", Range(0, 17))
            match(parser, "", Range(21, 21))
            match(parser, "Alpha beta gamma", Range(25, 41))
            matchEof(parser)
        }

        @Test
        @DisplayName("blank line at start; trim only")
        fun blankLineAtStart_Trim() {
            val parser = XQDocCommentLineExtractor("\n :Lorem ipsum dolor")
            matchStart(parser, false)
            match(parser, "Lorem ipsum dolor", Range(3, 20))
            matchEof(parser)
        }

        @Test
        @DisplayName("blank line at start; trim and whitespace")
        fun blankLineAtStart_TrimAndWhitespace() {
            val parser = XQDocCommentLineExtractor("\n : Lorem ipsum dolor")
            matchStart(parser, false)
            match(parser, "Lorem ipsum dolor", Range(4, 21))
            matchEof(parser)
        }

        @Test
        @DisplayName("blank line at start; multiple")
        fun blankLineAtStart_Multiple() {
            val parser = XQDocCommentLineExtractor("\n : \n : Lorem ipsum dolor")
            matchStart(parser, false)
            match(parser, "Lorem ipsum dolor", Range(8, 25))
            matchEof(parser)
        }
    }

    @Nested
    @DisplayName("description")
    internal inner class Description {
        @Test
        @DisplayName("empty")
        fun empty() {
            val parser = XQDocCommentLineExtractor("~")
            matchStart(parser, true)
            matchEof(parser)
        }

        @Test
        @DisplayName("single line")
        fun singleLine() {
            val parser = XQDocCommentLineExtractor("~Lorem ipsum dolor")
            matchStart(parser, true)
            match(parser, "Lorem ipsum dolor", Range(1, 18))
            matchEof(parser)
        }

        @Test
        @DisplayName("multiple lines; trim only")
        fun multipleLines_Trim() {
            val parser = XQDocCommentLineExtractor("~Lorem ipsum dolor\n Alpha beta gamma\r\n :One two three")
            matchStart(parser, true)
            match(parser, "Lorem ipsum dolor", Range(1, 18))
            match(parser, "Alpha beta gamma", Range(20, 36))
            match(parser, "One two three", Range(40, 53))
            matchEof(parser)
        }

        @Test
        @DisplayName("multiple lines; trim and whitespace")
        fun multipleLines_TrimAndWhitespace() {
            val parser = XQDocCommentLineExtractor("~Lorem ipsum dolor\n : Alpha beta gamma\r\n : One two three")
            matchStart(parser, true)
            match(parser, "Lorem ipsum dolor", Range(1, 18))
            match(parser, "Alpha beta gamma", Range(22, 38))
            match(parser, "One two three", Range(43, 56))
            matchEof(parser)
        }

        @Test
        @DisplayName("blank line; trim only")
        fun blankLine_Trim() {
            val parser = XQDocCommentLineExtractor("~Lorem ipsum dolor\n \n Alpha beta gamma")
            matchStart(parser, true)
            match(parser, "Lorem ipsum dolor", Range(1, 18))
            match(parser, "", Range(20, 20))
            match(parser, "Alpha beta gamma", Range(22, 38))
            matchEof(parser)
        }

        @Test
        @DisplayName("blank line; trim and whitespace")
        fun blankLine_TrimAndWhitespace() {
            val parser = XQDocCommentLineExtractor("~Lorem ipsum dolor\n : \n : Alpha beta gamma")
            matchStart(parser, true)
            match(parser, "Lorem ipsum dolor", Range(1, 18))
            match(parser, "", Range(22, 22))
            match(parser, "Alpha beta gamma", Range(26, 42))
            matchEof(parser)
        }

        @Test
        @DisplayName("blank line at start; trim only")
        fun blankLineAtStart_Trim() {
            val parser = XQDocCommentLineExtractor("~\n :Lorem ipsum dolor")
            matchStart(parser, true)
            match(parser, "Lorem ipsum dolor", Range(4, 21))
            matchEof(parser)
        }

        @Test
        @DisplayName("blank line at start; trim and whitespace")
        fun blankLineAtStart_TrimAndWhitespace() {
            val parser = XQDocCommentLineExtractor("~\n : Lorem ipsum dolor")
            matchStart(parser, true)
            match(parser, "Lorem ipsum dolor", Range(5, 22))
            matchEof(parser)
        }

        @Test
        @DisplayName("blank line at start; multiple")
        fun blankLineAtStart_Multiple() {
            val parser = XQDocCommentLineExtractor("~\n : \n : Lorem ipsum dolor")
            matchStart(parser, true)
            match(parser, "Lorem ipsum dolor", Range(9, 26))
            matchEof(parser)
        }
    }

    @Nested
    @DisplayName("predefined entities")
    internal inner class PredefinedEntityRef {
        @Test
        @DisplayName("in xquery comment")
        fun xqueryComment() {
            val parser = XQDocCommentLineExtractor("Alpha &amp; Beta &amp Gamma &; Delta") // valid; partial; empty
            matchStart(parser, false)
            match(parser, "Alpha &amp; Beta &amp Gamma &; Delta", Range(0, 36))
            matchEof(parser)
        }

        @Test
        @DisplayName("in xqdoc comment")
        fun xqdocComment() {
            val parser = XQDocCommentLineExtractor("~Alpha &amp; Beta &amp Gamma &; Delta") // valid; partial; empty
            matchStart(parser, true)
            match(parser, "Alpha &amp; Beta &amp Gamma &; Delta", Range(1, 37))
            matchEof(parser)
        }
    }

    @Nested
    @DisplayName("character references")
    internal inner class CharRef {
        @Test
        @DisplayName("in xquery comment")
        fun xqueryComment() {
            val parser = XQDocCommentLineExtractor("Alpha&#20;Beta")
            matchStart(parser, false)
            match(parser, "Alpha&#20;Beta", Range(0, 14))
            matchEof(parser)
        }

        @Test
        @DisplayName("in xqdoc comment")
        fun xqdocComment() {
            val parser = XQDocCommentLineExtractor("~Alpha&#20;Beta")
            matchStart(parser, true)
            match(parser, "Alpha&#20;Beta", Range(1, 15))
            matchEof(parser)
        }
    }

    @Nested
    @DisplayName("element")
    internal inner class DirElemConstructor {
        @Test
        @DisplayName("in xquery comment")
        fun xqueryComment() {
            val parser = XQDocCommentLineExtractor("Alpha <one lorem='ipsum'>Beta</one> Gamma <two/> Delta")
            matchStart(parser, false)
            match(parser, "Alpha <one lorem='ipsum'>Beta</one> Gamma <two/> Delta", Range(0, 54))
            matchEof(parser)
        }

        @Test
        @DisplayName("in xqdoc comment")
        fun xqdocComment() {
            val parser = XQDocCommentLineExtractor("~Alpha <one lorem='ipsum'>Beta</one> Gamma <two/> Delta")
            matchStart(parser, true)
            match(parser, "Alpha <one lorem='ipsum'>Beta</one> Gamma <two/> Delta", Range(1, 55))
            matchEof(parser)
        }
    }
}
