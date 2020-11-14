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
import uk.co.reecedunn.intellij.plugin.core.psi.contains
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xpm.module.resolveUri
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi.XsID
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi.XsUntypedAtomic
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEscapeCharacter
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.resolve
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.model.XQueryPrologResolver
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType

class PluginDirAttributePsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), PluginDirAttribute, XQueryPrologResolver {
    // region PsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedNodeValue.invalidate()
    }

    // endregion
    // region XdmAttributeNode

    override val nodeName: XsQNameValue?
        get() = firstChild as? XsQNameValue

    override val typedValue: XsAnyAtomicType?
        get() = cachedNodeValue.get()

    private val cachedNodeValue = CacheableProperty {
        val qname = nodeName ?: return@CacheableProperty null
        val attrValue = children().filterIsInstance<XQueryDirAttributeValue>().firstOrNull()
            ?: return@CacheableProperty null
        val contents =
            if (attrValue.contains(XQueryElementType.ENCLOSED_EXPR))
                null // Cannot evaluate enclosed content expressions statically.
            else
                attrValue.children().map { child ->
                    when (child.elementType) {
                        XQueryTokenType.XML_ATTRIBUTE_VALUE_START, XQueryTokenType.XML_ATTRIBUTE_VALUE_END ->
                            null
                        XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE ->
                            (child as XQueryPredefinedEntityRef).entityRef.value
                        XQueryTokenType.XML_CHARACTER_REFERENCE ->
                            (child as XQueryCharRef).codepoint.toString()
                        XQueryTokenType.XML_ESCAPED_CHARACTER ->
                            (child as XPathEscapeCharacter).unescapedCharacter.toString()
                        else ->
                            child.text
                    }
                }.filterNotNull().joinToString(separator = "")
        when {
            contents == null -> {
                @Suppress("USELESS_CAST") // Needed, otherwise type inference results in `Any?` with warnings.
                null as XsAnyAtomicType?
            }
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
