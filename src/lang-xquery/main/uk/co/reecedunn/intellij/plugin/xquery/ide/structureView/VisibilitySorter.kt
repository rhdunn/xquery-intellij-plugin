/*
 * Copyright (C) 2021 Reece H. Dunn
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

import com.intellij.icons.AllIcons
import com.intellij.ide.structureView.StructureViewBundle
import com.intellij.ide.util.treeView.smartTree.ActionPresentation
import com.intellij.ide.util.treeView.smartTree.Sorter
import uk.co.reecedunn.intellij.plugin.xpm.optree.annotation.XpmAccessLevel
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle
import java.util.Comparator
import javax.swing.Icon

object VisibilitySorter : Sorter, ActionPresentation, Comparator<Any> {
    // region Sorter

    override fun getName(): String = "XQUERY_VISIBILITY_SORTER"

    override fun getComparator(): Comparator<*> = this

    override fun isVisible(): Boolean = true

    override fun getPresentation(): ActionPresentation = this

    // endregion
    // region ActionPresentation

    override fun getText(): String = XQueryBundle.message("action.structureview.sort.by.visibility")

    override fun getDescription(): String? = null

    override fun getIcon(): Icon = AllIcons.ObjectBrowser.VisibilitySort

    // endregion
    // region Comparator

    override fun compare(descriptor1: Any, descriptor2: Any): Int {
        return getAccessLevel(descriptor2).sortOrder - getAccessLevel(descriptor1).sortOrder
    }

    private fun getAccessLevel(element: Any): XpmAccessLevel = when (element) {
        is StructureViewLeafNode -> element.accessLevel
        else -> XpmAccessLevel.Unknown
    }

    // endregion
}
