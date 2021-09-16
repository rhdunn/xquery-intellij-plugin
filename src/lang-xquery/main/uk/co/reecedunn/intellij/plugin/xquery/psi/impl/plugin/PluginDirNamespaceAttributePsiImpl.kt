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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.plugin

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Key
import uk.co.reecedunn.intellij.plugin.core.lang.injection.PsiElementTextDecoder
import uk.co.reecedunn.intellij.plugin.core.psi.ASTWrapperPsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmNamespaceNode.Companion.EMPTY_PREFIX
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.resolve
import uk.co.reecedunn.intellij.plugin.xpm.module.resolveUri
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirNamespaceAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.model.XQueryPrologResolver
import java.util.*

class PluginDirNamespaceAttributePsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    PluginDirNamespaceAttribute,
    XQueryPrologResolver {
    companion object {
        private val NAMESPACE_URI = Key.create<Optional<XsAnyUriValue>>("NAMESPACE_URI")
    }
    // region PsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(NAMESPACE_URI)
    }

    // endregion
    // region XdmNamespaceNode

    private val stringValue: String?
        get() {
            val attrValue = children().filterIsInstance<XQueryDirAttributeValue>().firstOrNull()
            return when {
                attrValue == null -> null
                !attrValue.isValidHost -> null // Cannot evaluate enclosed content expressions statically.
                else -> {
                    val value = StringBuilder()
                    attrValue.children().filterIsInstance<PsiElementTextDecoder>().forEach { it.decode(value) }
                    value.toString()
                }
            }
        }

    override val namespacePrefix: XsNCNameValue
        get() = (firstChild as? XsQNameValue)?.takeIf { it.prefix?.data == "xmlns" }?.localName ?: EMPTY_PREFIX

    override val namespaceUri: XsAnyUriValue?
        get() = computeUserDataIfAbsent(NAMESPACE_URI) {
            val contents = stringValue ?: return@computeUserDataIfAbsent Optional.empty()
            Optional.of(
                XsAnyUri(contents, XdmUriContext.NamespaceDeclaration, XdmModuleType.MODULE_OR_SCHEMA, this)
            )
        }.orElse(null)

    override val parentNode: XdmNode?
        get() = parent as? XdmElementNode

    // endregion
    // region XQueryNamespaceDeclaration

    override fun accepts(namespaceType: XdmNamespaceType): Boolean = when (namespacePrefix) {
        EMPTY_PREFIX -> when (namespaceType) {
            XdmNamespaceType.DefaultElement, XdmNamespaceType.DefaultType -> true
            else -> false
        }
        else -> namespaceType === XdmNamespaceType.Prefixed
    }

    // endregion
    // region XQueryPrologResolver

    override val prolog: Sequence<XQueryProlog>
        get() {
            val file = namespaceUri?.let {
                it.resolve() ?: it.resolveUri<XQueryModule>()
            }
            val library = file?.children()?.filterIsInstance<XQueryLibraryModule>()?.firstOrNull()
            return (library as? XQueryPrologResolver)?.prolog ?: emptySequence()
        }

    // endregion
}
