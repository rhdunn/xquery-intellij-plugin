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
package uk.co.reecedunn.intellij.plugin.xdm.xml.impl

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlText
import uk.co.reecedunn.intellij.plugin.xdm.xml.NodeKind
import uk.co.reecedunn.intellij.plugin.xdm.xml.XmlAccessors
import uk.co.reecedunn.intellij.plugin.xdm.xml.XmlAccessorsProvider

object XmlPsiAccessorsProvider : XmlAccessorsProvider, XmlAccessors {
    // region XmlAccessorsProvider

    override fun element(node: Any): Pair<Any, XmlAccessors>? = when (node) {
        is XmlTag -> node to this
        else -> null
    }

    override fun attribute(node: Any): Pair<Any, XmlAccessors>? = when (node) {
        is XmlAttributeValue -> node.parent to this
        is XmlAttribute -> node to this
        is PsiElement -> node.context?.let { attribute(it) }
        else -> null
    }

    override fun text(node: Any): Pair<Any, XmlAccessors>? = when (node) {
        is XmlText -> node to this
        else -> null
    }

    // endregion
    // region XmlAccessors

    override fun attributes(node: Any): Sequence<Any> = when (node) {
        is XmlTag -> node.attributes.asSequence().filter { !it.isNamespaceDeclaration }
        else -> sequenceOf()
    }

    override fun attributeValueNode(node: Any): PsiElement? = when (node) {
        is XmlAttribute -> node.valueElement
        else -> null
    }

    override fun children(node: Any): Sequence<Any> = when (node) {
        is XmlTag -> node.value.children.asSequence()
        else -> emptySequence()
    }

    override fun nodeKind(node: Any): NodeKind? = when (node) {
        is XmlTag -> NodeKind.Element
        is XmlAttribute -> NodeKind.Attribute
        is XmlText -> NodeKind.Text
        else -> null
    }

    override fun namespaceUri(node: Any): String? = when (node) {
        is XmlTag -> node.namespace
        is XmlAttribute -> node.namespace
        else -> null
    }

    override fun localName(node: Any): String? = when (node) {
        is XmlTag -> node.localName
        is XmlAttribute -> node.localName
        else -> null
    }

    override fun parent(node: Any): Any? = when (node) {
        is XmlTag -> node.parent
        is XmlAttribute -> node.parent
        else -> null
    }

    private val STRING_VALUE = Key.create<CachedValue<String>>("STRING_VALUE")

    override fun stringValue(node: Any): String? = when (node) {
        is XmlTag -> CachedValuesManager.getCachedValue(node, STRING_VALUE) {
            val value = StringBuilder()
            node.value.children.forEach { child ->
                when (child) {
                    is XmlText -> value.append(child.value)
                    is XmlTag -> value.append(stringValue(child))
                    else -> {
                    }
                }
            }
            CachedValueProvider.Result.create(value.toString(), PsiModificationTracker.MODIFICATION_COUNT)
        }
        is XmlAttribute -> node.displayValue
        is XmlText -> node.value
        else -> null
    }

    // endregion
}
