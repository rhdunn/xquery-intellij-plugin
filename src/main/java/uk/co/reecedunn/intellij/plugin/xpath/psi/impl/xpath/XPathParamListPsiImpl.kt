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
import com.intellij.psi.tree.TokenSet
import com.intellij.util.Range
import uk.co.reecedunn.intellij.plugin.core.data.Cacheable
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.data.`is`
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQueryIntelliJPlugin
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParam
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParamList
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableBinding
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType

private val PARAM_OR_VARIADIC = TokenSet.create(XPathElementType.PARAM, XQueryTokenType.ELLIPSIS)
private val XQUERY1: List<Version> = listOf()
private val EXPATH = listOf(XQueryIntelliJPlugin.VERSION_1_4)

class XPathParamListPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathParamList,
    VersionConformance {
    // region VersionConformance

    override val requiresConformance: List<Version>
        get() = if (conformanceElement.node.elementType == XPathElementType.PARAM) XQUERY1 else EXPATH

    override val conformanceElement: PsiElement
        get() = children().reversed().firstOrNull { e -> PARAM_OR_VARIADIC.contains(e.node.elementType) } ?: firstChild

    // endregion
    // region PsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedArguments.invalidate()
        cachedArity.invalidate()
    }

    // endregion
    // region XPathParamList

    private val cachedArguments = CacheableProperty {
        children().filterIsInstance<XPathParam>().map { param -> param as XPathVariableBinding }.toList() `is` Cacheable
    }

    private val cachedArity = CacheableProperty {
        cachedArguments.get()!!.size.let {
            if (conformanceElement.node.elementType == XPathElementType.PARAM)
                Range(it, it) // non-variadic parameter list
            else
                Range(it - 1, Int.MAX_VALUE) // variadic parameter list
        } `is` Cacheable
    }

    override val arity get(): Range<Int> = cachedArity.get()!!

    // endregion
}
