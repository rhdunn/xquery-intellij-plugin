/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.execution.configurations

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.lang.Language
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.compat.execution.configurations.RunConfigurationBase
import uk.co.reecedunn.intellij.plugin.core.lang.findByAssociations
import uk.co.reecedunn.intellij.plugin.core.lang.getLanguageMimeTypes
import uk.co.reecedunn.intellij.plugin.intellij.execution.executors.DefaultProfileExecutor
import uk.co.reecedunn.intellij.plugin.intellij.lang.RDF_FORMATS
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSubset
import uk.co.reecedunn.intellij.plugin.processor.intellij.settings.QueryProcessors
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings

data class QueryProcessorRunConfigurationData(
    var processorId: Int? = null,
    var rdfOutputFormat: String? = null,
    var updating: Boolean = false,
    var xpathSubset: XPathSubset = XPathSubset.XPath,
    var server: String? = null,
    var database: String? = null,
    var modulePath: String? = null,
    var scriptFile: String? = null,
    var scriptSource: QueryProcessorDataSourceType = QueryProcessorDataSourceType.LocalFile,
    var contextItem: String? = null,
    var contextItemSource: QueryProcessorDataSourceType? = null,
    var reformatResults: Boolean = false
) : RunConfigurationOptions()

class QueryProcessorRunConfiguration(
    project: Project,
    factory: ConfigurationFactory,
    private vararg val languages: Language
) :
    RunConfigurationBase<QueryProcessorRunConfigurationData>(project, factory, "") {

    @Suppress("UsePropertyAccessSyntax") // IntelliJ <= 2018.2 compatibility.
    private val data: QueryProcessorRunConfigurationData
        get() = getState()!!

    val language: Language
        get() {
            return if (languages.size == 1) {
                languages[0]
            } else {
                languages.findByAssociations(scriptFilePath ?: "") ?: languages[0]
            }
        }

    // region Query Processor

    var processorId: Int?
        get() = data.processorId
        set(value) {
            data.processorId = value
        }

    var processor: QueryProcessorSettings?
        get() = QueryProcessors.getInstance().processors.firstOrNull { processor -> processor.id == data.processorId }
        set(value) {
            data.processorId = value?.id
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
    // region Server

    var server: String?
        get() = data.server
        set(value) {
            data.server = value
        }

    // endregion
    // region Database

    var database: String?
        get() = data.database
        set(value) {
            data.database = value
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

    val scriptFile get(): VirtualFile? = data.scriptSource.find(data.scriptFile, project)

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

    val contextItem get(): VirtualFile? = data.contextItemSource?.find(data.contextItem, project)

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
