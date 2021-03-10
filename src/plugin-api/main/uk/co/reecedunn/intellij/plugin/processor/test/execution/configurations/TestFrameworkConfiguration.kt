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
package uk.co.reecedunn.intellij.plugin.processor.test.execution.configurations

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.testframework.sm.runner.SMRunnerConsolePropertiesProvider
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import uk.co.reecedunn.intellij.plugin.core.execution.configurations.RunConfigurationBase
import uk.co.reecedunn.intellij.plugin.processor.run.execution.runners.QueryRunProfile
import uk.co.reecedunn.intellij.plugin.processor.test.TestFormat

abstract class TestFrameworkConfiguration<T>(
    project: Project,
    factory: ConfigurationFactory,
    private val testFrameworkName: String
) : RunConfigurationBase<T>(project, factory, ""),
    SMRunnerConsolePropertiesProvider,
    QueryRunProfile {
    // region RunConfigurationBase

    abstract override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration>

    abstract override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState?

    // endregion
    // region SMRunnerConsolePropertiesProvider

    override fun createTestConsoleProperties(executor: Executor): SMTRunnerConsoleProperties {
        return TestRunnerConsoleProperties(this, testFrameworkName, executor, outputFormat, testLocator)
    }

    // endregion
    // region Configuration Settings

    abstract var outputFormat: TestFormat

    open val testLocator: SMTestLocator? = null

    // endregion
}
