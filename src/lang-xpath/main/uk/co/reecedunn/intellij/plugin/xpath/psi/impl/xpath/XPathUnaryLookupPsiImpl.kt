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

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.ASTWrapperPsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmWildcardValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNCName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathStringLiteral
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathUnaryLookup
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathVarRef
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.impl.XdmWildcardExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.impl.XpmContextItem
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.impl.XsNCNameExpression

class XPathUnaryLookupPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), XPathUnaryLookup, XpmSyntaxValidationElement {
    companion object {
        private val KEY_EXPRESSION = Key.create<XpmExpression>("KEY_EXPRESSION")
    }
    // region PsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(KEY_EXPRESSION)
    }

    // endregion
    // region XpmLookupExpression

    override val expressionElement: PsiElement
        get() = firstChild

    override val contextExpression: XpmExpression = XpmContextItem

    override val keyExpression: XpmExpression
        get() = computeUserDataIfAbsent(KEY_EXPRESSION) {
            children().mapNotNull {
                when (it) {
                    is XpmExpression -> it
                    is XPathNCName -> XsNCNameExpression(it.localName!!)
                    is XdmWildcardValue -> XdmWildcardExpression
                    else -> null
                }
            }.first()
        }

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = when (val lookup = keyExpression) {
            is XPathStringLiteral -> lookup
            is XPathVarRef -> lookup
            else -> expressionElement
        }

    // endregion
}
