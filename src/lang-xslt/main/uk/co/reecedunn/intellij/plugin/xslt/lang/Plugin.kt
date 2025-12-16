// Copyright (C) 2019-2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xslt.lang

import com.intellij.compat.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId

private val INTELLIJ_XPATH_PLUGIN_ID = PluginId.getId("XPathView")

fun isIntellijXPathPluginEnabled(): Boolean {
    if (!PluginManagerCore.isLoaded(INTELLIJ_XPATH_PLUGIN_ID)) return false
    return !PluginManagerCore.isDisabled(INTELLIJ_XPATH_PLUGIN_ID)
}
