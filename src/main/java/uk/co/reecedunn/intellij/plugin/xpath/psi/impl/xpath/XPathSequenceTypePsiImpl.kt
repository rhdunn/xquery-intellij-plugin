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
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathSequenceType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance

private val XQUERY10_REC_EMPTY: List<Version> = listOf(
    XQuerySpec.REC_1_0_20070123,
    EXistDB.VERSION_4_0
)
private val XQUERY10_REC_OCCURRENCE: List<Version> = listOf()
private val XQUERY10_WD_EMPTY: List<Version> = listOf(
    XQuerySpec.WD_1_0_20030502,
    XQuerySpec.MARKLOGIC_0_9,
    until(EXistDB.VERSION_4_0)
)

private val OCCURRENCE_INDICATOR = TokenSet.create(
    XPathTokenType.PLUS,
    XPathTokenType.OPTIONAL,
    XPathTokenType.STAR
)

class XPathSequenceTypePsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathSequenceType, VersionConformance {
    override val requiresConformance: List<Version>
        get() {
            return if (conformanceElement.node.elementType == XQueryTokenType.K_EMPTY)
                XQUERY10_WD_EMPTY
            else if (findChildByType<PsiElement>(OCCURRENCE_INDICATOR) != null)
                XQUERY10_REC_OCCURRENCE
            else
                XQUERY10_REC_EMPTY
        }

    override val conformanceElement: PsiElement get() = firstChild
}
