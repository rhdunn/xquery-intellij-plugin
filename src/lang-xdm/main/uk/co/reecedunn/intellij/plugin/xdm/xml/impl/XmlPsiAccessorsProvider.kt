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

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag
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

    // endregion
    // region Accessors (5.10) node-name

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

    override fun hasNodeName(node: Any, namespaceUri: String, localName: String): Boolean = when (node) {
        is XmlTag -> node.namespace == namespaceUri && node.localName == localName
        is XmlAttribute -> node.namespace == namespaceUri && node.localName == localName
        else -> false
    }

    override fun hasNodeName(node: Any, namespaceUri: String, localName: Set<String>): Boolean = when (node) {
        is XmlTag -> node.namespace == namespaceUri && localName.contains(node.localName)
        is XmlAttribute -> node.namespace == namespaceUri && localName.contains(node.localName)
        else -> false
    }

    // endregion
    // region Accessors (5.11) parent

    override fun parent(node: Any): Any? = when (node) {
        is XmlTag -> node.parent
        is XmlAttribute -> node.parent
        else -> null
    }

    // endregion
}
