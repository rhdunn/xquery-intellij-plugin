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
import uk.co.reecedunn.intellij.plugin.core.data.Cacheable
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.core.data.`is`
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.filterNotToken
import uk.co.reecedunn.intellij.plugin.core.sequences.withNext
import uk.co.reecedunn.intellij.plugin.xdm.XsUntyped
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmConstantExpression
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmLexicalValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathPostfixExpr
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType

class XPathPostfixExprPsiImpl(node: ASTNode):
        ASTWrapperPsiElement(node),
        XPathPostfixExpr,
        XdmConstantExpression {

    override fun subtreeChanged() {
        super.subtreeChanged()
        staticEval.invalidate()
    }

    /**
     * Perform static evaluation on the PostfixExpr to determine the static type and value.
     */
    private val staticEval: CacheableProperty<Pair<XdmSequenceType, Any?>?> = CacheableProperty {
        val children = children().filterNotToken(XQueryElementType.WHITESPACE_OR_COMMENT).iterator()
        children.withNext { value ->
            if (value !is XdmLexicalValue || children.hasNext())
                null
            else // Literal without a Predicate, ArgumentList, or Lookup expression.
                Pair(value.staticType, value.lexicalRepresentation)
        } `is` Cacheable
    }

    override val cacheable get(): CachingBehaviour = staticEval.cachingBehaviour

    override val staticType get(): XdmSequenceType = staticEval.get()?.first ?: XsUntyped

    override val constantValue get(): Any? = staticEval.get()?.second
}
