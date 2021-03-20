/*
 * Copyright (C) 2019-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.plugin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.reverse
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.filterNotWhitespace
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginArrowFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArgumentList
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.optree.expr.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.map.XpmMapEntry

class PluginArrowFunctionCallPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    PluginArrowFunctionCall,
    XpmSyntaxValidationElement {
    // region PsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedPositionalArguments.invalidate()
        cachedKeywordArguments.invalidate()
    }

    // endregion
    // region XpmExpression

    override val expressionElement: PsiElement
        get() = this

    // endregion
    // region XdmFunctionReference

    override val functionName: XsQNameValue?
        get() = firstChild as? XsQNameValue

    override val arity: Int
        get() = positionalArguments.size + keywordArguments.size + 1

    // endregion
    // region XpmFunctionCall

    private val argumentList: XPathArgumentList
        get() = children().filterIsInstance<XPathArgumentList>().first()

    private val cachedPositionalArguments = CacheableProperty {
        argumentList.children().filterIsInstance<XpmExpression>().toList()
    }

    override val positionalArguments: List<XpmExpression>
        get() = cachedPositionalArguments.get()!!

    private val cachedKeywordArguments = CacheableProperty {
        argumentList.children().filterIsInstance<XpmMapEntry>().toList()
    }

    override val keywordArguments: List<XpmMapEntry>
        get() = cachedKeywordArguments.get()!!

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = reverse(siblings()).filterNotWhitespace().firstOrNull() ?: firstChild

    // endregion
}
