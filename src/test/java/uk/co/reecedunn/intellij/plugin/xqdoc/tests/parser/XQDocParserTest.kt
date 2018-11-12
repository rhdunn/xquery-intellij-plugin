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

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xqdoc.parser.XQDocParser

@DisplayName("xqDoc - Parser")
class XQDocParserTest {
    @Test
    @DisplayName("empty comment")
    fun emptyComment() {
        val parser = XQDocParser("")
        assertThat(parser.isXQDoc, `is`(false))

        assertThat(parser.next(), `is`(false))
    }

    @Test
    @DisplayName("xquery comment (no documentation)")
    fun xqueryComment() {
        val parser = XQDocParser("Lorem ipsum dolor")
        assertThat(parser.isXQDoc, `is`(false))

        assertThat(parser.next(), `is`(false))
    }

    @Test
    @DisplayName("xqdoc comment")
    fun xqdocComment() {
        val parser = XQDocParser("~Lorem ipsum dolor")
        assertThat(parser.isXQDoc, `is`(true))

        assertThat(parser.next(), `is`(false))
    }
}
