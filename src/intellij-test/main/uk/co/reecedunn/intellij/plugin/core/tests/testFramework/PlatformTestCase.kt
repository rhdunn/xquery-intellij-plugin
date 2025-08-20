// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.testFramework

import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import uk.co.reecedunn.intellij.plugin.core.extensions.PluginDescriptorProvider

interface PlatformTestCase : PluginDescriptorProvider {
    val app: Application get() = ApplicationManager.getApplication()

    val project: Project
}
