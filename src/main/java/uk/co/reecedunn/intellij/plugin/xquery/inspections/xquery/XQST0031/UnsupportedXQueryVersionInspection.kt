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
@file:Suppress("PackageName")

package uk.co.reecedunn.intellij.plugin.xquery.inspections.xquery.XQST0031

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiFile
import com.intellij.util.SmartList
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.inspections.Inspection
import uk.co.reecedunn.intellij.plugin.intellij.lang.Specification
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.intellij.settings.XQueryProjectSettings

/** XQST0081 error condition
 *
 * It is a *static error* if a VersionDecl specifies a version that is
 * not supported by the implementation.
 */
class UnsupportedXQueryVersionInspection : Inspection("xqst/XQST0081.md") {
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is XQueryModule) return null

        val settings = XQueryProjectSettings.getInstance(file.getProject())
        val descriptors = SmartList<ProblemDescriptor>()
        var mainVersion: Specification? = null
        var isFirstVersion = true
        file.XQueryVersions.forEach(fun (version) {
            if (isFirstVersion) {
                mainVersion = version.getVersionOrDefault(file.project)
            }

            if (version.version == null && version.declaration == null)
                return

            if (version.version == null) {
                // Unrecognised XQuery version string.
                val description = XQueryBundle.message("inspection.XQST0031.unsupported-version.message")
                descriptors.add(manager.createProblemDescriptor(version.declaration!!, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR, isOnTheFly))
                return
            }

            val xqueryVersion = XQuery.versionForXQuery(settings.product, settings.productVersion, version.version.label)
            if (xqueryVersion == null) {
                // The XQuery version is not supported by the implementation.
                val description = XQueryBundle.message("inspection.XQST0031.unsupported-version.message")
                descriptors.add(manager.createProblemDescriptor(version.declaration!!, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR, isOnTheFly))
                return
            }

            if (!isFirstVersion && mainVersion != xqueryVersion) {
                val description = XQueryBundle.message("inspection.XQST0031.unsupported-version.different-version-for-transaction")
                descriptors.add(manager.createProblemDescriptor(version.declaration!!, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR, isOnTheFly))
            }

            isFirstVersion = false
        })
        return descriptors.toTypedArray()
    }
}
