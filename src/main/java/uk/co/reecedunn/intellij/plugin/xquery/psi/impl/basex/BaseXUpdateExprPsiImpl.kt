/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.basex

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xquery.ast.basex.BaseXUpdateExpr
import uk.co.reecedunn.intellij.plugin.xquery.lang.BaseX
import uk.co.reecedunn.intellij.plugin.xquery.lang.ImplementationItem
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle

class BaseXUpdateExprPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), BaseXUpdateExpr, XQueryConformanceCheck {
    internal val requiredVersion get(): XQueryVersion {
        if (findChildByType<PsiElement>(XQueryTokenType.BLOCK_OPEN) != null) {
            return XQueryVersion.VERSION_8_5
        }
        return XQueryVersion.VERSION_8_4
    }

    override fun conformsTo(implementation: ImplementationItem): Boolean =
        // NOTE: UpdateExpr was introduced in BaseX 7.8, but this plugin only supports >= 8.4.
        implementation.getVersion(BaseX).supportsVersion(requiredVersion)

    override val conformanceElement get(): PsiElement {
        val element = findChildByType<PsiElement>(XQueryTokenType.BLOCK_OPEN)
        return element ?: findChildByType<PsiElement>(XQueryTokenType.K_UPDATE) ?: firstChild
    }

    override val conformanceErrorMessage get(): String =
        // NOTE: UpdateExpr was introduced in BaseX 7.8, but this plugin only supports >= 8.4.
        XQueryBundle.message("requires.feature.basex.version", requiredVersion)
}
