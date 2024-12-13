// Copyright (C) 2018, 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.parser

import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import uk.co.reecedunn.intellij.plugin.core.io.decode
import java.io.InputStream

/**
 * Convert Markdown text to HTML.
 *
 * This provides a consistent API that can be used in the plugin, allowing the
 * Markdown processor to be switched out.
 *
 * This logic also encodes formatting rules to make the resulting HTML usable
 * in the IntelliJ inspection description pane. Specifically:
 *
 * 1.  It wraps the generated markdown in `html` and `body` tags, as the
 *     IntelliJ inspection description rendering code looks for that to signify
 *     HTML content (otherwise, it assumes plain text and replaces all newlines
 *     with `<br/>` tags).
 *
 * 2.  The first <p> tag group is removed, otherwise IntelliJ will add a blank
 *     line (1em top margin) at the start of the description.
 */
object Markdown {
    private val flavour = GFMFlavourDescriptor()
    private val parser = MarkdownParser(flavour)

    fun parse(text: String): String {
        val root = parser.buildMarkdownTreeFromString(text)
        var html = HtmlGenerator(text, root, flavour).generateHtml()
            .replace("<body>", "")
            .replace("</body>", "")
        if (html.startsWith("<p>")) {
            val firstParagraph = html.substringBefore("</p>").replace("<p>", "")
            val otherParagraphs = html.substringAfter("</p>")
            html = firstParagraph + otherParagraphs
        }
        return "<html><body>$html</body></html>"
    }

    fun parse(text: InputStream): String = parse(text.decode())
}
