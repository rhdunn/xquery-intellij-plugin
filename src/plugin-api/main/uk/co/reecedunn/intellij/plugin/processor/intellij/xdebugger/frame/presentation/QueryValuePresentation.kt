/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.intellij.xdebugger.frame.presentation

import com.intellij.xdebugger.frame.presentation.XRegularValuePresentation
import com.intellij.xdebugger.frame.presentation.XValuePresentation
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult

object QueryValuePresentation {
    internal const val SEPARATOR = " := "

    val EmptySequence: XValuePresentation = XRegularValuePresentation("()", "empty-sequence()", SEPARATOR)

    fun forValue(value: String, type: String? = null): XValuePresentation = when {
        derivesFromUnion(type, "xs:numeric") -> NumericValuePresentation(value, type!!)
        derivesFrom(type, "xs:string") || type == "text()" -> StringValuePresentation(value, type!!)
        else -> XRegularValuePresentation(value, type, SEPARATOR)
    }

    fun forResults(results: List<QueryResult>): XValuePresentation = when (results.size) {
        0 -> EmptySequence
        1 -> results.first().let { forValue(it.value.toString(), it.type) }
        else -> {
            var itemType: String? = null
            results.forEach { result -> itemType = itemTypeUnion(result.type, itemType) }
            forValue("size = ${results.size}", "${itemType}+")
        }
    }

    private fun itemTypeUnion(aType: String, bType: String?): String = when {
        bType == null -> aType
        subtypeItemType(aType, bType) -> bType
        subtypeItemType(bType, aType) -> aType
        isAtomicOrUnionType(aType) && isAtomicOrUnionType(bType) -> commonSimpleOrComplexType(aType, bType) ?: "item()"
        isKindTest(aType) && isKindTest(bType) -> "node()"
        else -> "item()"
    }

    private fun subtypeItemType(aType: String, bType: String): Boolean = when {
        isAtomicOrUnionType(aType) && isAtomicOrUnionType(bType) -> when (bType) {
            "xs:numeric" -> derivesFromUnion(aType, bType)
            else -> derivesFrom(aType, bType)
        }
        aType == "xs:error" -> isAtomicOrUnionType(bType)
        bType == "item()" -> true
        bType == "node()" -> isKindTest(aType)
        else -> aType == bType
    }

    private fun isAtomicOrUnionType(type: String): Boolean {
        return derivesFrom(type, "xs:anyAtomicType") || type == "xs:numeric"
    }

    private fun isKindTest(type: String): Boolean = KIND_TYPES.contains(type)

    @Suppress("SameParameterValue")
    private fun derivesFromUnion(aType: String?, bType: String): Boolean = when (bType) {
        "xs:numeric" -> {
            derivesFrom(aType, "xs:decimal") || derivesFrom(aType, "xs:float") || derivesFrom(aType, "xs:double")
        }
        else -> false
    }

    private fun derivesFrom(aType: String?, bType: String): Boolean {
        val parent = PARENT_TYPES[aType]
        return when {
            aType == bType -> true
            parent == null -> false
            else -> derivesFrom(parent, bType)
        }
    }

    private fun commonSimpleOrComplexType(aType: String, bType: String?): String? = when {
        bType == null -> null
        derivesFrom(aType, bType) -> bType
        else -> commonSimpleOrComplexType(aType, PARENT_TYPES[bType])
    }

    private val PARENT_TYPES = mapOf(
        "xs:anyAtomicType" to "xs:anySimpleType",
        "xs:anySimpleType" to "xs:anyType",
        "xs:anyURI" to "xs:anyAtomicType",
        "xs:base64Binary" to "xs:anyAtomicType",
        "xs:boolean" to "xs:anyAtomicType",
        "xs:byte" to "xs:short",
        "xs:date" to "xs:anyAtomicType",
        "xs:dateTime" to "xs:anyAtomicType",
        "xs:dateTimeStamp" to "xs:dateTime",
        "xs:dayTimeDuration" to "xs:duration",
        "xs:decimal" to "xs:anyAtomicType",
        "xs:double" to "xs:anyAtomicType",
        "xs:duration" to "xs:anyAtomicType",
        "xs:float" to "xs:anyAtomicType",
        "xs:gDay" to "xs:anyAtomicType",
        "xs:gMonth" to "xs:anyAtomicType",
        "xs:gMonthDay" to "xs:anyAtomicType",
        "xs:gYear" to "xs:anyAtomicType",
        "xs:gYearMonth" to "xs:anyAtomicType",
        "xs:hexBinary" to "xs:anyAtomicType",
        "xs:int" to "xs:long",
        "xs:integer" to "xs:decimal",
        "xs:language" to "xs:token",
        "xs:long" to "xs:integer",
        "xs:negativeInteger" to "xs:nonPositiveInteger",
        "xs:nonNegativeInteger" to "xs:integer",
        "xs:nonPositiveInteger" to "xs:integer",
        "xs:normalizedString" to "xs:string",
        "xs:numeric" to "xs:anySimpleType",
        "xs:positiveInteger" to "xs:nonNegativeInteger",
        "xs:short" to "xs:int",
        "xs:string" to "xs:anyAtomicType",
        "xs:time" to "xs:anyAtomicType",
        "xs:token" to "xs:normalizedString",
        "xs:unsignedByte" to "xs:unsignedShort",
        "xs:unsignedInt" to "xs:unsignedLong",
        "xs:unsignedLong" to "xs:nonNegativeInteger",
        "xs:unsignedShort" to "xs:unsignedInt",
        "xs:untyped" to "xs:anyType",
        "xs:untypedAtomic" to "xs:anyAtomicType",
        "xs:yearMonthDuration" to "xs:duration",
        "xs:ENTITIES" to "xs:anySimpleType",
        "xs:ENTITY" to "xs:NCName",
        "xs:ID" to "xs:NCName",
        "xs:IDREF" to "xs:NCName",
        "xs:IDREFS" to "xs:anySimpleType",
        "xs:Name" to "xs:token",
        "xs:NCName" to "xs:Name",
        "xs:NMTOKEN" to "xs:normalizedString",
        "xs:NMTOKENS" to "xs:anySimpleType",
        "xs:NOTATION" to "xs:anyAtomicType",
        "xs:QName" to "xs:anyAtomicType"
    )

    private val KIND_TYPES = setOf(
        "array-node()", // MarkLogic
        "attribute()",
        "boolean-node()", // MarkLogic
        "comment()",
        "document-node()",
        "element()",
        "namespace-node()",
        "null-node()", // MarkLogic
        "number-node()", // MarkLogic
        "object-node()", // MarkLogic
        "processing-instruction()",
        "text()"
    )
}
