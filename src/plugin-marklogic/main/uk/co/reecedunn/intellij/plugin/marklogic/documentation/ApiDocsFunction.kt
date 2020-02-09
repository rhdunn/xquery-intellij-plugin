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

import uk.co.reecedunn.intellij.plugin.core.text.camelCase
import uk.co.reecedunn.intellij.plugin.core.xml.XmlElement
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.XQDocFunctionDocumentation
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType

data class ApiDocsFunction(private val xml: XmlElement, val namespace: String?) : XQDocFunctionDocumentation {
    // region apidoc:function

    val lib: String by lazy { xml.attribute("lib")!! }

    fun name(moduleType: XdmModuleType): String = when (moduleType) {
        XdmModuleType.XPath, XdmModuleType.XQuery -> xml.attribute("name")!!
        XdmModuleType.JavaScript -> {
            val jsname = xml.children("apidoc:name").filter { it.attribute("class") == "javascript" }.firstOrNull()
            jsname?.text() ?: xml.attribute("name")!!.camelCase()
        }
        else -> throw UnsupportedOperationException("No name for $moduleType.")
    }

    val isBuiltin: Boolean by lazy { xml.attribute("type") == "builtin" }

    val category: String by lazy { xml.attribute("category")!! }

    val subcategory: String? by lazy { xml.attribute("subcategory") }

    val bucket: String? by lazy { xml.attribute("bucket") }

    // endregion
    // region XdmDocumentation

    override val moduleTypes: Array<XdmModuleType> by lazy { xml.moduleTypes }

    override fun href(moduleType: XdmModuleType): String? = when (moduleType) {
        XdmModuleType.JavaScript -> "https://docs.marklogic.com/$lib.${name(moduleType)}"
        else -> "https://docs.marklogic.com/$lib:${name(moduleType)}"
    }

    override fun summary(moduleType: XdmModuleType): String? {
        return xml.children("apidoc:summary").find { it.moduleTypes.contains(moduleType) }?.innerXml()
    }

    override fun notes(moduleType: XdmModuleType): String? = null

    override fun examples(moduleType: XdmModuleType): Sequence<String> {
        return xml.children("apidoc:example").mapNotNull {
            if (it.moduleTypes.contains(moduleType)) {
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

    override fun rules(moduleType: XdmModuleType): String? {
        return xml.children("apidoc:usage").find { it.moduleTypes.contains(moduleType) }?.innerXml()
    }

    override val errorConditions: String? = null

    // endregion
}
