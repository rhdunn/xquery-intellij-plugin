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
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import uk.co.reecedunn.intellij.plugin.core.completion.CompletionProviderEx
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathAtomicOrUnionTypeLookup
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathInsertText
import uk.co.reecedunn.intellij.plugin.xpath.completion.lookup.XPathKeywordLookup
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathCompletionProperty
import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue

object XPathSequenceTypeProvider : CompletionProviderEx {
    private val XPATH_20_WD_2003_SEQUENCE_TYPE = XPathKeywordLookup("empty", XPathInsertText.EMPTY_PARAMS)

    private val XPATH_20_REC_SEQUENCE_TYPE = XPathKeywordLookup("empty-sequence", XPathInsertText.EMPTY_PARAMS)

    @Suppress("MoveVariableDeclarationIntoWhen") // Feature not supported in Kotlin 1.2 (IntelliJ 2018.1).
    override fun apply(element: PsiElement, context: ProcessingContext, result: CompletionResultSet) {
        val version = context[XPathCompletionProperty.XPATH_VERSION]
        when (version) {
            XPathSpec.REC_1_0_19991116 -> {
            }
            XPathSpec.WD_2_0_20030502 -> result.addElement(XPATH_20_WD_2003_SEQUENCE_TYPE)
            else -> result.addElement(XPATH_20_REC_SEQUENCE_TYPE)
        }
    }
}

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

    @Suppress("MoveVariableDeclarationIntoWhen") // Feature not supported in Kotlin 1.2 (IntelliJ 2018.1).
    override fun apply(element: PsiElement, context: ProcessingContext, result: CompletionResultSet) {
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
            else -> {
            }
        }
    }
}

object XPathUnionTypeProvider : CompletionProviderEx {
    private val UNION_TYPE = XPathKeywordLookup("union", XPathInsertText.PARAMS_TYPES)

    override fun apply(element: PsiElement, context: ProcessingContext, result: CompletionResultSet) {
        val version = context[XPathCompletionProperty.XPATH_PRODUCT_VERSION]
        if (version.kind === Saxon && version.value >= 9.8) {
            result.addElement(UNION_TYPE)
        }
    }
}

object XPathAtomicOrUnionTypeProvider : CompletionProviderEx {
    private const val XS_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema"

    private fun createXsd10Types(prefix: String?): List<LookupElement> {
        return listOf(
            XPathAtomicOrUnionTypeLookup("anyAtomicType", prefix), // XSD 1.1 type supported in XPath/XQuery
            XPathAtomicOrUnionTypeLookup("anySimpleType", prefix),
            XPathAtomicOrUnionTypeLookup("anyURI", prefix),
            XPathAtomicOrUnionTypeLookup("base64Binary", prefix),
            XPathAtomicOrUnionTypeLookup("boolean", prefix),
            XPathAtomicOrUnionTypeLookup("byte", prefix),
            XPathAtomicOrUnionTypeLookup("date", prefix),
            XPathAtomicOrUnionTypeLookup("dateTime", prefix),
            XPathAtomicOrUnionTypeLookup("dayTimeDuration", prefix), // XSD 1.1 type supported in XPath/XQuery
            XPathAtomicOrUnionTypeLookup("decimal", prefix),
            XPathAtomicOrUnionTypeLookup("double", prefix),
            XPathAtomicOrUnionTypeLookup("duration", prefix),
            XPathAtomicOrUnionTypeLookup("ENTITY", prefix),
            XPathAtomicOrUnionTypeLookup("float", prefix),
            XPathAtomicOrUnionTypeLookup("gDay", prefix),
            XPathAtomicOrUnionTypeLookup("gMonth", prefix),
            XPathAtomicOrUnionTypeLookup("gMonthDay", prefix),
            XPathAtomicOrUnionTypeLookup("gYear", prefix),
            XPathAtomicOrUnionTypeLookup("gYearMonth", prefix),
            XPathAtomicOrUnionTypeLookup("hexBinary", prefix),
            XPathAtomicOrUnionTypeLookup("ID", prefix),
            XPathAtomicOrUnionTypeLookup("IDREF", prefix),
            XPathAtomicOrUnionTypeLookup("int", prefix),
            XPathAtomicOrUnionTypeLookup("integer", prefix),
            XPathAtomicOrUnionTypeLookup("language", prefix),
            XPathAtomicOrUnionTypeLookup("long", prefix),
            XPathAtomicOrUnionTypeLookup("Name", prefix),
            XPathAtomicOrUnionTypeLookup("NCName", prefix),
            XPathAtomicOrUnionTypeLookup("negativeInteger", prefix),
            XPathAtomicOrUnionTypeLookup("NMTOKEN", prefix),
            XPathAtomicOrUnionTypeLookup("nonNegativeInteger", prefix),
            XPathAtomicOrUnionTypeLookup("nonPositiveInteger", prefix),
            XPathAtomicOrUnionTypeLookup("normalizedString", prefix),
            XPathAtomicOrUnionTypeLookup("NOTATION", prefix),
            XPathAtomicOrUnionTypeLookup("numeric", prefix),
            XPathAtomicOrUnionTypeLookup("positiveInteger", prefix),
            XPathAtomicOrUnionTypeLookup("QName", prefix),
            XPathAtomicOrUnionTypeLookup("short", prefix),
            XPathAtomicOrUnionTypeLookup("string", prefix),
            XPathAtomicOrUnionTypeLookup("time", prefix),
            XPathAtomicOrUnionTypeLookup("token", prefix),
            XPathAtomicOrUnionTypeLookup("unsignedByte", prefix),
            XPathAtomicOrUnionTypeLookup("unsignedInt", prefix),
            XPathAtomicOrUnionTypeLookup("unsignedLong", prefix),
            XPathAtomicOrUnionTypeLookup("unsignedShort", prefix),
            XPathAtomicOrUnionTypeLookup("untypedAtomic", prefix),
            XPathAtomicOrUnionTypeLookup("yearMonthDuration", prefix) // XSD 1.1 type supported in XPath/XQuery
        )
    }

