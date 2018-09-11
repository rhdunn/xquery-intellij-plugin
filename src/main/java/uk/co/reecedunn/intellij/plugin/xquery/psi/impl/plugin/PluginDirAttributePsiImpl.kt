/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.data.Cacheable
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.data.`is`
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirAttributeValue

class PluginDirAttributePsiImpl(node: ASTNode):
    ASTWrapperPsiElement(node),
    PluginDirAttribute,
    XPathDefaultNamespaceDeclaration {

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedNamespacePrefix.invalidate()
        cachedNamespaceUri.invalidate()
    }

    // region XQueryNamespaceDeclaration

    override val namespaceType
        get(): XPathNamespaceType {
            return children().filterIsInstance<XsQNameValue>().map { qname ->
                if (qname.prefix?.data == "xmlns")
                    XPathNamespaceType.Prefixed
                else if (qname.localName?.data == "xmlns")
                    XPathNamespaceType.DefaultElementOrType
                else
                    null
            }.firstOrNull() ?: XPathNamespaceType.None
        }

    override val namespacePrefix get(): XsNCNameValue? = cachedNamespacePrefix.get()
    private val cachedNamespacePrefix = CacheableProperty {
        children().filterIsInstance<XsQNameValue>().map { qname ->
            if (qname.prefix?.data == "xmlns")
                qname.localName
            else
                null
        }.firstOrNull() `is` Cacheable
    }

    override val namespaceUri get(): XsAnyUriValue? = cachedNamespaceUri.get()
    private val cachedNamespaceUri = CacheableProperty {
        children().filterIsInstance<XQueryDirAttributeValue>().map { attr ->
            attr.value as? XsAnyUriValue
        }.firstOrNull() `is` Cacheable
    }

    // endregion
}
