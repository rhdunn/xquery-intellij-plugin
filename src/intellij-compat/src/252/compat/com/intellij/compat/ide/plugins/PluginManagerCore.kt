// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.ide.plugins

import com.intellij.openapi.extensions.PluginId
import com.intellij.ide.plugins.PluginManagerCore as PluginManagerCoreIJ

object PluginManagerCore {
    fun isLoaded(id: PluginId): Boolean = PluginManagerCoreIJ.getPlugin(id) != null

    fun isDisabled(id: PluginId): Boolean = PluginManagerCoreIJ.isDisabled(id)
}
