/*
 * Copyright (C) 2017, 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.plugin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginTypeDecl
import uk.co.reecedunn.intellij.plugin.xpath.intellij.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import javax.swing.Icon

class PluginTypeDeclImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), PluginTypeDecl, XpmSyntaxValidationElement, ItemPresentation {
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedPresentableText.invalidate()
    }

    // endregion
    // region PluginTypeDecl

    override val typeName: XsQNameValue?
        get() = children().filterIsInstance<XsQNameValue>().firstOrNull()

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = findChildByType(XPathTokenType.K_TYPE) ?: firstChild

    // endregion
    // region NavigationItem

    override fun getPresentation(): ItemPresentation = this

    // endregion
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon = XPathIcons.Nodes.TypeDecl

    override fun getLocationString(): String? = null

    private val cachedPresentableText = CacheableProperty {
        typeName?.let { op_qname_presentation(it) }
    }

    override fun getPresentableText(): String? = cachedPresentableText.get()

    // endregion
    // region SortableTreeElement

    override fun getAlphaSortKey(): String = presentableText ?: ""

    // endregion
}
