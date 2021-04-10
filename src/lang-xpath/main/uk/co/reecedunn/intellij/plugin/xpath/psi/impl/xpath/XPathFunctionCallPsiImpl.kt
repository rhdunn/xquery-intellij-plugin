/*
 * Copyright (C) 2016-2021 Reece H. Dunn
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

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.ASTWrapperPsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArgumentList
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArgumentPlaceholder
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFunctionCall
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.XpmMapEntry

class XPathFunctionCallPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathFunctionCall {
    companion object {
        private val POSITIONAL_ARGUMENTS = Key.create<List<XpmExpression>>("POSITIONAL_ARGUMENTS")
        private val KEYWORD_ARGUMENTS = Key.create<List<XpmMapEntry>>("KEYWORD_ARGUMENTS")
    }
    // region PsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(POSITIONAL_ARGUMENTS)
        clearUserData(KEYWORD_ARGUMENTS)
    }

    // endregion
    // region XpmExpression

    private val argumentList: XPathArgumentList
        get() = children().filterIsInstance<XPathArgumentList>().first()

    override val expressionElement: PsiElement
        get() = when {
            positionalArguments.find { it is XPathArgumentPlaceholder } != null -> argumentList
            else -> this
        }

    // endregion
    // region XpmFunctionReference

    override val functionName: XsQNameValue?
        get() = firstChild as? XsQNameValue

    override val positionalArity: Int
        get() = positionalArguments.size

    override val keywordArity: Int
        get() = keywordArguments.size

    // endregion
    // region XpmFunctionCall

    override val functionCallExpression: XpmExpression
        get() = this

    override val positionalArguments: List<XpmExpression>
        get() = computeUserDataIfAbsent(POSITIONAL_ARGUMENTS) {
            argumentList.children().filterIsInstance<XpmExpression>().toList()
        }

    override val keywordArguments: List<XpmMapEntry>
        get() = computeUserDataIfAbsent(KEYWORD_ARGUMENTS) {
            argumentList.children().filterIsInstance<XpmMapEntry>().toList()
        }

    // endregion
}
