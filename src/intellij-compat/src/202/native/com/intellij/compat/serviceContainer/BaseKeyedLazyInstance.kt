/*
 * Copyright (C) 2020 Reece H. Dunn
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
package com.intellij.compat.serviceContainer

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.PluginAware
import com.intellij.openapi.extensions.PluginDescriptor
import com.intellij.serviceContainer.LazyExtensionInstance
import org.picocontainer.PicoContainer

abstract class BaseKeyedLazyInstance<T> : LazyExtensionInstance<T>(), PluginAware {
    // For the AbstractExtensionPointBean#instantiateBean compatibility API on IntelliJ 2020.1 and earlier.
    @Suppress("UNUSED_PARAMETER")
    fun <T> instantiateClass(className: String, container: PicoContainer): T = TODO()

    @Transient
    var pluginDescriptor: PluginDescriptor? = null
        private set

    override fun setPluginDescriptor(pluginDescriptor: PluginDescriptor) {
        this.pluginDescriptor = pluginDescriptor
    }

    @Suppress("ReplaceNotNullAssertionWithElvisReturn")
    open fun getInstance(): T = getInstance(ApplicationManager.getApplication(), pluginDescriptor!!)
}