    private fun createXsd11Types(prefix: String?): List<LookupElement> {
        return listOf(
            XPathAtomicOrUnionTypeLookup("dateTimeStamp", prefix),
            XPathAtomicOrUnionTypeLookup("error", prefix)
        )
    }

    private fun addXsdTypes(context: ProcessingContext, result: CompletionResultSet, prefix: String?) {
        val product = context[XPathCompletionProperty.XPATH_PRODUCT] ?: W3C.SPECIFICATIONS
        val version = context[XPathCompletionProperty.XPATH_PRODUCT_VERSION] ?: defaultProductVersion(product)

        if (product.conformsTo(version, XmlSchemaSpec.REC_1_0_20041028)) result.addAllElements(createXsd10Types(prefix))
        if (product.conformsTo(version, XmlSchemaSpec.REC_1_1_20120405)) result.addAllElements(createXsd11Types(prefix))
    }

    override fun apply(element: PsiElement, context: ProcessingContext, result: CompletionResultSet) {
        val namespaces = context[XPathCompletionProperty.STATICALLY_KNOWN_ELEMENT_OR_TYPE_NAMESPACES]
        val prefix = namespaces.find { it.namespaceUri?.data == XS_NAMESPACE_URI } ?: return
        val prefixName = prefix.namespacePrefix?.data

        val qname = element.parent as XsQNameValue
        when (qname.completionType(element)) {
            EQNameCompletionType.QNamePrefix, EQNameCompletionType.NCName -> addXsdTypes(context, result, prefixName)
            EQNameCompletionType.QNameLocalName -> {
                if (qname.prefix?.data == prefixName) {
                    addXsdTypes(context, result, null) // Prefix already specified.
                }
            }
            EQNameCompletionType.URIQualifiedNameLocalName -> {
                if (qname.namespace?.data == XS_NAMESPACE_URI) {
                    addXsdTypes(context, result, null) // Prefix already specified.
                }
            }
            else -> {
            }
        }
    }
}

object XPathKindTestProvider : CompletionProviderEx {
    private val XPATH_10_KIND_TESTS = listOf(
        XPathKeywordLookup("comment", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("node", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("processing-instruction", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("processing-instruction", XPathInsertText.PARAMS_NAME_STRING),
        XPathKeywordLookup("text", XPathInsertText.EMPTY_PARAMS)
    )

    private val XPATH_20_WD_2003_KIND_TESTS = listOf(
        XPathKeywordLookup("attribute", XPathInsertText.PARAMS_SCHEMA_CONTEXT),
        XPathKeywordLookup("comment", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("document-node", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("document-node", XPathInsertText.PARAMS_ROOT_ELEMENT),
        XPathKeywordLookup("element", XPathInsertText.PARAMS_SCHEMA_CONTEXT),
        XPathKeywordLookup("namespace-node", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("node", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("processing-instruction", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("processing-instruction", XPathInsertText.PARAMS_NAME_STRING),
        XPathKeywordLookup("text", XPathInsertText.EMPTY_PARAMS)
    )

    private val XPATH_20_REC_KIND_TESTS = listOf(
        XPathKeywordLookup("attribute", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("attribute", XPathInsertText.PARAMS_WILDCARD),
        XPathKeywordLookup("attribute", XPathInsertText.PARAMS_NAME),
        XPathKeywordLookup("attribute", XPathInsertText.PARAMS_WILDCARD_AND_TYPE),
        XPathKeywordLookup("attribute", XPathInsertText.PARAMS_NAME_AND_TYPE),
        XPathKeywordLookup("comment", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("document-node", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("document-node", XPathInsertText.PARAMS_ROOT_ELEMENT),
        XPathKeywordLookup("element", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("element", XPathInsertText.PARAMS_WILDCARD),
        XPathKeywordLookup("element", XPathInsertText.PARAMS_NAME),
        XPathKeywordLookup("element", XPathInsertText.PARAMS_WILDCARD_AND_TYPE),
        XPathKeywordLookup("element", XPathInsertText.PARAMS_NAME_AND_TYPE),
        XPathKeywordLookup("namespace-node", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("node", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("processing-instruction", XPathInsertText.EMPTY_PARAMS),
        XPathKeywordLookup("processing-instruction", XPathInsertText.PARAMS_NAME),
        XPathKeywordLookup("processing-instruction", XPathInsertText.PARAMS_NAME_STRING),
        XPathKeywordLookup("schema-attribute", XPathInsertText.PARAMS_NAME),
        XPathKeywordLookup("schema-element", XPathInsertText.PARAMS_NAME),
        XPathKeywordLookup("text", XPathInsertText.EMPTY_PARAMS)
    )

    @Suppress("MoveVariableDeclarationIntoWhen") // Feature not supported in Kotlin 1.2 (IntelliJ 2018.1).
    override fun apply(element: PsiElement, context: ProcessingContext, result: CompletionResultSet) {
        val version = context[XPathCompletionProperty.XPATH_VERSION]
        when (version) {
            XPathSpec.REC_1_0_19991116 -> result.addAllElements(XPATH_10_KIND_TESTS)
            XPathSpec.WD_2_0_20030502 -> result.addAllElements(XPATH_20_WD_2003_KIND_TESTS)
            else -> result.addAllElements(XPATH_20_REC_KIND_TESTS)
        }
    }
}
