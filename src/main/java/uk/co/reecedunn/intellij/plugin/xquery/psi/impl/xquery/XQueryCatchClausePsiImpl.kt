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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCatchClause
import uk.co.reecedunn.intellij.plugin.xquery.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.xquery.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance

private val XQUERY10: List<Version> = listOf()
private val MARKLOGIC60: List<Version> = listOf(MarkLogic.VERSION_6_0)

class XQueryCatchClausePsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryCatchClause, XQueryConformance {
    override val requiresConformance get(): List<Version> {
        if (isMarkLogicExtension) { // MarkLogic CatchClause
            return MARKLOGIC60
        }
        return XQUERY10 // XQuery 3.0 CatchClause -- handled by the TryClause conformance checks.
    }

    override val conformanceElement get(): PsiElement =
        firstChild

    override val isMarkLogicExtension get(): Boolean =
        findChildByType<PsiElement>(XQueryTokenType.PARENTHESIS_OPEN) != null
}
