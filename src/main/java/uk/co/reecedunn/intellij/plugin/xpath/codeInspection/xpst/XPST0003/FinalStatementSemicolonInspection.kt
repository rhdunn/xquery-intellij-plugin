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

package uk.co.reecedunn.intellij.plugin.xpath.codeInspection.xpst.XPST0003

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiFile
import com.intellij.util.SmartList
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xpath.ast.scripting.ScriptingConcatExpr
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginTransactionSeparator
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.core.codeInspection.Inspection
import uk.co.reecedunn.intellij.plugin.intellij.lang.Scripting
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.intellij.settings.XQueryProjectSettings

class FinalStatementSemicolonInspection : Inspection("ijst/IJST0005.md") {
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is XQueryModule) return null

        val settings = XQueryProjectSettings.getInstance(file.getProject())
        val product = settings.product
        val productVersion = settings.productVersion
        val requiresSemicolon = product.conformsTo(productVersion, Scripting.NOTE_1_0_20140918)

        val descriptors = SmartList<ProblemDescriptor>()
        file.walkTree().filterIsInstance<PluginTransactionSeparator>().forEach(fun (element) {
            if (element.parent.node.elementType === XQueryElementType.FILE)
                return

            if (element.siblings().filterIsInstance<ScriptingConcatExpr>().firstOrNull() !== null)
                return

            val haveSemicolon = element.firstChild !== null
            if (haveSemicolon != requiresSemicolon && requiresSemicolon) {
                val context = if (element.firstChild === null) file.findElementAt(element.textOffset - 1)!! else element
                val description = XQueryBundle.message("inspection.XPST0003.final-statement-semicolon.required", Scripting.NOTE_1_0_20140918)
                descriptors.add(manager.createProblemDescriptor(context, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly))
            }
        })
        return descriptors.toTypedArray()
    }
}