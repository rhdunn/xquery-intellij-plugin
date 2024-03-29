// Copyright (C) 2018, 2022-2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.roots

import com.intellij.compat.openapi.roots.ContentEntry
import com.intellij.openapi.roots.ExcludeFolder
import com.intellij.openapi.roots.ModuleRootModel
import com.intellij.openapi.roots.SourceFolder
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.jps.model.JpsElement
import org.jetbrains.jps.model.java.JavaSourceRootType
import org.jetbrains.jps.model.module.JpsModuleSourceRootType

class MockContentEntry(private val file: VirtualFile) : ContentEntry {
    override fun isSynthetic(): Boolean = TODO()

    override fun getFile(): VirtualFile = TODO()

    override fun getUrl(): String = TODO()

    override fun getSourceFolders(): Array<SourceFolder> {
        return arrayOf(MockSourceFolder(file, JavaSourceRootType.SOURCE))
    }

    override fun getSourceFolders(rootType: JpsModuleSourceRootType<*>): MutableList<SourceFolder> = TODO()

    override fun getSourceFolders(
        rootTypes: MutableSet<out JpsModuleSourceRootType<*>>
    ): MutableList<SourceFolder> = TODO()

    override fun getSourceFolderFiles(): Array<VirtualFile> = TODO()

    override fun getExcludeFolders(): Array<ExcludeFolder> = TODO()

    override fun getExcludeFolderUrls(): MutableList<String> = TODO()

    override fun getExcludeFolderFiles(): Array<VirtualFile> = TODO()

    override fun addSourceFolder(file: VirtualFile, isTestSource: Boolean): SourceFolder = TODO()

    override fun addSourceFolder(
        file: VirtualFile,
        isTestSource: Boolean,
        packagePrefix: String
    ): SourceFolder = TODO()

    override fun <P : JpsElement> addSourceFolder(
        file: VirtualFile,
        type: JpsModuleSourceRootType<P>,
        properties: P
    ): SourceFolder = TODO()

    override fun <P : JpsElement?> addSourceFolder(
        file: VirtualFile,
        type: JpsModuleSourceRootType<P>
    ): SourceFolder = TODO()

    override fun addSourceFolder(url: String, isTestSource: Boolean): SourceFolder = TODO()

    override fun <P : JpsElement?> addSourceFolder(
        url: String,
        type: JpsModuleSourceRootType<P>
    ): SourceFolder = TODO()

    override fun <P : JpsElement> addSourceFolder(
        url: String,
        type: JpsModuleSourceRootType<P>,
        properties: P
    ): SourceFolder = TODO()

    override fun removeSourceFolder(sourceFolder: SourceFolder): Unit = TODO()

    override fun clearSourceFolders(): Unit = TODO()

    override fun addExcludeFolder(file: VirtualFile): ExcludeFolder = TODO()

    override fun addExcludeFolder(url: String): ExcludeFolder = TODO()

    override fun removeExcludeFolder(excludeFolder: ExcludeFolder): Unit = TODO()

    override fun removeExcludeFolder(url: String): Boolean = TODO()

    override fun clearExcludeFolders(): Unit = TODO()

    override fun getExcludePatterns(): MutableList<String> = TODO()

    override fun addExcludePattern(pattern: String): Unit = TODO()

    override fun removeExcludePattern(pattern: String): Unit = TODO()

    override fun setExcludePatterns(patterns: MutableList<String>): Unit = TODO()

    override fun getRootModel(): ModuleRootModel = TODO()
}
