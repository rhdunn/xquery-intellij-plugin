/*
 * Copyright (C) 2019-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.plugin

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.ASTWrapperPsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.reverse
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.xpath.ast.filterNotWhitespace
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginArrowDynamicFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArgumentList
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmArrowOperation
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.XpmMapEntry

class PluginArrowDynamicFunctionCallPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    PluginArrowDynamicFunctionCall,
    XpmSyntaxValidationElement {
    companion object {
        private val POSITIONAL_ARGUMENTS = Key.create<List<XpmExpression>>("POSITIONAL_ARGUMENTS")
    }
    // region PsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(POSITIONAL_ARGUMENTS)
    }

    // endregion
    // region XpmExpression

    override val expressionElement: XPathArgumentList
        get() = children().filterIsInstance<XPathArgumentList>().first()

    // endregion
    // region XpmArrowFunctionCall

    override val functionCallExpression: XpmExpression?
        get() = children().filterIsInstance<XpmExpression>().firstOrNull()

    override val positionalArguments: List<XpmExpression>
        get() = computeUserDataIfAbsent(POSITIONAL_ARGUMENTS) {
            val argumentList = children().filterIsInstance<XPathArgumentList>().first()
            argumentList.children().filterIsInstance<XpmExpression>().toList()
        }

    override val keywordArguments: List<XpmMapEntry> = listOf()

    override val sourceExpression: XpmExpression?
        get() = reverse(siblings()).filterIsInstance<XpmExpression>().firstOrNull()

    override val operation: XpmArrowOperation
        get() = when (conformanceElement.elementType) {
            XPathTokenType.THIN_ARROW -> XpmArrowOperation.Mapping
            else -> XpmArrowOperation.Chaining
        }

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = reverse(siblings()).filterNotWhitespace().firstOrNull() ?: firstChild

    // endregion
}
