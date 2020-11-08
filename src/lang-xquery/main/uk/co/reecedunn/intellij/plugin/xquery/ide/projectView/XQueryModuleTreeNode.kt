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
package uk.co.reecedunn.intellij.plugin.xquery.ide.projectView

import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.variable.XpmVariableDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginTypeDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*

class XQueryModuleTreeNode(module: XQueryModule, viewSettings: ViewSettings) :
    PsiFileNode(module.project, module, viewSettings) {

    override fun getChildrenImpl(): MutableCollection<AbstractTreeNode<Any>> = when (settings.isShowMembers) {
        true -> getPrologDeclarations()?.toMutableList() ?: mutableListOf()
        else -> mutableListOf()
    }

    @Suppress("UNCHECKED_CAST")
    private fun getPrologDeclarations(): Sequence<AbstractTreeNode<Any>>? {
        val prolog: XQueryProlog? = (value as XQueryModule).mainOrLibraryModule?.prolog?.firstOrNull()
        return prolog?.children()?.flatMap { decl ->
            when (decl) {
                is XQueryAnnotatedDecl -> {
                    decl.children().map { annotatedDecl ->
                        when (annotatedDecl) {
                            is XQueryFunctionDecl -> {
                                (annotatedDecl as XpmFunctionDeclaration).functionName?.localName?.let {
                                    XQueryLeafNode(annotatedDecl, settings) as AbstractTreeNode<Any>
                                }
                            }
                            is XQueryVarDecl -> {
                                (annotatedDecl as XpmVariableDeclaration).variableName?.localName?.let {
                                    XQueryLeafNode(annotatedDecl, settings) as AbstractTreeNode<Any>
                                }
                            }
                            else -> null
                        }
                    }
                }
                is PluginTypeDecl -> sequenceOf(XQueryLeafNode(decl, settings) as AbstractTreeNode<Any>)
                else -> emptySequence()
            }
        }?.filterNotNull()
    }
}
