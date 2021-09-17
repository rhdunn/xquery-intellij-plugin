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
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.core.psi.ASTWrapperPsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.reverse
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmAnnotation
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathInlineFunctionExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParam
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.psi.blockOpen
import uk.co.reecedunn.intellij.plugin.xpath.psi.isEmptyEnclosedExpr
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.optree.annotation.XpmAccessLevel
import uk.co.reecedunn.intellij.plugin.xpm.optree.annotation.XpmVariadic
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.impl.XpmEmptyExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmParameter

class XPathInlineFunctionExprPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathInlineFunctionExpr,
    XpmSyntaxValidationElement {
    companion object {
        private val PARAMETERS = Key.create<List<XpmParameter>>("PARAMETERS")
        private val VARIADIC_TYPE = Key.create<XpmVariadic>("VARIADIC_TYPE")
        private val PARAM_LIST_PRESENTABLE_TEXT = Key.create<String>("PARAM_LIST_PRESENTABLE_TEXT")

        private val PARAM_OR_VARIADIC = TokenSet.create(
            XPathElementType.PARAM,
            XPathTokenType.ELLIPSIS
        )
    }
    // region PsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(PARAMETERS)
        clearUserData(VARIADIC_TYPE)
        clearUserData(PARAM_LIST_PRESENTABLE_TEXT)
    }

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() {
            val context = findChildByType<PsiElement>(XPathTokenType.INLINE_FUNCTION_TOKENS)!!
            val blockOpen = blockOpen
            return when {
                context.elementType === XPathTokenType.THIN_ARROW -> context
                blockOpen?.isEmptyEnclosedExpr == true -> blockOpen
                else -> when (val parameter = variadicParameter) {
                    null -> context
                    is XPathParam -> context
                    else -> parameter
                }
            }
        }

    // endregion
    // region XdmAnnotatedDeclaration

    override val annotations: Sequence<XdmAnnotation>
        get() = children().filterIsInstance<XdmAnnotation>()

    override val accessLevel: XpmAccessLevel
        get() = XpmAccessLevel.get(this)

    // endregion
    // region XdmFunctionDeclaration (Data Model)

    override val functionName: XsQNameValue? = null

    override val parameters: List<XpmParameter>
        get() = computeUserDataIfAbsent(PARAMETERS) {
            children().filterIsInstance<XPathParam>().toList()
        }

    override val returnType: XdmSequenceType?
        get() = children().filterIsInstance<XdmSequenceType>().firstOrNull()

    override val functionBody: XpmExpression
        get() = children().filterIsInstance<XpmExpression>().firstOrNull() ?: XpmEmptyExpression

    // endregion
    // region XdmFunctionDeclaration (Variadic Type and Arity)

    private val variadicParameter: PsiElement?
        get() = reverse(children()).firstOrNull { e -> PARAM_OR_VARIADIC.contains(e.elementType) }

    override val variadicType: XpmVariadic
        get() = computeUserDataIfAbsent(VARIADIC_TYPE) {
            val variadicParameter = variadicParameter
            when (variadicParameter.elementType) {
                XPathTokenType.ELLIPSIS -> XpmVariadic.Ellipsis
                else -> XpmVariadic.No
            }
        }

    override val declaredArity: Int
        get() = parameters.size

    private val defaultArgumentCount: Int
        get() = when (variadicType) {
            XpmVariadic.Ellipsis -> 1
            else -> 0
        }

    override val requiredArity: Int
        get() = declaredArity - defaultArgumentCount

    // endregion
    // region XdmFunctionDeclaration (Presentation)

    override val paramListPresentableText: String
        get() = computeUserDataIfAbsent(PARAM_LIST_PRESENTABLE_TEXT) {
            val params = parameters.mapNotNull { param ->
                (param as NavigationItem).presentation?.presentableText
            }.joinToString()
            if (variadicType === XpmVariadic.Ellipsis) "($params ...)" else "($params)"
        }

    override val functionRefPresentableText: String? = null

    // endregion
    // region XpmExpression

    override val expressionElement: PsiElement?
        get() = null

    // endregion
}
