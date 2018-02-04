/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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
/*
 * XPath and XQuery Type System Part 3: Atomic Types
 *
 * Reference: https://www.w3.org/TR/xpath-datamodel-31
 * Reference: https://www.w3.org/TR/2012/REC-xmlschema11-1-20120405
 */
package uk.co.reecedunn.intellij.plugin.xdm

import uk.co.reecedunn.intellij.plugin.xdm.datatype.FOCA0002
import uk.co.reecedunn.intellij.plugin.xdm.datatype.FORG0001
import uk.co.reecedunn.intellij.plugin.xdm.datatype.XPTY0004
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmAtomicType
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmTypeCastResult
import java.math.BigDecimal
import java.math.BigInteger

val XsUntypedAtomic = XdmAtomicType(xs("untypedAtomic"), XsAnyAtomicType)

val XsDateTime = XdmAtomicType(xs("dateTime"), XsAnyAtomicType)

val XsDateTimeStamp = XdmAtomicType(xs("dateTimeStamp"), XsDateTime)

val XsDate = XdmAtomicType(xs("date"), XsAnyAtomicType)

val XsTime = XdmAtomicType(xs("time"), XsAnyAtomicType)

val XsDuration = XdmAtomicType(xs("duration"), XsAnyAtomicType)

val XsYearMonthDuration = XdmAtomicType(xs("yearMonthDuration"), XsDuration)

val XsDayTimeDuration = XdmAtomicType(xs("dayTimeDuration"), XsDuration)

object XsFloat : XdmAtomicType(xs("float"), XsAnyAtomicType) {
    // @see https://www.w3.org/TR/xpath-functions/#casting-to-numerics
    // @see https://www.w3.org/TR/xmlschema11-2/#float
    override fun castPrimitive(value: Any?, type: XdmSequenceType): XdmTypeCastResult {
        return when (type) {
            XsFloat -> XdmTypeCastResult(value, type)
            XsDouble -> XdmTypeCastResult((value as Double).toFloat(), XsFloat)
            XsDecimal, XsInteger -> XdmTypeCastResult(value.toString().toFloat(), XsFloat)
            XsBoolean -> XdmTypeCastResult(if (value as Boolean) 1.0f else 0.0f, XsFloat)
            XsString, XsUntypedAtomic -> {
                val v = value as String
                when (v) {
                    // NOTE: `[+-]INF` results in NumberFormatException errors in `toDouble`.
                    "-INF" -> XdmTypeCastResult(Float.NEGATIVE_INFINITY, XsFloat)
                    "+INF", "INF" -> XdmTypeCastResult(Float.POSITIVE_INFINITY, XsFloat)
                    else ->
                        try {
                            XdmTypeCastResult(v.toFloat(), XsFloat)
                        } catch (e: NumberFormatException) {
                            createCastError(FORG0001, "fnerror.FORG0001.lexical-representation", this)
                        }
                }
            }
            else -> createCastError(XPTY0004, "fnerror.XPTY0004.incompatible-types", type, this)
        }
    }
}

object XsDouble : XdmAtomicType(xs("double"), XsAnyAtomicType) {
    // @see https://www.w3.org/TR/xpath-functions/#casting-to-numerics
    // @see https://www.w3.org/TR/xmlschema11-2/#double
    override fun castPrimitive(value: Any?, type: XdmSequenceType): XdmTypeCastResult {
        return when (type) {
            XsDouble -> XdmTypeCastResult(value, type)
            XsFloat -> XdmTypeCastResult((value as Float).toDouble(), XsDouble)
            XsDecimal, XsInteger -> XdmTypeCastResult(value.toString().toDouble(), XsDouble)
            XsBoolean -> XdmTypeCastResult(if (value as Boolean) 1.0 else 0.0, XsDouble)
            XsString, XsUntypedAtomic -> {
                val v = value as String
                when (v) {
                    // NOTE: `[+-]INF` results in NumberFormatException errors in `toDouble`.
                    "-INF" -> XdmTypeCastResult(Double.NEGATIVE_INFINITY, XsDouble)
                    "+INF", "INF" -> XdmTypeCastResult(Double.POSITIVE_INFINITY, XsDouble)
                    else ->
                        try {
                            XdmTypeCastResult(v.toDouble(), XsDouble)
                        } catch (e: NumberFormatException) {
                            createCastError(FORG0001, "fnerror.FORG0001.lexical-representation", this)
                        }
                }
            }
            else -> createCastError(XPTY0004, "fnerror.XPTY0004.incompatible-types", type, this)
        }
    }
}

