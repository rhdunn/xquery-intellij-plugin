// Copyright (C) 2018-2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.processor.query.execution.configurations

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.lang.Language
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.core.lang.findByAssociations
import uk.co.reecedunn.intellij.plugin.core.lang.getLanguageMimeTypes
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSubset
import uk.co.reecedunn.intellij.plugin.processor.profile.execution.DefaultProfileExecutor
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings
import uk.co.reecedunn.intellij.plugin.processor.query.execution.configurations.rdf.RDF_FORMATS
import uk.co.reecedunn.intellij.plugin.processor.query.settings.QueryProcessors
import uk.co.reecedunn.intellij.plugin.processor.run.execution.runners.QueryRunProfile

class QueryProcessorRunConfiguration(
    project: Project,
    factory: ConfigurationFactory,
    private vararg val languages: Language
) :
    RunConfigurationBase<QueryProcessorRunConfigurationData>(project, factory, ""),
    QueryRunProfile {

    private val data: QueryProcessorRunConfigurationData
        get() = state!!

    override val language: Language
        get() = when (languages.size) {
            1 -> languages[0]
            else -> languages.findByAssociations(scriptFilePath ?: "") ?: languages[0]
        }

    override fun canRun(executorId: String): Boolean {
        return processor?.api?.canExecute(language, executorId) == true
    }

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
    // region RDF Output Format

    var rdfOutputFormat: Language?
        get() = RDF_FORMATS.find { it.getLanguageMimeTypes().contains(data.rdfOutputFormat) }
        set(value) {
            data.rdfOutputFormat = value?.getLanguageMimeTypes()?.get(0)
        }

    // endregion
    // region Updating

    var updating: Boolean
        get() = data.updating
        set(value) {
            data.updating = value
        }

    // endregion
    // region Reformat Results

    var reformatResults: Boolean
        get() = data.reformatResults
        set(value) {
            data.reformatResults = value
        }

    // endregion
    // region XPath Subset

    var xpathSubset: XPathSubset
        get() = data.xpathSubset
        set(value) {
            data.xpathSubset = value
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
    // region Script File

    var scriptFilePath: String?
        get() = data.scriptFile
        set(value) {
            data.scriptFile = value
        }

    var scriptSource: QueryProcessorDataSourceType
        get() = data.scriptSource
        set(value) {
            data.scriptSource = value
        }

    val scriptFile: VirtualFile?
        get() = data.scriptSource.find(data.scriptFile, project)

    // endregion
    // region Context Item

    var contextItemValue: String?
        get() = data.contextItem
        set(value) {
            data.contextItem = value
        }

    var contextItemSource: QueryProcessorDataSourceType?
        get() = data.contextItemSource
        set(value) {
            data.contextItemSource = value
        }

    val contextItem: VirtualFile?
        get() = data.contextItemSource?.find(data.contextItem, project)

    // endregion
    // region RunConfigurationBase

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return QueryProcessorRunConfigurationEditor(project, *languages)
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? {
        return when (executor.id) {
            DefaultRunExecutor.EXECUTOR_ID -> QueryProcessorRunState(environment)
            DefaultProfileExecutor.EXECUTOR_ID -> QueryProcessorRunState(environment)
            DefaultDebugExecutor.EXECUTOR_ID -> QueryProcessorRunState(environment)
            else -> null
        }
    }

    // endregion
}
