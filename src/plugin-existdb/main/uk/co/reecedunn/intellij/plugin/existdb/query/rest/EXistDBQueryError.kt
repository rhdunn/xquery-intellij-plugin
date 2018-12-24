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
package uk.co.reecedunn.intellij.plugin.existdb.query.rest

import uk.co.reecedunn.intellij.plugin.core.xml.XmlDocument
import uk.co.reecedunn.intellij.plugin.core.xml.children
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError

private val RE_EXISTDB_MESSAGE =
    "^(exerr:ERROR )?(org.exist.xquery.XPathException: )?([^ ]+) (.*)\n?$".toRegex()

private val RE_EXISTDB_LOCATION =
    "^line ([0-9]+), column ([0-9]+).*$".toRegex()

class EXistDBQueryError(exception: String) : QueryError() {
    private val xml = XmlDocument.parse(exception)
    private val parts by lazy {
        val messageText = xml.root.children("message").first().firstChild.nodeValue
        RE_EXISTDB_MESSAGE.matchEntire(messageText)?.groupValues
            ?: throw RuntimeException("Cannot parse eXist-db error message: $messageText")
    }
    private val locationParts = RE_EXISTDB_LOCATION.matchEntire(parts[4].substringAfter(" [at "))?.groupValues

    override val standardCode: String =
        (parts[3].let { if (it == "Type:") null else it } ?: "FOER0000").replace("^err:".toRegex(), "")

    override val vendorCode: String? = null

    override val description: String? = parts[4].substringBefore(" [at ")

    override val module: String? = xml.root.children("path").first().firstChild.nodeValue

    override val lineNumber: Int? = locationParts?.get(1)?.toIntOrNull()

    override val columnNumber: Int? = locationParts?.get(2)?.toIntOrNull()
}
