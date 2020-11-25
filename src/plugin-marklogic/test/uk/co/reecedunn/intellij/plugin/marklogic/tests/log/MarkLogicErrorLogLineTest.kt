/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.tests.log

import com.intellij.openapi.extensions.PluginId
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.execution.ui.ConsoleViewRecorder
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.marklogic.log.MarkLogicErrorLogExceptionLocation
import uk.co.reecedunn.intellij.plugin.marklogic.log.MarkLogicErrorLogLine

@Suppress("XmlPathReference")
@DisplayName("IntelliJ - Base Platform - Run Configuration - Query Log - MarkLogic ErrorLog")
class MarkLogicErrorLogLineTest : IdeaPlatformTestCase() {
    override val pluginId: PluginId = PluginId.getId("MarkLogicErrorLogLineTest")

    @Test
    @DisplayName("empty")
    fun empty() {
        val line = ""
        assertThat(MarkLogicErrorLogLine.parse(line), `is`(line))
    }

    @Test
    @DisplayName("Java exception")
    fun javaException() {
        val lines = arrayOf(
            "WARNING: JNI local refs: zu, exceeds capacity: zu",
            "\tat java.lang.System.initProperties(Native Method)",
            "\tat java.lang.System.initializeSystemClass(System.java:1166)"
        )

        assertThat(MarkLogicErrorLogLine.parse(lines[0]), `is`(lines[0]))
        assertThat(MarkLogicErrorLogLine.parse(lines[1]), `is`(lines[1]))
        assertThat(MarkLogicErrorLogLine.parse(lines[2]), `is`(lines[2]))
    }

    @Test
    @DisplayName("simple message (MarkLogic >= 9.0)")
    fun simpleMessage() {
        val line = "2001-01-10 12:34:56.789 Info: Lorem ipsum dolor"
        val logLine = MarkLogicErrorLogLine.parse(line) as MarkLogicErrorLogLine

        assertThat(logLine.date, `is`("2001-01-10"))
        assertThat(logLine.time, `is`("12:34:56.789"))
        assertThat(logLine.logLevel, `is`("Info"))
        assertThat(logLine.appServer, `is`(nullValue()))
        assertThat(logLine.continuation, `is`(false))
        assertThat(logLine.message, `is`("Lorem ipsum dolor"))

        val console = ConsoleViewRecorder()
        logLine.print(console)
        assertThat(console.printed, `is`("[QUERY_LOG_LEVEL_INFO|$line]"))
    }

    @Test
    @DisplayName("message with TaskServer (MarkLogic <= 8.0)")
    fun messageWithTaskServer() {
        val line = "2001-01-10 12:34:56.789 Debug: TaskServer: Lorem ipsum dolor"
        val logLine = MarkLogicErrorLogLine.parse(line) as MarkLogicErrorLogLine

        assertThat(logLine.date, `is`("2001-01-10"))
        assertThat(logLine.time, `is`("12:34:56.789"))
        assertThat(logLine.logLevel, `is`("Debug"))
        assertThat(logLine.appServer, `is`("TaskServer"))
        assertThat(logLine.continuation, `is`(false))
        assertThat(logLine.message, `is`("Lorem ipsum dolor"))

        val console = ConsoleViewRecorder()
        logLine.print(console)
        assertThat(console.printed, `is`("[QUERY_LOG_LEVEL_DEBUG|$line]"))
    }

    @Test
    @DisplayName("message with AppServer (MarkLogic <= 8.0)")
    fun messageWithAppServer() {
        val line = "2001-01-10 12:34:56.789 Debug: abc-2d_3e: Lorem ipsum dolor"
        val logLine = MarkLogicErrorLogLine.parse(line) as MarkLogicErrorLogLine

        assertThat(logLine.date, `is`("2001-01-10"))
        assertThat(logLine.time, `is`("12:34:56.789"))
        assertThat(logLine.logLevel, `is`("Debug"))
        assertThat(logLine.appServer, `is`("abc-2d_3e"))
        assertThat(logLine.continuation, `is`(false))
        assertThat(logLine.message, `is`("Lorem ipsum dolor"))

        val console = ConsoleViewRecorder()
        logLine.print(console)
        assertThat(console.printed, `is`("[QUERY_LOG_LEVEL_DEBUG|$line]"))
    }

    @Test
    @DisplayName("message continuation (MarkLogic >= 9.0)")
    fun messageContinuation() {
        val line = "2001-01-10 12:34:56.789 Info:+Lorem ipsum dolor"
        val logLine = MarkLogicErrorLogLine.parse(line) as MarkLogicErrorLogLine

        assertThat(logLine.date, `is`("2001-01-10"))
        assertThat(logLine.time, `is`("12:34:56.789"))
        assertThat(logLine.logLevel, `is`("Info"))
        assertThat(logLine.appServer, `is`(nullValue()))
        assertThat(logLine.continuation, `is`(true))
        assertThat(logLine.message, `is`("Lorem ipsum dolor"))

        val console = ConsoleViewRecorder()
        logLine.print(console)
        assertThat(console.printed, `is`("[QUERY_LOG_LEVEL_INFO|$line]"))
    }

