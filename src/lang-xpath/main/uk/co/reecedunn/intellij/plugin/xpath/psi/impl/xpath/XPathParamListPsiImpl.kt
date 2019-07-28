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
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.util.Range
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQueryIntelliJPlugin
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParam
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParamList
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathVariableBinding
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import javax.swing.Icon

private val PARAM_OR_VARIADIC = TokenSet.create(XPathElementType.PARAM, XPathTokenType.ELLIPSIS)
private val XQUERY1: List<Version> = listOf()
private val EXPATH = listOf(XQueryIntelliJPlugin.VERSION_1_4)

class XPathParamListPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), XPathParamList, ItemPresentation, VersionConformance {
    // region VersionConformance

    override val requiresConformance: List<Version> get() = if (isVariadic) EXPATH else XQUERY1

    override val conformanceElement: PsiElement
        get() = children().reversed().firstOrNull { e -> PARAM_OR_VARIADIC.contains(e.node.elementType) } ?: firstChild

    // endregion
    // region PsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedPresentableText.invalidate()
        cachedParams.invalidate()
        cachedArity.invalidate()
    }

    // endregion
    // region NavigationItem

    override fun getPresentation(): ItemPresentation? = this

    // endregion
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon? = null

    override fun getLocationString(): String? = null

    private val cachedPresentableText = CacheableProperty {
        val params = params.mapNotNull { param ->
            (param as NavigatablePsiElement).presentation?.presentableText
        }.joinToString()
        if (isVariadic) "($params ...)" else "($params)"
    }

    override fun getPresentableText(): String? = cachedPresentableText.get()

    // endregion
    // region XPathParamList

    private val cachedParams = CacheableProperty {
        children().filterIsInstance<XPathParam>().map { param -> param as XPathVariableBinding }.toList()
    }

    override val params: List<XPathVariableBinding> get() = cachedParams.get()!!

    private val cachedArity = CacheableProperty {
        params.size.let {
            if (conformanceElement.node.elementType == XPathElementType.PARAM)
                Range(it, it) // non-variadic parameter list
            else
                Range(it - 1, Int.MAX_VALUE) // variadic parameter list
        }
    }

    override val arity get(): Range<Int> = cachedArity.get()!!

    override val isVariadic: Boolean get() = conformanceElement.node.elementType == XPathTokenType.ELLIPSIS

    // endregion
}
