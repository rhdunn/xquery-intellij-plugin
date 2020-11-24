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
import uk.co.reecedunn.intellij.plugin.processor.log.LogLine

data class MarkLogicErrorLogLine(
    val date: String,
    val time: String,
    val logLevel: String,
    val appServer: String?,
    val message: String
) : LogLine {
    override fun print(consoleView: ConsoleView) {
        when (appServer) {
            null -> consoleView.print("$date $time $logLevel: $message", ConsoleViewContentType.NORMAL_OUTPUT)
            else -> consoleView.print("$date $time $logLevel: $appServer: $message", ConsoleViewContentType.NORMAL_OUTPUT)
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
            \s                        #
            (.*)                      # 6: Message
        ${'$'}""".toRegex(RegexOption.COMMENTS)

        fun parse(line: String): Any {
            val groups = LOG_LINE_RE.find(line)?.groupValues ?: return line
            val appServer = groups[5].takeIf { it.isNotEmpty() }
            return MarkLogicErrorLogLine(groups[1], groups[2], groups[3], appServer, groups[6])
        }
    }
}
