/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.tests.log.psi

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.marklogic.log.ast.error.MarkLogicErrorLog
import uk.co.reecedunn.intellij.plugin.marklogic.log.ast.error.MarkLogicErrorLogLine
import uk.co.reecedunn.intellij.plugin.marklogic.log.lang.MarkLogicErrorLog.ParserDefinition
import uk.co.reecedunn.intellij.plugin.marklogic.log.lexer.MarkLogicErrorLogTokenType

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("Reformat", "ClassName", "RedundantVisibilityModifier")
@DisplayName("MarkLogic 8.0 ErrorLog")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
private class MarkLogic9ErrorLogPsiTest : ParsingTestCase<MarkLogicErrorLog>("log", ParserDefinition()) {
    @BeforeAll
    override fun setUp() = super.setUp()

    @AfterAll
    override fun tearDown() = super.tearDown()

    @Test
    @DisplayName("message only")
    fun messageOnly() {
        val line = parse<MarkLogicErrorLogLine>(
            "\tat java.lang.System.initProperties(Native Method)"
        )[0]

        assertThat(line.logLevel, `is`(nullValue()))
    }

    @Test
    @DisplayName("log level (warning) and message only")
    fun logLevelAndMessage() {
        val line = parse<MarkLogicErrorLogLine>(
            "WARNING: JNI local refs: zu, exceeds capacity: zu"
        )[0]

        assertThat(line.logLevel, `is`(MarkLogicErrorLogTokenType.LogLevel.WARNING))
    }

    @Test
    @DisplayName("simple message")
    fun simpleMessage() {
        val line = parse<MarkLogicErrorLogLine>(
            "2001-01-10 12:34:56.789 Info: Lorem ipsum dolor"
        )[0]

        assertThat(line.logLevel, `is`(MarkLogicErrorLogTokenType.LogLevel.INFO))
    }
}
