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
    @Nested
    @DisplayName("xquery comment")
    internal inner class XQueryComment {
        @Test
        @DisplayName("empty")
        fun empty() {
            val parser = XQDocParser("")
            assertThat(parser.isXQDoc, `is`(false))
            assertThat(parser.elementType, `is`(nullValue()))
            assertThat(parser.text, `is`(nullValue()))
            assertThat(parser.textRange, `is`(nullValue()))

            assertThat(parser.next(), `is`(false))
            assertThat(parser.elementType, `is`(nullValue()))
            assertThat(parser.text, `is`(nullValue()))
            assertThat(parser.textRange, `is`(nullValue()))
        }

        @Test
        @DisplayName("xquery comment")
        fun xqueryComment() {
            val parser = XQDocParser("Lorem ipsum dolor")
            assertThat(parser.isXQDoc, `is`(false))
            assertThat(parser.elementType, `is`(nullValue()))
            assertThat(parser.text, `is`(nullValue()))
            assertThat(parser.textRange, `is`(nullValue()))

            assertThat(parser.next(), `is`(false))
            assertThat(parser.elementType, `is`(nullValue()))
            assertThat(parser.text, `is`(nullValue()))
            assertThat(parser.textRange, `is`(nullValue()))
        }
    }

    @Nested
    @DisplayName("description")
    internal inner class Description {
        @Test
        @DisplayName("empty")
        fun empty() {
            val parser = XQDocParser("~")
            assertThat(parser.isXQDoc, `is`(true))
            assertThat(parser.elementType, `is`(nullValue()))
            assertThat(parser.text, `is`(nullValue()))
            assertThat(parser.textRange, `is`(nullValue()))

            assertThat(parser.next(), `is`(false))
            assertThat(parser.elementType, `is`(nullValue()))
            assertThat(parser.text, `is`(nullValue()))
            assertThat(parser.textRange, `is`(nullValue()))
        }

        @Test
        @DisplayName("single line")
        fun singleLine() {
            val parser = XQDocParser("~Lorem ipsum dolor")
            assertThat(parser.isXQDoc, `is`(true))
            assertThat(parser.elementType, `is`(nullValue()))
            assertThat(parser.text, `is`(nullValue()))
            assertThat(parser.textRange, `is`(nullValue()))

            assertThat(parser.next(), `is`(true))
            assertThat(parser.elementType, `is`(XQDocElementType.DESCRIPTION))
            assertThat(parser.text, `is`("Lorem ipsum dolor"))
            assertThat(parser.textRange, `is`(Range(1, 18)))

            assertThat(parser.next(), `is`(false))
            assertThat(parser.elementType, `is`(nullValue()))
            assertThat(parser.text, `is`(nullValue()))
            assertThat(parser.textRange, `is`(nullValue()))
        }
    }
}
