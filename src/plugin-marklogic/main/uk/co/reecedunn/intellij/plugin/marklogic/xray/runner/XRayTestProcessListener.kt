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
package uk.co.reecedunn.intellij.plugin.marklogic.xray.runner

import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessOutputType
import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.core.execution.testframework.TestProcessHandlerEvents
import uk.co.reecedunn.intellij.plugin.core.io.printCharsToString
import uk.co.reecedunn.intellij.plugin.core.math.toMilliseconds
import uk.co.reecedunn.intellij.plugin.processor.test.TestCase
import uk.co.reecedunn.intellij.plugin.processor.test.TestSuite
import uk.co.reecedunn.intellij.plugin.processor.test.TestResult
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.process.QueryResultListener
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.process.QueryResultTime
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.test.TestFormat
import uk.co.reecedunn.intellij.plugin.xdm.types.XsDurationValue

class XRayTestProcessListener(processHandler: ProcessHandler, private val outputFormat: TestFormat) :
    TestProcessHandlerEvents(processHandler),
    QueryResultListener {

    override fun onBeginResults() {
        notifyTestsStarted()
    }

    override fun onEndResults(): PsiFile? {
        return null
    }

    override fun onQueryResult(result: QueryResult) {
        outputFormat.parse(result)?.let { results ->
            results.testSuites.forEach { onTestSuite(it) }
            notifyTextAvailable(
                "Finished: Total ${results.total}, Failed ${results.failed}, Ignored ${results.ignored}, Errors ${results.errors}, Passed ${results.passed}\n\n",
                ProcessOutputType.STDOUT
            )
        }
        notifyTextAvailable(result.value as String, ProcessOutputType.STDOUT)
    }

    override fun onException(e: Throwable) {
        notifyTextAvailable(printCharsToString { e.printStackTrace(it) }, ProcessOutputType.STDERR)
    }

    override fun onQueryResultTime(resultTime: QueryResultTime, time: XsDurationValue) {
    }

    override fun onQueryResultsPsiFile(psiFile: PsiFile) {
    }

    private fun onTestSuite(module: TestSuite) {
        notifyTestSuiteStarted(module.name)
        notifyTextAvailable("Module ${module.name}\n", ProcessOutputType.STDOUT)
        module.testCases.forEach { onTestCase(it) }
        notifyTestSuiteFinished(module.name)
    }

    private fun onTestCase(test: TestCase) {
        notifyTestStarted(test.name)
        when (test.result) {
            TestResult.Passed -> {
                notifyTextAvailable("-- ${test.name} -- PASSED\n", ProcessOutputType.STDOUT)
            }
            TestResult.Ignored -> {
                notifyTextAvailable("-- ${test.name} -- IGNORED\n", ProcessOutputType.STDOUT)
                notifyTestIgnored(test.name)
            }
            TestResult.Failed -> {
                notifyTextAvailable("-- ${test.name} -- FAILED\n", ProcessOutputType.STDERR)
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
            TestResult.Error -> {
                notifyTextAvailable("-- ${test.name} -- ERROR\n", ProcessOutputType.STDERR)
                when (val error = test.error) {
                    null -> notifyTestError(test.name, message = "")
                    else -> notifyTestError(test.name, exception = error)
                }
            }
        }
        notifyTestFinished(test.name, duration = test.duration?.seconds?.data?.toMilliseconds())
    }
}
