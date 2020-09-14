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
package uk.co.reecedunn.intellij.plugin.xpm.lang.validation

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xpm.intellij.resources.XpmBundle
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmProductVersion
import uk.co.reecedunn.intellij.plugin.xpm.lang.diagnostics.XpmDiagnostics
import uk.co.reecedunn.intellij.plugin.xpm.lang.displayName
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.requires.XpmRequiresConformanceTo

class XpmSyntaxValidation : XpmSyntaxErrorReporter {
    override var product: XpmProductVersion? = null

    private var required: XpmRequiresConformanceTo? = null
    private var conformanceElement: PsiElement? = null
    private var conformanceName: String? = null

    override fun requires(
        element: XpmSyntaxValidationElement,
        requires: XpmRequiresConformanceTo,
        conformanceName: String?
    ) {
        if (!requires.conformanceTo(this)) {
            this.required = requires
            this.conformanceElement = element.conformanceElement
            this.conformanceName = conformanceName
        }
    }

    fun validate(file: PsiFile, diagnostics: XpmDiagnostics) {
        file.walkTree().filterIsInstance<XpmSyntaxValidationElement>().forEach {
            validate(it, diagnostics)
        }
    }

    fun validate(element: XpmSyntaxValidationElement, diagnostics: XpmDiagnostics) {
        required = null

        XpmSyntaxValidator.validators.forEach {
            it.validate(element, this)
        }

        if (required != null) {
            val message =
                if (conformanceName != null)
                    XpmBundle.message(
                        "diagnostic.unsupported-syntax-name",
                        product!!.displayName, required!!, conformanceName!!
                    )
                else
                    XpmBundle.message("diagnostic.unsupported-syntax", product!!.displayName, required!!)
            diagnostics.error(conformanceElement!!, XpmDiagnostics.XPST0003, message)
        }
    }
}
