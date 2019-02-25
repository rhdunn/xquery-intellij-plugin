/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.compat.execution.configurations

import com.intellij.configurationStore.serializeStateInto
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import org.jdom.Element

// IntelliJ >= 183 adds a generic parameter to RunConfigurationBase.
abstract class RunConfigurationBase<T>(project: Project, factory: ConfigurationFactory, name: String) :
    com.intellij.execution.configurations.RunConfigurationBase(project, factory, name) {

    // IntelliJ <= 182 does not implement PersistentStateComponent.getState.
    @Suppress("UNCHECKED_CAST")
    fun getState(): T? = options as T?

    // IntelliJ <= 182 does not implement PersistentStateComponent.loadState.
    fun loadState(state: T) {
        when (state) {
            is Element -> super.loadState(state)
            else -> XmlSerializerUtil.copyBean(state, getState()!!)
        }
    }
}

fun serializeConfigurationInto(configuration: RunConfiguration, element: Element) {
    if (configuration is PersistentStateComponent<*>) {
        configuration.serializeStateInto(element)
    } else {
        configuration.writeExternal(element)
    }
}
