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
import uk.co.reecedunn.intellij.plugin.intellij.lang.Saxon
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.intellij.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xpath.functions.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue
import javax.swing.Icon

class PluginTypeDeclImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), PluginTypeDecl, VersionConformance, ItemPresentation {
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedPresentableText.invalidate()
    }

    // endregion
    // region PluginTypeDecl

    override val typeName get(): XsQNameValue? = children().filterIsInstance<XsQNameValue>().firstOrNull()

    // endregion
    // region VersionConformance

    override val requiresConformance get(): List<Version> = listOf(Saxon.VERSION_9_8)

    override val conformanceElement get(): PsiElement = findChildByType(XQueryTokenType.K_TYPE) ?: firstChild

    // endregion
    // region NavigationItem

    override fun getPresentation(): ItemPresentation? = this

    // endregion
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon? = XPathIcons.Nodes.TypeDecl

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