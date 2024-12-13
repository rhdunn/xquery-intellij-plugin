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
package uk.co.reecedunn.intellij.plugin.existdb.tests.log

import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.extensions.PluginId
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.execution.ui.ConsoleViewRecorder
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.existdb.log.Log4JLogLine
import uk.co.reecedunn.intellij.plugin.existdb.log.Log4JPattern
import uk.co.reecedunn.intellij.plugin.processor.log.LogFileContentType

@Suppress("Reformat")
@DisplayName("IntelliJ - Base Platform - Run Configuration - Query Log - eXist-db Log4J logs")
class Log4JLogLineTest : IdeaPlatformTestCase() {
    override val pluginId: PluginId = PluginId.getId("Log4JLogLineTest")

    @Nested
    @DisplayName("default eXist-db pattern")
    inner class DefaultEXistDBPattern {
        private val pattern = Log4JPattern.create(Log4JPattern.DEFAULT_EXISTDB_PATTERN)

        @Test
        @DisplayName("empty")
        fun empty() {
            val line = ""
            assertThat(pattern.parse(line), `is`(line))
        }

        @Test
        @DisplayName("Java exception")
        fun javaException() {
            val lines = arrayOf(
                "\tat java.lang.System.initProperties(Native Method)",
                "\tat java.lang.System.initializeSystemClass(System.java:1166)"
            )

            assertThat(pattern.parse(lines[0]), `is`(lines[0]))
            assertThat(pattern.parse(lines[1]), `is`(lines[1]))
        }

        @Test
        @DisplayName("log line")
        fun logLine() {
            val line = "2020-11-29 09:48:44,592 [db.exist.scheduler.quartz-worker-4] INFO  (BTree.java [printStatistics]:2660) - values.dbx INDEX Buffers occupation : 11% (7 out of 64) Cache efficiency : 100% "
            val logLine = pattern.parse(line) as Log4JLogLine

            assertThat(logLine.date, `is`("2020-11-29"))
            assertThat(logLine.time, `is`("09:48:44,592"))
            assertThat(logLine.thread, `is`("db.exist.scheduler.quartz-worker-4"))
            assertThat(logLine.logLevel, `is`("INFO"))
            assertThat(logLine.filename, `is`("BTree.java"))
            assertThat(logLine.method, `is`("printStatistics"))
            assertThat(logLine.line, `is`(2660))
            assertThat(logLine.message, `is`("values.dbx INDEX Buffers occupation : 11% (7 out of 64) Cache efficiency : 100% "))

            val console = ConsoleViewRecorder()
            logLine.print(console)
            assertThat(console.printed[0], `is`(LogFileContentType.DATE_TIME to "2020-11-29 09:48:44,592"))
            assertThat(console.printed[1], `is`(ConsoleViewContentType.NORMAL_OUTPUT to " ["))
            assertThat(console.printed[2], `is`(LogFileContentType.THREAD to "db.exist.scheduler.quartz-worker-4"))
            assertThat(console.printed[3], `is`(ConsoleViewContentType.NORMAL_OUTPUT to "] "))
            assertThat(console.printed[4], `is`(LogFileContentType.INFO to "INFO  (BTree.java [printStatistics]:2660) - values.dbx INDEX Buffers occupation : 11% (7 out of 64) Cache efficiency : 100% "))
            assertThat(console.printed.size, `is`(5))
        }

        @Test
        @DisplayName("log line without trailing space")
        fun logLineWithoutTrailingSpace() {
            val line = "2020-11-29 09:48:44,592 [db.exist.scheduler.quartz-worker-4] INFO  (BTree.java [printStatistics]:2660) - values.dbx INDEX Buffers occupation : 11% (7 out of 64) Cache efficiency : 100%"
            val logLine = pattern.parse(line) as Log4JLogLine

            assertThat(logLine.date, `is`("2020-11-29"))
            assertThat(logLine.time, `is`("09:48:44,592"))
            assertThat(logLine.thread, `is`("db.exist.scheduler.quartz-worker-4"))
            assertThat(logLine.logLevel, `is`("INFO"))
            assertThat(logLine.filename, `is`("BTree.java"))
            assertThat(logLine.method, `is`("printStatistics"))
            assertThat(logLine.line, `is`(2660))
            assertThat(logLine.message, `is`("values.dbx INDEX Buffers occupation : 11% (7 out of 64) Cache efficiency : 100%"))

            val console = ConsoleViewRecorder()
            logLine.print(console)
            assertThat(console.printed[0], `is`(LogFileContentType.DATE_TIME to "2020-11-29 09:48:44,592"))
            assertThat(console.printed[1], `is`(ConsoleViewContentType.NORMAL_OUTPUT to " ["))
            assertThat(console.printed[2], `is`(LogFileContentType.THREAD to "db.exist.scheduler.quartz-worker-4"))
            assertThat(console.printed[3], `is`(ConsoleViewContentType.NORMAL_OUTPUT to "] "))
            assertThat(console.printed[4], `is`(LogFileContentType.INFO to "INFO  (BTree.java [printStatistics]:2660) - values.dbx INDEX Buffers occupation : 11% (7 out of 64) Cache efficiency : 100%"))
            assertThat(console.printed.size, `is`(5))
        }

        @Test
        @DisplayName("log levels")
        fun logLevels() {
            val lines = listOf(
                "2001-01-10 12:34:56.789 [thread] DEBUG (LogLevel.java [test]:12) - Lorem ipsum dolor ",
                "2001-01-10 12:34:56.789 [thread] INFO  (LogLevel.java [test]:12) - Lorem ipsum dolor ",
                "2001-01-10 12:34:56.789 [thread] WARN  (LogLevel.java [test]:12) - Lorem ipsum dolor ",
                "2001-01-10 12:34:56.789 [thread] ERROR (LogLevel.java [test]:12) - Lorem ipsum dolor ",
                "2001-01-10 12:34:56.789 [thread] FATAL (LogLevel.java [test]:12) - Lorem ipsum dolor ",
                "2001-01-10 12:34:56.789 [thread] UNKN  (LogLevel.java [test]:12) - Lorem ipsum dolor " // unknown
            )

            val console = ConsoleViewRecorder()
            lines.forEach {
                (pattern.parse(it) as Log4JLogLine).print(console)
            }

            assertThat(console.printed[4], `is`(LogFileContentType.DEBUG to "DEBUG (LogLevel.java [test]:12) - Lorem ipsum dolor "))
            assertThat(console.printed[9], `is`(LogFileContentType.INFO to "INFO  (LogLevel.java [test]:12) - Lorem ipsum dolor "))
            assertThat(console.printed[14], `is`(LogFileContentType.WARNING to "WARN  (LogLevel.java [test]:12) - Lorem ipsum dolor "))
            assertThat(console.printed[19], `is`(LogFileContentType.ERROR to "ERROR (LogLevel.java [test]:12) - Lorem ipsum dolor "))
            assertThat(console.printed[24], `is`(LogFileContentType.FATAL to "FATAL (LogLevel.java [test]:12) - Lorem ipsum dolor "))
            assertThat(console.printed[29], `is`(ConsoleViewContentType.NORMAL_OUTPUT to "UNKN  (LogLevel.java [test]:12) - Lorem ipsum dolor "))
        }
    }

    @Nested
    @DisplayName("unknown pattern")
    inner class UnknownPattern {
        private val pattern = Log4JPattern.create("%z")

        @Test
        @DisplayName("empty")
        fun empty() {
            val line = ""
            assertThat(pattern.parse(line), `is`(line))
        }

        @Test
        @DisplayName("Java exception")
        fun javaException() {
            val lines = arrayOf(
                "\tat java.lang.System.initProperties(Native Method)",
                "\tat java.lang.System.initializeSystemClass(System.java:1166)"
            )

            assertThat(pattern.parse(lines[0]), `is`(lines[0]))
            assertThat(pattern.parse(lines[1]), `is`(lines[1]))
        }

        @Test
        @DisplayName("log line")
        fun logLine() {
            val line = "2020-11-29 09:48:44,592 [db.exist.scheduler.quartz-worker-4] INFO  (BTree.java [printStatistics]:2660) - values.dbx INDEX Buffers occupation : 11% (7 out of 64) Cache efficiency : 100% "
            assertThat(pattern.parse(line), `is`(line))
        }
    }
}
