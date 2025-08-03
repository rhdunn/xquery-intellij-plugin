// Copyright (C) 2021, 2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.marklogic.tests.xray.format

import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import com.intellij.compat.openapi.vfs.encoding.EncodingManagerImpl
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.vfs.encoding.EncodingManager
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.impl.XDebuggerUtilImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
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

@DisplayName("XRay Unit Tests - XUnit XML Output Format")
class XRayXUnitFormatTest : IdeaPlatformTestCase() {
    override val pluginId: PluginId = PluginId.getId("XRayXUnitFormatTest")

    private val res = ResourceVirtualFileSystem(this::class.java.classLoader)

    private fun parse(resource: String): TestSuites {
        val xml = res.findFileByPath(resource)?.decode()!!
        val value = QueryResult(0, xml, "document-node()", QueryResult.APPLICATION_XML)
        return XRayTestFormat.format("xunit").parse(value)!!
    }

    override fun registerServicesAndExtensions() {
        val app = ApplicationManager.getApplication()
        app.registerService(XDebuggerUtil::class.java, XDebuggerUtilImpl())
        app.registerService(EditorFactory::class.java, MockEditorFactoryEx())
        app.registerService(EncodingManager::class.java, EncodingManagerImpl(CoroutineScope(Dispatchers.IO)))
    }

    @Nested
    @DisplayName("test suites")
    internal inner class Suites {
        @Test
        @DisplayName("empty")
        fun empty() {
            val tests = parse("xray/format/xunit/empty.xml")
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
            val tests = parse("xray/format/xunit/single.xml")
            assertThat(tests.passed, `is`(1))
            assertThat(tests.failed, `is`(0))
            assertThat(tests.ignored, `is`(0))
            assertThat(tests.errors, `is`(0))
            assertThat(tests.total, `is`(1))

            assertThat(tests.testSuites.count(), `is`(1))
        }

        @Test
        @DisplayName("module exception")
        fun moduleException() {
            val tests = parse("xray/format/xunit/syntax-error.xml")
            assertThat(tests.passed, `is`(0))
            assertThat(tests.failed, `is`(0))
            assertThat(tests.ignored, `is`(0))
            assertThat(tests.errors, `is`(1))
            assertThat(tests.total, `is`(0))

            assertThat(tests.testSuites.count(), `is`(1))
        }
    }

    @Nested
    @DisplayName("test suite")
    internal inner class Suite {
        @Test
        @DisplayName("single test case")
        fun single() {
            val tests = parse("xray/format/xunit/single.xml")
            val suite = tests.testSuites.first()

            assertThat(suite.name, `is`("/xray/test/default-fn-namespace.xqy"))
            assertThat(suite.error, `is`(nullValue()))
            assertThat(suite.testCases.count(), `is`(1))

            val statistics = suite as TestStatistics
            assertThat(statistics.passed, `is`(1))
            assertThat(statistics.failed, `is`(0))
            assertThat(statistics.ignored, `is`(0))
            assertThat(statistics.errors, `is`(0))
            assertThat(statistics.total, `is`(1))
        }

        @Test
        @DisplayName("exception")
        fun exception() {
            val tests = parse("xray/format/xunit/syntax-error.xml")
            val suite = tests.testSuites.first()

            assertThat(suite.name, `is`("/xray/test/syntax-error.xqy"))
            assertThat(suite.testCases.count(), `is`(0))

            val statistics = suite as TestStatistics
            assertThat(statistics.passed, `is`(0))
            assertThat(statistics.failed, `is`(0))
            assertThat(statistics.ignored, `is`(0))
            assertThat(statistics.errors, `is`(1))
            assertThat(statistics.total, `is`(0))

            val e = suite.error as QueryError
            assertThat(e.standardCode, `is`("XPST0003"))
            assertThat(e.vendorCode, `is`("XDMP-UNEXPECTED"))
            assertThat(e.description, `is`("Unexpected token"))
        }
    }

