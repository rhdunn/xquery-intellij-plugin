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
package uk.co.reecedunn.intellij.plugin.intellij.documentation

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathInlineFunctionExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNCName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathVarName
import uk.co.reecedunn.intellij.plugin.xpath.functions.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModuleDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryVarDecl
import uk.co.reecedunn.intellij.plugin.xquery.model.XQueryElement

object XQueryDocumentationProvider : AbstractDocumentationProvider() {
    override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? {
        return when (val parent = element?.parent) {
            is XQueryFunctionDecl -> {
                val sig = parent.presentation?.presentableText
                "declare function $sig"
            }
            is XPathInlineFunctionExpr -> {
                (parent as XPathFunctionDeclaration).let {
                    val params = it.paramListPresentation?.presentableText ?: "()"
                    val returnType = it.returnType
                    if (returnType == null)
                        "function $params"
                    else
                        "function $params as ${returnType.typeName}"
                }
            }
            is XPathVarName -> {
                (parent.parent as? XQueryVarDecl)?.let {
                    val sig = it.presentation?.presentableText
                    "declare variable $sig"
                }
            }
            is XPathNCName -> {
                (parent.parent as? XPathNamespaceDeclaration)?.let { decl ->
                    val prefix = decl.namespacePrefix?.data
                    val uri = decl.namespaceUri?.data ?: return null
                    if (parent.parent is XQueryModuleDecl)
                        prefix?.let { "module namespace $it = \"$uri\"" } ?: "module namespace \"{$uri}\""
                    else
                        prefix?.let { "namespace $it = \"$uri\"" } ?: "namespace \"{$uri}\""
                }
            }
            is XQueryElement -> {
                val dynamic = XQueryBundle.message("element.dynamic")
                "element ${parent.nodeName?.let { op_qname_presentation(it) } ?: dynamic} {...}"
            }
            else -> null
        }
    }
}
