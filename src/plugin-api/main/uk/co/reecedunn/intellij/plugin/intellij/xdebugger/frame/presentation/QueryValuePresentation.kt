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

object QueryValuePresentation {
    internal const val SEPARATOR = " := "

    fun forValue(value: String, type: String? = null): XValuePresentation {
        return when {
            NUMERIC_TYPES.contains(type) -> NumericValuePresentation(value, type!!)
            STRING_TYPES.contains(type) -> StringValuePresentation(value, type!!)
            else -> XRegularValuePresentation(value, type, SEPARATOR)
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

    private val STRING_TYPES = setOf(
        "xs:language",
        "xs:normalizedString",
        "xs:string",
        "xs:token",
        "xs:ENTITY",
        "xs:ID",
        "xs:IDREF",
        "xs:Name",
        "xs:NCName",
        "xs:NMTOKEN"
    )
}
