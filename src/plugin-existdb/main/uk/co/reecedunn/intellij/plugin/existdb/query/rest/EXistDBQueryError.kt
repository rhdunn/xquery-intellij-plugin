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
package uk.co.reecedunn.intellij.plugin.existdb.query.rest

import com.intellij.openapi.vfs.VirtualFile
import org.jsoup.Jsoup
import uk.co.reecedunn.intellij.plugin.core.xml.dom.XmlDocument
import uk.co.reecedunn.intellij.plugin.processor.debug.frame.VirtualFileStackFrame
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModuleUri

@Suppress("RegExpAnonymousGroup")
private val RE_EXISTDB_MESSAGE =
    "^(exerr:ERROR )?(org.exist.xquery.XPathException: )?([^ ]+) (.*)\n?$".toRegex()

@Suppress("RegExpAnonymousGroup")
private val RE_EXISTDB_LOCATION =
    "^line ([0-9]+), column ([0-9]+).*$".toRegex()

fun String.toEXistDBQueryError(queryFile: VirtualFile): QueryError = when {
    this.startsWith("<html>") -> parseMessageHtml()
    else -> this.parseMessageXml(queryFile)
}

private fun String.parseMessageHtml(): QueryError {
    val html = Jsoup.parse(this)
    return QueryError(
        standardCode = "FOER0000",
        vendorCode = null,
        description = html.select("title").text(),
        value = listOf(),
        frames = listOf()
    )
}

private fun String.parseMessageXml(queryFile: VirtualFile): QueryError {
    val xml = XmlDocument.parse(this, mapOf())
    val messageText = xml.root.children("message").first().text()!!.split("\n")[0]
    val parts =
        RE_EXISTDB_MESSAGE.matchEntire(messageText)?.groupValues
            ?: throw RuntimeException("Cannot parse eXist-db error message: $messageText")
    val locationParts = RE_EXISTDB_LOCATION.matchEntire(parts[4].substringAfter(" [at "))?.groupValues

    val path = xml.root.children("path").first().text()
    val line = locationParts?.get(1)?.toIntOrNull() ?: 1
    val col = locationParts?.get(2)?.toIntOrNull() ?: 1

    val frame = when (path) {
        null, "/db" -> VirtualFileStackFrame(queryFile, line - 1, col - 1)
        else -> VirtualFileStackFrame(XpmModuleUri(queryFile, path), line - 1, col - 1)
    }
    return QueryError(
        standardCode = (parts[3].let { if (it == "Type:") null else it } ?: "FOER0000").replace("^err:".toRegex(), ""),
        vendorCode = null,
        description = parts[4].substringBefore(" [at "),
        value = listOf(),
        frames = listOf(frame)
    )
}
