/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.structureView

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import uk.co.reecedunn.intellij.plugin.intellij.ide.structureView.XQueryStructureViewElement

class StructureViewLeafNode(val leaf: XQueryStructureViewElement) : StructureViewTreeElement {
    // region Navigatable

    override fun navigate(requestFocus: Boolean) = leaf.navigate(requestFocus)

    override fun canNavigate(): Boolean = leaf.canNavigate()

    override fun canNavigateToSource(): Boolean = leaf.canNavigateToSource()

    // endregion
    // region TreeElement

    override fun getPresentation(): ItemPresentation = leaf.presentation!!

    override fun getChildren(): Array<TreeElement> = TreeElement.EMPTY_ARRAY

    // endregion
    // region StructureViewTreeElement

    override fun getValue(): Any = leaf

    // endregion
}
