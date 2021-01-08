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
package uk.co.reecedunn.intellij.plugin.marklogic.xray.configuration

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import uk.co.reecedunn.intellij.plugin.core.execution.configurations.RunConfigurationBase
import uk.co.reecedunn.intellij.plugin.marklogic.xray.runner.XRayTestRunState
import uk.co.reecedunn.intellij.plugin.processor.intellij.settings.QueryProcessors
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings

class XRayTestConfiguration(project: Project, factory: ConfigurationFactory) :
    RunConfigurationBase<XRayTestConfigurationData>(project, factory, "") {
    // region RunConfigurationBase

    private val data: XRayTestConfigurationData
        get() = state!!

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return XRayTestConfigurationEditor(project)
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return XRayTestRunState(environment)
    }

    // endregion
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
    // region Server

    var server: String?
        get() = data.server
        set(value) {
            data.server = value
        }

    // endregion
}
