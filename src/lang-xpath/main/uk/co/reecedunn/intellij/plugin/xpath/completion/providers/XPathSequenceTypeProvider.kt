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
package uk.co.reecedunn.intellij.plugin.xpath.completion.providers

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.core.completion.CompletionProviderEx
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathInsertText
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathKeywordLookup
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathCompletionProperty

object XPathSequenceTypeProvider : CompletionProviderEx {
    private val XPATH_20_WD_2003_SEQUENCE_TYPE = XPathKeywordLookup("empty", XPathInsertText.EMPTY_PARAMS)

    private val XPATH_20_REC_SEQUENCE_TYPE = XPathKeywordLookup("empty-sequence", XPathInsertText.EMPTY_PARAMS)

    override fun apply(element: PsiElement, context: ProcessingContext, result: CompletionResultSet) {
        when (context[XPathCompletionProperty.XPATH_VERSION]) {
            XPathSpec.REC_1_0_19991116 -> {
            }
            XPathSpec.WD_2_0_20030502 -> result.addElement(XPATH_20_WD_2003_SEQUENCE_TYPE)
            else -> result.addElement(XPATH_20_REC_SEQUENCE_TYPE)
        }
    }
}
