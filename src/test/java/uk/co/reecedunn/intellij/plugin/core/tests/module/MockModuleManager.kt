/*
 * Copyright (C) 2018 Reece H. Dunn
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

import com.intellij.openapi.module.*
import com.intellij.openapi.project.Project
import com.intellij.util.graph.Graph
import java.util.Comparator

class MockModuleManager(project: Project) : ModuleManager() {
    private val modules: Array<Module> = arrayOf(
        MockModule(project)
    )

    override fun setUnloadedModules(unloadedModuleNames: MutableList<String>) {
        TODO("not implemented")
    }

    override fun getModifiableModel(): ModifiableModuleModel {
        TODO("not implemented")
    }

    override fun newModule(filePath: String, moduleTypeId: String): Module {
        TODO("not implemented")
    }

    override fun getModuleDependentModules(module: Module): MutableList<Module> {
        TODO("not implemented")
    }

    override fun moduleDependencyComparator(): Comparator<Module> {
        TODO("not implemented")
    }

    override fun moduleGraph(): Graph<Module> {
        TODO("not implemented")
    }

    override fun moduleGraph(includeTests: Boolean): Graph<Module> {
        TODO("not implemented")
    }

    override fun getUnloadedModuleDescriptions(): MutableCollection<UnloadedModuleDescription> {
        TODO("not implemented")
    }

    override fun hasModuleGroups(): Boolean {
        TODO("not implemented")
    }

    override fun isModuleDependent(module: Module, onModule: Module): Boolean {
        TODO("not implemented")
    }

    override fun getAllModuleDescriptions(): MutableCollection<ModuleDescription> {
        TODO("not implemented")
    }

    override fun getModuleGroupPath(module: Module): Array<String>? {
        TODO("not implemented")
    }

    override fun getModuleGrouper(model: ModifiableModuleModel?): ModuleGrouper {
        TODO("not implemented")
    }

    override fun loadModule(filePath: String): Module {
        TODO("not implemented")
    }

    override fun getUnloadedModuleDescription(moduleName: String): UnloadedModuleDescription? {
        TODO("not implemented")
    }

    override fun getModules(): Array<Module> = modules

    override fun getSortedModules(): Array<Module> = modules

    override fun findModuleByName(name: String): Module? {
        TODO("not implemented")
    }

    override fun disposeModule(module: Module) {
        TODO("not implemented")
    }
}