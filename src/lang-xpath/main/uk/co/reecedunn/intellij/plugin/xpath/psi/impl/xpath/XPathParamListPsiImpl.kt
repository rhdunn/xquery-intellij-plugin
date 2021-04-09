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

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.Key
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.util.Range
import uk.co.reecedunn.intellij.plugin.core.psi.ASTWrapperPsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.reverse
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParam
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParamList
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.optree.annotation.XpmVariadic
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmParameter
import javax.swing.Icon

class XPathParamListPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), XPathParamList, ItemPresentation, XpmSyntaxValidationElement {
    companion object {
        private val PRESENTABLE_TEXT = Key.create<String>("PRESENTABLE_TEXT")

        private val PARAM_OR_VARIADIC = TokenSet.create(XPathElementType.PARAM, XPathTokenType.ELLIPSIS)
    }
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = reverse(children()).firstOrNull { e -> PARAM_OR_VARIADIC.contains(e.elementType) } ?: firstChild

    // endregion
    // region PsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(PRESENTABLE_TEXT)
    }

    // endregion
    // region NavigationItem

    override fun getPresentation(): ItemPresentation = this

    // endregion
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon? = null

    override fun getLocationString(): String? = null

    override fun getPresentableText(): String? = computeUserDataIfAbsent(PRESENTABLE_TEXT) {
        val params = params.mapNotNull { param ->
            (param as NavigatablePsiElement).presentation?.presentableText
        }.joinToString()
        if (variadicType === XpmVariadic.Ellipsis) "($params ...)" else "($params)"
    }

    // endregion
    // region XPathParamList

    private val params: List<XpmParameter>
        get() = children().filterIsInstance<XPathParam>().toList()

    private val variadicType: XpmVariadic
        get() = when (conformanceElement.elementType) {
            XPathTokenType.ELLIPSIS -> XpmVariadic.Ellipsis
            else -> XpmVariadic.No
        }

    // endregion
}
