/*
 * Copyright (C) 2018, 2022 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.tests.module

import com.intellij.compat.openapi.module.MockModuleManager
import uk.co.reecedunn.intellij.plugin.core.extensions.registerServiceInstance
import com.intellij.openapi.module.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.TestOnly
import uk.co.reecedunn.intellij.plugin.core.tests.roots.MockModuleRootsManager

@TestOnly
class MockModuleManager(private val project: Project) : MockModuleManager() {
    override fun createModule(moduleFile: VirtualFile): Module {
        val module = MockModule(project, moduleFile)
        module.registerServiceInstance(ModuleRootManager::class.java, MockModuleRootsManager(module))
        return module
    }
}
