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
package com.intellij.compat.openapi.module

import com.intellij.openapi.module.*
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.graph.Graph
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.TestOnly
import java.nio.file.Path

@TestOnly
@Suppress("NonExtendableApiUsage")
abstract class MockModuleManager : ModuleManager() {
    private var modules: Array<Module> = arrayOf()

    protected abstract fun createModule(moduleFile: VirtualFile): Module

    fun addModule(moduleFile: VirtualFile): Module {
        val module = createModule(moduleFile)
        modules = arrayOf(module, *modules)
        return module
    }

    @Suppress("UnstableApiUsage")
    override fun setUnloadedModules(unloadedModuleNames: MutableList<String>): Unit = TODO()

    override fun getModifiableModel(): ModifiableModuleModel = TODO()

    override fun newModule(filePath: String, moduleTypeId: String): Module = TODO()

    override fun getModuleDependentModules(module: Module): MutableList<Module> = TODO()

    override fun moduleDependencyComparator(): Comparator<Module> = TODO()

    override fun moduleGraph(): Graph<Module> = TODO()

    override fun moduleGraph(includeTests: Boolean): Graph<Module> = TODO()

    @ApiStatus.Experimental
    @Suppress("UnstableApiUsage")
    override fun getUnloadedModuleDescriptions(): MutableCollection<UnloadedModuleDescription> = TODO()

    override fun hasModuleGroups(): Boolean = TODO()

    override fun isModuleDependent(module: Module, onModule: Module): Boolean = TODO()

    @ApiStatus.Experimental
    @Suppress("UnstableApiUsage")
    override fun getAllModuleDescriptions(): MutableCollection<ModuleDescription> = TODO()

    override fun getModuleGroupPath(module: Module): Array<String> = TODO()

    @ApiStatus.Experimental
    @Suppress("UnstableApiUsage")
    override fun getModuleGrouper(model: ModifiableModuleModel?): ModuleGrouper = TODO()

    @Deprecated("Deprecated in Java", ReplaceWith("TODO()"))
    override fun loadModule(filePath: String): Module = TODO()

    override fun loadModule(file: Path): Module = TODO()

    @ApiStatus.Experimental
    @Suppress("UnstableApiUsage")
    override fun getUnloadedModuleDescription(moduleName: String): UnloadedModuleDescription = TODO()

    override fun getModules(): Array<Module> = modules

    override fun getSortedModules(): Array<Module> = modules

    override fun findModuleByName(name: String): Module = TODO()

    override fun disposeModule(module: Module): Unit = TODO()
}
