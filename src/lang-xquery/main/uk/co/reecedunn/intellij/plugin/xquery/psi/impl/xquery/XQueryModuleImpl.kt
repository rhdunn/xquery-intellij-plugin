/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.intellij.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.intellij.settings.XQueryProjectSettings
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.model.*

class XQueryModuleImpl(provider: FileViewProvider) :
    PsiFileBase(provider, XQuery), XQueryModule, XPathStaticContext {
    // region Object

    override fun toString(): String = "XQueryModule(" + containingFile.name + ")"

    // endregion
    // region PsiFile

    override fun getFileType(): FileType = XQueryFileType

    // endregion
    // region XQueryModule

    private val settings: XQueryProjectSettings = XQueryProjectSettings.getInstance(project)

    private var product: Product? = null
    private var productVersion: Version? = null
    private var xquery: Specification? = null

    private var staticContextCache: XQueryProlog? = null

    @get:Synchronized
    override val predefinedStaticContext
        get(): XQueryProlog? {
            val version: Specification = XQueryVersion.getVersionOrDefault(project)
            if (product !== settings.product || productVersion !== settings.productVersion || xquery !== version) {
                product = settings.product
                productVersion = settings.productVersion
                xquery = version

                var context = product?.implementation?.staticContext(product, productVersion, xquery)
                if (context == null) context = defaultStaticContext(xquery)

                val file = context?.let { StaticContextDefinitions.resolve(it)?.toPsiFile<XQueryModule>(project) }
                val module = file?.children()?.filterIsInstance<XQueryMainModule>()?.firstOrNull()
                staticContextCache = (module as? XQueryPrologResolver)?.prolog?.firstOrNull()
            }
            return staticContextCache
        }

    @Suppress("PropertyName")
    override val XQueryVersions
        get(): Sequence<XQueryVersionRef> {
            var isFirst = true
            return children().map { child ->
                when (child) {
                    is XQueryVersionDecl -> {
                        isFirst = false
                        val version = child.version
                        val xquery = XQuerySpec.versionsForXQuery((version?.value as? XsStringValue)?.data)
                        XQueryVersionRef(version, xquery.firstOrNull())
                    }
                    is XQueryLibraryModule, is XQueryMainModule -> {
                        if (isFirst) {
                            isFirst = false
                            XQueryVersionRef(null, null) // No XQueryVersionDecl for the primary module.
                        } else
                            null
                    }
                    else -> null
                }
            }.filterNotNull()
        }

    @Suppress("PropertyName")
    override val XQueryVersion
        get(): XQueryVersionRef = XQueryVersions.firstOrNull() ?: XQueryVersionRef(null, null)

    override val mainOrLibraryModule
        get(): XQueryPrologResolver? = children().filterIsInstance<XQueryPrologResolver>().firstOrNull()

    // endregion
    // region XPathStaticContext

    override fun staticallyKnownNamespaces(context: PsiElement): Sequence<XPathNamespaceDeclaration> {
        return context.walkTree().reversed().flatMap { node ->
            when (node) {
                is XPathNamespaceDeclaration ->
                    sequenceOf(node as XPathNamespaceDeclaration)
                is XQueryDirElemConstructor ->
                    node.children().filterIsInstance<XQueryDirAttributeList>().firstOrNull()
                        ?.children()?.filterIsInstance<PluginDirAttribute>()?.map { it as XPathNamespaceDeclaration }
                        ?: emptySequence()
                is XQueryProlog ->
                    node.children().reversed().filterIsInstance<XPathNamespaceDeclaration>()
                is XQueryModule ->
                    node.predefinedStaticContext?.children()?.reversed()?.filterIsInstance<XPathNamespaceDeclaration>()
                        ?: emptySequence()
                else -> emptySequence()
            }
        }.filterNotNull().distinct().filter { node -> node.namespacePrefix != null && node.namespaceUri != null }
    }

    override fun defaultNamespace(
        context: PsiElement,
        type: XPathNamespaceType
    ): Sequence<XPathDefaultNamespaceDeclaration> {
        return context.defaultNamespace(type, true)
    }

    override fun staticallyKnownFunctions(): Sequence<XPathFunctionDeclaration?> {
        val prolog = mainOrLibraryModule?.prolog?.firstOrNull() ?: predefinedStaticContext ?: return emptySequence()
        return prolog.importedPrologs().flatMap {
            it.annotatedDeclarations<XPathFunctionDeclaration>()
        }.filter { decl -> decl?.functionName != null }
    }

    override fun staticallyKnownFunctions(eqname: XPathEQName): Sequence<XPathFunctionDeclaration> {
        return eqname.importedPrologsForQName().flatMap { (name, prolog) ->
            prolog.staticallyKnownFunctions(name!!)
        }.filterNotNull()
    }

    override fun inScopeVariables(context: PsiElement): Sequence<XPathVariableDefinition> {
        return context.xqueryInScopeVariables()
    }

    // endregion
}
