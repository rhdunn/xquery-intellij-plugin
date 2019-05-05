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
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*

class XQueryModuleStructureView(val module: XQueryModule) : StructureViewTreeElement {
    // region Navigatable

    override fun navigate(requestFocus: Boolean) = module.navigate(requestFocus)

    override fun canNavigate(): Boolean = module.canNavigate()

    override fun canNavigateToSource(): Boolean = module.canNavigateToSource()

    // endregion
    // region TreeElement

    override fun getPresentation(): ItemPresentation = module.presentation!!

    override fun getChildren(): Array<TreeElement> {
        val mainOrLibraryModule =
            module.children().filter { it is XQueryMainModule || it is XQueryLibraryModule }.firstOrNull()
        return mainOrLibraryModule?.children()?.flatMap { child ->
            when (child) {
                is XQueryProlog -> child.children().flatMap { decl ->
                    when (decl) {
                        is XQueryAnnotatedDecl -> {
                            decl.children().map { annotatedDecl ->
                                when (annotatedDecl) {
                                    is XQueryFunctionDecl -> StructureViewLeafNode(annotatedDecl)
                                    is XQueryVarDecl -> StructureViewLeafNode(annotatedDecl)
                                    else -> null
                                }
                            }
                        }
                        else -> emptySequence()
                    }
                }
                is XQueryQueryBody -> sequenceOf(StructureViewLeafNode(child))
                else -> emptySequence()
            }
        }?.filterNotNull()?.toList<TreeElement>()?.toTypedArray() ?: TreeElement.EMPTY_ARRAY
    }

    // endregion
    // region StructureViewTreeElement

    override fun getValue(): Any = module

    // endregion
}
