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
package uk.co.reecedunn.intellij.plugin.processor.intellij.execution.testframework

import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessOutputType
import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.core.execution.testframework.TestProcessHandlerEvents
import uk.co.reecedunn.intellij.plugin.core.io.printCharsToString
import uk.co.reecedunn.intellij.plugin.core.math.toMilliseconds
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.process.QueryResultListener
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.process.QueryResultTime
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.test.*
import uk.co.reecedunn.intellij.plugin.xdm.types.XsDurationValue

open class TestProcessListener(
    processHandler: ProcessHandler,
    private val outputFormat: TestFormat,
    private val locationProvider: TestLocationProvider? = null
) :
    TestProcessHandlerEvents(processHandler),
    QueryResultListener {
    // region QueryResultListener

    override fun onBeginResults() {
        notifyTestsStarted()
    }

    override fun onEndResults(handler: (PsiFile) -> Unit) {
    }

    override fun onQueryResult(result: QueryResult) {
        outputFormat.parse(result)?.let { results ->
            results.testSuites.forEach { onTestSuite(it) }
            onTestsFinished(results)
        }
    }

    override fun onException(e: Throwable) {
        notifyTextAvailable(printCharsToString { e.printStackTrace(it) }, ProcessOutputType.STDERR)
    }

    override fun onQueryResultTime(resultTime: QueryResultTime, time: XsDurationValue) {
    }

    override fun onQueryResultsPsiFile(psiFile: PsiFile) {
    }

    // endregion
    // region TestProcessListener

    private fun onTestSuite(testsuite: TestSuite) {
        onTestSuiteStarted(testsuite)
        testsuite.error?.let {
            val name = testsuite.name.split("/").last()
            notifyTestStarted(name, locationHint = locationProvider?.locationHint(testsuite.name))
            notifyTestError(name, it)
            notifyTestFinished(name)
        }
        testsuite.testCases.forEach { onTestCase(it, testsuite) }
        onTestSuiteFinished(testsuite)
    }

    private fun onTestCase(testcase: TestCase, testsuite: TestSuite) {
        onTestStarted(testcase, testsuite)
        when (testcase.result) {
            TestResult.Passed -> onTestPassed(testcase)
            TestResult.Ignored -> onTestIgnored(testcase)
            TestResult.Failed -> onTestFailed(testcase)
            TestResult.Error -> onTestError(testcase)
        }
        onTestFinished(testcase)
    }

    open fun onTestSuiteStarted(testsuite: TestSuite) {
        notifyTestSuiteStarted(testsuite.name)
    }

    open fun onTestSuiteFinished(testsuite: TestSuite) {
        notifyTestSuiteFinished(testsuite.name)
    }

    open fun onTestStarted(test: TestCase, testsuite: TestSuite) {
        notifyTestStarted(test.name, locationHint = locationProvider?.locationHint(test.name, testsuite.name))
    }

    open fun onTestPassed(test: TestCase) {
    }

    open fun onTestIgnored(test: TestCase) {
        notifyTestIgnored(test.name)
    }

    open fun onTestFailed(test: TestCase) {
        test.failures.forEach { failures ->
            if (failures.result == TestResult.Failed) {
                notifyTestFailed(
                    test.name,
                    message = failures.message ?: "Assertion failure",
                    expected = failures.expected,
                    actual = failures.actual
                )
            }
        }
    }

    open fun onTestError(test: TestCase) {
        when (val error = test.error) {
            null -> notifyTestError(test.name, message = "")
            else -> notifyTestError(test.name, exception = error)
        }
    }

    open fun onTestFinished(test: TestCase) {
        notifyTestFinished(test.name, duration = test.duration?.seconds?.data?.toMilliseconds())
    }

    open fun onTestsFinished(results: TestStatistics) {
    }

    // endregion
}
