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
package uk.co.reecedunn.intellij.plugin.xpath.tests.model

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.model.toXsDuration
import java.math.BigDecimal
import java.math.BigInteger

@DisplayName("XQuery 3.1 - Data Model - xs:duration")
class XsDurationTest {
    @Nested
    @DisplayName("string to duration")
    internal inner class StringToDuration {
        @Test
        @DisplayName("empty")
        fun empty() {
            assertThat("".toXsDuration(), `is`(nullValue()))
        }

        @Test
        @DisplayName("invalid")
        fun invalid() {
            assertThat("23".toXsDuration(), `is`(nullValue()))
            assertThat("-23".toXsDuration(), `is`(nullValue()))

            assertThat("P".toXsDuration(), `is`(nullValue()))
            assertThat("-P".toXsDuration(), `is`(nullValue()))

            assertThat("PT".toXsDuration(), `is`(nullValue()))
            assertThat("-PT".toXsDuration(), `is`(nullValue()))

            assertThat("P8YT".toXsDuration(), `is`(nullValue()))
            assertThat("-P3YT".toXsDuration(), `is`(nullValue()))

            assertThat("P31".toXsDuration(), `is`(nullValue())) // missing part type
            assertThat("-P31".toXsDuration(), `is`(nullValue())) // missing part type

            assertThat("P7H".toXsDuration(), `is`(nullValue())) // Missing 'T'
            assertThat("-P7H".toXsDuration(), `is`(nullValue())) // Missing 'T'

            assertThat("P31.5D".toXsDuration(), `is`(nullValue())) // decimal in non-decimal fragment
            assertThat("-P31.5D".toXsDuration(), `is`(nullValue())) // decimal in non-decimal fragment

            assertThat("PT3.S".toXsDuration(), `is`(nullValue())) // incomplete decimal
            assertThat("-PT3.S".toXsDuration(), `is`(nullValue())) // incomplete decimal
        }

        @Test
        @DisplayName("years only")
        fun yearsOnly() {
            val d1 = "P2Y".toXsDuration()!!
            assertThat(d1.months.data, `is`(24.toBigInteger()))
            assertThat(d1.seconds.data, `is`(BigDecimal.ZERO))

            val d2 = "-P5Y".toXsDuration()!!
            assertThat(d2.months.data, `is`((-60).toBigInteger()))
            assertThat(d2.seconds.data, `is`(BigDecimal.ZERO))
        }

        @Test
        @DisplayName("months only")
        fun monthsOnly() {
            val d1 = "P3M".toXsDuration()!!
            assertThat(d1.months.data, `is`(3.toBigInteger()))
            assertThat(d1.seconds.data, `is`(BigDecimal.ZERO))

            val d2 = "-P7M".toXsDuration()!!
            assertThat(d2.months.data, `is`((-7).toBigInteger()))
            assertThat(d2.seconds.data, `is`(BigDecimal.ZERO))
        }

        @Test
        @DisplayName("days only")
        fun daysOnly() {
            val d1 = "P3D".toXsDuration()!!
            assertThat(d1.months.data, `is`(BigInteger.ZERO))
            assertThat(d1.seconds.data, `is`(259200.toBigDecimal()))

            val d2 = "-P5D".toXsDuration()!!
            assertThat(d2.months.data, `is`(BigInteger.ZERO))
            assertThat(d2.seconds.data, `is`((-432000).toBigDecimal()))
        }

        @Test
        @DisplayName("hours only")
        fun hoursOnly() {
            val d1 = "PT2H".toXsDuration()!!
            assertThat(d1.months.data, `is`(BigInteger.ZERO))
            assertThat(d1.seconds.data, `is`(7200.toBigDecimal()))

            val d2 = "-PT4H".toXsDuration()!!
            assertThat(d2.months.data, `is`(BigInteger.ZERO))
            assertThat(d2.seconds.data, `is`((-14400).toBigDecimal()))
        }

        @Test
        @DisplayName("minutes only")
        fun minutesOnly() {
            val d1 = "PT5M".toXsDuration()!!
            assertThat(d1.months.data, `is`(BigInteger.ZERO))
            assertThat(d1.seconds.data, `is`(300.toBigDecimal()))

            val d2 = "-PT8M".toXsDuration()!!
            assertThat(d2.months.data, `is`(BigInteger.ZERO))
            assertThat(d2.seconds.data, `is`((-480).toBigDecimal()))
        }

        @Test
        @DisplayName("seconds only")
        fun secondsOnly() {
            val d1 = "PT12S".toXsDuration()!!
            assertThat(d1.months.data, `is`(BigInteger.ZERO))
            assertThat(d1.seconds.data, `is`(12.toBigDecimal()))

            val d2 = "-PT8S".toXsDuration()!!
            assertThat(d2.months.data, `is`(BigInteger.ZERO))
            assertThat(d2.seconds.data, `is`((-8).toBigDecimal()))
        }

        @Test
        @DisplayName("seconds and milliseconds")
        fun secondsAndMilliseconds() {
            val d1 = "PT11.000123S".toXsDuration()!!
            assertThat(d1.months.data, `is`(BigInteger.ZERO))
            assertThat(d1.seconds.data, `is`("11.000123".toBigDecimal()))

            val d2 = "-PT22.000525S".toXsDuration()!!
            assertThat(d2.months.data, `is`(BigInteger.ZERO))
            assertThat(d2.seconds.data, `is`("-22.000525".toBigDecimal()))
        }

        @Test
        @DisplayName("all month parts")
        fun allMonthParts() {
            val d1 = "P2Y5M".toXsDuration()!!
            assertThat(d1.months.data, `is`(29.toBigInteger()))
            assertThat(d1.seconds.data, `is`(BigDecimal.ZERO))

            val d2 = "-P4Y2M".toXsDuration()!!
            assertThat(d2.months.data, `is`((-50).toBigInteger()))
            assertThat(d2.seconds.data, `is`(BigDecimal.ZERO))
        }

        @Test
        @DisplayName("all seconds parts")
        fun allSecondsParts() {
            val d1 = "P2DT3H40M5.025S".toXsDuration()!!
            assertThat(d1.months.data, `is`(BigInteger.ZERO))
            assertThat(d1.seconds.data, `is`("186005.025".toBigDecimal()))

            val d2 = "-P4DT5H20M15.675S".toXsDuration()!!
            assertThat(d2.months.data, `is`(BigInteger.ZERO))
            assertThat(d2.seconds.data, `is`("-364815.675".toBigDecimal()))
        }

        @Test
        @DisplayName("all parts")
        fun allParts() {
            val d1 = "P2Y5M".toXsDuration()!!
            assertThat(d1.months.data, `is`(29.toBigInteger()))
            assertThat(d1.seconds.data, `is`(BigDecimal.ZERO))

            val d2 = "-P4Y2M".toXsDuration()!!
            assertThat(d2.months.data, `is`((-50).toBigInteger()))
            assertThat(d2.seconds.data, `is`(BigDecimal.ZERO))
        }
    }
}
