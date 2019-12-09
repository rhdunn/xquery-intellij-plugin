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
package uk.co.reecedunn.intellij.plugin.intellij.ui

import com.intellij.lang.Language
import com.intellij.psi.PsiElement
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider
import uk.co.reecedunn.intellij.plugin.intellij.documentation.XQueryDocumentationProvider
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathInlineFunctionExpr
import uk.co.reecedunn.intellij.plugin.xdm.functions.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import uk.co.reecedunn.intellij.plugin.xquery.model.XQueryElement

object XQueryBreadcrumbProvider : BreadcrumbsProvider {
    private val languages: Array<Language> = arrayOf(XQuery)

    override fun getLanguages(): Array<Language> = languages

    override fun acceptElement(element: PsiElement): Boolean {
        return when (element) {
            is XQueryFunctionDecl -> true
            is XPathInlineFunctionExpr -> true
            is XQueryElement -> true
            else -> false
        }
    }

    override fun getElementInfo(element: PsiElement): String {
        val name = when (element) {
            is XQueryFunctionDecl -> (element as XPathFunctionDeclaration).functionName
            is XPathInlineFunctionExpr -> return "function"
            is XQueryElement -> element.nodeName ?: return "element"
            else -> null
        }
        return name?.let { op_qname_presentation(it) } ?: ""
    }

    override fun getElementTooltip(element: PsiElement): String? {
        return XQueryDocumentationProvider.getQuickNavigateInfo(element.firstChild, null) ?: ""
    }
}
