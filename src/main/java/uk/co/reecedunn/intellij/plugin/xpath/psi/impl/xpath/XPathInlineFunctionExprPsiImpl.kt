/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathInlineFunctionExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParamList
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionArguments
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableBinding
import uk.co.reecedunn.intellij.plugin.xquery.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.xquery.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance

private val XQUERY10: List<Version> = listOf()
private val XQUERY30: List<Version> = listOf(XQuery.REC_3_0_20140408, MarkLogic.VERSION_6_0)

class XPathInlineFunctionExprPsiImpl(node: ASTNode):
        ASTWrapperPsiElement(node),
        XPathInlineFunctionExpr,
        XQueryConformance,
        XPathFunctionDeclaration {
    // region XQueryConformance

    override val requiresConformance get(): List<Version> {
        if (findChildByType<PsiElement>(XQueryTokenType.K_FUNCTION) == null) {
            return XQUERY10 // Annotation with a missing 'function' keyword.
        }
        return XQUERY30
    }

    override val conformanceElement get(): PsiElement =
        findChildByType(XQueryTokenType.K_FUNCTION) ?: firstChild

    // endregion
    // region XPathFunctionDeclaration

    private val paramList get(): XPathFunctionArguments<XPathVariableBinding>? {
        @Suppress("UNCHECKED_CAST")
        return children().filterIsInstance<XPathParamList>().firstOrNull() as? XPathFunctionArguments<XPathVariableBinding>
    }

    override val cacheable get(): CachingBehaviour = paramList?.cacheable ?: CachingBehaviour.Cache

    override val functionName get(): QName? = null

    override val arity get(): Int = paramList?.arity ?: 0

    override val arguments get(): List<XPathVariableBinding> = paramList?.arguments ?: emptyList()

    // TODO: Locate the `as SequenceType` declaration if present, otherwise default to `item()*`.
    override val returnType get(): XdmSequenceType? = null

    // endregion
}
