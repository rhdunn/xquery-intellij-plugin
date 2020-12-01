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
import uk.co.reecedunn.intellij.plugin.processor.log.LogFileContentType

object Log4JDefaultEXistDBPattern : Log4JPattern {
    override fun parse(line: String): Any {
        val groups = LOG_LINE_RE.find(line)?.groupValues ?: return line
        return Log4JLogLine(
            groups[1],
            groups[2],
            groups[3],
            groups[4],
            groups[5],
            groups[6],
            groups[7].toInt(),
            groups[8],
            this
        )
    }

    override fun print(consoleView: ConsoleView, line: Log4JLogLine) {
        consoleView.print("${line.date} ${line.time} ", LogFileContentType.DATE_TIME)
        consoleView.print(
            "[${line.thread}] ${line.logLevel.padEnd(5)} (${line.filename} [${line.method}]:${line.line}) - ${line.message}",
            line.contentType
        )
    }

    @Suppress("RegExpAnonymousGroup", "RegExpRepeatedSpace")
    private val LOG_LINE_RE = """^
        ([0-9\\-]+)               # 1: Date
        \s                        #
        ([0-9:.,]+)               # 2: Time
        \s                        #
        \[([^]]+)]                # 3: Thread
        \s                        #
        ([A-Za-z]+)               # 4: Log Level
        \s+                       #
        \(                        #
        ([^\s]+)                  # 5: Filename
        \s                        #
        \[([^]]+)]:               # 6: Method
        ([0-9]+)                  # 7: Line
        \)                        #
        \s-\s                     #
        (.*)                      # 8: Message
    ${'$'}""".toRegex(RegexOption.COMMENTS)
}
