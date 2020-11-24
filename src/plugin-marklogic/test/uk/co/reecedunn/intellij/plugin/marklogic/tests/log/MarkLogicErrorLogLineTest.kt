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
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
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
}
