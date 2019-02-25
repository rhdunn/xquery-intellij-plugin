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

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.*
import org.jdom.Element
import java.lang.reflect.Type

// IntelliJ >= 183 adds a generic parameter to RunConfigurationBase.
abstract class RunConfigurationBase<T>(project: Project, factory: ConfigurationFactory, name: String) :
    com.intellij.execution.configurations.RunConfigurationBase<T>(project, factory, name) {

    // Settings serialization bug: https://youtrack.jetbrains.com/issue/IDEA-207705
    override fun writeExternal(element: Element) {
        super.writeExternal(element)

        // IntelliJ >= 183 does not serialize the settings for the configuration state object.
        val beanBinding = serializer.getClassBinding(optionsClass) as BeanBinding
        beanBinding.serializeInto(options, element, null)
    }
}

private val serializer = object : XmlSerializerImpl.XmlSerializerBase() {
    override fun getClassBinding(aClass: Class<*>, originalType: Type, accessor: MutableAccessor?): Binding {
        val beanBinding = BeanBinding(aClass, accessor)
        beanBinding.init(aClass, this)
        return beanBinding
    }
}

fun serializeConfigurationInto(configuration: RunConfiguration, element: Element) {
    com.intellij.execution.impl.serializeConfigurationInto(configuration, element)
}
