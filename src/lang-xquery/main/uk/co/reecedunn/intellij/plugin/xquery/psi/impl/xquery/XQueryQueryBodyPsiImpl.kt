/*
 * Copyright (C) 2016, 2019-2020 Reece H. Dunn
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

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.ui.IconManager
import com.intellij.util.BitUtil
import com.intellij.util.PlatformIcons
import uk.co.reecedunn.intellij.plugin.core.navigation.ItemPresentationEx
import uk.co.reecedunn.intellij.plugin.core.psi.resourcePath
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xpm.optree.XpmExpression
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryIcons
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryQueryBody
import javax.swing.Icon

class XQueryQueryBodyPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryQueryBody, ItemPresentationEx {
    // region ElementBase

    override fun getElementIcon(flags: Int): Icon {
        val icon = getIcon(false)
        val isLocked = BitUtil.isSet(flags, ICON_FLAG_READ_STATUS) && !isWritable
        val baseIcon = IconManager.getInstance().createLayeredIcon(this, icon, if (isLocked) FLAGS_LOCKED else 0)
        if (BitUtil.isSet(flags, ICON_FLAG_VISIBILITY)) {
            val emptyIcon = IconManager.getInstance().createEmptyIcon(PlatformIcons.PUBLIC_ICON)
            baseIcon.setIcon(emptyIcon, 1)
        }
        return baseIcon
    }

    // endregion
    // region XpmExpression

    override val expressionElement: PsiElement?
        get() {
            val expr = walkTree().mapNotNull {
                if (it !== this)
                    (it as? XpmExpression)?.expressionElement
                else
                    null
            }.firstOrNull()
            return if (expr == null) this else null
        }

    // endregion
    // region NavigationItem

    override fun getPresentation(): ItemPresentation = this

    // endregion
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon = XQueryIcons.Nodes.QueryBody

    override fun getLocationString(): String? = null

    override fun getPresentableText(): String {
        return "${getPresentableText(ItemPresentationEx.Type.StructureView)} [${containingFile.resourcePath()}]"
    }

    override fun getPresentableText(type: ItemPresentationEx.Type): String = when (type) {
        ItemPresentationEx.Type.Default -> presentableText
        else -> XQueryBundle.message("structure-view.query-body")
    }

    // endregion
    // region SortableTreeElement

    override fun getAlphaSortKey(): String = presentableText

    // endregion
}
