/*
 * Copyright (C) 2018-2021 Reece H. Dunn
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

import com.google.gson.JsonObject
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.core.gson.getOrNull
import uk.co.reecedunn.intellij.plugin.core.xml.dom.XmlDocument
import uk.co.reecedunn.intellij.plugin.core.xml.dom.XmlElement
import uk.co.reecedunn.intellij.plugin.core.xml.dom.children
import uk.co.reecedunn.intellij.plugin.processor.debug.frame.VirtualFileStackFrame
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModuleUri

private val ERROR_NAMESPACES = mapOf(
    "err" to "http://www.w3.org/2005/xqt-errors",
    "error" to "http://marklogic.com/xdmp/error",
    "dbg" to "http://reecedunn.co.uk/xquery/debug"
)

fun String.toMarkLogicQueryError(queryFile: VirtualFile?): QueryError {
    val doc = XmlDocument.parse(this, ERROR_NAMESPACES)
    return doc.root.toMarkLogicQueryError(queryFile)
}

fun XmlElement.toMarkLogicQueryError(queryFile: VirtualFile?): QueryError {
    val code = children("error:code").first().text()!!
    val name = children("error:name").firstOrNull()?.text() ?: "err:FOER0000"
    val message = children("error:message").first().text()
    val formatString = children("error:format-string").first().text()?.let {
        val value = if (it.startsWith("$code: ")) it.substringAfter(": ") else it
        if (value.startsWith("($name) ")) value.substringAfter(") ") else value
    }

    return QueryError(
        standardCode = name.replace("^err:".toRegex(), ""),
        vendorCode = if (code == message) null else code,
        description = message ?: formatString,
        value = children("error:data").children("error:datum").mapNotNull { it.text() }.toList(),
        frames = children("error:stack").children("error:frame").mapNotNull { frame ->
            val path = frame.child("error:uri")?.text()?.nullize()
            val line = (frame.child("error:line")?.text()?.toIntOrNull() ?: 1) - 1
            val column = frame.child("error:column")?.text()?.toIntOrNull() ?: 0
            val context = frame.child("error:operation")?.text()?.nullize()
            when (path) {
                null -> queryFile?.let { VirtualFileStackFrame(queryFile, line, column, context) }
                "/eval" -> null
                else -> VirtualFileStackFrame(XpmModuleUri(queryFile, path), line, column, context)
            }
        }.toList()
    )
}

fun JsonObject.toMarkLogicQueryError(queryFile: VirtualFile?): QueryError {
    val code = get("code").asString
    val name = get("name").asString
    val message = get("message").asString
    val formatString = get("format-string").asString.nullize()?.let {
        val value = if (it.startsWith("$code: ")) it.substringAfter(": ") else it
        if (value.startsWith("($name) ")) value.substringAfter(") ") else value
    }
    val data = getOrNull("data")

    return QueryError(
        standardCode = name.replace("^err:".toRegex(), ""),
        vendorCode = if (code == message) null else code,
        description = message ?: formatString,
        value = when {
            data == null -> listOf()
            data.isJsonObject -> listOf(data.asJsonObject.get("datum").asString)
            data.isJsonArray -> data.asJsonArray.map { it.asJsonObject.get("datum").asString }.toList()
            else -> listOf()
        },
        frames = getAsJsonObject("stack").getAsJsonArray("frame").mapNotNull {
            val frame = it.asJsonObject
            val path = frame.getOrNull("uri")?.asString
            val line = (frame.get("line").asString.toIntOrNull() ?: 1) - 1
            val column = frame.get("column").asString.toIntOrNull() ?: 0
            val context = frame.getOrNull("context")?.asString
            when (path) {
                null -> queryFile?.let { VirtualFileStackFrame(queryFile, line, column, context) }
                "/eval" -> null
                else -> VirtualFileStackFrame(XpmModuleUri(queryFile, path), line, column, context)
            }
        }.toList()
    )
}
