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
package uk.co.reecedunn.intellij.plugin.xquery.completion.providers

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.core.completion.CompletionProviderEx
import uk.co.reecedunn.intellij.plugin.intellij.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuerySpec
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathInsertText
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathKeywordLookup
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathCompletionProperty
import uk.co.reecedunn.intellij.plugin.xquery.completion.property.XQueryCompletionProperty

object XQueryForwardOrReverseAxisProvider : CompletionProviderEx {
    private val XQUERY_AXIS_STEPS = listOf(
        XPathKeywordLookup("ancestor", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("ancestor-or-self", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("attribute", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("child", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("descendant", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("descendant-or-self", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("following", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("following-sibling", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("parent", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("preceding", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("preceding-sibling", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("self", XPathInsertText.AXIS_MARKER)
    )

    private val MARKLOGIC_AXIS_STEPS = listOf(
        XPathKeywordLookup("namespace", XPathInsertText.AXIS_MARKER),
        XPathKeywordLookup("property", XPathInsertText.AXIS_MARKER)
    )

    private fun isMarkLogicXQueryVersion(context: ProcessingContext): Boolean {
        return when (context[XQueryCompletionProperty.XQUERY_VERSION]) {
            XQuerySpec.MARKLOGIC_1_0 -> true
            XQuerySpec.MARKLOGIC_0_9 -> true
            else -> false
        }
    }

    @Suppress("MoveVariableDeclarationIntoWhen") // Feature not supported in Kotlin 1.2 (IntelliJ 2018.1).
    override fun apply(element: PsiElement, context: ProcessingContext, result: CompletionResultSet) {
        result.addAllElements(XQUERY_AXIS_STEPS)

        val version = context[XPathCompletionProperty.XPATH_PRODUCT_VERSION]
        if (version.kind === MarkLogic && isMarkLogicXQueryVersion(context)) {
            result.addAllElements(MARKLOGIC_AXIS_STEPS)
        }
    }
}
