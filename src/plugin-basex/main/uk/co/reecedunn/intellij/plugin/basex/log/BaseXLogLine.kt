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
import uk.co.reecedunn.intellij.plugin.processor.log.LogFileContentType
import uk.co.reecedunn.intellij.plugin.processor.log.LogLine

data class BaseXLogLine(
    val date: String,
    val time: String,
    val elapsed: String?,
    val address: String,
    val user: String,
    override val logLevel: String,
    override val message: String
) : LogLine {

    val contentType: ConsoleViewContentType
        get() = when (logLevel) {
            "OK" -> LogFileContentType.OK
            "ERROR" -> LogFileContentType.ERROR
            "REQUEST" -> LogFileContentType.REQUEST
            else -> ConsoleViewContentType.NORMAL_OUTPUT
        }

    override fun print(consoleView: ConsoleView) {
        consoleView.print("$date $time ", LogFileContentType.DATE_TIME)
        when (elapsed) {
            null -> consoleView.print("$address $user $logLevel: $message", contentType)
            else -> consoleView.print("in ${elapsed}ms $address $user $logLevel: $message", contentType)
        }
    }
}
