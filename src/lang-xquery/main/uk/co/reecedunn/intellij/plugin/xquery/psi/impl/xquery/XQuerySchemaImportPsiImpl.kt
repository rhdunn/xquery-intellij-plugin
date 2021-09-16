/*
 * Copyright (C) 2016-2021 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmNamespaceNode.Companion.EMPTY_PREFIX
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginLocationURIList
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQuerySchemaImport
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQuerySchemaPrefix

class XQuerySchemaImportPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQuerySchemaImport {
    // region XdmDefaultNamespaceDeclaration

    private val schemaPrefix
        get() = children().filterIsInstance<XQuerySchemaPrefix>().firstOrNull()

    override val namespacePrefix: XsNCNameValue
        get() = schemaPrefix?.let {
            it.children().filterIsInstance<XsQNameValue>().firstOrNull()?.localName
        } ?: EMPTY_PREFIX

    override val namespaceUri: XsAnyUriValue?
        get() = children().filterIsInstance<XsAnyUriValue>().firstOrNull()

    override val parentNode: XdmNode? = null

    // endregion
    // region XpmNamespaceDeclaration

    override fun accepts(namespaceType: XdmNamespaceType): Boolean {
        return when (schemaPrefix?.firstChild?.node?.elementType) {
            XPathTokenType.K_NAMESPACE -> namespaceType === XdmNamespaceType.Prefixed
            else -> when (namespaceType) {
                XdmNamespaceType.DefaultElement, XdmNamespaceType.DefaultType -> true
                else -> false
            }
        }
    }

    // endregion
    // region XQueryImport

    override val locationUris: Sequence<XsAnyUriValue>
        get() {
            val uris = children().filterIsInstance<PluginLocationURIList>().firstOrNull()
            return uris?.children()?.filterIsInstance<XsAnyUriValue>() ?: emptySequence()
        }

    // endregion
}
