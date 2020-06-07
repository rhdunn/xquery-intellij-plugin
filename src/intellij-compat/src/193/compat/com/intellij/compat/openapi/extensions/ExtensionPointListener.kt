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
package com.intellij.compat.openapi.extensions

import com.intellij.openapi.extensions.PluginDescriptor

abstract class ExtensionPointListener<T> : com.intellij.openapi.extensions.ExtensionPointListener<T> {
    override fun extensionAdded(extension: T, pluginDescriptor: PluginDescriptor?) = extensionAdded(extension)

    abstract fun extensionAdded(extension: T)

    override fun extensionRemoved(extension: T, pluginDescriptor: PluginDescriptor?) = extensionRemoved(extension)

    abstract fun extensionRemoved(extension: T)
}
