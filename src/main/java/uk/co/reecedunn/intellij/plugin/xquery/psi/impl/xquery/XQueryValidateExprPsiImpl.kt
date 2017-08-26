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
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryValidateExpr
import uk.co.reecedunn.intellij.plugin.xquery.lang.*
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle

private val VALIDATE_BY_TYPENAME = TokenSet.create(XQueryTokenType.K_AS, XQueryTokenType.K_TYPE)

class XQueryValidateExprPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryValidateExpr, XQueryConformanceCheck {
    override fun conformsTo(implementation: ImplementationItem): Boolean {
        val element = conformanceElement
        if (element !== firstChild) {
            if (element.node.elementType === XQueryTokenType.K_TYPE) {
                return implementation.getVersion(XQuery).supportsVersion(XQueryVersion.VERSION_3_0) ||
                       implementation.getVersion(MarkLogic).supportsVersion(XQueryVersion.VERSION_6_0)
            }
            return implementation.getVersion(MarkLogic).supportsVersion(XQueryVersion.VERSION_6_0)
        }
        return true
    }

    override val conformanceElement get(): PsiElement =
        findChildByType<PsiElement>(VALIDATE_BY_TYPENAME) ?: firstChild

    override val conformanceErrorMessage get(): String {
        if (node.findChildByType(XQueryTokenType.K_AS) != null) {
            return XQueryBundle.message("requires.feature.marklogic.version", XQueryVersion.VERSION_6_0)
        } else if (node.findChildByType(XQueryTokenType.K_TYPE) != null) {
            return XQueryBundle.message("requires.feature.marklogic-xquery.version")
        }
        return XQueryBundle.message("requires.feature.minimal-conformance.version", XQueryVersion.VERSION_1_0)
    }
}
