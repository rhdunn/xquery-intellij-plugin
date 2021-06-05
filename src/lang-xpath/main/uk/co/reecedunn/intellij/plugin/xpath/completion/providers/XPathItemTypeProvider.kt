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
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSpec
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathInsertText
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathKeywordLookup
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathCompletionProperty

object XPathItemTypeProvider : CompletionProviderEx {
    private val XPATH_20_ITEM_TYPES = listOf(
        XPathKeywordLookup("item", XPathInsertText.EMPTY_PARAMS)
    )

    private val XPATH_30_ITEM_TYPES = listOf(
        XPathKeywordLookup("function", XPathInsertText.PARAMS_WILDCARD),
        XPathKeywordLookup("function", XPathInsertText.TYPED_FUNCTION),
        XPathKeywordLookup("item", XPathInsertText.EMPTY_PARAMS)
    )

    private val XPATH_30_IN_XSLT_ITEM_TYPES = listOf(
        XPathKeywordLookup("function", XPathInsertText.PARAMS_WILDCARD),
        XPathKeywordLookup("function", XPathInsertText.TYPED_FUNCTION),
        XPathKeywordLookup("item", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("map", XPathInsertText.PARAMS_WILDCARD), // XSLT 3.0 includes support for maps.
        XPathKeywordLookup("map", XPathInsertText.PARAMS_KEY_VALUE_TYPE) // XSLT 3.0 includes support for maps.
    )

    private val XPATH_31_ITEM_TYPES = listOf(
        XPathKeywordLookup("array", XPathInsertText.PARAMS_WILDCARD),
        XPathKeywordLookup("array", XPathInsertText.PARAMS_TYPE),
        XPathKeywordLookup("function", XPathInsertText.PARAMS_WILDCARD),
        XPathKeywordLookup("function", XPathInsertText.TYPED_FUNCTION),
        XPathKeywordLookup("item", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("map", XPathInsertText.PARAMS_WILDCARD),
        XPathKeywordLookup("map", XPathInsertText.PARAMS_KEY_VALUE_TYPE)
    )

    private val XPATH_40_ITEM_TYPES = listOf(
        XPathKeywordLookup("array", XPathInsertText.PARAMS_WILDCARD),
        XPathKeywordLookup("array", XPathInsertText.PARAMS_TYPE),
        XPathKeywordLookup("enum", XPathInsertText.PARAMS_VALUES),
        XPathKeywordLookup("function", XPathInsertText.PARAMS_WILDCARD),
        XPathKeywordLookup("function", XPathInsertText.TYPED_FUNCTION),
        XPathKeywordLookup("item", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("map", XPathInsertText.PARAMS_WILDCARD),
        XPathKeywordLookup("map", XPathInsertText.PARAMS_KEY_VALUE_TYPE),
        XPathKeywordLookup("record", XPathInsertText.PARAMS_FIELD_DECLS),
        XPathKeywordLookup("union", XPathInsertText.PARAMS_TYPES)
    )

    override fun apply(element: PsiElement, context: ProcessingContext, result: CompletionResultSet) {
        when (context[XPathCompletionProperty.XPATH_VERSION]) {
            XPathSpec.WD_2_0_20030502 -> result.addAllElements(XPATH_20_ITEM_TYPES)
            XPathSpec.REC_2_0_20070123 -> result.addAllElements(XPATH_20_ITEM_TYPES)
            XPathSpec.REC_3_0_20140408 -> {
                if (context[XPathCompletionProperty.XSLT_VERSION] == null) {
                    result.addAllElements(XPATH_30_ITEM_TYPES)
                } else {
                    result.addAllElements(XPATH_30_IN_XSLT_ITEM_TYPES)
                }
            }
            XPathSpec.CR_3_1_20151217 -> result.addAllElements(XPATH_31_ITEM_TYPES)
            XPathSpec.REC_3_1_20170321 -> result.addAllElements(XPATH_31_ITEM_TYPES)
            XPathSpec.ED_4_0_20210113 -> result.addAllElements(XPATH_40_ITEM_TYPES)
            else -> {
            }
        }
    }
}
