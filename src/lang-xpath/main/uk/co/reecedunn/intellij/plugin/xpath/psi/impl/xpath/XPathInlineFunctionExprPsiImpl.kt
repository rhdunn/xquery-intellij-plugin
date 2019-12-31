/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xdm.functions.XdmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathInlineFunctionExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParamList
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xdm.variables.XdmVariableBinding
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue

private val XQUERY10: List<Version> = listOf()
private val XQUERY30: List<Version> = listOf(XQuerySpec.REC_3_0_20140408, MarkLogic.VERSION_6_0)

class XPathInlineFunctionExprPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathInlineFunctionExpr,
    XdmFunctionDeclaration,
    VersionConformance {
    // region VersionConformance

    override val requiresConformance
        get(): List<Version> {
            if (findChildByType<PsiElement>(XPathTokenType.K_FUNCTION) == null) {
                return XQUERY10 // Annotation with a missing 'function' keyword.
            }
            return XQUERY30
        }

    override val conformanceElement get(): PsiElement = findChildByType(XPathTokenType.K_FUNCTION) ?: firstChild

    // endregion
    // region XdmFunctionDeclaration

    private val paramList get(): XPathParamList? = children().filterIsInstance<XPathParamList>().firstOrNull()

    override val functionName: XsQNameValue? = null

    override val arity get(): Range<Int> = paramList?.arity ?: XdmFunctionDeclaration.ARITY_ZERO

    override val returnType get(): XdmSequenceType? = children().filterIsInstance<XdmSequenceType>().firstOrNull()

    override val params get(): List<XdmVariableBinding> = paramList?.params ?: emptyList()

    override val paramListPresentation get(): ItemPresentation? = paramList?.presentation

    override val isVariadic get(): Boolean = paramList?.isVariadic == true

    // endregion
}
