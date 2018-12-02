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
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArgumentList
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathPostfixExpr
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType

private val ARGUMENTS = TokenSet.create(
    XQueryElementType.ARGUMENT,
    XPathElementType.ARGUMENT_PLACEHOLDER
)

private val XQUERY10: List<Version> = listOf()
private val XQUERY30: List<Version> = listOf(XQuerySpec.REC_3_0_20140408, MarkLogic.VERSION_6_0)

class XPathArgumentListPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathArgumentList, VersionConformance {
    override val requiresConformance
        get(): List<Version> {
            if (parent !is XPathPostfixExpr) {
                return XQUERY10
            }
            return XQUERY30
        }

    override val conformanceElement get(): PsiElement = firstChild

    override val arity
        get(): Int = children().filter { e -> ARGUMENTS.contains(e.node.elementType) }.count()
}
