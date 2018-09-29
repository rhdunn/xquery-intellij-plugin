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
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathMapConstructorEntry
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.core.codeInspection.Inspection
import uk.co.reecedunn.intellij.plugin.intellij.lang.Saxon
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.intellij.settings.XQueryProjectSettings

class IJVS0004 : Inspection("ijvs/IJVS0004.md") {
    private fun conformsTo(element: XPathMapConstructorEntry, productVersion: Version?): Boolean {
        val conformanceElement = element.separator
        if (conformanceElement === element.firstChild) {
            return true
        }
        val isSaxonExtension =
            productVersion?.kind === Saxon && productVersion.value >= 9.4 && productVersion.value <= 9.7
        return conformanceElement.node.elementType === XQueryTokenType.ASSIGN_EQUAL == isSaxonExtension
    }

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is XQueryModule) return null

        val settings = XQueryProjectSettings.getInstance(file.getProject())
        val productVersion: Version = settings.productVersion

        val descriptors = SmartList<ProblemDescriptor>()
        file.walkTree().filterIsInstance<XPathMapConstructorEntry>().forEach { element ->
            if (!conformsTo(element, productVersion)) {
                val context = element.separator
                val description = XQueryBundle.message("inspection.XPST0003.map-constructor-entry.message")
                descriptors.add(manager.createProblemDescriptor(context, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly))
            }
        }
        return descriptors.toTypedArray()
    }
}
