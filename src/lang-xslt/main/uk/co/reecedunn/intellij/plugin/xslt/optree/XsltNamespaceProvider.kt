/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.optree

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.plugin.core.psi.contextOfType
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestors
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi.XsNCName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPath
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceProvider

object XsltNamespaceProvider : XpmNamespaceProvider {
    override fun staticallyKnownNamespaces(context: PsiElement): Sequence<XpmNamespaceDeclaration> {
        if (context.containingFile !is XPath) return emptySequence()
        return staticallyKnownXPathNamespaces(context)
    }

    override fun defaultNamespace(context: PsiElement, type: XdmNamespaceType): Sequence<XpmNamespaceDeclaration> {
        if (context.containingFile !is XPath) return emptySequence()
        return when (type) {
            XdmNamespaceType.DefaultElement -> defaultElementOrTypeXPathNamespace(context)
            XdmNamespaceType.DefaultFunctionDecl -> DefaultFunctionDeclOrRefNamespace
            XdmNamespaceType.DefaultFunctionRef -> DefaultFunctionDeclOrRefNamespace
            XdmNamespaceType.DefaultType -> defaultElementOrTypeXPathNamespace(context)
            else -> emptySequence()
        }
    }

    private val DefaultFunctionDeclOrRefNamespace = sequenceOf(DefaultFunctionXPathNamespace)

    private fun defaultElementOrTypeXPathNamespace(context: PsiElement): Sequence<XpmNamespaceDeclaration> {
        val tags = context.contextOfType<XmlAttributeValue>(false)?.ancestors()?.filterIsInstance<XmlTag>()
        return tags?.flatMap { tag ->
            tag.attributes.asSequence().map { attribute -> toDefaultNamespaceDeclaration(attribute) }
        }?.filterNotNull() ?: sequenceOf()
    }

    private fun toDefaultNamespaceDeclaration(attribute: XmlAttribute): XpmNamespaceDeclaration? {
        if (!attribute.isNamespaceDeclaration) return null
        val prefix = attribute.namespacePrefix
        val value = attribute.value ?: return null
        return if (prefix.isEmpty()) {
            object : XpmNamespaceDeclaration {
                override val namespacePrefix: XsNCNameValue? = null

                override val namespaceUri: XsAnyUriValue =
                    XsAnyUri(value, XdmUriContext.Namespace, XdmModuleType.NONE, attribute.originalElement)

                override val parentNode: XdmNode? = null

                override fun accepts(namespaceType: XdmNamespaceType): Boolean = when (namespaceType) {
                    XdmNamespaceType.DefaultElement, XdmNamespaceType.DefaultType -> true
                    else -> false
                }
            }
        } else {
            null
        }
    }

    private fun staticallyKnownXPathNamespaces(context: PsiElement): Sequence<XpmNamespaceDeclaration> {
        val tags = context.contextOfType<XmlAttributeValue>(false)?.ancestors()?.filterIsInstance<XmlTag>()
        return tags?.flatMap { tag ->
            tag.attributes.asSequence().map { attribute -> toNamespaceDeclaration(attribute) }
        }?.filterNotNull() ?: sequenceOf()
    }

    private fun toNamespaceDeclaration(attribute: XmlAttribute): XpmNamespaceDeclaration? {
        if (!attribute.isNamespaceDeclaration) return null
        val prefix = attribute.namespacePrefix
        val localName = attribute.localName
        val value = attribute.value ?: return null
        return if (prefix.isEmpty()) {
            null
        } else {
            object : XpmNamespaceDeclaration {
                override val namespacePrefix: XsNCNameValue = XsNCName(localName, attribute.originalElement)

                override val namespaceUri: XsAnyUriValue =
                    XsAnyUri(value, XdmUriContext.Namespace, XdmModuleType.MODULE, attribute.originalElement)

                override val parentNode: XdmNode? = null

                override fun accepts(namespaceType: XdmNamespaceType): Boolean {
                    return namespaceType === XdmNamespaceType.Prefixed
                }
            }
        }
    }
}
