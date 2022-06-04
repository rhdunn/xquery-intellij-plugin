/*
 * Copyright (C) 2021-2022 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.search.options

import com.intellij.compat.psi.util.PsiModificationTracker
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.marklogic.search.options.reference.CustomConstraintFunctionReference
import uk.co.reecedunn.intellij.plugin.marklogic.search.options.reference.CustomConstraintFunctionReference.Companion.REFERENCE_TYPES
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmElementNode
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.localName
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.namespaceUri
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery

class SearchOptions {
    companion object {
        const val NAMESPACE: String = "http://marklogic.com/appservices/search"

        private val OPTIONS = Key.create<CachedValue<List<PsiElement>>>("XIJP_MARKLOGIC_SEARCH_OPTIONS")

        private val CUSTOM_FACET_REFS =
            Key.create<CachedValue<List<CustomConstraintFunctionReference>>>("XIJP_MARKLOGIC_SEARCH_OPTIONS_CUSTOM_FACET_REFS")

        fun getInstance(): SearchOptions = ApplicationManager.getApplication().getService(SearchOptions::class.java)
    }

    private fun getModificationTracker(project: Project): ModificationTracker {
        return PsiModificationTracker.getInstance(project).forLanguages {
            it === XQuery
        }
    }

    private fun getSearchOptions(project: Project): List<PsiElement> {
        return CachedValuesManager.getManager(project).getCachedValue(project, OPTIONS, {
            val options = ArrayList<PsiElement>()
            ProjectRootManager.getInstance(project).fileIndex.iterateContent {
                when (val file = it.toPsiFile(project)) {
                    is XQueryModule -> processOptions(file, options)
                    else -> {
                    }
                }
                true
            }
            CachedValueProvider.Result.create(options, getModificationTracker(project))
        }, false)
    }

    private fun processOptions(node: PsiElement, options: ArrayList<PsiElement>) {
        when (node) {
            is XdmElementNode -> when (node.namespaceUri) {
                NAMESPACE -> options.add(node)
                else -> {
                }
            }
            else -> node.children().forEach { child -> processOptions(child, options) }
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun getCustomConstraintFunctionReferences(project: Project): List<CustomConstraintFunctionReference> {
        return CachedValuesManager.getManager(project).getCachedValue(project, CUSTOM_FACET_REFS, {
            val refs = ArrayList<CustomConstraintFunctionReference>()
            getSearchOptions(project).map { node ->
                when (node) {
                    is XdmElementNode -> node.walkTree().filterIsInstance<XdmElementNode>().forEach { element ->
                        if (element.localName in REFERENCE_TYPES && element.namespaceUri == NAMESPACE) {
                            refs.add(CustomConstraintFunctionReference(element as PsiElement, element))
                        }
                    }
                }
            }
            CachedValueProvider.Result.create(refs, PsiModificationTracker.MODIFICATION_COUNT)
        }, false)
    }

    fun getCustomConstraintFunctionReferences(function: XpmFunctionDeclaration): List<CustomConstraintFunctionReference> {
        val name = function.functionName ?: return listOf()
        return getCustomConstraintFunctionReferences((name as PsiElement).project).filter { ref ->
            if (ref.apply != name.localName?.data) return@filter false

            val module = name.containingFile as XQueryModule
            val library = module.mainOrLibraryModule as? XQueryLibraryModule ?: return@filter false
            val decl = library.children().filterIsInstance<XQueryModuleDecl>().firstOrNull() ?: return@filter false

            ref.moduleNamespace == decl.namespaceUri?.data
        }
    }
}
