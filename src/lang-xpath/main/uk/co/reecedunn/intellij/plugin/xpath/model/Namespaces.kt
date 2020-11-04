/*
 * Copyright (C) 2017-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.model

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.plugin.core.psi.contextOfType
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestors
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xpm.namespace.XpmDefaultNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.namespace.XpmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi.XsNCName

private fun XmlAttribute.toDefaultNamespaceDeclaration(): XpmNamespaceDeclaration? {
    if (!isNamespaceDeclaration) return null
    val prefix = namespacePrefix
    val value = value ?: return null
    return if (prefix.isEmpty()) {
        object : XpmNamespaceDeclaration {
            override val namespacePrefix: XsNCNameValue? = null
            override val namespaceUri: XsAnyUriValue? =
                XsAnyUri(value, XdmUriContext.Namespace, XdmModuleType.NONE, originalElement)

            override fun accepts(namespaceType: XdmNamespaceType): Boolean {
                return namespaceType === XdmNamespaceType.DefaultElementOrType
            }
        }
    } else {
        null
    }
}

private fun XmlAttribute.toNamespaceDeclaration(): XpmNamespaceDeclaration? {
    if (!isNamespaceDeclaration) return null
    val prefix = namespacePrefix
    val localName = localName
    val value = value ?: return null
    return if (prefix.isEmpty()) {
        null
    } else {
        object : XpmNamespaceDeclaration {
            override val namespacePrefix: XsNCNameValue? = XsNCName(localName, originalElement)
            override val namespaceUri: XsAnyUriValue? =
                XsAnyUri(value, XdmUriContext.Namespace, XdmModuleType.MODULE, originalElement)

            override fun accepts(namespaceType: XdmNamespaceType): Boolean {
                return namespaceType === XdmNamespaceType.Prefixed
            }
        }
    }
}

private object DefaultFunctionXPathNamespace : XpmDefaultNamespaceDeclaration {
    private const val FN_NAMESPACE_URI = "http://www.w3.org/2005/xpath-functions"

    override val namespacePrefix: XsNCNameValue? = null
    override val namespaceUri: XsAnyUriValue? =
        XsAnyUri(FN_NAMESPACE_URI, XdmUriContext.Namespace, XdmModuleType.MODULE, null as PsiElement?)

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    override fun accepts(namespaceType: XdmNamespaceType): Boolean {
        return (
            namespaceType === XdmNamespaceType.DefaultFunctionDecl ||
            namespaceType === XdmNamespaceType.DefaultFunctionRef
        )
    }
}

@Suppress("unused")
fun PsiElement.defaultFunctionXPathNamespace(): Sequence<XpmDefaultNamespaceDeclaration> {
    return sequenceOf(DefaultFunctionXPathNamespace)
}

fun PsiElement.defaultElementOrTypeXPathNamespace(): Sequence<XpmNamespaceDeclaration> {
    return contextOfType<XmlAttributeValue>(false)?.ancestors()?.filterIsInstance<XmlTag>()?.flatMap { tag ->
        tag.attributes.asSequence().map { attribute -> attribute.toDefaultNamespaceDeclaration() }
    }?.filterNotNull() ?: sequenceOf()
}

fun PsiElement.staticallyKnownXPathNamespaces(): Sequence<XpmNamespaceDeclaration> {
    return contextOfType<XmlAttributeValue>(false)?.ancestors()?.filterIsInstance<XmlTag>()?.flatMap { tag ->
        tag.attributes.asSequence().map { attribute -> attribute.toNamespaceDeclaration() }
    }?.filterNotNull() ?: sequenceOf()
}
