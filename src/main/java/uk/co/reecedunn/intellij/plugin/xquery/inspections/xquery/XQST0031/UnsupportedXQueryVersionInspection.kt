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
package uk.co.reecedunn.intellij.plugin.xquery.inspections.xquery.XQST0031

import com.intellij.codeInspection.*
import com.intellij.psi.PsiFile
import com.intellij.util.SmartList
import uk.co.reecedunn.intellij.plugin.core.extensions.children
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryVersionRef
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings

/** XQST0081 error condition
 *
 * It is a *static error* if a VersionDecl specifies a version that is
 * not supported by the implementation.
 */
class UnsupportedXQueryVersionInspection : LocalInspectionTool() {
    override fun getDisplayName(): String =
        XQueryBundle.message("inspection.XQST0031.unsupported-version.display-name")

    override fun getDescriptionFileName(): String? =
        id + ".html"

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is XQueryFile) return null

        val settings = XQueryProjectSettings.getInstance(file.getProject())
        val descriptors = SmartList<ProblemDescriptor>()
        file.children().filterIsInstance<XQueryModule>().forEach { module ->
            val version = module.XQueryVersion
            if (version.version == null || version.declaration == null) {
                if (version.declaration != null) {
                    createUnsupportedVersionProblemDescriptors(descriptors, manager, version, isOnTheFly)
                }
            } else {
                val xqueryVersion = XQuery.versionForXQuery(settings.product!!, settings.productVersion!!, version.version.label)
                if (xqueryVersion == null) {
                    createUnsupportedVersionProblemDescriptors(descriptors, manager, version, isOnTheFly)
                }
            }
        }
        return descriptors.toTypedArray()
    }

    private fun createUnsupportedVersionProblemDescriptors(descriptors: SmartList<ProblemDescriptor>, manager: InspectionManager, version: XQueryVersionRef, isOnTheFly: Boolean) {
        val description = XQueryBundle.message("inspection.XQST0031.unsupported-version.message")
        descriptors.add(manager.createProblemDescriptor(version.declaration!!, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR, isOnTheFly))
    }
}
