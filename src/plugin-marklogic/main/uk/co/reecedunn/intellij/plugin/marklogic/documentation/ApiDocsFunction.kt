/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.documentation

import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.core.xml.XmlElement
import uk.co.reecedunn.intellij.plugin.xdm.documentation.XdmFunctionDocumentation
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType

data class ApiDocsFunction(private val xml: XmlElement, val namespace: String?) : XdmFunctionDocumentation {
    // region apidoc:function

    val lib: String by lazy { xml.attribute("lib")!! }

    val name: String by lazy { xml.attribute("name")!! }

    val isBuiltin: Boolean by lazy { xml.attribute("type") == "builtin" }

    val category: String by lazy { xml.attribute("category")!! }

    val subcategory: String? by lazy { xml.attribute("subcategory") }

    val bucket: String? by lazy { xml.attribute("bucket") }

    // endregion
    // region XdmDocumentation

    override val href: String? = "https://docs.marklogic.com/$lib:$name"

    override val summary: String? by lazy { xml.child("apidoc:summary")?.innerXml() }

    override val notes: String? = null

    override fun examples(moduleType: XdmModuleType): Sequence<String> {
        return xml.children("apidoc:example").mapNotNull {
            val etype = when (val name = it.attribute("class")) {
                "javascript" -> XdmModuleType.JavaScript
                "xquery", null -> XdmModuleType.XQuery
                else -> throw UnsupportedOperationException("Unknown MarkLogic example class '$name'")
            }
            if (moduleType === etype) {
                val code = it.child("pre")?.text()!!.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                "<div class=\"example\"><pre xml:space=\"preserve\">${code}</pre></div>"
            } else
                null
        }
    }

    // endregion
    // region XdmFunctionDocumentation

    override val operatorMapping: String? = null

    override val signatures: String? = null

    override val properties: String? = null

    override val privileges: String? by lazy { xml.child("apidoc:privilege")?.innerXml() }

    override val rules: String? by lazy { xml.child("apidoc:usage")?.innerXml() }

    override val errorConditions: String? = null

    // endregion
}
