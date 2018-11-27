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
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParenthesizedItemType
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType

private val XQUERY3 = listOf(XQuerySpec.REC_3_0_20140408, MarkLogic.VERSION_6_0)
private val SEMANTICS = listOf(FormalSemantics.REC_1_0_20070123)

class XPathParenthesizedItemTypePsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathParenthesizedItemType,
    VersionConformance,
    VersionConformanceName {

    override val requiresConformance: List<Version>
        get() = if (findChildByType<PsiElement>(XQueryElementType.SEQUENCE_TYPE) != null) SEMANTICS else XQUERY3

    override val conformanceElement get(): PsiElement = firstChild

    override val conformanceName: String?
        get() {
            return if (findChildByType<PsiElement>(XQueryElementType.SEQUENCE_TYPE) != null)
                XQueryBundle.message("construct.parenthesized-sequence-type")
            else
                null
        }
}
