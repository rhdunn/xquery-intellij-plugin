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
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathForwardAxis
import uk.co.reecedunn.intellij.plugin.intellij.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance

private val MARKLOGIC_AXIS = TokenSet.create(XPathTokenType.K_NAMESPACE, XPathTokenType.K_PROPERTY)

private val XQUERY10: List<Version> = listOf()
private val MARKLOGIC60: List<Version> = listOf(MarkLogic.VERSION_6_0)

class XPathForwardAxisPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathForwardAxis, VersionConformance {
    override val requiresConformance
        get(): List<Version> {
            val node = node.findChildByType(MARKLOGIC_AXIS)
            if (node != null) {
                return MARKLOGIC60
            }
            return XQUERY10
        }

    override val conformanceElement get(): PsiElement = firstChild
}
