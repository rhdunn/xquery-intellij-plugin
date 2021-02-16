/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpm.lang.validation

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xpm.lang.configuration.XpmLanguageConfiguration
import uk.co.reecedunn.intellij.plugin.xpm.lang.diagnostics.XpmDiagnostics
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.requires.XpmRequiresConformanceTo

class XpmSyntaxValidation : XpmSyntaxErrorReporter {
    override var configuration: XpmLanguageConfiguration? = null

    private var required: XpmRequiresConformanceTo? = null
    private var conformanceElement: PsiElement? = null
    private var conformanceName: String? = null

    override fun requires(
        element: XpmSyntaxValidationElement,
        requires: XpmRequiresConformanceTo,
        conformanceName: String?
    ) {
        this.required = this.required?.or(requires) ?: requires
        if (!this.required!!.conformanceTo(configuration!!)) {
            this.conformanceElement = element.conformanceElement
            this.conformanceName = conformanceName
        } else {
            this.conformanceElement = null
        }
    }

    fun validate(file: PsiFile, diagnostics: XpmDiagnostics) {
        file.walkTree().filterIsInstance<XpmSyntaxValidationElement>().forEach {
            validate(it, diagnostics)
        }
    }

    fun validate(element: XpmSyntaxValidationElement, diagnostics: XpmDiagnostics) {
        required = null
        conformanceElement = null

        XpmSyntaxValidator.validators.forEach {
            it.validate(element, this)
        }

        if (conformanceElement != null) {
            val message = required!!.message(configuration!!, conformanceName)
            diagnostics.error(conformanceElement!!, XpmDiagnostics.XPST0003, message)
        }
    }
}
