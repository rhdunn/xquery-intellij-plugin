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
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem
import uk.co.reecedunn.intellij.plugin.xquery.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle

class XQueryCatchClausePsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryCatchClause, XQueryConformanceCheck {
    override fun conformsTo(implementation: ImplementationItem): Boolean {
        if (isMarkLogicExtension) { // MarkLogic CatchClause
            return implementation.getVersion(MarkLogic).supportsVersion(XQueryVersion.VERSION_6_0)
        }
        return true // XQuery 3.0 CatchClause -- handled by the TryClause conformance checks.
    }

    override val conformanceElement get(): PsiElement =
        firstChild

    override val conformanceErrorMessage get(): String =
        XQueryBundle.message("requires.feature.marklogic.version", XQueryVersion.VERSION_6_0)

    override val isMarkLogicExtension get(): Boolean =
        findChildByType<PsiElement>(XQueryTokenType.PARENTHESIS_OPEN) != null
}
