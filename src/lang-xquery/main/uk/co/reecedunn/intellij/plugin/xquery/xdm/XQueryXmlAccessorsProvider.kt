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

import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.xml.XmlAccessors
import uk.co.reecedunn.intellij.plugin.xdm.xml.XmlAccessorsProvider
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathStringLiteral
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginEnclosedAttrValueExpr
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCompAttrConstructor
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirAttributeValue

object XQueryXmlAccessorsProvider : XmlAccessorsProvider, XmlAccessors {
    // region XmlAccessorsProvider

    override fun attribute(node: Any): Pair<Any, XmlAccessors>? = when (node) {
        // computed attribute
        is PluginEnclosedAttrValueExpr -> node.parent to this
        is XQueryCompAttrConstructor -> node to this
        is XPathStringLiteral -> {
            val parent = node.parent
            when (parent.children().count { it is XpmExpression }) {
                1 -> attribute(parent) // single StringLiteral constructor value
                else -> null
            }
        }
        // direct attribute
        is XQueryDirAttributeValue -> node.parent to this
        is PluginDirAttribute -> node to this
        // other
        else -> null
    }

    // endregion
}
