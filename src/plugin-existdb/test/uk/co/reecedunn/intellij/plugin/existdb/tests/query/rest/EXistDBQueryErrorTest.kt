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
package uk.co.reecedunn.intellij.plugin.existdb.tests.query.rest

import uk.co.reecedunn.intellij.plugin.core.extensions.registerServiceInstance
import com.intellij.mock.MockFileTypeManager
import com.intellij.mock.MockLanguageFileType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.testFramework.LightVirtualFile
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.impl.XDebuggerUtilImpl
import org.hamcrest.CoreMatchers.*
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.existdb.query.rest.toEXistDBQueryError
import uk.co.reecedunn.intellij.plugin.processor.debug.position.QuerySourcePosition
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery

@Suppress("Reformat")
@DisplayName("IntelliJ - Base Platform - Run Configuration - XQuery Processor - EXistDBQueryError")
class EXistDBQueryErrorTest : IdeaPlatformTestCase() {
    override val pluginId: PluginId = PluginId.getId("EXistDBQueryErrorTest")

    override fun registerServicesAndExtensions() {
        val app = ApplicationManager.getApplication()
        app.registerServiceInstance(XDebuggerUtil::class.java, XDebuggerUtilImpl())

        val fileType = MockLanguageFileType(XQuery, "xq")
        app.registerServiceInstance(FileTypeManager::class.java, MockFileTypeManager(fileType))
    }

    @Test
    @DisplayName("XPathException")
    fun xpathException() {
        @Language("xml")
        val exception = """<?xml version="1.0" ?>
            <exception>
                <path>/db</path>
                <message>exerr:ERROR org.exist.xquery.XPathException: err:XPST0003 unexpected token: null [at line 1, column 7]
</message>
            </exception>
        """

        val testFile = LightVirtualFile("test.xq", XQuery, "1")
        val e = exception.toEXistDBQueryError(testFile)
        assertThat(e.standardCode, `is`("XPST0003"))
        assertThat(e.vendorCode, `is`(nullValue()))
        assertThat(e.description, `is`("unexpected token: null"))
        assertThat(e.frames[0].sourcePosition?.file, `is`(sameInstance(testFile)))
        assertThat(e.frames[0].sourcePosition?.line, `is`(0))
        assertThat((e.frames[0].sourcePosition as QuerySourcePosition).column, `is`(6))
    }

    @Test
    @DisplayName("type error")
    fun typeError() {
        @Language("xml")
        val exception = """<?xml version="1.0" ?>
            <exception>
                <path>/db</path>
                <message>exerr:ERROR Type: xs:dateTimeStamp is not defined</message>
            </exception>
        """

        val testFile = LightVirtualFile("test.xq", XQuery, "1")
        val e = exception.toEXistDBQueryError(testFile)
        assertThat(e.standardCode, `is`("FOER0000"))
        assertThat(e.vendorCode, `is`(nullValue()))
        assertThat(e.description, `is`("xs:dateTimeStamp is not defined"))
        assertThat(e.frames[0].sourcePosition?.file, `is`(sameInstance(testFile)))
        assertThat(e.frames[0].sourcePosition?.line, `is`(0))
        assertThat((e.frames[0].sourcePosition as QuerySourcePosition).column, `is`(0))
    }

    @Test
    @DisplayName("single line source")
    fun singleLineSource() {
        @Language("xml")
        val exception = """<?xml version="1.0" ?>
            <exception>
                <path>/db</path>
                <message>err:XPTY0004 Too many operands at the left of * [at line 1, column 11, source: (1, 2, 3) * 2]</message>
            </exception>
        """

        val testFile = LightVirtualFile("test.xq", XQuery, "1")
        val e = exception.toEXistDBQueryError(testFile)
        assertThat(e.standardCode, `is`("XPTY0004"))
        assertThat(e.vendorCode, `is`(nullValue()))
        assertThat(e.description, `is`("Too many operands at the left of *"))
        assertThat(e.frames[0].sourcePosition?.file, `is`(sameInstance(testFile)))
        assertThat(e.frames[0].sourcePosition?.line, `is`(0))
        assertThat((e.frames[0].sourcePosition as QuerySourcePosition).column, `is`(10))
    }

    @Test
    @DisplayName("multiple line source")
    fun multipleLineSource() {
        @Language("xml")
        val exception = """<?xml version="1.0" ?>
            <exception>
                <path>/db</path>
                <message>err:XPTY0004 Too many operands at the left of * [at line 1, column 11, source: (
    1,
    2,
    3
) * 2]</message>
            </exception>
        """

        val testFile = LightVirtualFile("test.xq", XQuery, "1")
        val e = exception.toEXistDBQueryError(testFile)
        assertThat(e.standardCode, `is`("XPTY0004"))
        assertThat(e.vendorCode, `is`(nullValue()))
        assertThat(e.description, `is`("Too many operands at the left of *"))
        assertThat(e.frames[0].sourcePosition?.file, `is`(sameInstance(testFile)))
        assertThat(e.frames[0].sourcePosition?.line, `is`(0))
        assertThat((e.frames[0].sourcePosition as QuerySourcePosition).column, `is`(10))
    }

    @Test
    @DisplayName("HTML 400 Error")
    fun html400Error() {
        @Suppress("HtmlRequiredLangAttribute")
        @Language("html")
        val exception =
            """
            <html>
            <head>
            <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
            <title>Error 400 Error while serializing xml: java.lang.ArrayIndexOutOfBoundsException</title>
            </head>
            <body><h2>HTTP ERROR 400 Error while serializing xml: java.lang.ArrayIndexOutOfBoundsException</h2>
            <table>
            <tr><th>URI:</th><td>/exist/rest/db</td></tr>
            <tr><th>STATUS:</th><td>400</td></tr>
            <tr><th>MESSAGE:</th><td>Error while serializing xml: java.lang.ArrayIndexOutOfBoundsException</td></tr>
            <tr><th>SERVLET:</th><td>EXistServlet</td></tr>
            </table>
            <hr><a href="http://eclipse.org/jetty">Powered by Jetty:// 9.4.26.v20200117</a><hr/>

            </body>
            </html>
            """.trimIndent()

        val testFile = LightVirtualFile("test.xq", XQuery, "1")
        val e = exception.toEXistDBQueryError(testFile)
        assertThat(e.standardCode, `is`("FOER0000"))
        assertThat(e.vendorCode, `is`(nullValue()))
        assertThat(e.description, `is`("Error 400 Error while serializing xml: java.lang.ArrayIndexOutOfBoundsException"))
        assertThat(e.frames.size, `is`(0))
    }
}
