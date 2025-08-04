// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.module

import com.intellij.mock.MockProjectEx
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.ProjectTestCase

interface ModuleTestCase : ProjectTestCase {
    fun registerModuleManager(module: VirtualFile? = null) {
        val manager = MockModuleManager(project as MockProjectEx)
        if (module != null) {
            manager.addModule(module)
        }
        project.registerService<ModuleManager>(manager)
    }
}
