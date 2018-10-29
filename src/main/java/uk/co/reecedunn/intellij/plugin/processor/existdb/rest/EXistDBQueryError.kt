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
package uk.co.reecedunn.intellij.plugin.processor.existdb.rest

import uk.co.reecedunn.intellij.plugin.core.xml.XmlDocument
import uk.co.reecedunn.intellij.plugin.core.xml.children
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError

private val RE_EXISTDB_MESSAGE =
    "^exerr:ERROR (org.exist.xquery.XPathException: )?([^ ]+) (.*)( \\[at line ([0-9]+), column ([0-9]+)]\n)?$".toRegex()

class EXistDBQueryError(exception: String) : QueryError() {
    private val xml = XmlDocument.parse(exception)
    private val msg = xml.root.children("message").first().firstChild.nodeValue
    private val parts = RE_EXISTDB_MESSAGE.matchEntire(msg)?.groupValues

    override val standardCode: String =
        (parts?.get(2)?.let { if (it == "Type:") null else it } ?: "FOER0000").replace("^err:".toRegex(), "")

    override val vendorCode: String? = null

    override val description: String? = parts?.get(3) ?: msg

    override val module: String? = xml.root.children("path").first().firstChild.nodeValue

    override val lineNumber: Int? = parts?.get(5)?.toIntOrNull()

    override val columnNumber: Int? = parts?.get(6)?.toIntOrNull()
}
