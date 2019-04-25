/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
/**
 * XPath 3.1 and XQuery 3.1 Type System Part 3: Atomic Types
 *
 * Immediate descendants of XsAnyAtomicType are the primitive types defined in
 * XPath and XQuery Functions and Operators 3.1 (19.1). This is:
 *   1.  The 19 primitive types from XML Schema 1.1 (3.3);
 *   2.  xs:untypedAtomic;
 *   3.  xs:integer;
 *   4.  xs:yearMonthDuration;
 *   5.  xs:dayTimeDuration.
 *
 * This is for two reasons. The first reason is that it makes it easier to
 * identify primitive types (they directly implement XsAnyAtomicType). The
 * second reason is that each primitive type has a different representation
 * for its data.
 */
package uk.co.reecedunn.intellij.plugin.xpath.model

import com.intellij.psi.PsiElement
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.core.text.Units
import java.lang.ref.WeakReference
import java.math.BigDecimal
import java.math.BigInteger
import java.text.NumberFormat

// region XML Schema 1.1 Part 2 (3.3.1) xs:string

interface XsStringValue : XsAnyAtomicType {
    val data: String

    val element: PsiElement?
}

data class XsString(
    override val data: String,
    private val reference: WeakReference<PsiElement>?
) : XsStringValue {
    constructor(data: String, element: PsiElement?) : this(data, element?.let { WeakReference(it) })

    override val element get(): PsiElement? = reference?.get()
}

// endregion
// region XML Schema 1.1 Part 2 (3.3.3) xs:decimal

interface XsDecimalValue : XsAnyAtomicType {
    val data: BigDecimal
}

data class XsDecimal(override val data: BigDecimal) : XsDecimalValue {
    companion object {
        fun milli(value: String): XsDecimal = XsDecimal(BigDecimal(value).scaleByPowerOfTen(-3).stripTrailingZeros())
        fun nano(value: Long): XsDecimal = XsDecimal(BigDecimal.valueOf(value, 9).stripTrailingZeros())
    }
}

// endregion
// region XML Schema 1.1 Part 2 (3.3.5) xs:double

interface XsDoubleValue : XsAnyAtomicType {
    val data: Double
}

// endregion
// region XML Schema 1.1 Part 2 (3.3.6) xs:duration

interface XsDurationValue : XsAnyAtomicType {
    val months: XsInteger
    val seconds: XsDecimal
}

fun XsDurationValue.toSeconds(precision: NumberFormat = Units.Precision.milli): String {
    return "${precision.format(seconds.data)} s"
}

data class XsDuration(
    override val months: XsInteger,
    override val seconds: XsDecimal
) : XsDurationValue {
    companion object {
        fun ms(value: String): XsDuration = XsDuration(XsInteger.ZERO, XsDecimal.milli(value))
        fun ns(value: Long): XsDuration = XsDuration(XsInteger.ZERO, XsDecimal.nano(value))
    }
}

private val RE_INVALID_PARTIAL_DURATION = """^-?P(.*T)?${'$'}""".toRegex()

@Suppress("RegExpRepeatedSpace")
private val RE_DURATION = """^
    (-)?
    P (([0-9]+)Y)? # years
      (([0-9]+)M)? # months
      (([0-9]+)D)? # days
    (T(([0-9]+)H)? # hours
      (([0-9]+)M)? # minutes
      (([0-9]+(\.[0-9]+)?)S)? # seconds
    )?
${'$'}""".toRegex(RegexOption.COMMENTS)

private val MONTHS_PER_YEAR = 12.toBigInteger()
private val HOURS_PER_DAY = 24.toBigInteger()
private val MINUTES_PER_HOUR = 60.toBigInteger()
private val SECONDS_PER_MINUTE = 60.toBigInteger()

private val SECONDS_PER_DAY = HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE
private val SECONDS_PER_HOUR = MINUTES_PER_HOUR * SECONDS_PER_MINUTE

