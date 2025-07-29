/*
 * Copyright (C) 2018, 2020 Reece H. Dunn
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

import com.intellij.compat.openapi.fileChooser.ProjectRootManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.OrderEnumerator
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.jps.model.module.JpsModuleSourceRootType

@Suppress("NonExtendableApiUsage")
class MockProjectRootsManager : ProjectRootManager() {
    override fun setProjectSdkName(name: String, sdkTypeName: String): Unit = TODO()

    override fun orderEntries(): OrderEnumerator = TODO()

    override fun orderEntries(modules: MutableCollection<out Module>): OrderEnumerator = TODO()

    override fun getContentRootsFromAllModules(): Array<VirtualFile> = TODO()

    override fun getProjectSdkTypeName(): String = TODO()

    override fun setProjectSdk(sdk: Sdk?): Unit = TODO()

    override fun getModuleSourceRoots(rootTypes: MutableSet<out JpsModuleSourceRootType<*>>): MutableList<VirtualFile> {
        TODO()
    }

    override fun getContentSourceRoots(): Array<VirtualFile> = arrayOf()

    override fun getFileIndex(): ProjectFileIndex = TODO()

    override fun getProjectSdkName(): String = TODO()

    override fun getProjectSdk(): Sdk = TODO()

    override fun getContentRoots(): Array<VirtualFile> = TODO()

    override fun getContentRootUrls(): MutableList<String> = TODO()
}
