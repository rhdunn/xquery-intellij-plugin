/*
 * Copyright (C) 2016-2020 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.intellij.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xquery.intellij.settings.XQueryProjectSettings
import uk.co.reecedunn.intellij.plugin.xdm.context.XstUsageType
import uk.co.reecedunn.intellij.plugin.xdm.functions.XdmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xdm.namespaces.XdmDefaultNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xdm.namespaces.XdmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xdm.namespaces.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.variables.XdmVariableDefinition
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.model.*
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType

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

                val file = context?.let { StaticContextDefinitions.resolve(it)?.toPsiFile(project) as? XQueryModule }
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
                        val xquery = XQuerySpec.versionsForXQuery(version?.data)
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
    // region XstContext

    override fun getUsageType(element: PsiElement): XstUsageType? {
        val parentType = element.parent.elementType
        return when {
            element.elementType === XQueryElementType.COMPATIBILITY_ANNOTATION -> XstUsageType.Annotation
            parentType === XQueryElementType.DIR_ATTRIBUTE -> {
                if ((element as? XsQNameValue)?.prefix?.data == "xmlns")
                    XstUsageType.Namespace
                else
                    XstUsageType.Attribute
            }
            else -> USAGE_TYPES[parentType]
        }
    }

    override fun expandQName(qname: XsQNameValue): Sequence<XsQNameValue> = qname.expandQName()

    // endregion
    // region XPathStaticContext

    override fun staticallyKnownNamespaces(context: PsiElement): Sequence<XdmNamespaceDeclaration> {
        return context.walkTree().reversed().flatMap { node ->
            when (node) {
                is XdmNamespaceDeclaration -> sequenceOf(node as XdmNamespaceDeclaration)
                is XQueryDirElemConstructor ->
                    node.children().filterIsInstance<XQueryDirAttributeList>().firstOrNull()
                        ?.children()?.filterIsInstance<PluginDirAttribute>()?.map { it as XdmNamespaceDeclaration }
                        ?: emptySequence()
                is XQueryProlog -> node.children().reversed().filterIsInstance<XdmNamespaceDeclaration>()
                is XQueryModule ->
                    node.predefinedStaticContext?.children()?.reversed()?.filterIsInstance<XdmNamespaceDeclaration>()
                        ?: emptySequence()
                else -> emptySequence()
            }
        }.filterNotNull().distinct().filter { node -> node.namespacePrefix != null && node.namespaceUri != null }
    }

    override fun defaultNamespace(
        context: PsiElement,
        type: XdmNamespaceType
    ): Sequence<XdmDefaultNamespaceDeclaration> {
        return context.defaultNamespace(type, true)
    }

    override fun staticallyKnownFunctions(): Sequence<XdmFunctionDeclaration?> {
        val prolog = mainOrLibraryModule?.prolog?.firstOrNull() ?: predefinedStaticContext ?: return emptySequence()
        return prolog.importedPrologs().flatMap {
            it.annotatedDeclarations<XdmFunctionDeclaration>()
        }.filter { decl -> decl?.functionName != null }
    }

    override fun staticallyKnownFunctions(eqname: XPathEQName): Sequence<XdmFunctionDeclaration> {
        return eqname.importedPrologsForQName().flatMap { (name, prolog) ->
            prolog.staticallyKnownFunctions(name!!)
        }.filterNotNull()
    }

    override fun inScopeVariables(context: PsiElement): Sequence<XdmVariableDefinition> {
        return context.xqueryInScopeVariables()
    }

    // endregion

    companion object {
        val USAGE_TYPES = mapOf(
            XQueryElementType.ANNOTATION to XstUsageType.Annotation,
            XPathElementType.ARROW_FUNCTION_SPECIFIER to XstUsageType.FunctionRef,
            XPathElementType.ATOMIC_OR_UNION_TYPE to XstUsageType.Type,
            XPathElementType.ATTRIBUTE_TEST to XstUsageType.Attribute,
            XQueryElementType.COMP_ATTR_CONSTRUCTOR to XstUsageType.Attribute,
            XQueryElementType.COMP_ELEM_CONSTRUCTOR to XstUsageType.Element,
            XQueryElementType.CURRENT_ITEM to XstUsageType.Variable,
            XQueryElementType.DECIMAL_FORMAT_DECL to XstUsageType.DecimalFormat,
            XQueryElementType.DIR_ELEM_CONSTRUCTOR to XstUsageType.Element,
            XPathElementType.ELEMENT_TEST to XstUsageType.Element,
            XPathElementType.FUNCTION_CALL to XstUsageType.FunctionRef,
            XQueryElementType.FUNCTION_DECL to XstUsageType.FunctionDecl,
            XQueryElementType.MODULE_DECL to XstUsageType.Namespace,
            XQueryElementType.MODULE_IMPORT to XstUsageType.Namespace,
            XPathElementType.NAMED_FUNCTION_REF to XstUsageType.FunctionRef,
            XQueryElementType.NAMESPACE_DECL to XstUsageType.Namespace,
            XQueryElementType.NEXT_ITEM to XstUsageType.Variable,
            XQueryElementType.OPTION_DECL to XstUsageType.Option,
            XPathElementType.PARAM to XstUsageType.Parameter,
            XPathElementType.PRAGMA to XstUsageType.Pragma,
            XQueryElementType.PREVIOUS_ITEM to XstUsageType.Variable,
            XPathElementType.SCHEMA_ATTRIBUTE_TEST to XstUsageType.Attribute,
            XPathElementType.SCHEMA_ELEMENT_TEST to XstUsageType.Element,
            XQueryElementType.SCHEMA_PREFIX to XstUsageType.Namespace,
            XPathElementType.SIMPLE_TYPE_NAME to XstUsageType.Type,
            XPathElementType.TYPE_ALIAS to XstUsageType.Type,
            XQueryElementType.TYPE_DECL to XstUsageType.Type,
            XPathElementType.TYPE_NAME to XstUsageType.Type,
            XPathElementType.UNION_TYPE to XstUsageType.Type,
            XPathElementType.VAR_NAME to XstUsageType.Variable
        )
    }
}
