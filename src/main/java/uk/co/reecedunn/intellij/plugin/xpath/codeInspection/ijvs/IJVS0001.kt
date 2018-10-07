/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.codeInspection.ijvs

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiFile
import com.intellij.util.SmartList
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.core.codeInspection.Inspection
import uk.co.reecedunn.intellij.plugin.intellij.lang.Specification
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.intellij.settings.XQueryProjectSettings

class IJVS0001 : Inspection("ijvs/IJVS0001.md") {
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is XQueryModule) return null

        val settings = XQueryProjectSettings.getInstance(file.project)
        val product = settings.product
        val productVersion = settings.productVersion
        val xquery = file.XQueryVersion.getVersionOrDefault(file.project)

        val descriptors = SmartList<ProblemDescriptor>()
        file.walkTree().filterIsInstance<XQueryConformance>().forEach { versioned ->
            val required = versioned.requiresConformance
            if (!required.isEmpty() && required.find { version -> product.conformsTo(productVersion, version) } == null) {
                val context = versioned.conformanceElement
                val description = XQueryBundle.message("inspection.XPST0003.unsupported-construct.message", productVersion, required.joinToString(", or "))
                descriptors.add(manager.createProblemDescriptor(context, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly))
            } else {
                val requiredXQuery = required.filterIsInstance<Specification>()
                if (!requiredXQuery.isEmpty() && requiredXQuery.find { version -> version.value <= xquery.value } == null) {
                    val context = versioned.conformanceElement
                    val description = XQueryBundle.message("inspection.XPST0003.unsupported-construct-version.message", xquery.versionId, required.joinToString(", or "))
                    descriptors.add(manager.createProblemDescriptor(context, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly))
                }
            }
        }
        return descriptors.toTypedArray()
    }
}
