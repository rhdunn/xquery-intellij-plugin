/*
 * Copyright (C) 2016, 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.vfs

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileSystem

import java.io.IOException

object ResourceVirtualFileSystem : VirtualFileSystem() {
    override fun getProtocol(): String = "res"

    override fun findFileByPath(path: String): VirtualFile? {
        return ResourceVirtualFile(ResourceVirtualFileSystem::class.java.classLoader, path, ResourceVirtualFileSystem)
    }

    override fun refresh(asynchronous: Boolean) {}

    override fun refreshAndFindFileByPath(path: String): VirtualFile? = findFileByPath(path)

    override fun addVirtualFileListener(listener: VirtualFileListener) {}

    override fun removeVirtualFileListener(listener: VirtualFileListener) {}

    @Throws(IOException::class)
    override fun deleteFile(requestor: Any, vFile: VirtualFile) = TODO()

    @Throws(IOException::class)
    override fun moveFile(requestor: Any, vFile: VirtualFile, newParent: VirtualFile) = TODO()

    @Throws(IOException::class)
    override fun renameFile(requestor: Any, vFile: VirtualFile, newName: String) = TODO()

    @Throws(IOException::class)
    override fun createChildFile(requestor: Any, vDir: VirtualFile, fileName: String): VirtualFile = TODO()

    @Throws(IOException::class)
    override fun createChildDirectory(requestor: Any, vDir: VirtualFile, dirName: String): VirtualFile = TODO()

    @Throws(IOException::class)
    override fun copyFile(requestor: Any, virtualFile: VirtualFile, newParent: VirtualFile, copyName: String): VirtualFile = TODO()

    override fun isReadOnly(): Boolean = true
}
