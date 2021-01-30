/*
 * Copyright (C) 2018-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xdm.types.impl.values

import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.xdm.types.XsDurationValue
import java.math.BigDecimal
import java.math.BigInteger

data class XsDuration(
    override val months: XsInteger,
    override val seconds: XsDecimal
) : XsDurationValue {
    override fun compareTo(other: XsDurationValue): Int {
        val compared = months.data.compareTo(other.months.data)
        return if (compared == 0)
            seconds.data.compareTo(other.seconds.data)
        else
            compared
    }

    companion object {
        val ZERO: XsDuration = XsDuration(XsInteger.ZERO, XsDecimal.ZERO)

        fun s(value: String): XsDuration = XsDuration(XsInteger.ZERO, XsDecimal.value(value))

        fun ms(value: String): XsDuration = XsDuration(XsInteger.ZERO, XsDecimal.milli(value))

        fun ns(value: Long): XsDuration = XsDuration(XsInteger.ZERO, XsDecimal.nano(value))
    }
}

@Suppress("RegExpAnonymousGroup")
private val RE_INVALID_PARTIAL_DURATION = """^-?P(.*T)?${'$'}""".toRegex()

@Suppress("RegExpRepeatedSpace", "RegExpAnonymousGroup")
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
