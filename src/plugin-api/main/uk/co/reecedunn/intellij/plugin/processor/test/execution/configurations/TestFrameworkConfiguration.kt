// Copyright (C) 2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.processor.test.execution.configurations

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.testframework.sm.runner.SMRunnerConsolePropertiesProvider
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
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
