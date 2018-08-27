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
package uk.co.reecedunn.intellij.plugin.core.tests.parser

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.parser.Markdown
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat

// NOTE: This tests the Markdown generation to ensure that it is consistent.
class MarkdownTest {
    @Test
    fun onelineParagraph() {
        assertThat(
            Markdown.parse("Lorem ipsum dolor."),
            `is`("<html><body>\nLorem ipsum dolor.\n</body></html>")
        )
    }

    @Test
    fun multilineParagraph() {
        assertThat(
            Markdown.parse("Lorem ipsum dolor sit amet,\r\nconsectetur adipiscing elit."),
            `is`("<html><body>\nLorem ipsum dolor sit amet,\nconsectetur adipiscing elit.\n</body></html>")
        )
    }

    @Test
    fun multipleParagraphs() {
        assertThat(
            Markdown.parse("Lorem ipsum dolor sit amet,\r\n\r\nconsectetur adipiscing elit."),
            `is`("<html><body>\nLorem ipsum dolor sit amet,\n\n<p>consectetur adipiscing elit.</p>\n</body></html>")
        )
    }

    @Test
    fun code() {
        assertThat(
            Markdown.parse("Lorem `ipsum` dolor sit amet."),
            `is`("<html><body>\nLorem <code>ipsum</code> dolor sit amet.\n</body></html>")
        )
    }

    @Test
    fun emphasis() {
        assertThat(
            Markdown.parse("Lorem *ipsum* dolor sit amet."),
            `is`("<html><body>\nLorem <em>ipsum</em> dolor sit amet.\n</body></html>")
        )
    }

    @Test
    fun strong() {
        assertThat(
            Markdown.parse("Lorem __ipsum__ dolor sit amet."),
            `is`("<html><body>\nLorem <strong>ipsum</strong> dolor sit amet.\n</body></html>")
        )
    }

    @Test
    fun onelinePre() {
        assertThat(
            Markdown.parse("    Lorem ipsum dolor sit amet."),
            `is`("<html><body>\n<pre><code>    Lorem ipsum dolor sit amet.\n</code></pre>\n</body></html>")
        )
    }

    @Test
    fun multilinePre() {
        assertThat(
            Markdown.parse("    Lorem ipsum dolor sit amet,\r\n    consectetur adipiscing elit."),
            `is`(
                "<html><body>\n<pre><code>    Lorem ipsum dolor sit amet,\n" +
                        "    consectetur adipiscing elit.\n</code></pre>\n</body></html>"
            )
        )
    }
}
