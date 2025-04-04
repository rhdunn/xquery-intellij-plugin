// Copyright (C) 2021, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.marklogic.xray.configuration

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.lang.Language
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicBundle
import uk.co.reecedunn.intellij.plugin.marklogic.xray.format.XRayTestFormat
import uk.co.reecedunn.intellij.plugin.marklogic.xray.format.xray.XRayXmlFormat
import uk.co.reecedunn.intellij.plugin.marklogic.xray.runner.XRayTestRunState
import uk.co.reecedunn.intellij.plugin.marklogic.xray.test.XRayTestService
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings
import uk.co.reecedunn.intellij.plugin.processor.query.settings.QueryProcessors
import uk.co.reecedunn.intellij.plugin.processor.test.TestFormat
import uk.co.reecedunn.intellij.plugin.processor.test.execution.configurations.TestFrameworkConfiguration
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpm.project.configuration.XpmProjectConfigurations
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.testframework.execution.XQueryTestLocationProvider

class XRayTestConfiguration(project: Project, factory: ConfigurationFactory) :
    TestFrameworkConfiguration<XRayTestConfigurationData>(project, factory, XRayTestService.FRAMEWORK_NAME) {
    // region TestFrameworkConfiguration

    private val data: XRayTestConfigurationData
        get() = state!!

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return XRayTestConfigurationEditor(project)
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return XRayTestRunState(environment)
    }

    override val testLocator: SMTestLocator = XQueryTestLocationProvider

    // endregion
    // region QueryRunProfile

    override val language: Language by lazy { XQuery }

    override fun canRun(executorId: String): Boolean = when (executorId) {
        DefaultDebugExecutor.EXECUTOR_ID -> false
        else -> processor?.api?.canExecute(language, executorId) == true
    }

    // endregion
    // region Query Processor

    var processorId: Int?
        get() = if (data.processorId == -1) null else data.processorId
        set(value) {
            data.processorId = value ?: -1
        }

    var processor: QueryProcessorSettings?
        get() = QueryProcessors.getInstance().processors.firstOrNull { processor -> processor.id == processorId }
        set(value) {
            processorId = value?.id
        }

    // endregion
    // region Database

    var database: String?
        get() = data.database
        set(value) {
            data.database = value
        }

    // endregion
    // region Server

    var server: String?
        get() = data.server
        set(value) {
            data.server = value
        }

    // endregion
    // region Module Path

    var modulePath: String?
        get() = data.modulePath
        set(value) {
            data.modulePath = value
        }

    // endregion
    // region Test Path

    var testPath: String?
        get() = data.testPath
        set(value) {
            data.testPath = value
        }

    // endregion
    // region Module Pattern

    var modulePattern: String?
        get() = data.modulePattern
        set(value) {
            data.modulePattern = value
        }

    // endregion
    // region Test Pattern

    var testPattern: String?
        get() = data.testPattern
        set(value) {
            data.testPattern = value
        }

    // endregion
    // region Output Format

    override var outputFormat: TestFormat
        get() = XRayTestFormat.format(data.outputFormat ?: XRayXmlFormat.id)
        set(value) {
            data.outputFormat = value.id
        }

    // endregion
    // region Reformat Results

    var reformatResults: Boolean
        get() = data.reformatResults
        set(value) {
            data.reformatResults = value
        }

    // endregion
    // region Configuration Provider :: Directory

    fun appliesTo(directory: PsiDirectory): Boolean = when {
        testPattern != null -> false
        modulePattern != null -> false
        else -> testPath == directory.virtualFile.canonicalPath
    }

    fun create(directory: PsiDirectory): Boolean {
        name = MarkLogicBundle.message("xray.configuration.name", directory.name)

        val projectConfiguration = XpmProjectConfigurations.getInstance(project)
        processorId = projectConfiguration.processorId
        database = projectConfiguration.databaseName
        server = projectConfiguration.applicationName

        testPath = directory.virtualFile.canonicalPath
        return true
    }

    // endregion
    // region Configuration Provider :: Module

    fun appliesTo(module: PsiFile): Boolean = when {
        testPath != null -> false
        testPattern != null -> false
        else -> modulePattern == "/${module.name}"
    }

    fun create(module: PsiFile): Boolean {
        name = MarkLogicBundle.message("xray.configuration.name", module.name.replace("\\.[a-z]+$".toRegex(), ""))

        val projectConfiguration = XpmProjectConfigurations.getInstance(project)
        processorId = projectConfiguration.processorId
        database = projectConfiguration.databaseName
        server = projectConfiguration.applicationName

        modulePattern = "/${module.name}"
        return true
    }

    // endregion
    // region Configuration Provider :: Function

    fun appliesTo(module: PsiFile, testCase: XPathEQName): Boolean = when {
        testPath != null -> false
        modulePattern != "/${module.name}" -> false
        else -> testCase.localName?.data?.let { testPattern == "^$it$" } == true
    }

    fun create(module: PsiFile, testCase: XPathEQName): Boolean {
        if (testCase.localName?.data == null) return false

        val moduleName = module.name.replace("\\.[a-z]+$".toRegex(), "")
        name = MarkLogicBundle.message("xray.configuration.name", "$moduleName (${testCase.localName?.data})")

        val projectConfiguration = XpmProjectConfigurations.getInstance(project)
        processorId = projectConfiguration.processorId
        database = projectConfiguration.databaseName
        server = projectConfiguration.applicationName

        modulePattern = "/${module.name}"
        testPattern = "^${testCase.localName?.data}$"
        return true
    }

    // endregion
}
