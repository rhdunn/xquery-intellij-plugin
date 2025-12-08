// Copyright (C) 2010, 2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.roots

import com.intellij.openapi.module.Module
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.OrderEnumerator
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.roots.ProjectRootManager
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

    override fun getModuleSourceRoots(
        rootTypes: MutableSet<out JpsModuleSourceRootType<*>>
    ): MutableList<VirtualFile> = TODO()

    override fun getContentSourceRoots(): Array<VirtualFile> = arrayOf()

    override fun getFileIndex(): ProjectFileIndex = TODO()

    override fun getProjectSdkName(): String = TODO()

    override fun getProjectSdk(): Sdk = TODO()

    override fun getContentRoots(): Array<VirtualFile> = TODO()

    override fun getContentRootUrls(): MutableList<String> = TODO()
}