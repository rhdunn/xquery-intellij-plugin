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
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathSequenceTypeLookup
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathCompletionProperty
import uk.co.reecedunn.intellij.plugin.xquery.completion.property.XQueryCompletionProperty

object XQueryKindTestProvider : CompletionProviderEx {
    private val MARKLOGIC_60_KIND_TESTS = listOf(
        XPathSequenceTypeLookup("binary")
    )

    private val MARKLOGIC_70_KIND_TESTS = listOf(
        XPathSequenceTypeLookup("attribute-decl"),
        XPathSequenceTypeLookup("complex-type"),
        XPathSequenceTypeLookup("element-decl"),
        XPathSequenceTypeLookup("schema-component"),
        XPathSequenceTypeLookup("schema-particle"),
        XPathSequenceTypeLookup("schema-root"),
        XPathSequenceTypeLookup("schema-type"),
        XPathSequenceTypeLookup("simple-type")
    )

    private val MARKLOGIC_80_KIND_TESTS = listOf(
        XPathSequenceTypeLookup("array-node"),
        XPathSequenceTypeLookup("array-node", "(key-name)", XPathInsertText.PARAMS),
        XPathSequenceTypeLookup("boolean-node"),
        XPathSequenceTypeLookup("boolean-node", "(key-name)", XPathInsertText.PARAMS),
        XPathSequenceTypeLookup("node", "(*)", XPathInsertText.PARAMS), // XPath/XQuery extension
        XPathSequenceTypeLookup("node", "(key-name)", XPathInsertText.PARAMS), // XPath/XQuery extension
        XPathSequenceTypeLookup("null-node"),
        XPathSequenceTypeLookup("null-node", "(key-name)", XPathInsertText.PARAMS),
        XPathSequenceTypeLookup("number-node"),
        XPathSequenceTypeLookup("number-node", "(key-name)", XPathInsertText.PARAMS),
        XPathSequenceTypeLookup("object-node"),
        XPathSequenceTypeLookup("object-node", "(key-name)", XPathInsertText.PARAMS),
        XPathSequenceTypeLookup("schema-facet"),
        XPathSequenceTypeLookup("text", "(key-name)", XPathInsertText.PARAMS) // XPath/XQuery extension
    )

    private fun isMarkLogicXQueryVersion(context: ProcessingContext): Boolean {
        return when (context[XQueryCompletionProperty.XQUERY_VERSION]) {
            XQuerySpec.MARKLOGIC_1_0 -> true
            XQuerySpec.MARKLOGIC_0_9 -> true
            else -> false
        }
    }

    override fun apply(element: PsiElement, context: ProcessingContext, result: CompletionResultSet) {
        val version = context[XPathCompletionProperty.XPATH_PRODUCT_VERSION]
        if (version.kind === MarkLogic && isMarkLogicXQueryVersion(context)) {
            if (version.value >= 6.0) result.addAllElements(MARKLOGIC_60_KIND_TESTS)
            if (version.value >= 7.0) result.addAllElements(MARKLOGIC_70_KIND_TESTS)
            if (version.value >= 8.0) result.addAllElements(MARKLOGIC_80_KIND_TESTS)
        }
    }
}
