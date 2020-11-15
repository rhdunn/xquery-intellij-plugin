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
import uk.co.reecedunn.intellij.plugin.xpath.completion.property.XPathCompletionProperty
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue

object XPathAtomicOrUnionTypeProvider : CompletionProviderEx {
    private const val XS_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema"

    private fun createXsd10Types(prefix: String?): List<LookupElement> = listOf(
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

    private fun createXsd11Types(prefix: String?): List<LookupElement> = listOf(
        XPathAtomicOrUnionTypeLookup("dateTimeStamp", prefix),
        XPathAtomicOrUnionTypeLookup("error", prefix)
    )

    private fun addXsdTypes(context: ProcessingContext, result: CompletionResultSet, prefix: String?) {
        val product = context[XPathCompletionProperty.XPATH_PRODUCT] ?: W3C.SPECIFICATIONS
        val version = context[XPathCompletionProperty.XPATH_PRODUCT_VERSION] ?: defaultProductVersion(product)

        if (product.conformsTo(version, XmlSchemaSpec.REC_1_0_20041028)) result.addAllElements(createXsd10Types(prefix))
        if (product.conformsTo(version, XmlSchemaSpec.REC_1_1_20120405)) result.addAllElements(createXsd11Types(prefix))
    }

    override fun apply(element: PsiElement, context: ProcessingContext, result: CompletionResultSet) {
        val namespaces = context[XPathCompletionProperty.STATICALLY_KNOWN_NAMESPACES]
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
