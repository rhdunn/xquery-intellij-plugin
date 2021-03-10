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

import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.execution.ui.ConsoleView
import uk.co.reecedunn.intellij.plugin.core.execution.ui.ConsoleRunnerLayoutUiBuilder
import uk.co.reecedunn.intellij.plugin.processor.profile.execution.DefaultProfileExecutor
import uk.co.reecedunn.intellij.plugin.processor.profile.execution.ui.FlatProfileTableView
import uk.co.reecedunn.intellij.plugin.processor.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.test.TestFormat
import uk.co.reecedunn.intellij.plugin.processor.test.execution.ui.TestConsoleOutputView

class TestRunnerConsoleProperties(
    config: RunConfiguration,
    testFrameworkName: String,
    executor: Executor,
    private val outputFormat: TestFormat,
    private val testLocator: SMTestLocator? = null
) : SMTRunnerConsoleProperties(config, testFrameworkName, executor) {

    override fun createConsole(): ConsoleView {
        val builder = ConsoleRunnerLayoutUiBuilder(super.createConsole())
        builder.contentProvider(TestConsoleOutputView(project, outputFormat))
        if (executor.id == DefaultProfileExecutor.EXECUTOR_ID) {
            builder.contentProvider(FlatProfileTableView(project), active = true)
        }
        builder.asRunnerLayout(project, "QueryTest", PluginApiBundle.message("test.runner.layout.title"))
        return builder.consoleView()
    }

    override fun getTestLocator(): SMTestLocator? = testLocator
}
