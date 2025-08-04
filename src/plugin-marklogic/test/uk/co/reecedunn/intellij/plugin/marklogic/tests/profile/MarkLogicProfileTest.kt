// Copyright (C) 2019-2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.marklogic.tests.profile

import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import com.intellij.mock.MockFileTypeManager
import com.intellij.mock.MockLanguageFileType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.testFramework.LightVirtualFile
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.impl.XDebuggerUtilImpl
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.sameInstance
import org.hamcrest.MatcherAssert.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.marklogic.profile.toMarkLogicProfileReport
import uk.co.reecedunn.intellij.plugin.processor.debug.position.QuerySourcePosition
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsDecimal
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsInteger
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery

@Suppress("XmlPathReference")
@DisplayName("IntelliJ - Base Platform - Run Configuration - Query Profiler - MarkLogicProfile")
class MarkLogicProfileTest : IdeaPlatformTestCase() {
    override val pluginId: PluginId = PluginId.getId("MarkLogicProfileTest")

    override fun registerServicesAndExtensions() {
        val app = ApplicationManager.getApplication()
        app.registerService<XDebuggerUtil>(XDebuggerUtilImpl())

        val fileType = MockLanguageFileType(XQuery, "xq")
        app.registerService<FileTypeManager>(MockFileTypeManager(fileType))
    }

    @Test
    @DisplayName("empty")
    fun empty() {
        @Language("xml")
        val profile = """
            <prof:report xsi:schemaLocation="http://marklogic.com/xdmp/profile profile.xsd" xmlns:prof="http://marklogic.com/xdmp/profile" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                <prof:metadata>
                    <prof:overall-elapsed>PT0.0000564S</prof:overall-elapsed>
                    <prof:created>2019-01-03T09:44:37.9608193Z</prof:created>
                    <prof:server-version>9.0-5</prof:server-version>
                </prof:metadata>
                <prof:histogram/>
            </prof:report>
        """

        val testFile = LightVirtualFile("test.xq", XQuery, "()")
        val p = profile.toMarkLogicProfileReport(testFile)
        assertThat(p.elapsed.months, `is`(XsInteger.ZERO))
        assertThat(p.elapsed.seconds, `is`(XsDecimal("0.0000564".toBigDecimal())))
        assertThat(p.created, `is`("2019-01-03T09:44:37.9608193Z"))
        assertThat(p.version, `is`("9.0-5"))

        val results = p.results.toList()
        assertThat(results.size, `is`(1))

        assertThat(results[0].id, `is`(""))
        assertThat(results[0].context, `is`(""))
        assertThat(results[0].frame.sourcePosition?.file, `is`(sameInstance(testFile)))
        assertThat(results[0].frame.sourcePosition?.line, `is`(0))
        assertThat((results[0].frame.sourcePosition as QuerySourcePosition).column, `is`(0))
        assertThat(results[0].count, `is`(1))
        assertThat(results[0].selfTime.months, `is`(XsInteger.ZERO))
        assertThat(results[0].selfTime.seconds, `is`(XsDecimal.ZERO))
        assertThat(results[0].totalTime.months, `is`(XsInteger.ZERO))
        assertThat(results[0].totalTime.seconds, `is`(XsDecimal("0.0000564".toBigDecimal())))
    }

    @Test
    @DisplayName("results; no uri")
    fun results_noUri() {
        @Language("xml")
        val profile = """
            <prof:report xsi:schemaLocation="http://marklogic.com/xdmp/profile profile.xsd" xmlns:prof="http://marklogic.com/xdmp/profile" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                <prof:metadata>
                    <prof:overall-elapsed>PT0.0000435S</prof:overall-elapsed>
                    <prof:created>2019-01-03T10:50:34.2913686Z</prof:created>
                    <prof:server-version>9.0-5</prof:server-version>
                </prof:metadata>
                <prof:histogram>
                    <prof:expression>
                        <prof:expr-id>7399381704112208326</prof:expr-id>
                        <prof:expr-source>for ${'$'}x in 1 to 10 return ${'$'}x</prof:expr-source>
                        <prof:uri/>
                        <prof:line>1</prof:line>
                        <prof:column>0</prof:column>
                        <prof:count>1</prof:count>
                        <prof:shallow-time>PT0.0000051S</prof:shallow-time>
                        <prof:deep-time>PT0.0000064S</prof:deep-time>
                    </prof:expression>
                    <prof:expression>
                        <prof:expr-id>16683152708792260640</prof:expr-id>
                        <prof:expr-source>1 to 10</prof:expr-source>
                        <prof:uri/>
                        <prof:line>1</prof:line>
                        <prof:column>12</prof:column>
                        <prof:count>2</prof:count>
                        <prof:shallow-time>PT0.0000013S</prof:shallow-time>
                        <prof:deep-time>PT0.0000014S</prof:deep-time>
                    </prof:expression>
                </prof:histogram>
            </prof:report>
        """

        val testFile = LightVirtualFile("test.xq", XQuery, "()")
        val p = profile.toMarkLogicProfileReport(testFile)
        assertThat(p.elapsed.months, `is`(XsInteger.ZERO))
        assertThat(p.elapsed.seconds, `is`(XsDecimal("0.0000435".toBigDecimal())))
        assertThat(p.created, `is`("2019-01-03T10:50:34.2913686Z"))
        assertThat(p.version, `is`("9.0-5"))

        val results = p.results.toList()
        assertThat(results.size, `is`(3))

        assertThat(results[2].id, `is`("16683152708792260640"))
        assertThat(results[2].context, `is`("1 to 10"))
        assertThat(results[2].frame.sourcePosition?.file, `is`(sameInstance(testFile)))
        assertThat(results[2].frame.sourcePosition?.line, `is`(0))
        assertThat((results[2].frame.sourcePosition as QuerySourcePosition).column, `is`(12))
        assertThat(results[2].count, `is`(2))
        assertThat(results[2].selfTime.months, `is`(XsInteger.ZERO))
        assertThat(results[2].selfTime.seconds, `is`(XsDecimal("0.0000013".toBigDecimal())))
        assertThat(results[2].totalTime.months, `is`(XsInteger.ZERO))
        assertThat(results[2].totalTime.seconds, `is`(XsDecimal("0.0000014".toBigDecimal())))
    }
}
