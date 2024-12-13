/*
 * Copyright (C) 2016-2018, 2020-2021 Reece H. Dunn
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
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmItemType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmNodeItem
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFilterExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathPrimaryExpr
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.path.XpmAxisType

class XPathFilterExprPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathFilterExpr {
    // region XpmPathStep

    override val axisType: XpmAxisType = XpmAxisType.Self

    override val nodeName: XsQNameValue? = null

    override val nodeType: XdmItemType = XdmNodeItem

    override val predicateExpression: XpmExpression
        get() = children().filterIsInstance<XpmExpression>().last()

    // endregion
    // region XpmExpression

    override val expressionElement: PsiElement?
        get() {
            var step: PsiElement = this
            while (step.firstChild != null && step.firstChild !is XPathPrimaryExpr) {
                step = step.firstChild!!
            }
            return (step.firstChild as? XpmExpression)?.expressionElement ?: step.firstChild
        }

    // endregion
}
