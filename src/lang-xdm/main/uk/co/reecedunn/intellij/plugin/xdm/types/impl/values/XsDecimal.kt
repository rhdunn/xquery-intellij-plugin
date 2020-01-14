/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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

import uk.co.reecedunn.intellij.plugin.xdm.types.XsDecimalValue
import java.math.BigDecimal

data class XsDecimal(override val data: BigDecimal) : XsDecimalValue {
    companion object {
        val ZERO = XsDecimal(BigDecimal.ZERO)

        fun milli(value: String): XsDecimal = XsDecimal(BigDecimal(value).scaleByPowerOfTen(-3).stripTrailingZeros())

        fun nano(value: Long): XsDecimal = XsDecimal(BigDecimal.valueOf(value, 9).stripTrailingZeros())
    }
}
