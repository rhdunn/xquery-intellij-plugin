/*
 * Copyright (C) 2016-2020 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmAnnotation
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathInlineFunctionExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParamList
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.variable.XpmVariableBinding
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue

private val XQUERY10: List<Version> = listOf()
private val XQUERY30: List<Version> = listOf(XQuerySpec.REC_3_0_20140408, MarkLogic.VERSION_6_0)

class XPathInlineFunctionExprPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathInlineFunctionExpr,
    VersionConformance {
    // region VersionConformance

    override val requiresConformance: List<Version>
        get() {
            if (findChildByType<PsiElement>(XPathTokenType.K_FUNCTION) == null) {
                return XQUERY10 // Annotation with a missing 'function' keyword.
            }
            return XQUERY30
        }

    override val conformanceElement: PsiElement
        get() = findChildByType(XPathTokenType.K_FUNCTION) ?: firstChild

    // endregion
    // region XdmFunctionDeclaration

    private val paramList: XPathParamList?
        get() = children().filterIsInstance<XPathParamList>().firstOrNull()

    override val functionName: XsQNameValue? = null

    override val arity: Range<Int>
        get() = paramList?.arity ?: XpmFunctionDeclaration.ARITY_ZERO

    override val returnType: XdmSequenceType?
        get() = children().filterIsInstance<XdmSequenceType>().firstOrNull()

    override val params: List<XpmVariableBinding>
        get() = paramList?.params ?: emptyList()

    override val paramListPresentation: ItemPresentation?
        get() = paramList?.presentation

    override val isVariadic: Boolean
        get() = paramList?.isVariadic == true

    override val functionRefPresentableText: String? = null

    override val annotations: Sequence<XdmAnnotation>
        get() = children().filterIsInstance<XdmAnnotation>()

    // endregion
    // region XpmExpression

    override val expressionElement: PsiElement?
        get() = null

    // endregion
}
