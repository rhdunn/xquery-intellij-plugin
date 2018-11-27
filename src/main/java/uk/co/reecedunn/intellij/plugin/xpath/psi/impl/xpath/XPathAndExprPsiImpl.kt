/*
 * Copyright (C) 2016, 2018 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.intellij.lang.Saxon
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathAndExpr
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance

private var XQUERY10: List<Version> = listOf()
private var SAXON99: List<Version> = listOf(Saxon.VERSION_9_9)

class XPathAndExprPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathAndExpr,
    VersionConformance {

    override val requiresConformance: List<Version>
        get() = if (conformanceElement === firstChild) XQUERY10 else SAXON99

    override val conformanceElement: PsiElement
        get() = findChildByType(XQueryTokenType.K_ANDALSO) ?: firstChild
}
