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
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.jps.model.module.JpsModuleSourceRootType

@Suppress("NonExtendableApiUsage")
class MockModuleRootsManager(private val module: Module) : ModuleRootManager() {
    override fun <R : Any?> processOrder(policy: RootPolicy<R>, initialValue: R): R = TODO()

    override fun isDependsOn(module: Module): Boolean = TODO()

    override fun getExcludeRoots(): Array<VirtualFile> = TODO()

    override fun orderEntries(): OrderEnumerator = TODO()

    @ApiStatus.Experimental
    @Suppress("UnstableApiUsage")
    override fun getExternalSource(): ProjectModelExternalSource? = TODO()

    override fun <T : Any?> getModuleExtension(klass: Class<T>): T = TODO()

    override fun getDependencyModuleNames(): Array<String> = TODO()

    override fun getModifiableModel(): ModifiableRootModel = TODO()

    override fun getModule(): Module = TODO()

    override fun isSdkInherited(): Boolean = TODO()

    override fun getOrderEntries(): Array<OrderEntry> = TODO()

    override fun getSourceRootUrls(): Array<String> = TODO()

    override fun getSourceRootUrls(includingTests: Boolean): Array<String> = TODO()

    override fun getContentEntries(): Array<ContentEntry> = arrayOf(MockContentEntry(module.moduleFile!!))

    override fun getExcludeRootUrls(): Array<String> = TODO()

    override fun getFileIndex(): ModuleFileIndex = TODO()

    override fun getSdk(): Sdk? = TODO()

    override fun getDependencies(): Array<Module> = TODO()

    override fun getDependencies(includeTests: Boolean): Array<Module> = TODO()

    override fun getSourceRoots(): Array<VirtualFile> = getSourceRoots(true)

    override fun getSourceRoots(includingTests: Boolean): Array<VirtualFile> = arrayOf(module.moduleFile!!)

    override fun getSourceRoots(rootType: JpsModuleSourceRootType<*>): MutableList<VirtualFile> = TODO()

    override fun getSourceRoots(rootTypes: MutableSet<out JpsModuleSourceRootType<*>>): MutableList<VirtualFile> {
        TODO()
    }

    override fun getContentRoots(): Array<VirtualFile> = TODO()

    override fun getContentRootUrls(): Array<String> = TODO()

    override fun getModuleDependencies(): Array<Module> = TODO()

    override fun getModuleDependencies(includeTests: Boolean): Array<Module> = TODO()
}
