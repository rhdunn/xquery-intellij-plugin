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
package uk.co.reecedunn.intellij.plugin.marklogic.log

import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import uk.co.reecedunn.intellij.plugin.processor.log.LogLevel
import uk.co.reecedunn.intellij.plugin.processor.log.LogLine

abstract class MarkLogicErrorLogLine(
    val date: String,
    val time: String,
    val logLevel: String,
    val appServer: String?,
    val continuation: Boolean
) : LogLine {

    abstract val message: String

    val contentType: ConsoleViewContentType
        get() = when (logLevel) {
            "Finest" -> LogLevel.FINEST
            "Finer" -> LogLevel.FINER
            "Fine" -> LogLevel.FINE
            "Debug" -> LogLevel.DEBUG
            "Config" -> LogLevel.CONFIG
            "Info" -> LogLevel.INFO
            "Notice" -> LogLevel.NOTICE
            "Warning" -> LogLevel.WARNING
            "Error" -> LogLevel.ERROR
            "Critical" -> LogLevel.CRITICAL
            "Alert" -> LogLevel.ALERT
            "Emergency" -> LogLevel.EMERGENCY
            else -> ConsoleViewContentType.NORMAL_OUTPUT
        }

    override fun print(consoleView: ConsoleView) {
        val separator = if (continuation) '+' else ' '
        when (appServer) {
            null -> consoleView.print("$date $time $logLevel:$separator$message", contentType)
            else -> consoleView.print("$date $time $logLevel:$separator$appServer: $message", contentType)
        }
    }

    companion object {
        @Suppress("RegExpAnonymousGroup", "RegExpRepeatedSpace")
        private val LOG_LINE_RE = """^
            ([0-9\\-]+)               # 1: Date
            \s                        #
            ([0-9:.]+)                # 2: Time
            \s                        #
            ([A-Za-z]+):              # 3: Log Level
            (\s([a-zA-Z0-9\-_]+):)?   # 5: application server name
            ([\s+])                   # 6: MarkLogic 9.0 continuation
            (.*)                      # 7: Message
        ${'$'}""".toRegex(RegexOption.COMMENTS)

        @Suppress("RegExpRepeatedSpace")
        private val EXCEPTION_LOCATION_RE = """^
            in\s                       #
            ([^ ,]+)                   # 1: Exception Path
            ,\sat\s                    #
            ([0-9]+)                   # 2: Line
            :                          #
            ([0-9]+)                   # 3: Column
            (\s\[([0-9.\-ml]+)])?      # 5: XQuery Version
        ${'$'}""".toRegex(RegexOption.COMMENTS)

        fun parse(line: String): Any {
            val groups = LOG_LINE_RE.find(line)?.groupValues ?: return line
            return when (val exception = EXCEPTION_LOCATION_RE.find(groups[7])?.groupValues) {
                null -> MarkLogicErrorLogMessage(
                    groups[1],
                    groups[2],
                    groups[3],
                    groups[5].takeIf { it.isNotEmpty() },
                    groups[6] == "+",
                    groups[7]
                )
                else -> MarkLogicErrorLogExceptionLocation(
                    groups[1],
                    groups[2],
                    groups[3],
                    groups[5].takeIf { it.isNotEmpty() },
                    groups[6] == "+",
                    exception[1],
                    exception[2].toInt(),
                    exception[3].toInt(),
                    exception[5].takeIf { it.isNotEmpty() }
                )
            }
        }
    }
}
