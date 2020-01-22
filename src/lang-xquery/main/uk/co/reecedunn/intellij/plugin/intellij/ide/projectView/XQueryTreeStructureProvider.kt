/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.ide.projectView

import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.DumbAware
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.ide.projectView.XQueryModuleTreeNode

class XQueryTreeStructureProvider : TreeStructureProvider, DumbAware {
    @Suppress("UNCHECKED_CAST", "MoveVariableDeclarationIntoWhen")
    override fun modify(
        parent: AbstractTreeNode<*>,
        children: MutableCollection<AbstractTreeNode<*>>,
        settings: ViewSettings
    ): MutableCollection<AbstractTreeNode<*>> {
        return children.map { child ->
            val value = child.value
            when (value) {
                is XQueryModule -> XQueryModuleTreeNode(value, settings) as AbstractTreeNode<Any>
                else -> child
            }
        }.toMutableList()
    }
}
