/*
 * Copyright (C) 2016-2017, 2021 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathMapConstructorEntry
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression

class XPathMapConstructorEntryPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathMapConstructorEntry {
    // region XPathMapConstructorEntry

    override val separator: PsiElement
        get() = findChildByType(ASSIGNMENT) ?: firstChild

    companion object {
        private val ASSIGNMENT = TokenSet.create(XPathTokenType.QNAME_SEPARATOR, XPathTokenType.ASSIGN_EQUAL)
    }

    // endregion
    // region XpmMapEntry

    override val keyExpression: XpmExpression
        get() = firstChild as XpmExpression

    override val valueExpression: XpmExpression?
        get() = firstChild.siblings().filterIsInstance<XpmExpression>().firstOrNull()

    // endregion
}
