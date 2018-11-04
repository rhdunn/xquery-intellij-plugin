/*
 * Copyright (C) 2018 Reece H. Dunn
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
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import uk.co.reecedunn.compat.execution.configurations.RunConfigurationBase
import uk.co.reecedunn.intellij.plugin.intellij.settings.QueryProcessors
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings

data class QueryProcessorRunConfigurationData(
    var processorId: Int? = null
) : RunConfigurationOptions()

class QueryProcessorRunConfiguration(project: Project, factory: ConfigurationFactory, name: String) :
    RunConfigurationBase<QueryProcessorRunConfigurationEditor>(project, factory, name),
    PersistentStateComponent<QueryProcessorRunConfigurationData> {
    // region Settings

    private val data = QueryProcessorRunConfigurationData()

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
    // region RunConfigurationBase

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return QueryProcessorRunConfigurationEditor(project)
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? {
        return QueryProcessorRunState(environment)
    }

    // endregion
    // region PersistentStateComponent

    override fun getState(): QueryProcessorRunConfigurationData? = data

    override fun loadState(state: QueryProcessorRunConfigurationData) = XmlSerializerUtil.copyBean(state, data)

    // endregion
}
