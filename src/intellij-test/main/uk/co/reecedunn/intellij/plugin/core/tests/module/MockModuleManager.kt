// Copyright (C) 2018, 2022-2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.module

import com.intellij.compat.openapi.module.MockModuleManager
import com.intellij.mock.MockProject
import uk.co.reecedunn.intellij.plugin.core.extensions.registerServiceInstance
import com.intellij.openapi.module.*
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.TestOnly
import uk.co.reecedunn.intellij.plugin.core.tests.roots.MockModuleRootsManager

@TestOnly
class MockModuleManager(private val project: MockProject) : MockModuleManager() {
    override fun createModule(moduleFile: VirtualFile): Module {
        val module = MockModule(project, moduleFile)
        module.registerServiceInstance(ModuleRootManager::class.java, MockModuleRootsManager(module))
        return module
    }
}
