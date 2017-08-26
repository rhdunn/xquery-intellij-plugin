/*
 * Copyright (C) 2016 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.inspections.XPST0003

import com.intellij.codeInspection.*
import com.intellij.psi.PsiFile
import com.intellij.util.SmartList
import uk.co.reecedunn.intellij.plugin.core.extensions.walkTree
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile
import uk.co.reecedunn.intellij.plugin.xquery.lang.Specification
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings

/** Checks non-XQuery 1.0 constructs against the selected implementation.
 *
 * Constructs that are not in the base XQuery 1.0 syntax implement the
 * [uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformanceCheck]
 * interface to determine if the construct is valid for the given XQuery
 * implementation and associated dialect.
 */
class UnsupportedConstructInspection : LocalInspectionTool() {
    override fun getDisplayName(): String =
        XQueryBundle.message("inspection.XPST0003.unsupported-construct.display-name")

    override fun getDescriptionFileName(): String? =
        id + ".html"

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is XQueryFile) return null

        val xqueryVersion = file.XQueryVersion.getVersionOrDefault(file.getProject())
        val settings = XQueryProjectSettings.getInstance(file.getProject())
        val version = XQuery.versions.find { v -> (v as Specification).label == xqueryVersion.toString() }
        val dialect = settings.getDialectForXQueryVersion(version!!)

        val descriptors = SmartList<ProblemDescriptor>()
        file.walkTree().filterIsInstance<XQueryConformanceCheck>().forEach { versioned ->
            if (!versioned.conformsTo(dialect)) {
                val context = versioned.conformanceElement
                val description = versioned.conformanceErrorMessage
                descriptors.add(manager.createProblemDescriptor(context, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly))
            }
        }
        return descriptors.toTypedArray()
    }
}
