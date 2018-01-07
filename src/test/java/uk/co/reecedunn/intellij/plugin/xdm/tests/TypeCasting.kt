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
import uk.co.reecedunn.intellij.plugin.xdm.datatype.FORG0001
import uk.co.reecedunn.intellij.plugin.xdm.datatype.FnErrorObject
import uk.co.reecedunn.intellij.plugin.xdm.datatype.XPTY0004
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmEmptySequence
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmOptional
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmTypeCastResult
import java.math.BigInteger
import java.util.*

class TypeCasting : TestCase() {
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
                `is`("The value does not match the lexical representation for 'Q{http://www.w3.org/2001/XMLSchema}boolean'."))

        result = XsBoolean.cast("2", XsString)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'Q{http://www.w3.org/2001/XMLSchema}boolean'."))

        result = XsBoolean.cast("True", XsString)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'Q{http://www.w3.org/2001/XMLSchema}boolean'."))
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

        result = XsBoolean.cast(0, XsUntypedAtomic)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(false))

        result = XsBoolean.cast(1, XsUntypedAtomic)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(true))

        result = XsBoolean.cast(false, XsUntypedAtomic)
        assertThat(result.type, `is`(XsBoolean as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(Boolean::class.java)))
        assertThat(result.value as Boolean, `is`(false))

        result = XsBoolean.cast(true, XsUntypedAtomic)
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
                `is`("The value does not match the lexical representation for 'Q{http://www.w3.org/2001/XMLSchema}boolean'."))

        result = XsBoolean.cast("2", XsUntypedAtomic)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'Q{http://www.w3.org/2001/XMLSchema}boolean'."))

        result = XsBoolean.cast("True", XsUntypedAtomic)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'Q{http://www.w3.org/2001/XMLSchema}boolean'."))

        result = XsBoolean.cast(2, XsUntypedAtomic)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'Q{http://www.w3.org/2001/XMLSchema}boolean'."))
    }

    fun testXsBoolean_FromIncompatiblePrimitiveType() {
        var result: XdmTypeCastResult

        result = XsBoolean.cast(Date(), XsDate)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(XPTY0004))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("Incompatible types when casting 'Q{http://www.w3.org/2001/XMLSchema}date' to 'Q{http://www.w3.org/2001/XMLSchema}boolean'."))
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
                `is`("The value does not match the lexical representation for 'Q{http://www.w3.org/2001/XMLSchema}boolean'."))

        result = XdmOptional(XsBoolean).cast("2", XsString)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'Q{http://www.w3.org/2001/XMLSchema}boolean'."))

        result = XdmOptional(XsBoolean).cast("True", XsString)
        assertThat(result.type, `is`(FnError as XdmSequenceType))
        assertThat(result.value, `is`(instanceOf(FnErrorObject::class.java)))
        assertThat((result.value as FnErrorObject).code, `is`(FORG0001))
        assertThat((result.value as FnErrorObject).description?.staticType, `is`(XsString as XdmSequenceType))
        assertThat((result.value as FnErrorObject).description?.staticValue as String,
                `is`("The value does not match the lexical representation for 'Q{http://www.w3.org/2001/XMLSchema}boolean'."))
    }

    // endregion
}
