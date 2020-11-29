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
package uk.co.reecedunn.intellij.plugin.existdb.log

import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import uk.co.reecedunn.intellij.plugin.processor.log.LogFileContentType
import uk.co.reecedunn.intellij.plugin.processor.log.LogLine

class Log4JLogLine(
    override val date: String,
    override val time: String,
    val thread: String,
    override val logLevel: String,
    val filename: String,
    val method: String,
    val line: Int,
    override val message: String,
    private val pattern: Log4JPattern
) : LogLine {

    val contentType: ConsoleViewContentType
        get() = when (logLevel) {
            "DEBUG" -> LogFileContentType.DEBUG
            "INFO" -> LogFileContentType.INFO
            "WARN" -> LogFileContentType.WARNING
            "ERROR" -> LogFileContentType.ERROR
            "FATAL" -> LogFileContentType.FATAL
            else -> ConsoleViewContentType.NORMAL_OUTPUT
        }

    override fun print(consoleView: ConsoleView) = pattern.print(consoleView, this)
}
