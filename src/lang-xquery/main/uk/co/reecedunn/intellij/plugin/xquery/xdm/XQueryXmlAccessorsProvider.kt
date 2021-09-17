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
package uk.co.reecedunn.intellij.plugin.xquery.xdm

import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xdm.xml.NodeKind
import uk.co.reecedunn.intellij.plugin.xdm.xml.XmlAccessors
import uk.co.reecedunn.intellij.plugin.xdm.xml.XmlAccessorsProvider
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNCName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathStringLiteral
import uk.co.reecedunn.intellij.plugin.xpm.context.expand
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirAttributeValue

object XQueryXmlAccessorsProvider : XmlAccessorsProvider, XmlAccessors {
    // region XmlAccessorsProvider

    override fun element(node: Any): Pair<Any, XmlAccessors>? = when (node) {
        is XdmElementNode -> node to this
        else -> null
    }

    override fun attribute(node: Any): Pair<Any, XmlAccessors>? = when (node) {
        is XdmAttributeNode -> node to this
        is XQueryDirAttributeValue -> node.parent to this
        is XPathStringLiteral -> {
            val parent = node.parent
            when (parent.children().count { it is XpmExpression }) {
                1 -> attribute(parent) // single StringLiteral constructor value
                else -> null
            }
        }
        is PsiElement -> node.context?.let { attribute(it) }
        else -> null
    }

    override fun text(node: Any): Pair<Any, XmlAccessors>? = when (node) {
        is XdmTextNode -> node to this
        is XPathStringLiteral -> {
            val parent = node.parent
            when (parent.children().count { it is XpmExpression }) {
                1 -> text(parent) // single StringLiteral constructor value
                else -> null
            }
        }
        else -> null
    }

    // endregion
    // region XmlAccessorsProvider

    override fun attributes(node: Any): Sequence<Any> = when (node) {
        is XdmElementNode -> node.attributes
        else -> sequenceOf()
    }

    override fun attributeValueNode(node: Any): PsiElement? = when (node) {
        is PluginDirAttribute -> node.children().filterIsInstance<XQueryDirAttributeValue>().firstOrNull()
        else -> null
    }

    override fun nodeKind(node: Any): NodeKind? = when (node) {
        is XdmNode -> node.nodeKind
        else -> null
    }

    private fun nodeName(node: Any): XsQNameValue? = when (node) {
        is XdmElementNode -> node.nodeName
        is XdmAttributeNode -> node.nodeName
        else -> null
    }

    override fun namespaceUri(node: Any): String? = when (val qname = nodeName(node)) {
        is XPathNCName, is XPathQName -> qname.expand().firstOrNull()?.namespace?.data
        else -> qname?.namespace?.data // Don't need to expand a URIQualifiedName to get the namespace URI.
    }

    override fun localName(node: Any): String? = nodeName(node)?.localName?.data

    override fun parent(node: Any): Any? = when (node) {
        is XdmElementNode -> node.parentNode
        is XdmAttributeNode -> node.parentNode
        else -> null
    }

    override fun stringValue(node: Any): String? = when (node) {
        is XdmElementNode -> node.stringValue
        is XdmAttributeNode -> node.stringValue
        is XdmTextNode -> node.stringValue
        else -> null
    }

    // endregion
}
