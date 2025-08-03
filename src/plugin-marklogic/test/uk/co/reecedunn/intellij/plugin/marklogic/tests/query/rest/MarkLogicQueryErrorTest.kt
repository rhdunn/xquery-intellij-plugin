/*
 * Copyright (C) 2018-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.tests.query.rest

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
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.toMarkLogicQueryError
import uk.co.reecedunn.intellij.plugin.processor.debug.position.QuerySourcePosition
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery

@DisplayName("IntelliJ - Base Platform - Run Configuration - XQuery Processor - MarkLogicQueryError")
class MarkLogicQueryErrorTest : IdeaPlatformTestCase() {
    override val pluginId: PluginId = PluginId.getId("MarkLogicQueryErrorTest")

    override fun registerServicesAndExtensions() {
        val app = ApplicationManager.getApplication()
        app.registerService(XDebuggerUtil::class.java, XDebuggerUtilImpl())

        val fileType = MockLanguageFileType(XQuery, "xq")
        app.registerService(FileTypeManager::class.java, MockFileTypeManager(fileType))
    }

    @Test
    @DisplayName("XQuery error code")
    fun xqueryErrorCode() {
        @Language("xml")
        val exception = """
            <error:error xmlns:error="http://marklogic.com/xdmp/error">
                <error:code>XDMP-UNEXPECTED</error:code>
                <error:name>err:XPST0003</error:name>
                <error:message>Unexpected token</error:message>
                <error:format-string>XDMP-UNEXPECTED: (err:XPST0003) Unexpected token</error:format-string>
                <error:data/>
                <error:stack>
                    <error:frame><error:line>1</error:line><error:column>5</error:column></error:frame>
                </error:stack>
            </error:error>
        """

        val testFile = LightVirtualFile("test.xq", XQuery, "()")
        val e = exception.toMarkLogicQueryError(testFile)
        assertThat(e.standardCode, `is`("XPST0003"))
        assertThat(e.vendorCode, `is`("XDMP-UNEXPECTED"))
        assertThat(e.errorCode, `is`("XPST0003"))
        assertThat(e.description, `is`("Unexpected token"))
        assertThat(e.message, `is`("[XPST0003] Unexpected token"))
        assertThat(e.frames[0].sourcePosition?.file, `is`(sameInstance(testFile)))
        assertThat(e.frames[0].sourcePosition?.line, `is`(0))
        assertThat((e.frames[0].sourcePosition as QuerySourcePosition).column, `is`(5))
    }

    @Test
    @DisplayName("MarkLogic error code")
    fun marklogicErrorCode() {
        @Language("xml")
        val exception = """
            <error:error xmlns:error="http://marklogic.com/xdmp/error">
                <error:code>XDMP-XQUERYVERSIONSWITCH</error:code>
                <error:name>err:FOER0000</error:name>
                <error:message>All modules in a module sequence must use the same XQuery version</error:message>
                <error:format-string>XDMP-XQUERYVERSIONSWITCH: (err:FOER0000) All modules in a module sequence must use the same XQuery version</error:format-string>
                <error:data/>
                <error:stack>
                    <error:frame><error:line>1</error:line><error:column>52</error:column></error:frame>
                </error:stack>
            </error:error>
        """

        val testFile = LightVirtualFile("test.xq", XQuery, "()")
        val e = exception.toMarkLogicQueryError(testFile)
        assertThat(e.standardCode, `is`("FOER0000"))
        assertThat(e.vendorCode, `is`("XDMP-XQUERYVERSIONSWITCH"))
        assertThat(e.errorCode, `is`("XDMP-XQUERYVERSIONSWITCH"))
        assertThat(e.description, `is`("All modules in a module sequence must use the same XQuery version"))
        assertThat(e.message, `is`("[XDMP-XQUERYVERSIONSWITCH] All modules in a module sequence must use the same XQuery version"))
        assertThat(e.frames[0].sourcePosition?.file, `is`(sameInstance(testFile)))
        assertThat(e.frames[0].sourcePosition?.line, `is`(0))
        assertThat((e.frames[0].sourcePosition as QuerySourcePosition).column, `is`(52))
    }

    @Test
    @DisplayName("fn:error")
    fun fnError() {
        @Language("xml")
        val exception = """
            <error:error xmlns:error="http://marklogic.com/xdmp/error">
                <error:code>An error message</error:code>
                <error:name>err:FOER0000</error:name>
                <error:message>An error message</error:message>
                <error:format-string>(err:FOAR0001) Division by zero</error:format-string>
                <error:data/>
                <error:stack>
                    <error:frame><error:line>1</error:line><error:column>5</error:column></error:frame>
                </error:stack>
            </error:error>
        """

        val testFile = LightVirtualFile("test.xq", XQuery, "()")
        val e = exception.toMarkLogicQueryError(testFile)
        assertThat(e.standardCode, `is`("FOER0000"))
        assertThat(e.vendorCode, `is`(nullValue()))
        assertThat(e.errorCode, `is`("FOER0000"))
        assertThat(e.description, `is`("An error message"))
        assertThat(e.message, `is`("[FOER0000] An error message"))
        assertThat(e.frames[0].sourcePosition?.file, `is`(sameInstance(testFile)))
        assertThat(e.frames[0].sourcePosition?.line, `is`(0))
        assertThat((e.frames[0].sourcePosition as QuerySourcePosition).column, `is`(5))
    }

    @Test
    @DisplayName("syntax error")
    fun syntaxError() {
        @Language("xml")
        val exception = """
            <error:error xmlns:error="http://marklogic.com/xdmp/error">
                <error:code>XDMP-UNEXPECTED</error:code>
                <error:name>err:XPST0003</error:name>
                <error:message>Unexpected token</error:message>
                <error:format-string>XDMP-UNEXPECTED: (err:XPST0003) Unexpected token syntax error, unexpected ${'$'}end, expecting Function30_ or Percent_</error:format-string>
                <error:data>
                    <error:datum>syntax error, unexpected ${'$'}end, expecting Function30_ or Percent_</error:datum>
                    <error:datum/>
                </error:data>
                <error:stack>
                    <error:frame><error:line>1</error:line><error:column>5</error:column></error:frame>
                </error:stack>
            </error:error>
        """

        val testFile = LightVirtualFile("test.xq", XQuery, "()")
        val e = exception.toMarkLogicQueryError(testFile)
        assertThat(e.standardCode, `is`("XPST0003"))
        assertThat(e.vendorCode, `is`("XDMP-UNEXPECTED"))
        assertThat(e.errorCode, `is`("XPST0003"))
        assertThat(e.description, `is`("Unexpected token"))
        assertThat(e.message, `is`("[XPST0003] Unexpected token"))
        assertThat(e.frames[0].sourcePosition?.file, `is`(sameInstance(testFile)))
        assertThat(e.frames[0].sourcePosition?.line, `is`(0))
        assertThat((e.frames[0].sourcePosition as QuerySourcePosition).column, `is`(5))
    }
}