    @Nested
    @DisplayName("test case")
    internal inner class Case {
        @Test
        @DisplayName("passed test")
        fun passed() {
            val tests = parse("xray/format/xunit/test-cases.xml")
            val suite = tests.testSuites.first()
            val case = suite.testCases.elementAt(3)

            assertThat(case.name, `is`("passing-test"))
            assertThat(case.result, `is`(TestResult.Passed))
            assertThat(case.duration?.months, `is`(XsInteger.ZERO))
            assertThat(case.duration?.seconds, `is`(XsDecimal(BigDecimal("0.0000662"))))
            assertThat(case.asserts.count(), `is`(0))
            assertThat(case.error, `is`(nullValue()))
        }

        @Test
        @DisplayName("failed test")
        fun failed() {
            val tests = parse("xray/format/xunit/test-cases.xml")
            val suite = tests.testSuites.first()
            val case = suite.testCases.elementAt(1)

            assertThat(case.name, `is`("failing-test"))
            assertThat(case.result, `is`(TestResult.Failed))
            assertThat(case.duration?.months, `is`(XsInteger.ZERO))
            assertThat(case.duration?.seconds, `is`(XsDecimal(BigDecimal("0.0001544"))))
            assertThat(case.asserts.count(), `is`(1))
            assertThat(case.error, `is`(nullValue()))
        }

        @Test
        @DisplayName("ignored test")
        fun ignored() {
            val tests = parse("xray/format/xunit/test-cases.xml")
            val suite = tests.testSuites.first()
            val case = suite.testCases.elementAt(2)

            assertThat(case.name, `is`("ignored-test"))
            assertThat(case.result, `is`(TestResult.Ignored))
            assertThat(case.duration?.months, `is`(XsInteger.ZERO))
            assertThat(case.duration?.seconds, `is`(XsDecimal(BigDecimal("0"))))
            assertThat(case.asserts.count(), `is`(0))
            assertThat(case.error, `is`(nullValue()))
        }

        @Test
        @DisplayName("exception")
        fun exception() {
            val tests = parse("xray/format/xunit/test-cases.xml")
            val suite = tests.testSuites.first()
            val case = suite.testCases.elementAt(0)

            assertThat(case.name, `is`("exception"))
            assertThat(case.result, `is`(TestResult.Error))
            assertThat(case.duration?.months, `is`(XsInteger.ZERO))
            assertThat(case.duration?.seconds, `is`(XsDecimal(BigDecimal("0.0007944"))))
            assertThat(case.asserts.count(), `is`(0))

            val e = case.error as QueryError
            assertThat(e.standardCode, `is`("FOER0000"))
            assertThat(e.vendorCode, `is`(nullValue()))
            assertThat(e.description, `is`("error"))
        }
    }

    @Nested
    @DisplayName("test assert")
    internal inner class Assert {
        @Test
        @DisplayName("failed assertion")
        fun failed() {
            val tests = parse("xray/format/xunit/test-cases.xml")
            val suite = tests.testSuites.first()
            val case = suite.testCases.elementAt(1)
            val assert = case.asserts.first()

            assertThat(assert.result, `is`(TestResult.Failed))
            assertThat(assert.type, `is`("equal"))
            assertThat(assert.expected, `is`("2"))
            assertThat(assert.actual, `is`("1"))
            assertThat(assert.message, `is`(nullValue()))
        }

        @Test
        @DisplayName("element() in expected and actual")
        fun element() {
            val tests = parse("xray/format/xunit/test-values.xml")
            val suite = tests.testSuites.first()
            val case = suite.testCases.elementAt(0)
            val assert = case.asserts.first()

            assertThat(assert.result, `is`(TestResult.Failed))
            assertThat(assert.type, `is`("equal"))
            assertThat(
                assert.expected, `is`(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<b lorem=\"ipsum\" xmlns=\"\"/>"
                )
            )
            assertThat(
                assert.actual, `is`(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<a lorem=\"ipsum\" xmlns=\"\"/>"
                )
            )
            assertThat(assert.message, `is`(nullValue()))
        }

        @Test
        @DisplayName("empty-sequence() in expected and actual")
        fun emptySequence() {
            val tests = parse("xray/format/xunit/test-values.xml")
            val suite = tests.testSuites.first()
            val case = suite.testCases.elementAt(1)

            var assert = case.asserts.elementAt(0)
            assertThat(assert.result, `is`(TestResult.Failed))
            assertThat(assert.type, `is`("equal"))
            assertThat(assert.expected, `is`("1"))
            assertThat(assert.actual, `is`(""))
            assertThat(assert.message, `is`(nullValue()))

            assert = case.asserts.elementAt(1)
            assertThat(assert.result, `is`(TestResult.Failed))
            assertThat(assert.type, `is`("equal"))
            assertThat(assert.expected, `is`(""))
            assertThat(assert.actual, `is`("2"))
            assertThat(assert.message, `is`(nullValue()))
        }

        @Test
        @DisplayName("mixed sequence in expected and actual")
        fun mixedSequence() {
            val tests = parse("xray/format/xunit/test-values.xml")
            val suite = tests.testSuites.first()
            val case = suite.testCases.elementAt(2)
            val assert = case.asserts.first()

            assertThat(assert.result, `is`(TestResult.Failed))
            assertThat(assert.type, `is`("equal"))
            assertThat(
                assert.expected, `is`(
                    """
                    |<?xml version="1.0" encoding="UTF-8"?>
                    |<a xmlns=""/>
                    |<?xml version="1.0" encoding="UTF-8"?>
                    |<d xmlns=""/>
                    |6 8
                    |<?xml version="1.0" encoding="UTF-8"?>
                    |<c xmlns=""/>
                    """.trimMargin()
                )
            )
            assertThat(
                assert.actual, `is`(
                    """
                    |<?xml version="1.0" encoding="UTF-8"?>
                    |<a xmlns=""/>
                    |<?xml version="1.0" encoding="UTF-8"?>
                    |<b xmlns=""/>
                    |3 4
                    |<?xml version="1.0" encoding="UTF-8"?>
                    |<c xmlns=""/>
                    """.trimMargin()
                )
            )
            assertThat(assert.message, `is`(nullValue()))
        }
    }
}
