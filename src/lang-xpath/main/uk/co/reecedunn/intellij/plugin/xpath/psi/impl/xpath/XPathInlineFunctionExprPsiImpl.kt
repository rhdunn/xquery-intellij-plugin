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

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.util.Range
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmAnnotation
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathInlineFunctionExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParamList
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.blockOpen
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.isEmptyEnclosedExpr
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.optree.annotation.XpmAccessLevel
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.impl.XpmEmptyExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmParameter

class XPathInlineFunctionExprPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathInlineFunctionExpr,
    XpmSyntaxValidationElement {
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() {
            val context = findChildByType<PsiElement>(XPathTokenType.INLINE_FUNCTION_TOKENS)!!
            val blockOpen = blockOpen
            return when {
                context.elementType === XPathTokenType.THIN_ARROW -> context
                blockOpen?.isEmptyEnclosedExpr == true -> blockOpen
                else -> context
            }
        }

    // endregion
    // region XdmAnnotatedDeclaration

    override val annotations: Sequence<XdmAnnotation>
        get() = children().filterIsInstance<XdmAnnotation>()

    override val accessLevel: XpmAccessLevel
        get() = XpmAccessLevel.get(this)

    // endregion
    // region XdmFunctionDeclaration

    private val paramList: XPathParamList?
        get() = children().filterIsInstance<XPathParamList>().firstOrNull()

    override val functionName: XsQNameValue? = null

    override val arity: Range<Int>
        get() = paramList?.arity ?: XpmFunctionDeclaration.ARITY_ZERO

    override val returnType: XdmSequenceType?
        get() = children().filterIsInstance<XdmSequenceType>().firstOrNull()

    override val parameters: List<XpmParameter>
        get() = paramList?.params ?: emptyList()

    override val paramListPresentation: ItemPresentation?
        get() = paramList?.presentation

    override val isVariadic: Boolean
        get() = paramList?.isVariadic == true

    override val functionRefPresentableText: String? = null

    override val functionBody: XpmExpression
        get() = children().filterIsInstance<XpmExpression>().firstOrNull() ?: XpmEmptyExpression

    // endregion
    // region XpmExpression

    override val expressionElement: PsiElement?
        get() = null

    // endregion
}
