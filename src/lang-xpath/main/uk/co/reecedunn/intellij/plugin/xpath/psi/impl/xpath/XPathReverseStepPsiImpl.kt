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
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmItemType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNameTest
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNodeTest
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathReverseStep
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.path.XpmAxisType

class XPathReverseStepPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathReverseStep {
    override val axisType: XpmAxisType
        get() = when (firstChild.elementType) {
            XPathTokenType.K_ANCESTOR -> XpmAxisType.Ancestor
            XPathTokenType.K_ANCESTOR_OR_SELF -> XpmAxisType.AncestorOrSelf
            XPathTokenType.K_PARENT -> XpmAxisType.Parent
            XPathTokenType.K_PRECEDING -> XpmAxisType.Preceding
            XPathTokenType.K_PRECEDING_SIBLING -> XpmAxisType.PrecedingSibling
            else -> throw UnsupportedOperationException()
        }

    override val nodeName: XsQNameValue?
        get() = (lastChild as? XPathNameTest)?.nodeName

    override val nodeType: XdmItemType
        get() = (lastChild as? XPathNodeTest)?.nodeType ?: axisType.principalNodeKind

    override val predicateExpression: XpmExpression? = null
}
