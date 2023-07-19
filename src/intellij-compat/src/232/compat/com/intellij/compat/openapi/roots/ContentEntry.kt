// Copyright (C) 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.openapi.roots

import com.intellij.openapi.roots.ExcludeFolder
import com.intellij.openapi.roots.ProjectModelExternalSource
import com.intellij.openapi.roots.SourceFolder
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.jps.model.JpsElement
import org.jetbrains.jps.model.module.JpsModuleSourceRootType

@Suppress("UnstableApiUsage", "NonExtendableApiUsage")
interface ContentEntry : com.intellij.openapi.roots.ContentEntry {
    abstract override fun addExcludeFolder(file: VirtualFile): ExcludeFolder

    abstract override fun addExcludeFolder(url: String): ExcludeFolder

    abstract fun addExcludeFolder(url: String, isAutomaticallyImported: Boolean): ExcludeFolder

    override fun addExcludeFolder(url: String, source: ProjectModelExternalSource?): ExcludeFolder = TODO()

    abstract fun <P : JpsElement> addSourceFolder(
        url: String,
        type: JpsModuleSourceRootType<P>,
        properties: P,
        isAutomaticallyImported: Boolean
    ): SourceFolder

    abstract override fun <P : JpsElement> addSourceFolder(
        url: String,
        type: JpsModuleSourceRootType<P>,
        properties: P,
        externalSource: ProjectModelExternalSource?
    ): SourceFolder
}
