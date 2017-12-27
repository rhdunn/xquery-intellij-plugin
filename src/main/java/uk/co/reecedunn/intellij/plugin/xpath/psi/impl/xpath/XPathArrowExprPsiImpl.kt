/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArrowExpr
import uk.co.reecedunn.intellij.plugin.xquery.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.xquery.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance

private val XQUERY10: List<Version> = listOf()
private val XQUERY31: List<Version> = listOf(XQuery.REC_3_1_20170321, MarkLogic.VERSION_9_0)

class XPathArrowExprPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathArrowExpr, XQueryConformance {
    override val requiresConformance get(): List<Version> {
        if (conformanceElement === firstChild) {
            return XQUERY10
        }
        return XQUERY31
    }

    override val conformanceElement get(): PsiElement =
        findChildByType(XQueryTokenType.ARROW) ?: firstChild
}
