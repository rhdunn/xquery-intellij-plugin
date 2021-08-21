/*
 * Copyright (C) 2019-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.ide.structureView

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import uk.co.reecedunn.intellij.plugin.core.navigation.ItemPresentationEx
import uk.co.reecedunn.intellij.plugin.xpm.optree.annotation.XpmAccessLevel
import uk.co.reecedunn.intellij.plugin.xpm.optree.annotation.XpmAnnotated

class StructureViewLeafNode(leaf: XQueryStructureViewElement) :
    PsiTreeElementBase<XQueryStructureViewElement>(leaf),
    SortableTreeElement {
    // region PsiTreeElementBase

    override fun getChildrenBase(): MutableCollection<StructureViewTreeElement> = mutableListOf()

    // endregion
    // region TreeElement

    override fun getPresentation(): ItemPresentation = this

    // endregion
    // region ItemPresentation

    override fun getPresentableText(): String? = when (val presentation = (element as? NavigationItem)?.presentation) {
        is ItemPresentationEx -> presentation.getPresentableText(ItemPresentationEx.Type.StructureView)
        else -> presentation?.presentableText
    }

    // endregion
    // region SortableTreeElement

    override fun getAlphaSortKey(): String = element!!.alphaSortKey

    val accessLevel: XpmAccessLevel = when (val element = element) {
        is XpmAnnotated -> element.accessLevel
        else -> XpmAccessLevel.Unknown
    }

    // endregion
}
