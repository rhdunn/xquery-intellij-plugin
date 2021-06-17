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
package uk.co.reecedunn.intellij.plugin.marklogic.tests.xray.format

import com.intellij.compat.testFramework.registerServiceInstance
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.vfs.encoding.EncodingManager
import com.intellij.openapi.vfs.encoding.EncodingManagerImpl
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.impl.XDebuggerUtilImpl
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.editor.MockEditorFactoryEx
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFileSystem
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.marklogic.xray.format.XRayTestFormat
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.test.TestResult
import uk.co.reecedunn.intellij.plugin.processor.test.TestStatistics
import uk.co.reecedunn.intellij.plugin.processor.test.TestSuites
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsDecimal
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.values.XsInteger
import java.math.BigDecimal

@DisplayName("XRay Unit Tests - Text Output Format")
class XRayTextFormatTest : IdeaPlatformTestCase() {
    override val pluginId: PluginId = PluginId.getId("XRayTextFormatTest")

    private val res = ResourceVirtualFileSystem(this::class.java.classLoader)

    private fun parse(resource: String): TestSuites {
        val text = res.findFileByPath(resource)?.decode()!!
        val value = QueryResult(0, text, "xs:string", QueryResult.TEXT_PLAIN)
        return XRayTestFormat.format("text").parse(value)!!
    }

    override fun registerServicesAndExtensions() {
        val app = ApplicationManager.getApplication()
        app.registerServiceInstance(XDebuggerUtil::class.java, XDebuggerUtilImpl())
        app.registerServiceInstance(EditorFactory::class.java, MockEditorFactoryEx())
        app.registerServiceInstance(EncodingManager::class.java, EncodingManagerImpl())
    }

    @Nested
    @DisplayName("test suites")
    internal inner class Suites {
        @Test
        @DisplayName("empty")
        fun empty() {
            val tests = parse("xray/format/text/empty.txt")
            assertThat(tests.passed, `is`(0))
            assertThat(tests.failed, `is`(0))
            assertThat(tests.ignored, `is`(0))
            assertThat(tests.errors, `is`(0))
            assertThat(tests.total, `is`(0))

            assertThat(tests.testSuites.count(), `is`(0))
        }

        @Test
        @DisplayName("single suite")
        fun single() {
            val tests = parse("xray/format/text/single.txt")
            assertThat(tests.passed, `is`(1))
            assertThat(tests.failed, `is`(0))
            assertThat(tests.ignored, `is`(0))
            assertThat(tests.errors, `is`(0))
            assertThat(tests.total, `is`(1))

            assertThat(tests.testSuites.count(), `is`(1))
        }
    }

    @Nested
    @DisplayName("test suite")
    internal inner class Suite {
        @Test
        @DisplayName("single test case")
        fun single() {
            val tests = parse("xray/format/text/single.txt")
            val suite = tests.testSuites.first()

            assertThat(suite.name, `is`("/xray/test/default-fn-namespace.xqy"))
            assertThat(suite.error, `is`(nullValue()))
            assertThat(suite.testCases.count(), `is`(1))

            assertThat(suite, `is`(not(instanceOf(TestStatistics::class.java))))
        }
    }

    @Nested
    @DisplayName("test case")
    internal inner class Case {
        @Test
        @DisplayName("passed test")
        fun passed() {
            val tests = parse("xray/format/text/test-cases.txt")
            val suite = tests.testSuites.first()
            val case = suite.testCases.elementAt(3)

            assertThat(case.name, `is`("passing-test"))
            assertThat(case.result, `is`(TestResult.Passed))
            assertThat(case.duration, `is`(nullValue()))
            assertThat(case.asserts.count(), `is`(0))
            assertThat(case.error, `is`(nullValue()))
        }

        @Test
        @DisplayName("failed test")
        fun failed() {
            val tests = parse("xray/format/text/test-cases.txt")
            val suite = tests.testSuites.first()
            val case = suite.testCases.elementAt(1)

            assertThat(case.name, `is`("failing-test"))
            assertThat(case.result, `is`(TestResult.Failed))
            assertThat(case.duration, `is`(nullValue()))
            assertThat(case.asserts.count(), `is`(1))
            assertThat(case.error, `is`(nullValue()))
        }

        @Test
        @DisplayName("ignored test")
        fun ignored() {
            val tests = parse("xray/format/text/test-cases.txt")
            val suite = tests.testSuites.first()
            val case = suite.testCases.elementAt(2)

            assertThat(case.name, `is`("ignored-test"))
            assertThat(case.result, `is`(TestResult.Ignored))
            assertThat(case.duration, `is`(nullValue()))
            assertThat(case.asserts.count(), `is`(0))
            assertThat(case.error, `is`(nullValue()))
        }

        @Test
        @DisplayName("exception")
        fun exception() {
            val tests = parse("xray/format/text/test-cases.txt")
            val suite = tests.testSuites.first()
            val case = suite.testCases.elementAt(0)

            assertThat(case.name, `is`("exception"))
            assertThat(case.result, `is`(TestResult.Error))
            assertThat(case.duration, `is`(nullValue()))
            assertThat(case.asserts.count(), `is`(0))

            val e = case.error as QueryError
            assertThat(e.standardCode, `is`("FOER0000"))
            assertThat(e.vendorCode, `is`(nullValue()))
            assertThat(e.description, `is`("error"))
        }
    }
}
