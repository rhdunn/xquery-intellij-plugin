// Copyright (C) 2018, 2022-2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.module

import com.intellij.mock.MockProject
import uk.co.reecedunn.intellij.plugin.core.extensions.registerServiceInstance
import com.intellij.openapi.module.*
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.graph.Graph
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.TestOnly
import uk.co.reecedunn.intellij.plugin.core.tests.roots.MockModuleRootsManager
import java.nio.file.Path

@TestOnly
@Suppress("NonExtendableApiUsage")
class MockModuleManager(private val project: MockProject) : ModuleManager() {
    override var modules: Array<Module> = arrayOf()

    override val sortedModules: Array<Module>
        get() = modules

    fun createModule(moduleFile: VirtualFile): Module {
        val module = MockModule(project, moduleFile)
        module.registerServiceInstance(ModuleRootManager::class.java, MockModuleRootsManager(module))
        return module
    }

    fun addModule(moduleFile: VirtualFile): Module {
        val module = createModule(moduleFile)
        modules = arrayOf(module, *modules)
        return module
    }

    override suspend fun setUnloadedModules(unloadedModuleNames: List<String>) = TODO()

    @Deprecated("Use setUnloadedModules", ReplaceWith("TODO()"))
    override fun setUnloadedModulesSync(unloadedModuleNames: List<String>) = TODO()

    override fun getModifiableModel(): ModifiableModuleModel = TODO()

    override fun newModule(filePath: String, moduleTypeId: String): Module = TODO()

    override fun getModuleDependentModules(module: Module): MutableList<Module> = TODO()

    override fun moduleDependencyComparator(): Comparator<Module> = TODO()

    override fun moduleGraph(): Graph<Module> = TODO()

    override fun moduleGraph(includeTests: Boolean): Graph<Module> = TODO()

    override fun hasModuleGroups(): Boolean = TODO()

    override fun isModuleDependent(module: Module, onModule: Module): Boolean = TODO()

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

    override fun findModuleByName(name: String): Module = TODO()

    override fun disposeModule(module: Module): Unit = TODO()

    @Suppress("UnstableApiUsage")
    override val allModuleDescriptions: Collection<ModuleDescription>
        get() = TODO()

    @Suppress("UnstableApiUsage")
    override val unloadedModuleDescriptions: Collection<UnloadedModuleDescription>
        get() = TODO()
}
