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

import com.intellij.xdebugger.frame.presentation.XNumericValuePresentation
import com.intellij.xdebugger.frame.presentation.XRegularValuePresentation
import com.intellij.xdebugger.frame.presentation.XStringValuePresentation

object QueryValuePresentation {
    internal const val SEPARATOR = " := "

    fun forValue(value: String, type: String? = null): XRegularValuePresentation {
        return XRegularValuePresentation(value, type, SEPARATOR)
    }

    fun forString(value: String, type: String): XStringValuePresentation = StringValuePresentation(value, type)

    fun forNumeric(value: String, type: String): XNumericValuePresentation = NumericValuePresentation(value, type)
}
