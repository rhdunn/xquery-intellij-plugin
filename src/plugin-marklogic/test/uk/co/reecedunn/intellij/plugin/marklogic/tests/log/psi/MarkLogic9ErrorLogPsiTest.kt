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

import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.util.elementType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.marklogic.log.ast.error.MarkLogicErrorLog
import uk.co.reecedunn.intellij.plugin.marklogic.log.ast.error.MarkLogicErrorLogLine
import uk.co.reecedunn.intellij.plugin.marklogic.log.lang.MarkLogicErrorLog.ParserDefinition
import uk.co.reecedunn.intellij.plugin.marklogic.log.lexer.MarkLogicErrorLogTokenType

@Suppress("Reformat", "ClassName", "RedundantVisibilityModifier")
@DisplayName("MarkLogic 8.0 ErrorLog")
class MarkLogic9ErrorLogPsiTest : ParsingTestCase<MarkLogicErrorLog>("log", ParserDefinition()) {
    override val pluginId: PluginId = PluginId.getId("MarkLogic9ErrorLogPsiTest")

    @Test
    @DisplayName("Java exception")
    fun javaException() {
        val lines = parse<MarkLogicErrorLogLine>(
            "WARNING: JNI local refs: zu, exceeds capacity: zu",
            "\tat java.lang.System.initProperties(Native Method)"
        )

        assertThat(lines[0].text, `is`("WARNING: JNI local refs: zu, exceeds capacity: zu"))
        assertThat(lines[0].logLevel?.elementType, `is`(MarkLogicErrorLogTokenType.LogLevel.WARNING))

        assertThat(lines[1].text, `is`("\tat java.lang.System.initProperties(Native Method)"))
        assertThat(lines[1].logLevel?.elementType, `is`(nullValue()))
    }

    @Test
    @DisplayName("simple message")
    fun simpleMessage() {
        val lines = parse<MarkLogicErrorLogLine>(
            "2001-01-10 12:34:56.789 Info: Lorem ipsum dolor",
            "2001-01-10 12:34:56.789 Info: Sed emit"
        )

        assertThat(lines[0].text, `is`("2001-01-10 12:34:56.789 Info: Lorem ipsum dolor"))
        assertThat(lines[0].logLevel?.elementType, `is`(MarkLogicErrorLogTokenType.LogLevel.INFO))

        assertThat(lines[1].text, `is`("2001-01-10 12:34:56.789 Info: Sed emit"))
        assertThat(lines[1].logLevel?.elementType, `is`(MarkLogicErrorLogTokenType.LogLevel.INFO))
    }
}
