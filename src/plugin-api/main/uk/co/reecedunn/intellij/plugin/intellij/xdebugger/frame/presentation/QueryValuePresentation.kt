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
package uk.co.reecedunn.intellij.plugin.intellij.xdebugger.frame.presentation

import com.intellij.xdebugger.frame.presentation.XRegularValuePresentation
import com.intellij.xdebugger.frame.presentation.XValuePresentation
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult

object QueryValuePresentation {
    internal const val SEPARATOR = " := "

    val EmptySequence = XRegularValuePresentation("()", "empty-sequence()", SEPARATOR)

    fun forValue(value: String, type: String? = null): XValuePresentation = when {
        NUMERIC_TYPES.contains(type) -> NumericValuePresentation(value, type!!)
        derivedFrom(type, "xs:string") || type == "text()" -> StringValuePresentation(value, type!!)
        else -> XRegularValuePresentation(value, type, SEPARATOR)
    }

    fun forResults(results: List<QueryResult>): XValuePresentation = when (results.size) {
        0 -> EmptySequence
        1 -> results.first().let { forValue(it.value.toString(), it.type) }
        else -> {
            var itemType: String? = null
            results.forEach { result ->
                itemType = when {
                    itemType == null -> result.type
                    itemType == result.type -> itemType
                    else -> "item()"
                }
            }
            forValue("size = ${results.size}", "${itemType}+")
        }
    }

    private val NUMERIC_TYPES = setOf(
        "xs:byte",
        "xs:decimal",
        "xs:double",
        "xs:float",
        "xs:int",
        "xs:integer",
        "xs:long",
        "xs:negativeInteger",
        "xs:nonNegativeInteger",
        "xs:nonPositiveInteger",
        "xs:positiveInteger",
        "xs:short",
        "xs:unsignedByte",
        "xs:unsignedInt",
        "xs:unsignedLong",
        "xs:unsignedShort"
    )

    private fun derivedFrom(aType: String?, bType: String): Boolean {
        val parent = PARENT_TYPES[aType]
        return when {
            aType == bType -> true
            parent == null -> false
            else -> derivedFrom(parent, bType)
        }
    }

    private val PARENT_TYPES = mapOf(
        "xs:language" to "xs:token",
        "xs:normalizedString" to "xs:string",
        "xs:token" to "xs:normalizedString",
        "xs:ENTITY" to "xs:NCName",
        "xs:ID" to "xs:NCName",
        "xs:IDREF" to "xs:NCName",
        "xs:Name" to "xs:token",
        "xs:NCName" to "xs:Name",
        "xs:NMTOKEN" to "xs:normalizedString"
    )
}
