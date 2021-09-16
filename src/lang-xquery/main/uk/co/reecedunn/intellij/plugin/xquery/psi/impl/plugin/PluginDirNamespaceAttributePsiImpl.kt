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
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi.XsID
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi.XsUntypedAtomic
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
        private val STRING_VALUE = Key.create<Optional<String>>("STRING_VALUE")
        private val TYPED_VALUE = Key.create<Optional<XsAnyAtomicType>>("TYPED_VALUE")
    }
    // region PsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(STRING_VALUE)
        clearUserData(TYPED_VALUE)
    }

    // endregion
    // region XdmAttributeNode

    override val nodeName: XsQNameValue?
        get() = firstChild as? XsQNameValue

    private val stringValue: String?
        get() = computeUserDataIfAbsent(STRING_VALUE) {
            val attrValue = children().filterIsInstance<XQueryDirAttributeValue>().firstOrNull()
                ?: return@computeUserDataIfAbsent Optional.empty()
            if (!attrValue.isValidHost)
                Optional.empty() // Cannot evaluate enclosed content expressions statically.
            else {
                val value = StringBuilder()
                attrValue.children().filterIsInstance<PsiElementTextDecoder>().forEach { it.decode(value) }
                Optional.of(value.toString())
            }
        }.orElse(null)

    override val typedValue: XsAnyAtomicType?
        get() = computeUserDataIfAbsent(TYPED_VALUE) {
            val contents = stringValue ?: return@computeUserDataIfAbsent Optional.empty()
            val qname = nodeName ?: return@computeUserDataIfAbsent Optional.empty()
            val ret = when {
                qname.prefix?.data == "xmlns" -> {
                    XsAnyUri(contents, XdmUriContext.NamespaceDeclaration, XdmModuleType.MODULE_OR_SCHEMA, this)
                }
                qname.localName?.data == "xmlns" && qname.prefix == null -> {
                    XsAnyUri(contents, XdmUriContext.NamespaceDeclaration, XdmModuleType.MODULE_OR_SCHEMA, this)
                }
                qname.prefix?.data == "xml" && qname.localName?.data == "id" -> {
                    XsID(contents, this)
                }
                else -> XsUntypedAtomic(contents, this)
            }
            Optional.of(ret)
        }.orElse(null)

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
    // region XQueryNamespaceDeclaration

    override fun accepts(namespaceType: XdmNamespaceType): Boolean {
        val qname = nodeName ?: return false
        return when {
            qname.prefix?.data == "xmlns" -> namespaceType === XdmNamespaceType.Prefixed
            qname.localName?.data == "xmlns" && qname.prefix == null -> when (namespaceType) {
                XdmNamespaceType.DefaultElement, XdmNamespaceType.DefaultType -> true
                else -> false
            }
            else -> namespaceType === XdmNamespaceType.Undefined
        }
    }

    override val namespacePrefix: XsNCNameValue?
        get() = nodeName?.takeIf { it.prefix?.data == "xmlns" }?.localName

    override val namespaceUri: XsAnyUriValue?
        get() = typedValue as? XsAnyUriValue

    // endregion
}
