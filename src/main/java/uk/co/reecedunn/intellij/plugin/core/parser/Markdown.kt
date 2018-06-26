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
package uk.co.reecedunn.intellij.plugin.core.parser

import com.petebevin.markdown.MarkdownProcessor
import org.apache.xmlbeans.impl.common.IOUtil
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter

/**
 * Convert Markdown text to HTML.
 *
 * This provides a consistent API that can be used in the plugin, allowing the
 * Markdown processor to be switched out. It also wraps the generated markdown
 * in `html` and `body` tags, as the IntelliJ inspection description rendering
 * code looks for that to signify HTML content (otherwise, it assumes plain
 * text and replaces all newlines with `<br/>` tags).
 */
object Markdown {
    val md = MarkdownProcessor()

    fun parse(text: String): String = "<html><body>\n${md.markdown(text)}</body></html>"

    fun parse(text: InputStream): String = parse(streamToString(text))

    private fun streamToString(stream: InputStream): String {
        val writer = StringWriter()
        IOUtil.copyCompletely(InputStreamReader(stream), writer)
        return writer.toString()
    }
}
