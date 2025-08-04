// Copyright (C) 2018-2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.basex.tests.query.session

import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import com.intellij.mock.MockFileTypeManager
import com.intellij.mock.MockLanguageFileType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.testFramework.LightVirtualFile
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.impl.XDebuggerUtilImpl
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.basex.query.session.toBaseXQueryError
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.processor.debug.position.QuerySourcePosition
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery

@DisplayName("IntelliJ - Base Platform - Run Configuration - XQuery Processor - BaseXQueryError")
class BaseXQueryErrorTest : IdeaPlatformTestCase() {
    override val pluginId: PluginId = PluginId.getId("BaseXQueryErrorTest")

    override fun registerServicesAndExtensions() {
        val app = ApplicationManager.getApplication()
        app.registerService<XDebuggerUtil>(XDebuggerUtilImpl())

        val fileType = MockLanguageFileType(XQuery, "xq")
        app.registerService<FileTypeManager>(MockFileTypeManager(fileType))
    }

    @Test
    @DisplayName("at runtime; BaseX 8.0")
    fun runtime80() {
        val testFile = LightVirtualFile("test.xq", XQuery, "1 div")
        val e = "Stopped at ., 1/6:\r\n[XPST0003] Calculation is incomplete.".toBaseXQueryError(testFile)
        assertThat(e.standardCode, `is`("XPST0003"))
        assertThat(e.vendorCode, `is`(nullValue()))
        assertThat(e.description, `is`("Calculation is incomplete."))
        assertThat(e.frames[0].sourcePosition?.file, `is`(sameInstance(testFile)))
        assertThat(e.frames[0].sourcePosition?.line, `is`(0))
        assertThat((e.frames[0].sourcePosition as QuerySourcePosition).column, `is`(5))
    }

    @Test
    @DisplayName("with context; BaseX 7.0")
    fun runtime70() {
        val testFile = LightVirtualFile("test.xq", XQuery, "1 div")
        val e = "Stopped at line 1, column 5:\r\n[XPST0003] Calculation is incomplete.".toBaseXQueryError(testFile)
        assertThat(e.standardCode, `is`("XPST0003"))
        assertThat(e.vendorCode, `is`(nullValue()))
        assertThat(e.description, `is`("Calculation is incomplete."))
        assertThat(e.frames[0].sourcePosition?.file, `is`(sameInstance(testFile)))
        assertThat(e.frames[0].sourcePosition?.line, `is`(0))
        assertThat((e.frames[0].sourcePosition as QuerySourcePosition).column, `is`(4))
    }

    @Test
    @DisplayName("bind context; unknown type")
    fun bindContextUnknownType() {
        // Bind context to unknown type -- "[]" as "array-node()".
        val testFile = LightVirtualFile("test.xq", XQuery, "1")
        val e = "[XPST0003] Unknown type: array-node().".toBaseXQueryError(testFile)
        assertThat(e.standardCode, `is`("XPST0003"))
        assertThat(e.vendorCode, `is`(nullValue()))
        assertThat(e.description, `is`("Unknown type: array-node()."))
        assertThat(e.frames[0].sourcePosition?.file, `is`(sameInstance(testFile)))
        assertThat(e.frames[0].sourcePosition?.line, `is`(0))
        assertThat((e.frames[0].sourcePosition as QuerySourcePosition).column, `is`(0))
    }
}
