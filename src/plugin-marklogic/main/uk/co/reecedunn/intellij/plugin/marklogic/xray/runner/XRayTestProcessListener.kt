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
import uk.co.reecedunn.intellij.plugin.processor.test.execution.process.TestProcessListener
import uk.co.reecedunn.intellij.plugin.processor.test.*
import uk.co.reecedunn.intellij.plugin.xquery.testframework.execution.XQueryTestLocationProvider

class XRayTestProcessListener(processHandler: ProcessHandler, outputFormat: TestFormat) :
    TestProcessListener(processHandler, outputFormat, XQueryTestLocationProvider) {

    override fun onTestSuiteStarted(testsuite: TestSuite) {
        super.onTestSuiteStarted(testsuite)
        notifyTextAvailable("Module ${testsuite.name}\n", ProcessOutputType.STDOUT)
    }

    override fun onTestPassed(test: TestCase) {
        notifyTextAvailable("-- ${test.name} -- PASSED\n", ProcessOutputType.STDOUT)
        super.onTestPassed(test)
    }

    override fun onTestIgnored(test: TestCase) {
        notifyTextAvailable("-- ${test.name} -- IGNORED\n", ProcessOutputType.STDOUT)
        super.onTestIgnored(test)
    }

    override fun onTestFailed(test: TestCase) {
        notifyTextAvailable("-- ${test.name} -- FAILED\n", ProcessOutputType.STDERR)
        super.onTestFailed(test)
    }

    override fun onTestError(test: TestCase) {
        notifyTextAvailable("-- ${test.name} -- ERROR\n", ProcessOutputType.STDERR)
        super.onTestError(test)
    }

    override fun onTestsFinished(results: TestStatistics) {
        notifyTextAvailable(
            "Finished: Total ${results.total}, Failed ${results.failed}, Ignored ${results.ignored}, Errors ${results.errors}, Passed ${results.passed}\n",
            ProcessOutputType.STDOUT
        )
    }
}
