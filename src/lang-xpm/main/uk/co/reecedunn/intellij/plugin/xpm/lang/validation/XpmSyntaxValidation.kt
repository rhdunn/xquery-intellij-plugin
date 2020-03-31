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
import uk.co.reecedunn.intellij.plugin.intellij.resources.XpmBundle
import uk.co.reecedunn.intellij.plugin.xpm.lang.XpmProductVersion
import uk.co.reecedunn.intellij.plugin.xpm.lang.diagnostics.XpmDiagnostics
import uk.co.reecedunn.intellij.plugin.xpm.lang.displayName
import uk.co.reecedunn.intellij.plugin.xpm.lang.ge

class XpmSyntaxValidation : XpmSyntaxErrorReporter {
    var product: XpmProductVersion? = null

    private var requiredProduct: XpmProductVersion? = null
    private var conformanceElement: PsiElement? = null
    private var conformanceName: String? = null

    override fun requireProduct(
        element: XpmSyntaxValidationElement,
        productVersion: XpmProductVersion,
        conformanceName: String?
    ) {
        if (!(product?.product === productVersion.product && product!!.ge(productVersion))) {
            this.requiredProduct = productVersion
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
        requiredProduct = null

        XpmSyntaxValidator.validators.forEach {
            it.validate(element, this)
        }

        if (requiredProduct != null) {
            val message =
                if (conformanceName != null)
                    XpmBundle.message(
                        "diagnostic.unsupported-syntax-name",
                        product!!.displayName, requiredProduct!!.displayName, conformanceName!!
                    )
                else
                    XpmBundle.message(
                        "diagnostic.unsupported-syntax",
                        product!!.displayName, requiredProduct!!.displayName
                    )
            diagnostics.error(conformanceElement!!, XpmDiagnostics.XPST0003, message)
        }
    }
}
