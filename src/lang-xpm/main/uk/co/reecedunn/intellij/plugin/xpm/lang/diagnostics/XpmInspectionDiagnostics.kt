/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpm.lang.diagnostics

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiElement
import com.intellij.util.SmartList

class XpmInspectionDiagnostics(val manager: InspectionManager, val isOnTheFly: Boolean) : XpmDiagnostics {
    private val descriptors: SmartList<ProblemDescriptor> = SmartList<ProblemDescriptor>()

    private fun report(element: PsiElement, code: String, description: String) {
        val descriptor = manager.createProblemDescriptor(
            element,
            "$code: $description",
            null as LocalQuickFix?,
            ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
            isOnTheFly
        )
        descriptors.add(descriptor)
    }

    override fun error(element: PsiElement, code: String, description: String) = report(element, code, description)

    override fun warning(element: PsiElement, code: String, description: String) = report(element, code, description)

    fun toTypedArray(): Array<ProblemDescriptor> = descriptors.toTypedArray()
}