object XsDecimal : XdmAtomicType(xs("decimal"), XsAnyAtomicType) {
    // @see https://www.w3.org/TR/xpath-functions/#casting-to-numerics
    // @see https://www.w3.org/TR/xmlschema11-2/#decimal
    override fun castPrimitive(value: Any?, type: XdmSequenceType): XdmTypeCastResult {
        return when (type) {
            XsDecimal -> XdmTypeCastResult(value, type)
            XsInteger -> XdmTypeCastResult(BigDecimal(value as BigInteger), XsDecimal)
            XsDouble, XsFloat -> {
                val v = if (type == XsDouble) value as Double else (value as Float).toDouble()
                if (v.isInfinite() || v.isNaN())
                    createCastError(FOCA0002, "fnerror.FORG0001.lexical-representation", this)
                else
                    XdmTypeCastResult(BigDecimal(v), XsDecimal)
            }
            XsBoolean ->
                XdmTypeCastResult(if (value as Boolean) BigDecimal.ONE else BigDecimal.ZERO, XsDecimal)
            XsString, XsUntypedAtomic -> {
                val v = value as String
                try {
                    if (v.contains('e', true)) // XQuery/XMLSchema don't support exponential xs:decimals.
                        createCastError(FORG0001, "fnerror.FORG0001.lexical-representation", this)
                    else
                        XdmTypeCastResult(BigDecimal(v), XsDecimal)
                } catch (e: NumberFormatException) {
                    createCastError(FORG0001, "fnerror.FORG0001.lexical-representation", this)
                }
            }
            else -> createCastError(XPTY0004, "fnerror.XPTY0004.incompatible-types", type, this)
        }
    }
}

val XsInteger = XdmAtomicType(xs("integer"), XsDecimal)

val XsNonPositiveInteger = XdmAtomicType(xs("nonPositiveInteger"), XsInteger)

val XsNegativeInteger = XdmAtomicType(xs("negativeInteger"), XsNonPositiveInteger)

val XsLong = XdmAtomicType(xs("long"), XsInteger)

val XsInt = XdmAtomicType(xs("int"), XsLong)

val XsShort = XdmAtomicType(xs("short"), XsInt)

val XsByte = XdmAtomicType(xs("byte"), XsShort)

val XsNonNegativeInteger = XdmAtomicType(xs("nonNegativeInteger"), XsInteger)

val XsUnsignedLong = XdmAtomicType(xs("unsignedLong"), XsNonNegativeInteger)

val XsUnsignedInt = XdmAtomicType(xs("unsignedInt"), XsUnsignedLong)

val XsUnsignedShort = XdmAtomicType(xs("unsignedShort"), XsUnsignedInt)

val XsUnsignedByte = XdmAtomicType(xs("unsignedByte"), XsUnsignedShort)

val XsPositiveInteger = XdmAtomicType(xs("positiveInteger"), XsNonNegativeInteger)

val XsGYearMonth = XdmAtomicType(xs("gYearMonth"), XsAnyAtomicType)

val XsGYear = XdmAtomicType(xs("gYear"), XsAnyAtomicType)

val XsGMonthDay = XdmAtomicType(xs("gMonthDay"), XsAnyAtomicType)

val XsGDay = XdmAtomicType(xs("gDay"), XsAnyAtomicType)

val XsGMonth = XdmAtomicType(xs("gMonth"), XsAnyAtomicType)

val XsString = XdmAtomicType(xs("string"), XsAnyAtomicType)

val XsNormalizedString = XdmAtomicType(xs("normalizedString"), XsString)

val XsToken = XdmAtomicType(xs("token"), XsNormalizedString)

val XsLanguage = XdmAtomicType(xs("language"), XsToken)

val XsNMTOKEN = XdmAtomicType(xs("NMTOKEN"), XsToken)

val XsName = XdmAtomicType(xs("Name"), XsToken)

val XsNCName = XdmAtomicType(xs("NCName"), XsName)

val XsID = XdmAtomicType(xs("ID"), XsNCName)

val XsIDREF = XdmAtomicType(xs("IDREF"), XsNCName)

val XsENTITY = XdmAtomicType(xs("ENTITY"), XsNCName)

object XsBoolean : XdmAtomicType(xs("boolean"), XsAnyAtomicType) {
    // @see https://www.w3.org/TR/xpath-functions/#casting-boolean
    // @see https://www.w3.org/TR/xmlschema11-2/#boolean
    override fun castPrimitive(value: Any?, type: XdmSequenceType): XdmTypeCastResult {
        return when (type) {
            XsBoolean ->
                XdmTypeCastResult(value, type)
            XsString, XsUntypedAtomic -> when (value as String) {
                "0", "false" -> XdmTypeCastResult(false, XsBoolean)
                "1", "true"  -> XdmTypeCastResult(true,  XsBoolean)
                else -> createCastError(FORG0001, "fnerror.FORG0001.lexical-representation", this)
            }
            XsFloat -> {
                val v = value as Float
                XdmTypeCastResult(v != 0.0f && !v.isNaN(), XsBoolean)
            }
            XsDouble -> {
                val v = value as Double
                XdmTypeCastResult(v != 0.0 && !v.isNaN(), XsBoolean)
            }
            XsDecimal -> {
                val v = value as BigDecimal
                XdmTypeCastResult(v != BigDecimal.ZERO, XsBoolean)
            }
            XsInteger -> {
                val v = value as BigInteger
                XdmTypeCastResult(v != BigInteger.ZERO, XsBoolean)
            }
            else -> createCastError(XPTY0004, "fnerror.XPTY0004.incompatible-types", type, this)
        }
    }
}

val XsBase64Binary = XdmAtomicType(xs("base64Binary"), XsAnyAtomicType)

val XsHexBinary = XdmAtomicType(xs("hexBinary"), XsAnyAtomicType)

val XsAnyURI = XdmAtomicType(xs("anyURI"), XsAnyAtomicType)

val XsQName = XdmAtomicType(xs("QName"), XsAnyAtomicType)

val XsNOTATION = XdmAtomicType(xs("NOTATION"), XsAnyAtomicType)
