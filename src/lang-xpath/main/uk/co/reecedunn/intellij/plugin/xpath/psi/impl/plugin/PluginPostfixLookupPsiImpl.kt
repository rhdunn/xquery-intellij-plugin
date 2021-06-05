/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.filterIsElementType
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmItemType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmNodeItem
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmWildcardValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginPostfixLookup
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNCName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathStringLiteral
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathVarRef
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.impl.XdmWildcardExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.impl.XsNCNameExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.path.XpmAxisType

class PluginPostfixLookupPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), PluginPostfixLookup, XpmSyntaxValidationElement {
    companion object {
        private val KEY_EXPRESSION = Key.create<XpmExpression>("KEY_EXPRESSION")
    }
    // region PsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(KEY_EXPRESSION)
    }

    // endregion
    // region XpmPathStep

    override val axisType: XpmAxisType = XpmAxisType.Self

    override val nodeName: XsQNameValue? = null

    override val nodeType: XdmItemType = XdmNodeItem

    override val predicateExpression: XpmExpression? = null

    // endregion
    // region XpmLookupExpression

    override val expressionElement: PsiElement
        get() = children().filterIsElementType(XPathTokenType.OPTIONAL).first()

    override val contextExpression: XpmExpression
        get() = firstChild as XpmExpression

    override val keyExpression: XpmExpression
        get() = computeUserDataIfAbsent(KEY_EXPRESSION) {
            expressionElement.siblings().mapNotNull {
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
