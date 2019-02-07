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
package uk.co.reecedunn.intellij.plugin.core.tests.roots

import com.intellij.openapi.module.Module
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.*
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.jps.model.module.JpsModuleSourceRootType

class MockModuleRootsManager(private val module: Module) : uk.co.reecedunn.compat.roots.ModuleRootManager() {
    override fun getExcludeRoots(): Array<VirtualFile> {
        TODO("not implemented")
    }

    override fun orderEntries(): OrderEnumerator {
        TODO("not implemented")
    }

    override fun getExternalSource(): ProjectModelExternalSource? {
        TODO("not implemented")
    }

    override fun <T : Any?> getModuleExtension(klass: Class<T>): T {
        TODO("not implemented")
    }

    override fun getDependencyModuleNames(): Array<String> {
        TODO("not implemented")
    }

    override fun getModifiableModel(): ModifiableRootModel {
        TODO("not implemented")
    }

    override fun getModule(): Module {
        TODO("not implemented")
    }

    override fun isSdkInherited(): Boolean {
        TODO("not implemented")
    }

    override fun getOrderEntries(): Array<OrderEntry> {
        TODO("not implemented")
    }

    override fun getSourceRootUrls(): Array<String> {
        TODO("not implemented")
    }

    override fun getSourceRootUrls(includingTests: Boolean): Array<String> {
        TODO("not implemented")
    }

    override fun getContentEntries(): Array<ContentEntry> {
        return arrayOf(MockContentEntry(module.moduleFile!!))
    }

    override fun getExcludeRootUrls(): Array<String> {
        TODO("not implemented")
    }

    override fun getFileIndex(): ModuleFileIndex {
        TODO("not implemented")
    }

    override fun getSdk(): Sdk? {
        TODO("not implemented")
    }

    override fun getDependencies(): Array<Module> {
        TODO("not implemented")
    }

    override fun getDependencies(includeTests: Boolean): Array<Module> {
        TODO("not implemented")
    }

    override fun getSourceRoots(): Array<VirtualFile> = getSourceRoots(true)

    override fun getSourceRoots(includingTests: Boolean): Array<VirtualFile> {
        return arrayOf(module.moduleFile!!)
    }

    override fun getSourceRoots(rootType: JpsModuleSourceRootType<*>): MutableList<VirtualFile> {
        TODO("not implemented")
    }

    override fun getSourceRoots(rootTypes: MutableSet<out JpsModuleSourceRootType<*>>): MutableList<VirtualFile> {
        TODO("not implemented")
    }

    override fun getContentRoots(): Array<VirtualFile> {
        TODO("not implemented")
    }

    override fun getContentRootUrls(): Array<String> {
        TODO("not implemented")
    }

    override fun getModuleDependencies(): Array<Module> {
        TODO("not implemented")
    }

    override fun getModuleDependencies(includeTests: Boolean): Array<Module> {
        TODO("not implemented")
    }
}
