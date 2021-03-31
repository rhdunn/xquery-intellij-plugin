/*
 * Copyright (C) 2016, 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmAttributeNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmItemType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmNamespaceNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNodeTest
import uk.co.reecedunn.intellij.plugin.xpm.optree.path.XpmAxisType
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression

class XPathNodeTestPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathNodeTest {
    override val axisType: XpmAxisType
        get() = when (nodeType.typeClass) {
            XdmAttributeNode::class.java -> XpmAxisType.Attribute
            XdmNamespaceNode::class.java -> XpmAxisType.Namespace
            else -> XpmAxisType.Child
        }

    override val nodeName: XsQNameValue? = null

    override val nodeType: XdmItemType
        get() = firstChild as XdmItemType

    override val predicateExpression: XpmExpression? = null
}
