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
package uk.co.reecedunn.intellij.plugin.xquery.completion.property

import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.core.completion.CompletionProperty
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathCompletionProperty

object XPathVersion : CompletionProperty {
    override fun computeProperty(element: PsiElement, context: ProcessingContext) {
        if (context[XPathCompletionProperty.XPATH_VERSION] == null) {
            XQueryVersion.computeProperty(element, context)

            val xquery = context[XQueryCompletionProperty.XQUERY_VERSION]
            context.put(XPathCompletionProperty.XPATH_VERSION, get(xquery))
        }
    }

    fun get(element: PsiElement): Version = get(XQueryVersion.get(element))

    private fun get(xqueryVersion: Version): Version = when (xqueryVersion) {
        XQuerySpec.REC_1_0_20070123 -> XPathSpec.REC_2_0_20070123
        XQuerySpec.REC_1_0_20101214 -> XPathSpec.REC_2_0_20101214
        XQuerySpec.WD_1_0_20030502 -> XPathSpec.WD_2_0_20030502
        XQuerySpec.REC_3_0_20140408 -> XPathSpec.REC_3_0_20140408
        XQuerySpec.CR_3_1_20151217 -> XPathSpec.CR_3_1_20151217
        XQuerySpec.REC_3_1_20170321 -> XPathSpec.REC_3_1_20170321
        XQuerySpec.ED_4_0_20210113 -> XPathSpec.ED_4_0_20210113
        XQuerySpec.MARKLOGIC_0_9 -> XPathSpec.WD_2_0_20030502
        XQuerySpec.MARKLOGIC_1_0 -> XPathSpec.REC_3_0_20140408
        else -> XPathSpec.REC_3_1_20170321
    }
}
