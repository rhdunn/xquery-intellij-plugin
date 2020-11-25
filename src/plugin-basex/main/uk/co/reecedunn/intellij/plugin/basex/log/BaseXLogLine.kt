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
package uk.co.reecedunn.intellij.plugin.basex.log

import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import uk.co.reecedunn.intellij.plugin.processor.log.LogLevel
import uk.co.reecedunn.intellij.plugin.processor.log.LogLine

data class BaseXLogLine(
    val date: String,
    val time: String,
    val elapsed: String?,
    val address: String,
    val user: String,
    val logLevel: String,
    val message: String
) : LogLine {

    val contentType: ConsoleViewContentType
        get() = when (logLevel) {
            "OK" -> LogLevel.OK
            "ERROR" -> LogLevel.ERROR
            "REQUEST" -> LogLevel.REQUEST
            else -> ConsoleViewContentType.NORMAL_OUTPUT
        }

    override fun print(consoleView: ConsoleView) {
        when (elapsed) {
            null -> consoleView.print("$date $time $address $user $logLevel: $message", contentType)
            else -> consoleView.print("$date $time in ${elapsed}ms $address $user $logLevel: $message", contentType)
        }
    }
}
