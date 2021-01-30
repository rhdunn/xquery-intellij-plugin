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
import uk.co.reecedunn.intellij.plugin.marklogic.xray.format.XRayTestFormat
import uk.co.reecedunn.intellij.plugin.marklogic.xray.test.XRayTest
import uk.co.reecedunn.intellij.plugin.marklogic.xray.test.XRayTestModule
import uk.co.reecedunn.intellij.plugin.marklogic.xray.test.XRayTestResult
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.process.QueryResultListener
import uk.co.reecedunn.intellij.plugin.processor.intellij.execution.process.QueryResultTime
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.xdm.types.XsDurationValue

class XRayTestProcessListener(processHandler: ProcessHandler, private val outputFormat: XRayTestFormat) :
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
            results.modules.forEach { module -> onTestSuite(module) }
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

    private fun onTestSuite(module: XRayTestModule) {
        notifyTestSuiteStarted(module.path)
        notifyTextAvailable("Module ${module.path}\n", ProcessOutputType.STDOUT)
        module.tests.forEach { test -> onTest(test) }
        notifyTestSuiteFinished(module.path)
    }

    private fun onTest(test: XRayTest) {
        notifyTestStarted(test.name)
        when (test.result) {
            XRayTestResult.Passed -> {
                notifyTextAvailable("-- ${test.name} -- PASSED\n", ProcessOutputType.STDOUT)
            }
            XRayTestResult.Ignored -> {
                notifyTextAvailable("-- ${test.name} -- IGNORED\n", ProcessOutputType.STDOUT)
                notifyTestIgnored(test.name)
            }
            XRayTestResult.Failed -> {
                notifyTextAvailable("-- ${test.name} -- FAILED\n", ProcessOutputType.STDERR)
                test.assertions.forEach { assertion ->
                    if (assertion.result == XRayTestResult.Failed) {
                        notifyTestFailed(
                            test.name,
                            message = assertion.message ?: "Assertion failure",
                            expected = assertion.expected,
                            actual = assertion.actual
                        )
                    }
                }
            }
            XRayTestResult.Error -> {
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
