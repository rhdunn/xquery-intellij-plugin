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
import uk.co.reecedunn.intellij.plugin.core.xml.XmlElement
import uk.co.reecedunn.intellij.plugin.processor.debug.StackFrame
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileEntry
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileReport
import uk.co.reecedunn.intellij.plugin.xpath.model.XsDurationValue
import uk.co.reecedunn.intellij.plugin.xpath.model.toXsDuration

private val PROFILE_NAMESPACES = mapOf("prof" to "http://marklogic.com/xdmp/profile")

class MarkLogicProfileEntry(entry: XmlElement, script: String) : ProfileEntry {
    override val id: String by lazy {
        entry.children("prof:expr-id").first().text()!!
    }

    override val expression: String by lazy {
        entry.children("prof:expr-source").first().text()!!
    }

    override val frame: StackFrame by lazy {
        val path = entry.children("prof:uri").first().text()
        StackFrame(
            if (path.isNullOrEmpty()) script else path,
            entry.children("prof:line").first().text()?.toInt(),
            entry.children("prof:column").first().text()?.toInt()
        )
    }

    override val hits: Int by lazy {
        entry.children("prof:count").first().text()!!.toInt()
    }

    override val shallowTime: XsDurationValue by lazy {
        entry.children("prof:shallow-time").first().text()?.toXsDuration()!!
    }

    override val deepTime: XsDurationValue by lazy {
        entry.children("prof:deep-time").first().text()?.toXsDuration()!!
    }
}

fun String.toMarkLogicProfileReport(script: String): ProfileReport {
    val doc = XmlDocument.parse(this, PROFILE_NAMESPACES)
    val metadata = doc.root.children("prof:metadata").first()
    val histogram = doc.root.children("prof:histogram").first()
    return ProfileReport(
        xml = this,
        elapsed = metadata.children("prof:overall-elapsed").first().text()?.toXsDuration()!!,
        created = metadata.children("prof:created").first().text()!!,
        version = metadata.children("prof:server-version").first().text()!!,
        results = histogram.children("prof:expression").map { expression -> MarkLogicProfileEntry(expression, script) }
    )
}
