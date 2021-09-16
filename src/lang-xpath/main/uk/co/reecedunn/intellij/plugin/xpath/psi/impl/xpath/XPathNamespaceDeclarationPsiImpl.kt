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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmNamespaceNode.Companion.EMPTY_PREFIX
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType

class XPathNamespaceDeclarationPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathNamespaceDeclaration {
    // region XdmNamespaceNode

    override val namespacePrefix: XsNCNameValue
        get() = (firstChild as? XsQNameValue)?.takeIf { it.prefix?.data == "xmlns" }?.localName ?: EMPTY_PREFIX

    override val namespaceUri: XsAnyUriValue?
        get() = children().filterIsInstance<XsAnyUriValue>().firstOrNull()

    override val parentNode: XdmNode? = null

    // endregion
    // region XpmNamespaceDeclaration

    override fun accepts(namespaceType: XdmNamespaceType): Boolean {
        val qname = (firstChild as? XsQNameValue) ?: return false
        return when {
            qname.prefix?.data == "xmlns" -> namespaceType === XdmNamespaceType.Prefixed
            qname.localName?.data == "xmlns" && qname.prefix == null -> when (namespaceType) {
                XdmNamespaceType.DefaultElement -> true
                else -> false
            }
            else -> namespaceType === XdmNamespaceType.Undefined
        }
    }

    // endregion
}
