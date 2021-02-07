/*
 * Copyright (C) 2019-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.w3.documentation

import com.intellij.navigation.ItemPresentation
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.*
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionReference
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmSpecificationType

internal class W3CFunctionReference(private val node: Element, baseHref: String) :
    XQDocFunctionDocumentation {
    private fun normalize(node: Element): Element {
        // JEditorPanel does not support vertical-align on tr/td elements, so use valign instead.
        node.select("tr").forEach { it.attr("valign", "top") }
        return node
    }

    val id: String = node.selectFirst("* > a").attr("id")

    override val moduleTypes: Array<XdmModuleType> = XdmModuleType.XPATH_OR_XQUERY

    override val href: String = "$baseHref#$id"

    private fun section(name: String): String? {
        val section = node.select("dl > dt").find { it.text() == name }?.nextElementSibling()
        return section?.let { normalize(it).html() }
    }

    override val summary: String?
        get() = section("Summary")

    override val notes: String?
        get() = section("Notes")

    override val examples: String?
        get() = section("Examples")

    override val operatorMapping: String?
        get() = section("Operator Mapping")

    override val signatures: String?
        get() = section("Signatures") ?: section("Signature")

    override val parameters: String? = null

    override val properties: String?
        get() = section("Properties")

    override val privileges: String? = null

    override val rules: String?
        get() = section("Rules")

    override val errorConditions: String?
        get() = section("Error Conditions")
}

internal data class W3CSpecificationDocument(
    val type: XpmSpecificationType,
    override val href: String,
    val id: String,
    override val version: String,
    private val namespaces: Map<String, String>
) : XQDocDocumentationSource {
    // region XQDocDocumentationSource

    override val presentation: ItemPresentation = type.presentation

    override val path: String = "w3/${type.id}-$id.html"

    // endregion
    // region XQDocDocumentationIndex

    private val doc = CacheableProperty {
        val file = XQDocDocumentationDownloader.getInstance().load(this, download = true)
        file?.let { Jsoup.parse(it.inputStream, null, "") }
    }

    override fun invalidate() = doc.invalidate()

    fun lookup(ref: XpmFunctionReference): XQDocFunctionDocumentation? {
        val prefix = namespaces[ref.functionName?.namespace?.data] ?: return null
        val localName = ref.functionName?.localName?.data ?: return null
        val lookupName = "$prefix:$localName"
        val match = doc.get()?.select("h3 > a, h4 > a")?.firstOrNull {
            val parts = (it.nextSibling() as? TextNode)?.text()?.split(" ") ?: return@firstOrNull false
            val name = parts.asReversed().find { part -> part.isNotEmpty() }
            name == lookupName
        }
        return match?.let { W3CFunctionReference(it.parent().parent(), href) }
    }

    // endregion
}
