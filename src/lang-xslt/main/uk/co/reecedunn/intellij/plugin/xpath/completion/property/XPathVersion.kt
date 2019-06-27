/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.completion.property

import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.core.completion.CompletionProperty
import uk.co.reecedunn.intellij.plugin.intellij.lang.NullSpecification
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSpec
import uk.co.reecedunn.intellij.plugin.intellij.lang.XsltSpec

object XPathVersion : CompletionProperty {
    override fun computeProperty(element: PsiElement, context: ProcessingContext) {
        if (context[XPathCompletionProperty.XPATH_VERSION] == null) {
            XsltVersion.computeProperty(element, context)

            val xslt = context[XsltCompletionProperty.XSLT_VERSION]
            context.put(XPathCompletionProperty.XPATH_VERSION, get(xslt) ?: NullSpecification)
        }
    }

    fun get(element: PsiElement): Version? {
        return get(XsltVersion.get(element))
    }

    private fun get(xsltVersion: Version?): Version? {
        return when (xsltVersion) {
            XsltSpec.REC_1_0_19991116 -> XPathSpec.REC_1_0_19991116
            XsltSpec.REC_2_0_20070123 -> XPathSpec.REC_2_0_20070123
            XsltSpec.REC_3_0_20170608 -> XPathSpec.REC_3_1_20170321 // TODO: Can be XPath 3.0 + maps on some processors.
            else -> null
        }
    }
}
