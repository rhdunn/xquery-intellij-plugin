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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import uk.co.reecedunn.intellij.plugin.core.data.Cacheable
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.core.data.`is`
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.xdm.XsAnyURI
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmConstantExpression
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmLexicalValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathQName
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirAttributeValue
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceResolver

private class DirAttributeNamespaceUri(override val lexicalRepresentation: String) : XdmLexicalValue {
    override val staticType: XdmSequenceType = XsAnyURI
    override val cacheable: CachingBehaviour = CachingBehaviour.Cache
}

class XQueryDirAttributePsiImpl(node: ASTNode):
        ASTWrapperPsiElement(node),
        XQueryDirAttribute,
        XQueryNamespaceResolver,
        XdmNamespaceDeclaration {

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedNamespacePrefix.invalidate()
        cachedNamespaceUri.invalidate()
    }

    // region XQueryNamespaceDeclaration

    override val namespacePrefix get(): XdmLexicalValue? = cachedNamespacePrefix.get()
    private val cachedNamespacePrefix = CacheableProperty {
        children().filterIsInstance<XPathEQName>().map { eqname ->
            val qname = (eqname as XdmConstantExpression).constantValue as? QName
            qname?.let {
                if (it.prefix?.lexicalRepresentation == "xmlns")
                    it.localName
                else
                    null
            }
        }.firstOrNull() `is` Cacheable
    }

    override val namespaceUri get(): XdmLexicalValue? = cachedNamespaceUri.get()
    private val cachedNamespaceUri = CacheableProperty {
        children().filterIsInstance<XQueryDirAttributeValue>().map { value ->
            (value as XdmConstantExpression).constantValue?.let {
                DirAttributeNamespaceUri(it as String)
            }
        }.firstOrNull() `is` Cacheable
    }

    // endregion
    // region XQueryNamespaceResolver

    override fun resolveNamespace(prefix: CharSequence?): XQueryNamespace? {
        return children().filterIsInstance<XPathQName>().map { name ->
            val localName = name.localName
            if (localName?.text == prefix) {
                val uri = name.siblings().filter { e ->
                    e.node.elementType === XQueryElementType.QNAME ||
                    e.node.elementType === XQueryElementType.DIR_ATTRIBUTE_VALUE
                }.firstOrNull()
                if (uri is XQueryDirAttributeValue) {
                    XQueryNamespace(localName, uri, this)
                } else {
                    XQueryNamespace(localName, null, this)
                }
            } else {
                null
            }
        }.filterNotNull().firstOrNull()
    }

    // endregion
}
