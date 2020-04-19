/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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

import com.intellij.compat.testFramework.PlatformLiteFixture
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.impl.XDebuggerUtilImpl
import org.hamcrest.CoreMatchers.*
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.xdebugger.QuerySourcePosition
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.toMarkLogicQueryError
import uk.co.reecedunn.intellij.plugin.processor.database.DatabaseModule

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("IntelliJ - Base Platform - Run Configuration - XQuery Processor - MarkLogicQueryError")
class MarkLogicQueryErrorTest : PlatformLiteFixture() {
    @BeforeAll
    override fun setUp() {
        super.setUp()
        initApplication()

        registerApplicationService(XDebuggerUtil::class.java, XDebuggerUtilImpl())
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    @DisplayName("XQuery error code")
    fun xqueryErrorCode() {
        @Language("xml")
        val exception = """
            <err:error xmlns:err="http://www.w3.org/2005/xqt-errors" xmlns:error="http://marklogic.com/xdmp/error">
                <err:code>err:XPST0003</err:code>
                <err:vendor-code>XDMP-UNEXPECTED</err:vendor-code>
                <err:description>Unexpected token</err:description>
                <err:value count="0"/>
                <err:module line="1" column="6"/>
                <error:stack>
                    <error:frame><error:line>1</error:line><error:column>5</error:column></error:frame>
                </error:stack>
            </err:error>
        """

        val e = exception.toMarkLogicQueryError(DatabaseModule("test.xqy"))
        assertThat(e.standardCode, `is`("XPST0003"))
        assertThat(e.vendorCode, `is`("XDMP-UNEXPECTED"))
        assertThat(e.description, `is`("Unexpected token"))
        assertThat(e.frames[0].sourcePosition?.file, `is`(DatabaseModule("test.xqy")))
        assertThat(e.frames[0].sourcePosition?.line, `is`(0))
        assertThat((e.frames[0].sourcePosition as QuerySourcePosition).column, `is`(5))
    }

    @Test
    @DisplayName("MarkLogic error code")
    fun marklogicErrorCode() {
        @Language("xml")
        val exception = """
            <err:error xmlns:err="http://www.w3.org/2005/xqt-errors" xmlns:error="http://marklogic.com/xdmp/error">
                <err:code>err:FOER0000</err:code>
                <err:vendor-code>XDMP-XQUERYVERSIONSWITCH</err:vendor-code>
                <err:description>All modules in a module sequence must use the same XQuery version</err:description>
                <err:value count="0"/>
                <err:module line="1" column="53"/>
                <error:stack>
                    <error:frame><error:line>1</error:line><error:column>52</error:column></error:frame>
                </error:stack>
            </err:error>
        """

        val e = exception.toMarkLogicQueryError(DatabaseModule("test.xqy"))
        assertThat(e.standardCode, `is`("FOER0000"))
        assertThat(e.vendorCode, `is`("XDMP-XQUERYVERSIONSWITCH"))
        assertThat(e.description, `is`("All modules in a module sequence must use the same XQuery version"))
        assertThat(e.frames[0].sourcePosition?.file, `is`(DatabaseModule("test.xqy")))
        assertThat(e.frames[0].sourcePosition?.line, `is`(0))
        assertThat((e.frames[0].sourcePosition as QuerySourcePosition).column, `is`(52))
    }
}