    @Test
    @DisplayName("log levels")
    fun logLevels() {
        val lines = listOf(
            "2001-01-10 12:34:56.789 Finest: Lorem ipsum dolor",
            "2001-01-10 12:34:56.789 Finer: Lorem ipsum dolor",
            "2001-01-10 12:34:56.789 Fine: Lorem ipsum dolor",
            "2001-01-10 12:34:56.789 Debug: Lorem ipsum dolor",
            "2001-01-10 12:34:56.789 Config: Lorem ipsum dolor",
            "2001-01-10 12:34:56.789 Info: Lorem ipsum dolor",
            "2001-01-10 12:34:56.789 Notice: Lorem ipsum dolor",
            "2001-01-10 12:34:56.789 Warning: Lorem ipsum dolor",
            "2001-01-10 12:34:56.789 Error: Lorem ipsum dolor",
            "2001-01-10 12:34:56.789 Critical: Lorem ipsum dolor",
            "2001-01-10 12:34:56.789 Alert: Lorem ipsum dolor",
            "2001-01-10 12:34:56.789 Emergency: Lorem ipsum dolor",
            "2001-01-10 12:34:56.789 Unknown: Lorem ipsum dolor"
        )

        val logLines = lines.map {
            val console = ConsoleViewRecorder()
            (MarkLogicErrorLogLine.parse(it) as MarkLogicErrorLogLine).print(console)
            console.printed
        }

        assertThat(logLines[0], `is`("[QUERY_LOG_LEVEL_FINEST|${lines[0]}]"))
        assertThat(logLines[1], `is`("[QUERY_LOG_LEVEL_FINER|${lines[1]}]"))
        assertThat(logLines[2], `is`("[QUERY_LOG_LEVEL_FINE|${lines[2]}]"))
        assertThat(logLines[3], `is`("[QUERY_LOG_LEVEL_DEBUG|${lines[3]}]"))
        assertThat(logLines[4], `is`("[QUERY_LOG_LEVEL_CONFIG|${lines[4]}]"))
        assertThat(logLines[5], `is`("[QUERY_LOG_LEVEL_INFO|${lines[5]}]"))
        assertThat(logLines[6], `is`("[QUERY_LOG_LEVEL_NOTICE|${lines[6]}]"))
        assertThat(logLines[7], `is`("[QUERY_LOG_LEVEL_WARNING|${lines[7]}]"))
        assertThat(logLines[8], `is`("[QUERY_LOG_LEVEL_ERROR|${lines[8]}]"))
        assertThat(logLines[9], `is`("[QUERY_LOG_LEVEL_CRITICAL|${lines[9]}]"))
        assertThat(logLines[10], `is`("[QUERY_LOG_LEVEL_ALERT|${lines[10]}]"))
        assertThat(logLines[11], `is`("[QUERY_LOG_LEVEL_EMERGENCY|${lines[11]}]"))
        assertThat(logLines[12], `is`("[NORMAL_OUTPUT|${lines[12]}]"))
    }

    @Test
    @DisplayName("exception location with XQuery version")
    fun exceptionLocationWithXQueryVersion() {
        val line = "2001-01-10 12:34:56.789 Notice:+in /lorem/ipsum/dolor.xqy, at 14:8 [1.0-ml]"
        val logLine = MarkLogicErrorLogLine.parse(line) as MarkLogicErrorLogExceptionLocation

        assertThat(logLine.date, `is`("2001-01-10"))
        assertThat(logLine.time, `is`("12:34:56.789"))
        assertThat(logLine.logLevel, `is`("Notice"))
        assertThat(logLine.appServer, `is`(nullValue()))
        assertThat(logLine.continuation, `is`(true))
        assertThat(logLine.message, `is`("in /lorem/ipsum/dolor.xqy, at 14:8 [1.0-ml]"))
        assertThat(logLine.path, `is`("/lorem/ipsum/dolor.xqy"))
        assertThat(logLine.line, `is`(14))
        assertThat(logLine.column, `is`(8))
        assertThat(logLine.xqueryVersion, `is`("1.0-ml"))

        val console = ConsoleViewRecorder()
        logLine.print(console)
        assertThat(
            console.printed,
            `is`(
                "[QUERY_LOG_LEVEL_NOTICE|2001-01-10 12:34:56.789 Notice:+in ]" +
                "[link^/lorem/ipsum/dolor.xqy]" +
                "[QUERY_LOG_LEVEL_NOTICE|, at 14:8 [1.0-ml]]"
            )
        )
    }

    @Test
    @DisplayName("exception location without XQuery version")
    fun exceptionLocationWithoutXQueryVersion() {
        val line = "2001-01-10 12:34:56.789 Notice:+in /lorem/ipsum/dolor.xqy, at 14:8"
        val logLine = MarkLogicErrorLogLine.parse(line) as MarkLogicErrorLogExceptionLocation

        assertThat(logLine.date, `is`("2001-01-10"))
        assertThat(logLine.time, `is`("12:34:56.789"))
        assertThat(logLine.logLevel, `is`("Notice"))
        assertThat(logLine.appServer, `is`(nullValue()))
        assertThat(logLine.continuation, `is`(true))
        assertThat(logLine.message, `is`("in /lorem/ipsum/dolor.xqy, at 14:8"))
        assertThat(logLine.path, `is`("/lorem/ipsum/dolor.xqy"))
        assertThat(logLine.line, `is`(14))
        assertThat(logLine.column, `is`(8))
        assertThat(logLine.xqueryVersion, `is`(nullValue()))

        val console = ConsoleViewRecorder()
        logLine.print(console)
        assertThat(
            console.printed,
            `is`(
                "[QUERY_LOG_LEVEL_NOTICE|2001-01-10 12:34:56.789 Notice:+in ]" +
                "[link^/lorem/ipsum/dolor.xqy]" +
                "[QUERY_LOG_LEVEL_NOTICE|, at 14:8]"
            )
        )
    }
}
