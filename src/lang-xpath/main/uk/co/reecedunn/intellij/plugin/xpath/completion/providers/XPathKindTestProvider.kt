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
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.core.completion.CompletionProviderEx
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSpec
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathCompletionProperty

fun createKindTestLookup(kindTest: String, tailText: String = "()"): LookupElementBuilder {
    return LookupElementBuilder.create(kindTest)
        .withBoldness(true)
        .withTailText(tailText)
}

object XPathKindTestProvider : CompletionProviderEx {
    private val XPATH_10_KIND_TESTS = listOf(
        createKindTestLookup("comment"),
        createKindTestLookup("node"),
        createKindTestLookup("processing-instruction", "(name?)"),
        createKindTestLookup("text")
    )

    private val XPATH_20_WD_2003_KIND_TESTS = listOf(
        createKindTestLookup("attribute", "(schema-context-or-name?, type?)"),
        createKindTestLookup("comment"),
        createKindTestLookup("document-node", "(root-element?)"),
        createKindTestLookup("element", "(schema-context-or-name?, nillable-type?)"),
        createKindTestLookup("namespace-node"),
        createKindTestLookup("node"),
        createKindTestLookup("processing-instruction", "(name?)"),
        createKindTestLookup("text")
    )

    private val XPATH_20_REC_KIND_TESTS = listOf(
        createKindTestLookup("attribute", "(name-or-wildcard?, type?)"),
        createKindTestLookup("comment"),
        createKindTestLookup("document-node", "(root-element?)"),
        createKindTestLookup("element", "(name-or-wildcard?, type?)"),
        createKindTestLookup("namespace-node"),
        createKindTestLookup("node"),
        createKindTestLookup("processing-instruction", "(name?)"),
        createKindTestLookup("schema-attribute", "(name)"),
        createKindTestLookup("schema-element", "(name)"),
        createKindTestLookup("text")
    )

    override fun apply(context: ProcessingContext, result: CompletionResultSet) {
        val version = context[XPathCompletionProperty.XPATH_VERSION]
        when {
            version === XPathSpec.REC_1_0_19991116 -> result.addAllElements(XPATH_10_KIND_TESTS)
            version === XPathSpec.WD_2_0_20030502 -> result.addAllElements(XPATH_20_WD_2003_KIND_TESTS)
            else -> result.addAllElements(XPATH_20_REC_KIND_TESTS)
        }
    }
}
