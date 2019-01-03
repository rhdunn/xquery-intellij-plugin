/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.profile

import org.w3c.dom.Element
import uk.co.reecedunn.intellij.plugin.core.xml.XmlDocument
import uk.co.reecedunn.intellij.plugin.core.xml.children
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.MarkLogicQueryError

class MarkLogicProfileEntry(entry: Element) {
    companion object {
        private val XMLNS_PROF = "http://marklogic.com/xdmp/profile"
    }

    val id: String by lazy {
        entry.children(XMLNS_PROF, "expr-id").first().firstChild!!.nodeValue
    }

    val expression: String by lazy {
        entry.children(XMLNS_PROF, "expr-source").first().firstChild!!.nodeValue
    }

    val module: String? by lazy {
        entry.children(XMLNS_PROF, "uri").first().firstChild?.nodeValue
    }

    val lineNumber: Int? by lazy {
        entry.children(XMLNS_PROF, "line").first().firstChild?.nodeValue?.toInt()
    }

    val columnNumber: Int? by lazy {
        entry.children(XMLNS_PROF, "column").first().firstChild?.nodeValue?.toInt()
    }

    val hits: Int by lazy {
        entry.children(XMLNS_PROF, "count").first().firstChild!!.nodeValue.toInt()
    }

    val shallowTime: String by lazy {
        entry.children(XMLNS_PROF, "shallow-time").first().firstChild!!.nodeValue
    }

    val deepTime: String by lazy {
        entry.children(XMLNS_PROF, "deep-time").first().firstChild!!.nodeValue
    }
}

class MarkLogicProfile(xml: String) {
    companion object {
        private val XMLNS_PROF = "http://marklogic.com/xdmp/profile"
    }

    private val doc = XmlDocument.parse(xml)

    val elapsed: String by lazy {
        val metadata = doc.root.children(XMLNS_PROF, "metadata").first()
        metadata.children(XMLNS_PROF, "overall-elapsed").first().firstChild!!.nodeValue
    }

    val created: String by lazy {
        val metadata = doc.root.children(XMLNS_PROF, "metadata").first()
        metadata.children(XMLNS_PROF, "created").first().firstChild!!.nodeValue
    }

    val version: String by lazy {
        val metadata = doc.root.children(XMLNS_PROF, "metadata").first()
        metadata.children(XMLNS_PROF, "server-version").first().firstChild!!.nodeValue
    }

    val results: Sequence<MarkLogicProfileEntry>
        get() {
            val histogram = doc.root.children(XMLNS_PROF, "histogram").first()
            return histogram.children(XMLNS_PROF, "expression").map { expression -> MarkLogicProfileEntry(expression) }
        }
}
