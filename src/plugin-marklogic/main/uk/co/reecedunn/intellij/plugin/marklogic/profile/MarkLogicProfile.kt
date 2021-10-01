/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.core.xml.dom.XmlDocument
import uk.co.reecedunn.intellij.plugin.core.xml.dom.XmlElement
import uk.co.reecedunn.intellij.plugin.processor.debug.frame.VirtualFileStackFrame
import uk.co.reecedunn.intellij.plugin.processor.profile.FlatProfileEntry
import uk.co.reecedunn.intellij.plugin.processor.profile.FlatProfileReport
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsDuration
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.toXsDuration
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModuleUri

private val PROFILE_NAMESPACES = mapOf("prof" to "http://marklogic.com/xdmp/profile")

private fun XmlElement.toProfileEntry(queryFile: VirtualFile): FlatProfileEntry {
    val path = child("prof:uri")?.text()?.nullize()
    val line = (child("prof:line")?.text()?.toIntOrNull() ?: 1) - 1
    val column = child("prof:column")?.text()?.toIntOrNull() ?: 0
    val frame = when (path) {
        null -> VirtualFileStackFrame(queryFile, line, column)
        else -> VirtualFileStackFrame(XpmModuleUri(queryFile, path), line, column)
    }

    return FlatProfileEntry(
        id = children("prof:expr-id").first().text()!!,
        context = children("prof:expr-source").first().text()!!,
        frame = frame,
        count = children("prof:count").first().text()!!.toInt(),
        selfTime = children("prof:shallow-time").first().text()?.toXsDuration()!!,
        totalTime = children("prof:deep-time").first().text()?.toXsDuration()!!
    )
}

fun String.toMarkLogicProfileReport(queryFile: VirtualFile): FlatProfileReport {
    val doc = XmlDocument.parse(this, PROFILE_NAMESPACES)
    val metadata = doc.root.children("prof:metadata").first()
    val histogram = doc.root.children("prof:histogram").first()
    val elapsed = metadata.children("prof:overall-elapsed").first().text()?.toXsDuration()!!
    return FlatProfileReport(
        xml = this,
        elapsed = elapsed,
        created = metadata.children("prof:created").first().text()!!,
        version = metadata.children("prof:server-version").first().text()!!,
        results = sequenceOf(
            sequenceOf(FlatProfileEntry("", "", 1, XsDuration.ZERO, elapsed, VirtualFileStackFrame(queryFile))),
            histogram.children("prof:expression").map { expression -> expression.toProfileEntry(queryFile) }
        ).flatten()
    )
}
