/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.inspections.xquery.XQST0033

import com.intellij.codeInspection.*
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.SmartList
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle

/** XQST0033 error condition
 *
 * It is a *static error* if the prolog contains duplicate namespace
 * prefix names in namespace declarations, including the module
 * declaration namespace.
 */
class DuplicateNamespacePrefixInspection : LocalInspectionTool() {
    override fun getDisplayName(): String =
        XQueryBundle.message("inspection.XQST0033.duplicate-namespace-prefix.display-name")

    override fun getDescriptionFileName(): String? =
        id + ".html"

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is XQueryModule) return null

        val descriptors = SmartList<ProblemDescriptor>()
        file.children().forEach { module ->
            val prefices = HashMap<String, XQueryUriLiteral?>()

            val moduleDecl = module.children().filterIsInstance<XQueryModuleDecl>().firstOrNull() as? XPathNamespaceDeclaration
            if (moduleDecl != null) {
                val prefix = moduleDecl.namespacePrefix?.staticValue
                val uri = moduleDecl.namespaceUri
                if (prefix != null) {
                    prefices.put(prefix, uri as XQueryUriLiteral)
                }
            }

            val prolog = (module as? XQueryPrologResolver)?.prolog
            prolog?.children()?.forEach(fun (child) {
                val ns = child as? XPathNamespaceDeclaration
                val prefix = ns?.namespacePrefix?.staticValue

                if (ns == null || prefix == null)
                    return

                val duplicate: XQueryUriLiteral? = prefices.get(prefix)
                if (duplicate != null) {
                    val description = XQueryBundle.message("inspection.XQST0033.duplicate-namespace-prefix.message", prefix)
                    descriptors.add(manager.createProblemDescriptor(ns.namespacePrefix as PsiElement, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR, isOnTheFly))
                }

                prefices.put(prefix, ns.namespaceUri as? XQueryUriLiteral)
            })
        }
        return descriptors.toTypedArray()
    }
}
