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
package uk.co.reecedunn.intellij.plugin.marklogic.xml.search.options

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.marklogic.xml.search.options.CustomFacetFunctionReference.Companion.REFERENCE_TYPES
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmElementNode
import uk.co.reecedunn.intellij.plugin.xdm.xml.hasNodeName
import uk.co.reecedunn.intellij.plugin.xdm.xml.impl.XmlPsiAccessorsProvider
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.xdm.XQueryXmlAccessorsProvider

object SearchOptions : UserDataHolderBase() {
    const val NAMESPACE: String = "http://marklogic.com/appservices/search"

    private val OPTIONS = Key.create<CachedValue<List<PsiElement>>>("OPTIONS")
    private val CUSTOM_FACET_REFS = Key.create<CachedValue<List<CustomFacetFunctionReference>>>("CUSTOM_FACET_REFS")

    fun options(project: Project): List<PsiElement> {
        return CachedValuesManager.getManager(project).getCachedValue(this, OPTIONS, {
            val options = ArrayList<PsiElement>()
            ProjectRootManager.getInstance(project).fileIndex.iterateContent {
                when (val file = it.toPsiFile(project)) {
                    is XmlFile -> optionsXml(file, options)
                    is XQueryModule -> optionsXQuery(file, options)
                    else -> {
                    }
                }
                true
            }
            CachedValueProvider.Result.create(options, PsiModificationTracker.MODIFICATION_COUNT)
        }, false)
    }

    private fun optionsXml(file: XmlFile, options: ArrayList<PsiElement>) {
        val root = file.rootTag ?: return
        if (root.namespace == NAMESPACE && root.localName == "options") {
            options.add(root)
        }
    }

    private fun optionsXQuery(node: PsiElement, options: ArrayList<PsiElement>) {
        when (node) {
            is XdmElementNode -> when {
                XQueryXmlAccessorsProvider.namespaceUri(node) == NAMESPACE -> options.add(node)
                else -> {
                }
            }
            else -> node.children().forEach { child -> optionsXQuery(child, options) }
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun customFacets(project: Project): List<CustomFacetFunctionReference> {
        return CachedValuesManager.getManager(project).getCachedValue(this, CUSTOM_FACET_REFS, {
            val refs = ArrayList<CustomFacetFunctionReference>()
            options(project).map { node ->
                when (node) {
                    is XmlTag -> node.walkTree().filterIsInstance<XmlTag>().forEach { tag ->
                        val accessors = XmlPsiAccessorsProvider
                        if (accessors.hasNodeName(tag, NAMESPACE, REFERENCE_TYPES)) {
                            refs.add(CustomFacetFunctionReference(tag, tag, accessors))
                        }
                    }
                    is XdmElementNode -> node.walkTree().filterIsInstance<XdmElementNode>().forEach { element ->
                        val accessors = XQueryXmlAccessorsProvider
                        if (accessors.hasNodeName(element, NAMESPACE, REFERENCE_TYPES)) {
                            refs.add(CustomFacetFunctionReference(element as PsiElement, element, accessors))
                        }
                    }
                }
            }
            CachedValueProvider.Result.create(refs, PsiModificationTracker.MODIFICATION_COUNT)
        }, false)
    }

    fun customFacets(function: XpmFunctionDeclaration): List<CustomFacetFunctionReference> {
        val name = function.functionName ?: return listOf()
        return customFacets((name as PsiElement).project).filter { ref ->
            if (ref.apply != name.localName?.data) return@filter false

            val module = name.containingFile as XQueryModule
            val library = module.mainOrLibraryModule as? XQueryLibraryModule ?: return@filter false
            val decl = library.children().filterIsInstance<XQueryModuleDecl>().firstOrNull() ?: return@filter false

            ref.moduleNamespace == decl.namespaceUri?.data
        }
    }
}
