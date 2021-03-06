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
package uk.co.reecedunn.intellij.plugin.xslt.completion.xpath.property

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlFile
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.core.completion.CompletionProperty
import uk.co.reecedunn.intellij.plugin.core.psi.contextOfType
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.intellij.lang.XsltSpec
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathCompletionProperty

object XsltVersion : CompletionProperty {
    override fun computeProperty(element: PsiElement, context: ProcessingContext) {
        if (context[XPathCompletionProperty.XSLT_VERSION] == null) {
            context.put(XPathCompletionProperty.XSLT_VERSION, get(element))
        }
    }

    fun get(element: PsiElement): Version {
        val file = element.contextOfType<XmlAttributeValue>(false)?.containingFile as? XmlFile
        return when (file?.rootTag?.getAttribute("version", "")?.value) {
            "1.0" -> XsltSpec.REC_1_0_19991116
            "2.0" -> XsltSpec.REC_2_0_20070123
            "3.0" -> XsltSpec.REC_3_0_20170608
            else -> XsltSpec.REC_3_0_20170608
        }
    }
}
