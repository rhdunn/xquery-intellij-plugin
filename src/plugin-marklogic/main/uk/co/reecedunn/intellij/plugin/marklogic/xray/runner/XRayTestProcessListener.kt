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
        }
        notifyTextAvailable(result.value as String, ProcessOutputType.STDOUT)
    }

    override fun onException(e: Throwable) {
        notifyTextAvailable(e.message ?: "", ProcessOutputType.STDERR)
    }

    override fun onQueryResultTime(resultTime: QueryResultTime, time: XsDurationValue) {
    }

    override fun onQueryResultsPsiFile(psiFile: PsiFile) {
    }

    private fun onTestSuite(module: XRayTestModule) {
        notifyTestSuiteStarted(module.path)
        module.tests.forEach { test -> onTest(test) }
        notifyTestSuiteFinished(module.path)
    }

    private fun onTest(test: XRayTest) {
        notifyTestStarted(test.name)
        when (test.result) {
            XRayTestResult.Ignored -> notifyTestIgnored(test.name)
            XRayTestResult.Failed -> notifyTestFailed(test.name, message = "")
            XRayTestResult.Error -> notifyTestError(test.name, message = "")
            XRayTestResult.Passed -> {
            }
        }
        notifyTestFinished(test.name)
    }
}
