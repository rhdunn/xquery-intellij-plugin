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

import uk.co.reecedunn.intellij.plugin.core.xml.XmlDocument
import uk.co.reecedunn.intellij.plugin.core.xml.children

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
}
