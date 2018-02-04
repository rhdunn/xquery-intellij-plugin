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
package uk.co.reecedunn.intellij.plugin.xdm.tests

import junit.framework.TestCase
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.*
import uk.co.reecedunn.intellij.plugin.xdm.datatype.FOCA0002
import uk.co.reecedunn.intellij.plugin.xdm.datatype.FORG0001
import uk.co.reecedunn.intellij.plugin.xdm.datatype.FnErrorObject
import uk.co.reecedunn.intellij.plugin.xdm.datatype.XPTY0004
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmTypeCastResult
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

class TypeCasting : TestCase() {
    // region Primitive Types :: xs:float
    //
    // Reference: https://www.w3.org/TR/xpath-functions/#casting-to-numerics
    // Reference: https://www.w3.org/TR/xmlschema11-2/#float

    fun testXsFloat_FromXsFloat() {
        var result: XdmTypeCastResult

        result = XsFloat.cast(1.23f, XsFloat)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(1.23f))
    }

    fun testXsFloat_FromXsDouble() {
        var result: XdmTypeCastResult

        result = XsFloat.cast(1.2300000190734863, XsDouble)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(1.23f))
    }

    fun testXsFloat_FromXsDecimal() {
        var result: XdmTypeCastResult

        result = XsFloat.cast(BigDecimal("1.23"), XsDecimal)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(1.23f))
    }

    fun testXsFloat_FromXsInteger() {
        var result: XdmTypeCastResult

        result = XsFloat.cast(BigInteger.valueOf(123), XsInteger)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(123.0f))
    }

    fun testXsFloat_FromXsBoolean() {
        var result: XdmTypeCastResult

        result = XsFloat.cast(true, XsBoolean)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(1.0f))

        result = XsFloat.cast(false, XsBoolean)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(0.0f))
    }

    fun testXsFloat_FromXsString() {
        var result: XdmTypeCastResult

        result = XsFloat.cast("12", XsString)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(12.0f))

        result = XsFloat.cast("-1.23", XsString)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(-1.23f))

        result = XsFloat.cast("2.3e8", XsString)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(2.3e8f))
    }

    fun testXsFloat_FromXsString_Zero() {
        var result: XdmTypeCastResult

        result = XsFloat.cast("-0", XsString)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(-0.0f))

        result = XsFloat.cast("0", XsString)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(0.0f))

        result = XsFloat.cast("+0", XsString)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(0.0f))
    }

    fun testXsFloat_FromXsString_Infinity() {
        var result: XdmTypeCastResult

        result = XsFloat.cast("-INF", XsString)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(Float.NEGATIVE_INFINITY))

        result = XsFloat.cast("INF", XsString)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(Float.POSITIVE_INFINITY))

        // Valid in XSD 1.1, and MarkLogic; Invalid in BaseX.
        result = XsFloat.cast("+INF", XsString)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(Float.POSITIVE_INFINITY))
    }

    fun testXsFloat_FromXsString_NaN() {
        var result: XdmTypeCastResult

        result = XsFloat.cast("NaN", XsString)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(Float.NaN))
    }

    fun testXsFloat_FromXsString_InvalidPattern() {
        var result: XdmTypeCastResult

        result = XsFloat.cast("()", XsString)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:float'."))

        result = XsFloat.cast("true", XsString)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:float'."))
    }

    fun testXsFloat_FromXsUntypedAtomic() {
        var result: XdmTypeCastResult

        result = XsFloat.cast("12", XsUntypedAtomic)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(12.0f))

        result = XsFloat.cast("-1.23", XsUntypedAtomic)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(-1.23f))

        result = XsFloat.cast("2.3e8", XsUntypedAtomic)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(2.3e8f))
    }

    fun testXsFloat_FromXsUntypedAtomic_Zero() {
        var result: XdmTypeCastResult

        result = XsFloat.cast("-0", XsUntypedAtomic)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(-0.0f))

        result = XsFloat.cast("0", XsUntypedAtomic)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(0.0f))

        result = XsFloat.cast("+0", XsUntypedAtomic)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(0.0f))
    }

    fun testXsFloat_FromXsUntypedAtomic_Infinity() {
        var result: XdmTypeCastResult

        result = XsFloat.cast("-INF", XsUntypedAtomic)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(Float.NEGATIVE_INFINITY))

        result = XsFloat.cast("INF", XsUntypedAtomic)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(Float.POSITIVE_INFINITY))

        // Valid in XSD 1.1, and MarkLogic; Invalid in BaseX.
        result = XsFloat.cast("+INF", XsUntypedAtomic)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(Float.POSITIVE_INFINITY))
    }

    fun testXsFloat_FromXsUntypedAtomic_NaN() {
        var result: XdmTypeCastResult

        result = XsFloat.cast("NaN", XsUntypedAtomic)
        assertThat(result.type, `is`(XsFloat as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Float::class.java)))
        assertThat(result.value as Float, `is`(Float.NaN))
    }

    fun testXsFloat_FromXsUntypedAtomic_InvalidPattern() {
        var result: XdmTypeCastResult

        result = XsFloat.cast("()", XsUntypedAtomic)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:float'."))

        result = XsFloat.cast("true", XsUntypedAtomic)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:float'."))
    }

    fun testXsFloat_FromIncompatiblePrimitiveType() {
        var result: XdmTypeCastResult

        result = XsFloat.cast(Date(), XsDate)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(XPTY0004))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("Incompatible types when casting 'xs:date' to 'xs:float'."))
    }

    // endregion
    // region Primitive Types :: xs:double
    //
    // Reference: https://www.w3.org/TR/xpath-functions/#casting-to-numerics
    // Reference: https://www.w3.org/TR/xmlschema11-2/#double

    fun testXsDouble_FromXsDouble() {
        var result: XdmTypeCastResult

        result = XsDouble.cast(1.23, XsDouble)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(1.23))
    }

    fun testXsDouble_FromXsFloat() {
        var result: XdmTypeCastResult

        result = XsDouble.cast(1.23f, XsFloat)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(1.2300000190734863))
    }

    fun testXsDouble_FromXsDecimal() {
        var result: XdmTypeCastResult

        result = XsDouble.cast(BigDecimal("1.23"), XsDecimal)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(1.23))
    }

    fun testXsDouble_FromXsInteger() {
        var result: XdmTypeCastResult

        result = XsDouble.cast(BigInteger.valueOf(123), XsInteger)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(123.0))
    }

    fun testXsDouble_FromXsBoolean() {
        var result: XdmTypeCastResult

        result = XsDouble.cast(true, XsBoolean)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(1.0))

        result = XsDouble.cast(false, XsBoolean)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(0.0))
    }

    fun testXsDouble_FromXsString() {
        var result: XdmTypeCastResult

        result = XsDouble.cast("12", XsString)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(12.0))

        result = XsDouble.cast("-1.23", XsString)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(-1.23))

        result = XsDouble.cast("2.3e8", XsString)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(2.3e8))
    }

    fun testXsDouble_FromXsString_Zero() {
        var result: XdmTypeCastResult

        result = XsDouble.cast("-0", XsString)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(-0.0))

        result = XsDouble.cast("0", XsString)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(0.0))

        result = XsDouble.cast("+0", XsString)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(0.0))
    }

    fun testXsDouble_FromXsString_Infinity() {
        var result: XdmTypeCastResult

        result = XsDouble.cast("-INF", XsString)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(Double.NEGATIVE_INFINITY))

        result = XsDouble.cast("INF", XsString)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(Double.POSITIVE_INFINITY))

        // Valid in XSD 1.1, and MarkLogic; Invalid in BaseX.
        result = XsDouble.cast("+INF", XsString)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(Double.POSITIVE_INFINITY))
    }

    fun testXsDouble_FromXsString_NaN() {
        var result: XdmTypeCastResult

        result = XsDouble.cast("NaN", XsString)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(Double.NaN))
    }

    fun testXsDouble_FromXsString_InvalidPattern() {
        var result: XdmTypeCastResult

        result = XsDouble.cast("()", XsString)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:double'."))

        result = XsDouble.cast("true", XsString)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:double'."))
    }

    fun testXsDouble_FromXsUntypedAtomic() {
        var result: XdmTypeCastResult

        result = XsDouble.cast("12", XsUntypedAtomic)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(12.0))

        result = XsDouble.cast("-1.23", XsUntypedAtomic)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(-1.23))

        result = XsDouble.cast("2.3e8", XsUntypedAtomic)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(2.3e8))
    }

    fun testXsDouble_FromXsUntypedAtomic_Zero() {
        var result: XdmTypeCastResult

        result = XsDouble.cast("-0", XsUntypedAtomic)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(-0.0))

        result = XsDouble.cast("0", XsUntypedAtomic)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(0.0))

        result = XsDouble.cast("+0", XsUntypedAtomic)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(0.0))
    }

    fun testXsDouble_FromXsUntypedAtomic_Infinity() {
        var result: XdmTypeCastResult

        result = XsDouble.cast("-INF", XsUntypedAtomic)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(Double.NEGATIVE_INFINITY))

        result = XsDouble.cast("INF", XsUntypedAtomic)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(Double.POSITIVE_INFINITY))

        // Valid in XSD 1.1, and MarkLogic; Invalid in BaseX.
        result = XsDouble.cast("+INF", XsUntypedAtomic)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(Double.POSITIVE_INFINITY))
    }

    fun testXsDouble_FromXsUntypedAtomic_NaN() {
        var result: XdmTypeCastResult

        result = XsDouble.cast("NaN", XsUntypedAtomic)
        assertThat(result.type, `is`(XsDouble as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Double::class.java)))
        assertThat(result.value as Double, `is`(Double.NaN))
    }

    fun testXsDouble_FromXsUntypedAtomic_InvalidPattern() {
        var result: XdmTypeCastResult

        result = XsDouble.cast("()", XsUntypedAtomic)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:double'."))

        result = XsDouble.cast("true", XsUntypedAtomic)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:double'."))
    }

    fun testXsDouble_FromIncompatiblePrimitiveType() {
        var result: XdmTypeCastResult

        result = XsDouble.cast(Date(), XsDate)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(XPTY0004))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("Incompatible types when casting 'xs:date' to 'xs:double'."))
    }

    // endregion
    // region Primitive Types :: xs:decimal
    //
    // Reference: https://www.w3.org/TR/xpath-functions/#casting-to-numerics
    // Reference: https://www.w3.org/TR/xmlschema11-2/#decimal

    fun testXsDecimal_FromXsDecimal() {
        var result: XdmTypeCastResult

        result = XsDecimal.cast(BigDecimal("1.23"), XsDecimal)
        assertThat(result.type, `is`(XsDecimal as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(BigDecimal::class.java)))
        assertThat(result.value as BigDecimal, `is`(BigDecimal("1.23")))
    }

    fun testXsDecimal_FromXsInteger() {
        var result: XdmTypeCastResult

        result = XsDecimal.cast(BigInteger("123"), XsInteger)
        assertThat(result.type, `is`(XsDecimal as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(BigDecimal::class.java)))
        assertThat(result.value as BigDecimal, `is`(BigDecimal(BigInteger("123"))))
    }

    fun testXsDecimal_FromXsFloat() {
        var result: XdmTypeCastResult

        // NOTE: The exact behaviour (re: rounding) is implementation dependent.
        result = XsDecimal.cast(1.23f, XsFloat)
        assertThat(result.type, `is`(XsDecimal as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(BigDecimal::class.java)))
        assertThat(result.value as BigDecimal, `is`(BigDecimal("1.230000019073486328125")))

        result = XsDecimal.cast(Float.NaN, XsFloat)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FOCA0002))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:decimal'."))

        result = XsDecimal.cast(Float.POSITIVE_INFINITY, XsFloat)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FOCA0002))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:decimal'."))

        result = XsDecimal.cast(Float.NEGATIVE_INFINITY, XsFloat)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FOCA0002))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:decimal'."))
    }

    fun testXsDecimal_FromXsDouble() {
        var result: XdmTypeCastResult

        // NOTE: The exact behaviour (re: rounding) is implementation dependent.
        result = XsDecimal.cast(1.23, XsDouble)
        assertThat(result.type, `is`(XsDecimal as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(BigDecimal::class.java)))
        assertThat(result.value as BigDecimal, `is`(BigDecimal("1.229999999999999982236431605997495353221893310546875")))

        result = XsDecimal.cast(Double.NaN, XsDouble)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FOCA0002))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:decimal'."))

        result = XsDecimal.cast(Double.POSITIVE_INFINITY, XsDouble)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FOCA0002))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:decimal'."))

        result = XsDecimal.cast(Double.NEGATIVE_INFINITY, XsDouble)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FOCA0002))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:decimal'."))
    }

    fun testXsDecimal_FromXsBoolean() {
        var result: XdmTypeCastResult

        result = XsDecimal.cast(true, XsBoolean)
        assertThat(result.type, `is`(XsDecimal as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(BigDecimal::class.java)))
        assertThat(result.value as BigDecimal, `is`(BigDecimal.ONE))

        result = XsDecimal.cast(false, XsBoolean)
        assertThat(result.type, `is`(XsDecimal as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(BigDecimal::class.java)))
        assertThat(result.value as BigDecimal, `is`(BigDecimal.ZERO))
    }

    fun testXsDecimal_FromXsString() {
        var result: XdmTypeCastResult

        result = XsDecimal.cast("12", XsString)
        assertThat(result.type, `is`(XsDecimal as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(BigDecimal::class.java)))
        assertThat(result.value as BigDecimal, `is`(BigDecimal.valueOf(12)))

        result = XsDecimal.cast("-1.23", XsString)
        assertThat(result.type, `is`(XsDecimal as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(BigDecimal::class.java)))
        assertThat(result.value as BigDecimal, `is`(BigDecimal.valueOf(-1.23)))
    }

    fun testXsDecimal_FromXsString_InvalidPattern() {
        var result: XdmTypeCastResult

        result = XsDecimal.cast("()", XsString)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:decimal'."))

        result = XsDecimal.cast("true", XsString)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:decimal'."))

        result = XsDecimal.cast("2e8", XsString)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:decimal'."))
    }

    fun testXsDecimal_FromXsUntypedAtomic() {
        var result: XdmTypeCastResult

        result = XsDecimal.cast("12", XsUntypedAtomic)
        assertThat(result.type, `is`(XsDecimal as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(BigDecimal::class.java)))
        assertThat(result.value as BigDecimal, `is`(BigDecimal.valueOf(12)))

        result = XsDecimal.cast("-1.23", XsUntypedAtomic)
        assertThat(result.type, `is`(XsDecimal as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(BigDecimal::class.java)))
        assertThat(result.value as BigDecimal, `is`(BigDecimal.valueOf(-1.23)))
    }

    fun testXsDecimal_FromXsUntypedAtomic_InvalidPattern() {
        var result: XdmTypeCastResult

        result = XsDecimal.cast("()", XsUntypedAtomic)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:decimal'."))

        result = XsDecimal.cast("true", XsUntypedAtomic)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:decimal'."))

        result = XsDecimal.cast("2e8", XsUntypedAtomic)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:decimal'."))
    }

    fun testXsDecimal_FromIncompatiblePrimitiveType() {
        var result: XdmTypeCastResult

        result = XsDecimal.cast(Date(), XsDate)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(XPTY0004))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("Incompatible types when casting 'xs:date' to 'xs:decimal'."))
    }

    // endregion
    // region Primitive Types :: xs:boolean
    //
    // Reference: https://www.w3.org/TR/xpath-functions/#casting-boolean
    // Reference: https://www.w3.org/TR/xmlschema11-2/#boolean

    fun testXsBoolean_FromXsBoolean() {
        var result: XdmTypeCastResult

        result = XsBoolean.cast(false, XsBoolean)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(false))

        result = XsBoolean.cast(true, XsBoolean)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(true))
    }

    fun testXsBoolean_FromXsFloat() {
        var result: XdmTypeCastResult

        result = XsBoolean.cast(0.0f, XsFloat)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(false))

        result = XsBoolean.cast(1.0f, XsFloat)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(true))

        result = XsBoolean.cast(2.0f, XsFloat)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(true))

        result = XsBoolean.cast("NaN".toFloat(), XsFloat)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(false))
    }

    fun testXsBoolean_FromXsDouble() {
        var result: XdmTypeCastResult

        result = XsBoolean.cast(0.0, XsDouble)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(false))

        result = XsBoolean.cast(1.0, XsDouble)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(true))

        result = XsBoolean.cast(2.0, XsDouble)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(true))

        result = XsBoolean.cast("NaN".toDouble(), XsDouble)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(false))
    }

    fun testXsBoolean_FromXsDecimal() {
        var result: XdmTypeCastResult

        result = XsBoolean.cast(BigDecimal.ZERO, XsDecimal)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(false))

        result = XsBoolean.cast(BigDecimal.ONE, XsDecimal)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(true))

        result = XsBoolean.cast(BigDecimal.valueOf(2), XsDecimal)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(true))
    }

    fun testXsBoolean_FromXsInteger() {
        var result: XdmTypeCastResult

        result = XsBoolean.cast(BigInteger.ZERO, XsInteger)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(false))

        result = XsBoolean.cast(BigInteger.ONE, XsInteger)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(true))

        result = XsBoolean.cast(BigInteger.valueOf(2), XsInteger)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(true))
    }

    fun testXsBoolean_FromXsString() {
        var result: XdmTypeCastResult

        result = XsBoolean.cast("0", XsString)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(false))

        result = XsBoolean.cast("1", XsString)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(true))

        result = XsBoolean.cast("false", XsString)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(false))

        result = XsBoolean.cast("true", XsString)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(true))
    }

    fun testXsBoolean_FromXsString_InvalidPattern() {
        var result: XdmTypeCastResult

        result = XsBoolean.cast("()", XsString)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:boolean'."))

        result = XsBoolean.cast("2", XsString)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:boolean'."))

        result = XsBoolean.cast("True", XsString)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:boolean'."))
    }

    fun testXsBoolean_FromXsUntypedAtomic() {
        var result: XdmTypeCastResult

        result = XsBoolean.cast("0", XsUntypedAtomic)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(false))

        result = XsBoolean.cast("1", XsUntypedAtomic)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(true))

        result = XsBoolean.cast("false", XsUntypedAtomic)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(false))

        result = XsBoolean.cast("true", XsUntypedAtomic)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(true))
    }

    fun testXsBoolean_FromXsUntypedAtomic_InvalidPattern() {
        var result: XdmTypeCastResult

        result = XsBoolean.cast("()", XsUntypedAtomic)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:boolean'."))

        result = XsBoolean.cast("2", XsUntypedAtomic)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:boolean'."))

        result = XsBoolean.cast("True", XsUntypedAtomic)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:boolean'."))
    }

    fun testXsBoolean_FromIncompatiblePrimitiveType() {
        var result: XdmTypeCastResult

        result = XsBoolean.cast(Date(), XsDate)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(XPTY0004))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("Incompatible types when casting 'xs:date' to 'xs:boolean'."))
    }

    // endregion
    // region itemType? [XdmOptional]

    fun testXdmOptional_FromEmptySequence() {
        var result: XdmTypeCastResult

        result = XdmOptional(XsBoolean).cast(null, XdmEmptySequence)
        assertThat(result.type, `is`(XdmEmptySequence as XdmSequenceType))
        assertThat(result.value, `is`(nullValue()))
    }

    fun testXdmOptional_FromUntyped() {
        var result: XdmTypeCastResult

        result = XdmOptional(XsBoolean).cast("true", XsUntyped)
        assertThat(result.type, `is`(XsUntyped as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(String::class.java)))
        assertThat(result.value as String, `is`("true"))
    }

    fun testXdmOptional_FromType() {
        var result: XdmTypeCastResult

        result = XdmOptional(XsBoolean).cast(false, XsBoolean)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(false))
    }

    fun testXdmOptional_FromCastableType() {
        var result: XdmTypeCastResult

        result = XdmOptional(XsBoolean).cast(BigInteger.valueOf(2), XsInteger)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(true))
    }

    fun testXdmOptional_FromCastableType_InvalidPattern() {
        var result: XdmTypeCastResult

        result = XdmOptional(XsBoolean).cast("()", XsString)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:boolean'."))

        result = XdmOptional(XsBoolean).cast("2", XsString)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:boolean'."))

        result = XdmOptional(XsBoolean).cast("True", XsString)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'xs:boolean'."))
    }

    // endregion
}
