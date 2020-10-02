/*
 * Copyright (C) 2016-2017, 2020 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryValidateExpr
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

private val XQUERY10: List<Version> = listOf()
private val XQUERY30: List<Version> = listOf(XQuerySpec.REC_3_0_20140408, MarkLogic.VERSION_6_0)
private val MARKLOGIC60: List<Version> = listOf()

class XQueryValidateExprPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), XQueryValidateExpr, XpmSyntaxValidationElement, VersionConformance {

    override val expressionElement: PsiElement
        get() = this

    override val requiresConformance: List<Version>
        get() {
            val element = conformanceElement
            if (element !== firstChild) {
                return when (element.elementType) {
                    XPathTokenType.K_AS, XQueryTokenType.K_FULL -> MARKLOGIC60
                    else -> XQUERY30
                }
            }
            return XQUERY10
        }

    override val conformanceElement: PsiElement
        get() = findChildByType(XQueryTokenType.VALIDATE_EXPR_MODE_OR_TYPE_TOKENS) ?: firstChild
}
