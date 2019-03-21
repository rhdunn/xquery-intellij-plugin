/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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

import uk.co.reecedunn.intellij.plugin.core.xml.XmlDocument
import uk.co.reecedunn.intellij.plugin.processor.debug.StackFrame
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError

private val ERROR_NAMESPACES = mapOf(
    "err" to "http://www.w3.org/2005/xqt-errors",
    "dbg" to "http://reecedunn.co.uk/xquery/debug"
)

fun String.toMarkLogicError(): QueryError {
    val doc = XmlDocument.parse(this, ERROR_NAMESPACES)
    return QueryError(
        standardCode = doc.root.children("err:code").first().text()!!.replace("^err:".toRegex(), ""),
        vendorCode = doc.root.children("err:vendor-code").first().text(),
        description = doc.root.children("err:description").first().text(),
        value = doc.root.children("err:value").first().children("err:item").map { it.text()!! }.toList(),
        frames = doc.root.children("dbg:stack").first().children("dbg:frame").map {
            val module = it.children("dbg:module").first()
            val path = module.text()
            val line = module.attribute("line")?.toInt()
            val col = module.attribute("column")?.toInt()
            StackFrame(path, line, col)
        }.toList()
    )
}
