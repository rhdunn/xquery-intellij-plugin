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

import com.intellij.psi.PsiElement
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.xml.XmlElement
import uk.co.reecedunn.intellij.plugin.xdm.documentation.XdmFunctionDocumentation
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsNCName
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue

data class ApiDocsFunction(private val xml: XmlElement, override val namespace: XsAnyUriValue?) :
    XsQNameValue, XdmFunctionDocumentation {
    // region apidoc:function

    var moduleType: XdmModuleType = XdmModuleType.XQuery
        set(value) {
            field = value
            cachedExample.invalidate()
        }

    val isBuiltin: Boolean by lazy { xml.attribute("type") == "builtin" }

    val category: String by lazy { xml.attribute("category")!! }

    val subcategory: String? by lazy { xml.attribute("subcategory") }

    val bucket: String? by lazy { xml.attribute("bucket") }

    // endregion
    // region XsQNameValue

    override val prefix: XsNCName by lazy { XsNCName(xml.attribute("lib")!!, null as PsiElement?) }

    override val localName: XsNCName by lazy { XsNCName(xml.attribute("name")!!, null as PsiElement?) }

    override val isLexicalQName: Boolean = true

    override val element: PsiElement? = null

    // endregion
    // region XdmDocumentation

    override val href: String? = "https://docs.marklogic.com/${prefix.data}:${localName.data}"

    override val summary: String? by lazy { xml.child("apidoc:summary")?.innerXml() }

    override val notes: String? = null

    val cachedExample = CacheableProperty {
        xml.children("apidoc:example").mapNotNull {
            val type = when (it.attribute("class")) {
                "javascript" -> XdmModuleType.JavaScript
                else -> XdmModuleType.XQuery
            }
            if (type === moduleType) {
                val code = it.child("pre")?.text()!!.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                "<div class=\"example\"><pre xml:space=\"preserve\">${code}</pre></div>"
            } else
                null
        }.joinToString("\n").nullize()
    }

    override val examples: String? get() = cachedExample.get()

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
