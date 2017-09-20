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
package uk.co.reecedunn.intellij.plugin.xquery.inspections.XPST0003

import com.intellij.codeInspection.*
import com.intellij.psi.PsiFile
import com.intellij.util.SmartList
import uk.co.reecedunn.intellij.plugin.core.extensions.walkTree
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryMapConstructorEntry
import uk.co.reecedunn.intellij.plugin.xquery.lang.*
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings

class MapConstructorEntryInspection : LocalInspectionTool() {
    private fun conformsTo(element: XQueryMapConstructorEntry, implementation: ImplementationItem): Boolean {
        val conformanceElement = element.separator
        if (conformanceElement === element.firstChild) {
            return true
        }
        val saxon = implementation.getVersion(Saxon)
        val isSaxonExtension = saxon.supportsVersion(XQueryVersion.VERSION_9_4) && !saxon.supportsVersion(XQueryVersion.VERSION_9_7)
        return conformanceElement.node.elementType === XQueryTokenType.ASSIGN_EQUAL == isSaxonExtension
    }

    override fun getDisplayName(): String =
        XQueryBundle.message("inspection.XPST0003.map-constructor-entry.display-name")

    override fun getDescriptionFileName(): String? =
        id + ".html"

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is XQueryFile) return null

        val settings = XQueryProjectSettings.getInstance(file.getProject())

        val xqueryVersion = file.XQueryVersion.getVersionOrDefault(file.getProject())
        val version = XQuery.versions.find { v -> (v as Specification).label == xqueryVersion.toString() }
        val dialect = settings.getDialectForXQueryVersion(version!!)

        val descriptors = SmartList<ProblemDescriptor>()
        file.walkTree().filterIsInstance<XQueryMapConstructorEntry>().forEach { element ->
            if (!conformsTo(element, dialect)) {
                val context = element.separator
                val description = XQueryBundle.message("inspection.XPST0003.map-constructor-entry.message")
                descriptors.add(manager.createProblemDescriptor(context, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly))
            }
        }
        return descriptors.toTypedArray()
    }
}
