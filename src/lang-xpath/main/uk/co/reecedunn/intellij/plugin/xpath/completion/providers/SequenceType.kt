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
import uk.co.reecedunn.intellij.plugin.intellij.lang.W3C
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSpec
import uk.co.reecedunn.intellij.plugin.intellij.lang.XmlSchemaSpec
import uk.co.reecedunn.intellij.plugin.intellij.lang.defaultProductVersion
import uk.co.reecedunn.intellij.plugin.intellij.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xpath.completion.XPathEmptyFunctionInsertHandler
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathCompletionProperty

fun createSequenceTypeLookup(kindTest: String, tailText: String = "()"): LookupElementBuilder {
    return LookupElementBuilder.create(kindTest)
        .withBoldness(true)
        .withTailText(tailText)
        .withInsertHandler(XPathEmptyFunctionInsertHandler)
}

fun createTypeNameLookup(localName: String, prefix: String? = null): LookupElementBuilder {
    return LookupElementBuilder.create(prefix?.let { "$it:$localName" } ?: localName)
        .withIcon(XPathIcons.Nodes.TypeDecl)
}

object XPathSequenceTypeProvider : CompletionProviderEx {
    private val XPATH_20_WD_2003_SEQUENCE_TYPE = createSequenceTypeLookup("empty")

    private val XPATH_20_REC_SEQUENCE_TYPE = createSequenceTypeLookup("empty-sequence")

    @Suppress("MoveVariableDeclarationIntoWhen") // Feature not supported in Kotlin 1.2 (IntelliJ 2018.1).
    override fun apply(context: ProcessingContext, result: CompletionResultSet) {
        val version = context[XPathCompletionProperty.XPATH_VERSION]
        when (version) {
            XPathSpec.REC_1_0_19991116 -> {}
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

object XPathAtomicOrUnionTypeProvider : CompletionProviderEx {
    private const val XS_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema"

    private fun createXsd10Types(prefix: String): List<LookupElementBuilder> {
        return listOf(
            createTypeNameLookup("anyAtomicType", prefix), // XSD 1.1 type supported in XPath/XQuery
            createTypeNameLookup("anySimpleType", prefix),
            createTypeNameLookup("anyURI", prefix),
            createTypeNameLookup("base64Binary", prefix),
            createTypeNameLookup("boolean", prefix),
            createTypeNameLookup("byte", prefix),
            createTypeNameLookup("date", prefix),
            createTypeNameLookup("dateTime", prefix),
            createTypeNameLookup("dayTimeDuration", prefix), // XSD 1.1 type supported in XPath/XQuery
            createTypeNameLookup("decimal", prefix),
            createTypeNameLookup("double", prefix),
            createTypeNameLookup("duration", prefix),
            createTypeNameLookup("ENTITY", prefix),
            createTypeNameLookup("float", prefix),
            createTypeNameLookup("gDay", prefix),
            createTypeNameLookup("gMonth", prefix),
            createTypeNameLookup("gMonthDay", prefix),
            createTypeNameLookup("gYear", prefix),
            createTypeNameLookup("gYearMonth", prefix),
            createTypeNameLookup("hexBinary", prefix),
            createTypeNameLookup("ID", prefix),
            createTypeNameLookup("IDREF", prefix),
            createTypeNameLookup("int", prefix),
            createTypeNameLookup("integer", prefix),
            createTypeNameLookup("language", prefix),
            createTypeNameLookup("long", prefix),
            createTypeNameLookup("Name", prefix),
            createTypeNameLookup("NCName", prefix),
            createTypeNameLookup("negativeInteger", prefix),
            createTypeNameLookup("NMTOKEN", prefix),
            createTypeNameLookup("nonNegativeInteger", prefix),
            createTypeNameLookup("nonPositiveInteger", prefix),
            createTypeNameLookup("normalizedString", prefix),
            createTypeNameLookup("NOTATION", prefix),
            createTypeNameLookup("numeric", prefix),
            createTypeNameLookup("positiveInteger", prefix),
            createTypeNameLookup("QName", prefix),
            createTypeNameLookup("short", prefix),
            createTypeNameLookup("string", prefix),
            createTypeNameLookup("time", prefix),
            createTypeNameLookup("token", prefix),
            createTypeNameLookup("unsignedByte", prefix),
            createTypeNameLookup("unsignedInt", prefix),
            createTypeNameLookup("unsignedLong", prefix),
            createTypeNameLookup("unsignedShort", prefix),
            createTypeNameLookup("untypedAtomic", prefix),
            createTypeNameLookup("yearMonthDuration", prefix) // XSD 1.1 type supported in XPath/XQuery
        )
    }

    private fun createXsd11Types(prefix: String): List<LookupElementBuilder> {
        return listOf(
            createTypeNameLookup("dateTimeStamp", prefix),
            createTypeNameLookup("error", prefix)
        )
    }

    override fun apply(context: ProcessingContext, result: CompletionResultSet) {
        val namespaces = context[XPathCompletionProperty.STATICALLY_KNOWN_NAMESPACES]
        val prefix = namespaces.find { it.namespaceUri?.data == XS_NAMESPACE_URI }?.namespacePrefix?.data ?: return

        val product = context[XPathCompletionProperty.XPATH_PRODUCT] ?: W3C.SPECIFICATIONS
        val version = context[XPathCompletionProperty.XPATH_PRODUCT_VERSION] ?: defaultProductVersion(product)

        if (product.conformsTo(version, XmlSchemaSpec.REC_1_0_20041028)) result.addAllElements(createXsd10Types(prefix))
        if (product.conformsTo(version, XmlSchemaSpec.REC_1_1_20120405)) result.addAllElements(createXsd11Types(prefix))
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