fun String.toXsDuration(): XsDurationValue? {
    if (RE_INVALID_PARTIAL_DURATION.matchEntire(this) != null) return null
    return RE_DURATION.matchEntire(this)?.let {
        val yearsAsMonths = (it.groupValues[3].nullize()?.toBigInteger() ?: BigInteger.ZERO) * MONTHS_PER_YEAR
        val months = it.groupValues[5].nullize()?.toBigInteger() ?: BigInteger.ZERO
        val daysAsSeconds = (it.groupValues[7].nullize()?.toBigInteger() ?: BigInteger.ZERO) * SECONDS_PER_DAY
        val hoursAsSeconds = (it.groupValues[10].nullize()?.toBigInteger() ?: BigInteger.ZERO) * SECONDS_PER_HOUR
        val minutesAsSeconds = (it.groupValues[12].nullize()?.toBigInteger() ?: BigInteger.ZERO) * SECONDS_PER_MINUTE
        val seconds = it.groupValues[14].nullize()?.toBigDecimal() ?: BigDecimal.ZERO

        val monthDuration = yearsAsMonths + months
        val secondDuration = (daysAsSeconds + hoursAsSeconds + minutesAsSeconds).toBigDecimal() + seconds
        if (it.groupValues[1] == "-")
            XsDuration(XsInteger(-monthDuration), XsDecimal(-secondDuration))
        else
            XsDuration(XsInteger(monthDuration), XsDecimal(secondDuration))
    }
}

// endregion
// region XML Schema 1.1 Part 2 (3.3.17) xs:anyURI

interface XsAnyUriValue : XsAnyAtomicType {
    val data: String

    val element: PsiElement?
}

data class XsAnyUri(
    override val data: String,
    private val reference: WeakReference<PsiElement>?
) : XsAnyUriValue {
    constructor(data: String, element: PsiElement?) : this(data, element?.let { WeakReference(it) })

    override val element get(): PsiElement? = reference?.get()
}

// endregion
// region XML Schema 1.1 Part 2 (3.3.18) xs:QName

interface XsQNameValue : XsAnyAtomicType {
    val namespace: XsAnyUriValue?
    val prefix: XsNCNameValue?
    val localName: XsNCNameValue?
    val isLexicalQName: Boolean

    val element: PsiElement?
}

data class XsQName(
    override val namespace: XsAnyUriValue?,
    override val prefix: XsNCNameValue?,
    override val localName: XsNCNameValue?,
    override val isLexicalQName: Boolean,
    private val reference: WeakReference<PsiElement>?
) : XsQNameValue {
    constructor(
        namespace: XsAnyUriValue?,
        prefix: XsNCNameValue?,
        localName: XsNCNameValue?,
        isLexicalQName: Boolean,
        element: PsiElement?
    ) : this(namespace, prefix, localName, isLexicalQName, element?.let { WeakReference(it) })

    override val element get(): PsiElement? = reference?.get()
}

// endregion
// region XML Schema 1.1 Part 2 (3.4.1) xs:normalizedString

interface XsNormalizedStringValue : XsStringValue

// endregion
// region XML Schema 1.1 Part 2 (3.4.2) xs:token

interface XsTokenValue : XsNormalizedStringValue

// endregion
// region XML Schema 1.1 Part 2 (3.4.6) xs:Name

interface XsNameValue : XsTokenValue

// endregion
// region XML Schema 1.1 Part 2 (3.4.7) xs:NCName

interface XsNCNameValue : XsNameValue

data class XsNCName(
    override val data: String,
    private val reference: WeakReference<PsiElement>?
) : XsNCNameValue {
    constructor(data: String, element: PsiElement?) : this(data, element?.let { WeakReference(it) })

    override val element get(): PsiElement? = reference?.get()
}

// endregion
// region XML Schema 1.1 Part 2 (3.4.13) xs:integer

interface XsIntegerValue : XsAnyAtomicType {
    val data: BigInteger
}

fun XsIntegerValue.toInt(): Int = data.toInt()

data class XsInteger(override val data: BigInteger) : XsIntegerValue {
    companion object {
        val ZERO = XsInteger(BigInteger.ZERO)
    }
}

// endregion
// region XQuery IntelliJ Plugin (2.2.3) xdm:wildcard

interface XdmWildcardValue : XsNCNameValue

// endregion
