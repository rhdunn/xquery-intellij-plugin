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

class MarkLogicQueryError(xml: String) : QueryError() {
    companion object {
        private val ERROR_NAMESPACES = mapOf(
            "err" to "http://www.w3.org/2005/xqt-errors",
            "dbg" to "http://reecedunn.co.uk/xquery/debug"
        )
    }

    private val doc = XmlDocument.parse(xml, ERROR_NAMESPACES)

    override val value: List<String> by lazy {
        doc.root.children("err:value").first().children("err:item").map { it.firstChild!!.nodeValue }.toList()
    }

    override val standardCode: String by lazy {
        doc.root.children("err:code").first().firstChild!!.nodeValue.replace("^err:".toRegex(), "")
    }

    override val vendorCode: String? by lazy {
        doc.root.children("err:vendor-code").first().firstChild?.nodeValue
    }

    override val description: String? by lazy {
        doc.root.children("err:description").first().firstChild?.nodeValue
    }

    override val frames: List<StackFrame> by lazy {
        doc.root.children("dbg:stack").first().children("dbg:frame").map {
            val module = it.children("dbg:module").first()
            val path = module.firstChild?.nodeValue
            val line = module.attribute("line")?.toInt()
            val col = module.attribute("column")?.toInt()
            StackFrame(path, line, col)
        }.toList()
    }
}
