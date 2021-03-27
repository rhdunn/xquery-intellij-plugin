/*
 * Copyright (C) 2017, 2019, 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryItemTypeDecl
import uk.co.reecedunn.intellij.plugin.xpath.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import java.util.*
import javax.swing.Icon

class XQueryItemTypeDeclPsiImpl(node: ASTNode) :
    XQueryAnnotatedDeclPsiImpl(node),
    XQueryItemTypeDecl,
    XpmSyntaxValidationElement,
    ItemPresentation {
    companion object {
        private val PRESENTABLE_TEXT = Key.create<Optional<String>>("PRESENTABLE_TEXT")
    }
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(PRESENTABLE_TEXT)
    }

    // endregion
    // region PluginTypeDecl

    override val typeName: XsQNameValue?
        get() = children().filterIsInstance<XsQNameValue>().firstOrNull()

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = findChildByType(XQueryTokenType.ITEM_TYPE_DECL_TOKENS) ?: firstChild

    // endregion
    // region NavigationItem

    override fun getPresentation(): ItemPresentation = this

    // endregion
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon = XPathIcons.Nodes.TypeDecl

    override fun getLocationString(): String? = null

    override fun getPresentableText(): String? = computeUserDataIfAbsent(PRESENTABLE_TEXT) {
        Optional.ofNullable(typeName?.let { op_qname_presentation(it) })
    }.orElse(null)

    // endregion
    // region SortableTreeElement

    override fun getAlphaSortKey(): String = presentableText ?: ""

    // endregion
}
