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
import uk.co.reecedunn.intellij.plugin.intellij.lang.NullSpecification
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSpec
import uk.co.reecedunn.intellij.plugin.xpath.completion.XPathEmptyFunctionInsertHandler
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathCompletionProperty

fun createSequenceTypeLookup(kindTest: String, tailText: String = "()"): LookupElementBuilder {
    return LookupElementBuilder.create(kindTest)
        .withBoldness(true)
        .withTailText(tailText)
        .withInsertHandler(XPathEmptyFunctionInsertHandler)
}

object XPathSequenceTypeProvider : CompletionProviderEx {
    private val XPATH_20_WD_2003_SEQUENCE_TYPE = createSequenceTypeLookup("empty")

    private val XPATH_20_REC_SEQUENCE_TYPE = createSequenceTypeLookup("empty-sequence")

    @Suppress("MoveVariableDeclarationIntoWhen") // Feature not supported in Kotlin 1.2 (IntelliJ 2018.1).
    override fun apply(context: ProcessingContext, result: CompletionResultSet) {
        val version = context[XPathCompletionProperty.XPATH_VERSION]
        when (version) {
            NullSpecification, XPathSpec.REC_1_0_19991116 -> {}
            XPathSpec.WD_2_0_20030502 -> result.addElement(XPATH_20_WD_2003_SEQUENCE_TYPE)
            else -> result.addElement(XPATH_20_REC_SEQUENCE_TYPE)
        }
    }
}

object XPathItemTypeProvider : CompletionProviderEx {
    private val XPATH_20_ITEM_TYPES = listOf(
        createSequenceTypeLookup("item")
    )

    private val XPATH_30_ITEM_TYPES = listOf(
        createSequenceTypeLookup("function", "(sequence-types-or-wildcard)"),
        createSequenceTypeLookup("item")
    )

    private val XPATH_30_IN_XSLT_ITEM_TYPES = listOf(
        createSequenceTypeLookup("function", "(sequence-types-or-wildcard)"),
        createSequenceTypeLookup("item"),
        createSequenceTypeLookup("map", "(key-type-or-wildcard, value-type?)") // XSLT 3.0 includes support for maps.
    )

    private val XPATH_31_ITEM_TYPES = listOf(
        createSequenceTypeLookup("array", "(type-or-wildcard)"),
        createSequenceTypeLookup("function", "(sequence-types-or-wildcard)"),
        createSequenceTypeLookup("item"),
        createSequenceTypeLookup("map", "(key-type-or-wildcard, value-type?)")
    )

    @Suppress("MoveVariableDeclarationIntoWhen") // Feature not supported in Kotlin 1.2 (IntelliJ 2018.1).
    override fun apply(context: ProcessingContext, result: CompletionResultSet) {
        val version = context[XPathCompletionProperty.XPATH_VERSION]
        when (version) {
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
            else -> {}
        }
    }
}

object XPathKindTestProvider : CompletionProviderEx {
    private val XPATH_10_KIND_TESTS = listOf(
        createSequenceTypeLookup("comment"),
        createSequenceTypeLookup("node"),
        createSequenceTypeLookup("processing-instruction", "(name?)"),
        createSequenceTypeLookup("text")
    )

    private val XPATH_20_WD_2003_KIND_TESTS = listOf(
        createSequenceTypeLookup("attribute", "(schema-context-or-name?, type?)"),
        createSequenceTypeLookup("comment"),
        createSequenceTypeLookup("document-node", "(root-element?)"),
        createSequenceTypeLookup("element", "(schema-context-or-name?, nillable-type?)"),
        createSequenceTypeLookup("namespace-node"),
        createSequenceTypeLookup("node"),
        createSequenceTypeLookup("processing-instruction", "(name?)"),
        createSequenceTypeLookup("text")
    )

    private val XPATH_20_REC_KIND_TESTS = listOf(
        createSequenceTypeLookup("attribute", "(name-or-wildcard?, type?)"),
        createSequenceTypeLookup("comment"),
        createSequenceTypeLookup("document-node", "(root-element?)"),
        createSequenceTypeLookup("element", "(name-or-wildcard?, type?)"),
        createSequenceTypeLookup("namespace-node"),
        createSequenceTypeLookup("node"),
        createSequenceTypeLookup("processing-instruction", "(name?)"),
        createSequenceTypeLookup("schema-attribute", "(name)"),
        createSequenceTypeLookup("schema-element", "(name)"),
        createSequenceTypeLookup("text")
    )

    @Suppress("MoveVariableDeclarationIntoWhen") // Feature not supported in Kotlin 1.2 (IntelliJ 2018.1).
    override fun apply(context: ProcessingContext, result: CompletionResultSet) {
        val version = context[XPathCompletionProperty.XPATH_VERSION]
        when (version) {
            XPathSpec.REC_1_0_19991116 -> result.addAllElements(XPATH_10_KIND_TESTS)
            XPathSpec.WD_2_0_20030502 -> result.addAllElements(XPATH_20_WD_2003_KIND_TESTS)
            else -> result.addAllElements(XPATH_20_REC_KIND_TESTS)
        }
    }
}
