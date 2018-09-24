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

import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.roots.ExcludeFolder
import com.intellij.openapi.roots.SourceFolder
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.jps.model.JpsElement
import org.jetbrains.jps.model.java.JavaSourceRootType
import org.jetbrains.jps.model.module.JpsModuleSourceRootType

class MockContentEntry(private val file: VirtualFile) : ContentEntry {
    override fun setExcludePatterns(patterns: MutableList<String>) {
        TODO("not implemented")
    }

    override fun getUrl(): String {
        TODO("not implemented")
    }

    override fun getExcludeFolders(): Array<ExcludeFolder> {
        TODO("not implemented")
    }

    override fun addExcludePattern(pattern: String) {
        TODO("not implemented")
    }

    override fun getFile(): VirtualFile? {
        TODO("not implemented")
    }

    override fun removeExcludeFolder(excludeFolder: ExcludeFolder) {
        TODO("not implemented")
    }

    override fun removeExcludeFolder(url: String): Boolean {
        TODO("not implemented")
    }

    override fun getExcludeFolderFiles(): Array<VirtualFile> {
        TODO("not implemented")
    }

    override fun getSourceFolderFiles(): Array<VirtualFile> {
        TODO("not implemented")
    }

    override fun clearExcludeFolders() {
        TODO("not implemented")
    }

    override fun removeExcludePattern(pattern: String) {
        TODO("not implemented")
    }

    override fun getExcludePatterns(): MutableList<String> {
        TODO("not implemented")
    }

    override fun addSourceFolder(file: VirtualFile, isTestSource: Boolean): SourceFolder {
        TODO("not implemented")
    }

    override fun addSourceFolder(file: VirtualFile, isTestSource: Boolean, packagePrefix: String): SourceFolder {
        TODO("not implemented")
    }

    override fun <P : JpsElement?> addSourceFolder(
        file: VirtualFile,
        type: JpsModuleSourceRootType<P>,
        properties: P
    ): SourceFolder {
        TODO("not implemented")
    }

    override fun <P : JpsElement?> addSourceFolder(file: VirtualFile, type: JpsModuleSourceRootType<P>): SourceFolder {
        TODO("not implemented")
    }

    override fun addSourceFolder(url: String, isTestSource: Boolean): SourceFolder {
        TODO("not implemented")
    }

    override fun <P : JpsElement?> addSourceFolder(url: String, type: JpsModuleSourceRootType<P>): SourceFolder {
        TODO("not implemented")
    }

    override fun <P : JpsElement?> addSourceFolder(
        url: String,
        type: JpsModuleSourceRootType<P>,
        properties: P
    ): SourceFolder {
        TODO("not implemented")
    }

    override fun clearSourceFolders() {
        TODO("not implemented")
    }

    override fun getSourceFolders(): Array<SourceFolder> = arrayOf(MockSourceFolder(file, JavaSourceRootType.SOURCE))

    override fun getSourceFolders(rootType: JpsModuleSourceRootType<*>): MutableList<SourceFolder> {
        TODO("not implemented")
    }

    override fun getSourceFolders(rootTypes: MutableSet<out JpsModuleSourceRootType<*>>): MutableList<SourceFolder> {
        TODO("not implemented")
    }

    override fun getExcludeFolderUrls(): MutableList<String> {
        TODO("not implemented")
    }

    override fun addExcludeFolder(file: VirtualFile): ExcludeFolder {
        TODO("not implemented")
    }

    override fun addExcludeFolder(url: String): ExcludeFolder {
        TODO("not implemented")
    }

    override fun isSynthetic(): Boolean {
        TODO("not implemented")
    }

    override fun removeSourceFolder(sourceFolder: SourceFolder) {
        TODO("not implemented")
    }
}
