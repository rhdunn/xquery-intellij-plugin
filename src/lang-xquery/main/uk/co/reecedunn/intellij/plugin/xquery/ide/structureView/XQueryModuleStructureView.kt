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
package uk.co.reecedunn.intellij.plugin.xquery.ide.structureView

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpm.optree.XpmAnnotatedDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryItemTypeDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.model.annotatedDeclarations
import javax.swing.Icon

class XQueryModuleStructureView(module: XQueryModule) : PsiTreeElementBase<XQueryModule>(module) {
    override fun getChildrenBase(): MutableCollection<out StructureViewTreeElement> {
        return element?.mainOrLibraryModule?.children()?.flatMap { child ->
            when (child) {
                is XQueryProlog -> child.annotatedDeclarations<XpmAnnotatedDeclaration>(reversed = false).map { decl ->
                    when (decl) {
                        is XQueryFunctionDecl -> {
                            (decl as XpmFunctionDeclaration).functionName?.localName?.let {
                                StructureViewLeafNode(decl)
                            }
                        }
                        is XQueryVarDecl -> {
                            (decl as XpmVariableDeclaration).variableName?.localName?.let {
                                StructureViewLeafNode(decl)
                            }
                        }
                        is XQueryItemTypeDecl -> StructureViewLeafNode(decl)
                        else -> null
                    }
                }
                is XQueryQueryBody -> sequenceOf(StructureViewLeafNode(child))
                else -> emptySequence()
            }
        }?.filterNotNull()?.toMutableList<StructureViewTreeElement>() ?: mutableListOf()
    }

    override fun getPresentableText(): String? = element?.presentation?.presentableText

    override fun getIcon(open: Boolean): Icon? = element?.presentation?.getIcon(open)
}
