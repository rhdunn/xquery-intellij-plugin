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
package uk.co.reecedunn.intellij.plugin.xquery.lang.documentation

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.navigation.NavigationItem
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.navigation.ItemPresentationEx
import uk.co.reecedunn.intellij.plugin.core.psi.resourcePath
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmElementNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathInlineFunctionExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNCName
import uk.co.reecedunn.intellij.plugin.xpm.context.expand
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionReference
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.impl.XpmFunctionReferenceImpl
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.XQDocDocumentation
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.XQDocDocumentationSourceProvider
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.sections
import uk.co.reecedunn.intellij.plugin.xqdoc.resources.XQDocTemplates
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle

class XQueryDocumentationProvider : AbstractDocumentationProvider() {
    companion object {
        private fun getElementPresentationText(decl: XpmNamespaceDeclaration, element: PsiElement): String? {
            val prefix = decl.namespacePrefix?.data
            val uri = decl.namespaceUri?.data ?: return null
            val path = decl.namespaceUri?.element?.containingFile?.resourcePath()
            return if (element is XQueryModuleDecl)
                prefix?.let { "module namespace $it = \"$uri\"\nat \"$path\"" }
                    ?: "module namespace \"$uri\"\nat \"$path\""
            else
                prefix?.let { "namespace $it = \"$uri\"" } ?: "namespace \"{$uri}\""
        }

        fun getElementPresentationText(element: PsiElement?): String? = when (val parent = element?.parent) {
            is XQueryFunctionDecl -> {
                val sig = when (val presentation = (parent as NavigationItem).presentation) {
                    is ItemPresentationEx -> presentation.getPresentableText(ItemPresentationEx.Type.StructureView)
                    else -> null
                }
                "declare function $sig"
            }
            is XPathInlineFunctionExpr -> (parent as XpmFunctionDeclaration).let {
                val params = it.paramListPresentableText
                val returnType = it.returnType
                if (returnType == null)
                    "function $params"
                else
                    "function $params as ${returnType.typeName}"
            }
            is XQueryVarDecl -> {
                val sig = (parent as NavigationItem).presentation?.presentableText
                "declare variable \$$sig"
            }
            is XPathNCName -> (parent.parent as? XpmNamespaceDeclaration)?.let { decl ->
                getElementPresentationText(decl, parent.parent)
            }
            is XdmElementNode -> {
                val dynamic = XQueryBundle.message("element.dynamic")
                "element ${parent.nodeName?.let { qname_presentation(it) } ?: dynamic} {...}"
            }
            else -> (element as? XQueryModule)?.let {
                when (val module = it.mainOrLibraryModule) {
                    is XQueryLibraryModule -> {
                        val moduleDecl = module.firstChild
                        getElementPresentationText(moduleDecl as XpmNamespaceDeclaration, moduleDecl)
                    }
                    else -> null
                }
            }
        }
    }

    override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? {
        return getElementPresentationText(element)
    }

    // Generate the main documentation for the documentation pane.
    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? = originalElement?.let {
        val text = lookup(it).firstOrNull()?.sections ?: return@let null
        return XQDocTemplates.QuickDocumentation.replace("[CONTENTS]", text)
    }

    // Generate the summary documentation for the documentation hover tooltip.
    override fun generateHoverDoc(element: PsiElement, originalElement: PsiElement?): String? = originalElement?.let {
        val text = lookup(it).firstOrNull()?.sections ?: return@let null
        return XQDocTemplates.QuickDocumentation.replace("[CONTENTS]", text)
    }

    // Generate the external documentation links displayed below the main/summary documentation.
    override fun getUrlFor(element: PsiElement?, originalElement: PsiElement?): List<String> {
        return originalElement?.let {
            lookup(it).mapNotNull { ref -> ref.href }.toList()
        } ?: emptyList()
    }

    private fun lookup(element: PsiElement): Sequence<XQDocDocumentation> {
        val parent = element.parent
        return when {
            (parent as? XsQNameValue)?.prefix?.element === element -> lookupPrefix(parent as XsQNameValue)
            (parent as? XsQNameValue)?.localName?.element === element -> lookupLocalName(parent as XsQNameValue)
            else -> emptySequence()
        }
    }

    private fun lookupPrefix(qname: XsQNameValue): Sequence<XQDocDocumentation> {
        val decl = qname.expand().firstOrNull()?.namespace?.element?.parent as? XpmNamespaceDeclaration
        return decl?.let { XQDocDocumentationSourceProvider.lookup(it) } ?: emptySequence()
    }

    private fun lookupLocalName(qname: XsQNameValue): Sequence<XQDocDocumentation> {
        return when (val ref = qname.element?.parent) {
            is XpmFunctionReference -> lookupFunction(ref.functionName, ref.positionalArity, ref.keywordArity)
            is XpmFunctionDeclaration -> lookupFunction(ref.functionName, ref.declaredArity, 0)
            is XpmNamespaceDeclaration -> XQDocDocumentationSourceProvider.lookup(ref)
            else -> emptySequence()
        }
    }

    private fun lookupFunction(
        functionName: XsQNameValue?,
        positionalArity: Int,
        keywordArity: Int
    ): Sequence<XQDocDocumentation> {
        // NOTE: NCName may bind to the current module (MarkLogic behaviour) and the default function namespace.
        return functionName?.expand()?.flatMap {
            XQDocDocumentationSourceProvider.lookup(XpmFunctionReferenceImpl(it, positionalArity, keywordArity))
        }?.filter { it.moduleTypes.contains(XdmModuleType.XQuery) } ?: emptySequence()
    }
}
