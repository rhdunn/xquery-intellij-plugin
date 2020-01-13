/*
 * Copyright (C) 2016-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.plugin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.module.path.resolve
import uk.co.reecedunn.intellij.plugin.xdm.module.resolveUri
import uk.co.reecedunn.intellij.plugin.xdm.namespaces.XdmDefaultNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xdm.namespaces.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirAttributeValue
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryLibraryModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryProlog
import uk.co.reecedunn.intellij.plugin.xquery.model.XQueryPrologResolver

class PluginDirAttributePsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    PluginDirAttribute,
    XdmAttributeNode,
    XQueryPrologResolver,
    XdmDefaultNamespaceDeclaration {
    // region PsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedNamespacePrefix.invalidate()
        cachedNamespaceUri.invalidate()
    }

    // endregion
    // region XdmAttributeNode

    override val nodeName: XsQNameValue get() = firstChild as XsQNameValue

    override val nodeValue: XsAnyAtomicType?
        get() = children().filterIsInstance<XQueryDirAttributeValue>().firstOrNull()?.value

    // endregion
    // region XQueryPrologResolver

    override val prolog: Sequence<XQueryProlog>
        get() {
            val file = namespaceUri?.let {
                it.resolve() ?: it.resolveUri<XQueryModule>(true)
            }
            val library = file?.children()?.filterIsInstance<XQueryLibraryModule>()?.firstOrNull()
            return (library as? XQueryPrologResolver)?.prolog ?: emptySequence()
        }

    // endregion
    // region XQueryNamespaceDeclaration

    override fun accepts(namespaceType: XdmNamespaceType): Boolean {
        val qname = nodeName
        return when {
            qname.prefix?.data == "xmlns" -> namespaceType === XdmNamespaceType.Prefixed
            qname.localName?.data == "xmlns" -> namespaceType === XdmNamespaceType.DefaultElementOrType
            else -> namespaceType === XdmNamespaceType.Undefined
        }
    }

    override val namespacePrefix get(): XsNCNameValue? = cachedNamespacePrefix.get()
    private val cachedNamespacePrefix = CacheableProperty {
        children().filterIsInstance<XsQNameValue>().map { qname ->
            if (qname.prefix?.data == "xmlns")
                qname.localName
            else
                null
        }.firstOrNull()
    }

    override val namespaceUri get(): XsAnyUriValue? = cachedNamespaceUri.get()
    private val cachedNamespaceUri = CacheableProperty {
        children().filterIsInstance<XQueryDirAttributeValue>().map { attr ->
            attr.value as? XsAnyUriValue
        }.firstOrNull()
    }

    // endregion
}
