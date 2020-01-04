/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.xdm.documentation.*
import uk.co.reecedunn.intellij.plugin.xdm.functions.XdmFunctionReference
import uk.co.reecedunn.intellij.plugin.xdm.lang.XdmSpecificationType
import uk.co.reecedunn.intellij.plugin.xdm.namespaces.XdmNamespaceDeclaration

internal class W3CFunctionReference(node: Element, baseHref: String): XdmDocumentationReference {
    private fun normalize(node: Element): Element {
        // JEditorPanel does not support vertical-align on tr/td elements, so use valign instead.
        node.select("tr").forEach { it.attr("valign", "top") }
        return node
    }

    val id: String = node.selectFirst("h4 > a").attr("id")

    override val href: String = "$baseHref#$id"

    override val summary: String = normalize(node.selectFirst("dl > dt").nextElementSibling()).html()

    override val documentation: String = normalize(node.selectFirst("dl")).outerHtml()
}

internal data class W3CSpecificationDocument(
    val type: XdmSpecificationType,
    override val href: String,
    val id: String,
    override val version: String,
    private val namespaces: Map<String, String>
) : XdmDocumentationSource, XdmDocumentationIndex {
    // region XdmDocumentationSource

    override val name: String = type.name

    override val path: String = "w3/${type.id}-$id.html"

    // endregion
    // region XdmDocumentationIndex

    private val doc = CacheableProperty {
        val file = XdmDocumentationDownloader.getInstance().load(this)
        file?.let { Jsoup.parse(it.inputStream, null, "") }
    }

    override fun invalidate() = doc.invalidate()

    override fun lookup(ref: XdmFunctionReference): XdmDocumentationReference? {
        val prefix = namespaces[ref.functionName?.namespace?.data] ?: return null
        val localName = ref.functionName?.localName?.data ?: return null
        val lookupName = "$prefix:$localName"
        val match = doc.get()?.select("h4 > a")?.firstOrNull {
            val parts = (it.nextSibling() as? TextNode)?.text()?.split(" ") ?: return@firstOrNull false
            val name = parts.asReversed().find { part -> part.isNotEmpty() }
            name == lookupName
        }
        return match?.let { W3CFunctionReference(it.parent().parent(), href) }
    }

    override fun lookup(decl: XdmNamespaceDeclaration): XdmDocumentationReference? {
        return null
    }

    // endregion
}
