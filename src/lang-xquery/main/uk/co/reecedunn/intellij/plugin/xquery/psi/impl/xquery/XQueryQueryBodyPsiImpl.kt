/*
 * Copyright (C) 2016, 2019 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.navigation.ItemPresentationEx
import uk.co.reecedunn.intellij.plugin.core.psi.resourcePath
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryIcons
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryQueryBody
import javax.swing.Icon

class XQueryQueryBodyPsiImpl(node: ASTNode) : XQueryExprPsiImpl(node), XQueryQueryBody, ItemPresentationEx {
    // region NavigationItem

    override fun getPresentation(): ItemPresentation? = this

    // endregion
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon? = XQueryIcons.Nodes.QueryBody

    override fun getLocationString(): String? = null

    override fun getPresentableText(): String? = "$structurePresentableText [${containingFile.resourcePath()}]"

    override val structurePresentableText: String? get() = XQueryBundle.message("structure-view.query-body")

    // endregion
    // region SortableTreeElement

    override fun getAlphaSortKey(): String = presentableText!!

    // endregion
}
