/*
 * Copyright (C) 2016-2021 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.reverse
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParam
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParamList
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmParameter
import javax.swing.Icon

class XPathParamListPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), XPathParamList, ItemPresentation, XpmSyntaxValidationElement {
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = reverse(children()).firstOrNull { e -> PARAM_OR_VARIADIC.contains(e.elementType) } ?: firstChild

    companion object {
        private val PARAM_OR_VARIADIC = TokenSet.create(XPathElementType.PARAM, XPathTokenType.ELLIPSIS)
    }

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

    override fun getPresentation(): ItemPresentation = this

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
        children().filterIsInstance<XPathParam>().toList()
    }

    override val params: List<XpmParameter>
        get() = cachedParams.get()!!

    private val cachedArity = CacheableProperty {
        params.size.let {
            if (conformanceElement.elementType == XPathElementType.PARAM)
                Range(it, it) // non-variadic parameter list
            else
                Range(it - 1, Int.MAX_VALUE) // variadic parameter list
        }
    }

    override val arity: Range<Int>
        get() = cachedArity.get()!!

    override val isVariadic: Boolean
        get() = conformanceElement.elementType == XPathTokenType.ELLIPSIS

    // endregion
}
