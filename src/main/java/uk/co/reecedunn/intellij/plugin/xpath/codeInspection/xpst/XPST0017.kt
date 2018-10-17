/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.codeInspection.xpst

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiFile
import com.intellij.util.SmartList
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionReference
import uk.co.reecedunn.intellij.plugin.xpath.model.staticallyKnownFunctions
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.core.codeInspection.Inspection
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryBundle

class XPST0017 : Inspection("xpst/XPST0017.md") {
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is XQueryModule) return null

        val descriptors = SmartList<ProblemDescriptor>()
        file.walkTree().filterIsInstance<XPathFunctionReference>()
                       .forEach { ref ->
            val qname = ref.functionName
            val declarations = (qname as XPathEQName).staticallyKnownFunctions().toList()
            if (ref.functionName == null) {
                // Missing local name -- do nothing.
            } else if (declarations.isEmpty()) {
                // 1. The expanded QName does not match the name of a function signature in the static context.
                val description = XQueryBundle.message("inspection.XPST0017.undefined-function.unresolved-qname")
                val decl = ref.functionName?.element!!
                descriptors.add(manager.createProblemDescriptor(decl, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR, isOnTheFly))
            } else {
                // 2. The number of arguments does not match the arity of a function signature in the static context.
                val arity = (qname.parent as? XPathFunctionReference)?.arity ?: -1
                if (declarations.firstOrNull { f -> f.arity == arity } == null) {
                    val description = XQueryBundle.message("inspection.XPST0017.undefined-function.unresolved-arity")
                    val decl = ref.functionName?.element!!
                    descriptors.add(manager.createProblemDescriptor(decl, description, null as LocalQuickFix?, ProblemHighlightType.GENERIC_ERROR, isOnTheFly))
                }
            }
        }
        return descriptors.toTypedArray()
    }
}
