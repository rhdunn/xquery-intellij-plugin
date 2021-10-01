/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.xray.format.xray

import com.intellij.lang.Language
import com.intellij.lang.xml.XMLLanguage
import uk.co.reecedunn.intellij.plugin.core.xml.dom.XmlDocument
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.test.TestFormat
import uk.co.reecedunn.intellij.plugin.processor.test.TestSuites

object XRayXmlFormat : TestFormat {
    override val id: String = "xml"

    override val name: String = "XRay XML"

    override val language: Language by lazy { XMLLanguage.INSTANCE }

    override fun parse(result: QueryResult): TestSuites? {
        if (result.mimetype != QueryResult.APPLICATION_XML) return null
        val doc = XmlDocument.parse(result.value as String, NAMESPACES)
        return when {
            doc.root.`is`("xray:tests") -> XRayXmlTests(doc.root)
            else -> null
        }
    }

    override fun toString(): String = name

    internal val NAMESPACES = mapOf(
        "dbg" to "http://reecedunn.co.uk/xquery/debug",
        "err" to "http://www.w3.org/2005/xqt-errors",
        "error" to "http://marklogic.com/xdmp/error",
        "xray" to "http://github.com/robwhitby/xray"
    )
}
