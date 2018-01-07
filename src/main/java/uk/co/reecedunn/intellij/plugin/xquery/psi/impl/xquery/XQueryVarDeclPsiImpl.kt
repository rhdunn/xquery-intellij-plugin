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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmStaticValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathVarName
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableName
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryVarDecl
import uk.co.reecedunn.intellij.plugin.xquery.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.xquery.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariable
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariableResolver

private val XQUERY10: List<Version> = listOf()
private val XQUERY30: List<Version> = listOf(XQuery.REC_3_0_20140408, MarkLogic.VERSION_6_0)

class XQueryVarDeclPsiImpl(node: ASTNode):
        ASTWrapperPsiElement(node),
        XQueryVarDecl,
        XQueryConformance,
        XQueryVariableResolver,
        XPathVariableDeclaration {
    // region XQueryConformance

    override val requiresConformance get(): List<Version> {
        if (conformanceElement === firstChild) {
            return XQUERY10
        }
        return XQUERY30
    }

    override val conformanceElement get(): PsiElement {
        val element = findChildByType<PsiElement>(XQueryTokenType.ASSIGN_EQUAL)
        var previous: PsiElement? = element?.prevSibling
        while (previous != null && (previous.node.elementType === XQueryElementType.COMMENT || previous.node.elementType === XQueryTokenType.WHITE_SPACE)) {
            previous = previous.prevSibling
        }
        return if (previous == null || previous.node.elementType !== XQueryTokenType.K_EXTERNAL) firstChild else element!!
    }

    // endregion
    // region XPathVariableDeclaration

    private val varName get(): XPathVariableName? =
        children().filterIsInstance<XPathVarName>().firstOrNull() as? XPathVariableName

    override val cacheable get(): CachingBehaviour = varName?.cacheable ?: CachingBehaviour.Cache

    override val variableName get(): QName? = varName?.variableName

    // TODO: Locate and use the TypeDeclaration if present.
    override val variableType: XdmSequenceType? = null

    // TODO: Locate and use the VarValue or VarDefaultValue if present.
    override val variableValue: XdmStaticValue? = null

    // endregion
    // region XQueryVariableResolver

    override fun resolveVariable(name: XPathEQName?): XQueryVariable? {
        val varName = findChildByType<PsiElement>(XQueryElementType.VAR_NAME)
        if (varName != null && varName == name) {
            return XQueryVariable(varName, this)
        }
        return null
    }

    // endregion
}
