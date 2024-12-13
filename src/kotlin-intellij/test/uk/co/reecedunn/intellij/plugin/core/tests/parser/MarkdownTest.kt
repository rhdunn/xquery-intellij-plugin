// Copyright (C) 2018, 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.parser

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.parser.Markdown

// NOTE: This tests the Markdown generation to ensure that it is consistent.
@DisplayName("IntelliJ - Custom Language Support - Code Inspections and Intentions - Markdown")
class MarkdownTest {
    @Test
    @DisplayName("paragraph; single line")
    fun onelineParagraph() {
        assertThat(
            Markdown.parse("Lorem ipsum dolor."),
            `is`("<html><body>Lorem ipsum dolor.</body></html>")
        )
    }

    @Test
    @DisplayName("paragraph; multiple lines")
    fun multilineParagraph() {
        assertThat(
            Markdown.parse("Lorem ipsum dolor sit amet,\r\nconsectetur adipiscing elit."),
            `is`("<html><body>Lorem ipsum dolor sit amet,\r\nconsectetur adipiscing elit.</body></html>")
        )
    }

    @Test
    @DisplayName("paragraph; multiple paragraphs")
    fun multipleParagraphs() {
        assertThat(
            Markdown.parse("Lorem ipsum dolor sit amet,\r\n\r\nconsectetur adipiscing elit."),
            `is`("<html><body>Lorem ipsum dolor sit amet,\r\n\r\nconsectetur adipiscing elit.</body></html>")
        )
    }

    @Test
    @DisplayName("inline content: code")
    fun code() {
        assertThat(
            Markdown.parse("Lorem `ipsum` dolor sit amet."),
            `is`("<html><body>Lorem <code>ipsum</code> dolor sit amet.</body></html>")
        )
    }

    @Test
    @DisplayName("inline content: emphasis")
    fun emphasis() {
        assertThat(
            Markdown.parse("Lorem *ipsum* dolor sit amet."),
            `is`("<html><body>Lorem <em>ipsum</em> dolor sit amet.</body></html>")
        )
    }

    @Test
    @DisplayName("inline content: strong")
    fun strong() {
        assertThat(
            Markdown.parse("Lorem __ipsum__ dolor sit amet."),
            `is`("<html><body>Lorem <strong>ipsum</strong> dolor sit amet.</body></html>")
        )
    }

    @Test
    @DisplayName("pre; single line")
    fun onelinePre() {
        assertThat(
            Markdown.parse("    Lorem ipsum dolor sit amet."),
            `is`("<html><body><pre><code>Lorem ipsum dolor sit amet.\n</code></pre></body></html>")
        )
    }

    @Test
    @DisplayName("pre; multiple lines")
    fun multilinePre() {
        assertThat(
            Markdown.parse("    Lorem ipsum dolor sit amet,\r\n    consectetur adipiscing elit."),
            `is`(
                "<html><body><pre><code>Lorem ipsum dolor sit amet,\r\n" +
                        "consectetur adipiscing elit.\n</code></pre></body></html>"
            )
        )
    }
}
