// Copyright (C) 2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.marklogic.tests.log.psi

import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.util.elementType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parse
import uk.co.reecedunn.intellij.plugin.core.tests.lang.requiresIFileElementTypeParseContents
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresPsiFileGetChildren
import uk.co.reecedunn.intellij.plugin.core.tests.psi.requiresVirtualFileToPsiFile
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.marklogic.log.ast.error.MarkLogicErrorLogLine
import uk.co.reecedunn.intellij.plugin.marklogic.log.fileTypes.MarkLogicErrorLogFileType
import uk.co.reecedunn.intellij.plugin.marklogic.log.lang.MarkLogicErrorLog
import uk.co.reecedunn.intellij.plugin.marklogic.log.lexer.MarkLogicErrorLogTokenType

@Suppress("Reformat", "ClassName", "RedundantVisibilityModifier")
@DisplayName("MarkLogic 8.0 ErrorLog")
class MarkLogic9ErrorLogPsiTest : IdeaPlatformTestCase(), LanguageTestCase {
    override val pluginId: PluginId = PluginId.getId("MarkLogic9ErrorLogPsiTest")
    override val language: Language = MarkLogicErrorLog

    override fun registerServicesAndExtensions() {
        requiresVirtualFileToPsiFile()
        requiresIFileElementTypeParseContents()
        requiresPsiFileGetChildren()

        MarkLogicErrorLog.ParserDefinition().registerExtension(project)
        MarkLogicErrorLogFileType.registerFileType()
    }

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
