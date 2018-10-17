/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.codeInspection.xqst

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiFile
import com.intellij.util.SmartList
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModuleImport
import uk.co.reecedunn.intellij.plugin.core.codeInspection.Inspection
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryBundle

class XQST0047 : Inspection("xqst/XQST0047.md") {
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is XQueryModule) return null

        val descriptors = SmartList<ProblemDescriptor>()
        file.children().forEach { module ->
            val uris = HashMap<String, XPathNamespaceDeclaration>()

            val prolog = (module as? XQueryPrologResolver)?.prolog?.firstOrNull()
            prolog?.children()?.filterIsInstance<XQueryModuleImport>()?.forEach(fun (child) {
                val ns = child as? XPathNamespaceDeclaration
                val uri = ns?.namespaceUri?.data

                if (ns == null || uri == null)
                    return

                val duplicate: XPathNamespaceDeclaration? = uris[uri]
                if (duplicate != null) {
                    val description = XQueryBundle.message("inspection.XQST0047.duplicate-namespace-uri.message", uri)
                    descriptors.add(manager.createProblemDescriptor(ns.namespaceUri?.element!!, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR, isOnTheFly))
                }

                uris[uri] = ns
            })
        }
        return descriptors.toTypedArray()
    }
}