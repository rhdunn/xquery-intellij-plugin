/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.query.rest

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.core.xml.XmlDocument
import uk.co.reecedunn.intellij.plugin.core.xml.children
import uk.co.reecedunn.intellij.plugin.intellij.xdebugger.frame.ModuleUriStackFrame
import uk.co.reecedunn.intellij.plugin.intellij.xdebugger.frame.VirtualFileStackFrame
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError

private val ERROR_NAMESPACES = mapOf(
    "err" to "http://www.w3.org/2005/xqt-errors",
    "error" to "http://marklogic.com/xdmp/error",
    "dbg" to "http://reecedunn.co.uk/xquery/debug"
)

fun String.toMarkLogicQueryError(queryFile: VirtualFile): QueryError {
    val doc = XmlDocument.parse(this, ERROR_NAMESPACES)
    val code = doc.root.children("error:code").first().text()!!
    val name = doc.root.children("error:name").firstOrNull()?.text() ?: "err:FOER0000"
    val message = doc.root.children("error:message").first().text()
    val formatString = doc.root.children("error:format-string").first().text()?.let {
        val value = if (it.startsWith("$code: ")) it.substringAfter(": ") else it
        if (value.startsWith("($name) ")) value.substringAfter(") ") else value
    }

    return QueryError(
        standardCode = name.replace("^err:".toRegex(), ""),
        vendorCode = if (code == message) null else code,
        description = message ?: formatString,
        value = doc.root.children("error:data").children("error:datum").mapNotNull { it.text() }.toList(),
        frames = doc.root.children("error:stack").children("error:frame").map { frame ->
            val path = frame.child("error:uri")?.text()?.nullize()
            val line = (frame.child("error:line")?.text()?.toIntOrNull() ?: 1) - 1
            val column = frame.child("error:column")?.text()?.toIntOrNull() ?: 0
            val context = frame.child("error:operation")?.text()?.nullize()
            when (path) {
                null -> VirtualFileStackFrame(queryFile, line, column, context)
                else -> ModuleUriStackFrame(path, line, column, context)
            }
        }.toList()
    )
}
