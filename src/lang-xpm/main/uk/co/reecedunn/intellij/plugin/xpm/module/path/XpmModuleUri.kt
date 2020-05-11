/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpm.module.path

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import org.jetbrains.jps.model.java.JavaSourceRootType
import uk.co.reecedunn.intellij.plugin.core.roots.sourceFolders
import java.io.InputStream
import java.io.OutputStream

class XpmModuleUri(private val path: String) : VirtualFile() {
    // region VirtualFile

    override fun refresh(asynchronous: Boolean, recursive: Boolean, postRunnable: Runnable?) = TODO("not implemented")

    override fun getLength(): Long = TODO("not implemented")

    override fun getFileSystem(): VirtualFileSystem = TODO("not implemented")

    override fun getPath(): String = path

    override fun isDirectory(): Boolean = TODO("not implemented")

    override fun getTimeStamp(): Long = TODO("not implemented")

    override fun getName(): String = path

    override fun contentsToByteArray(): ByteArray = TODO("not implemented")

    override fun isValid(): Boolean = TODO("not implemented")

    override fun getInputStream(): InputStream = TODO("not implemented")

    override fun getParent(): VirtualFile = TODO("not implemented")

    override fun getChildren(): Array<VirtualFile> = TODO("not implemented")

    override fun isWritable(): Boolean = TODO("not implemented")

    override fun getOutputStream(
        requestor: Any?,
        newModificationStamp: Long,
        newTimeStamp: Long
    ): OutputStream = TODO("not implemented")

    // endregion
    // region Object

    override fun toString(): String = path

    override fun equals(other: Any?): Boolean {
        if (other !is XpmModuleUri) return false
        return path == other.path
    }

    override fun hashCode(): Int = path.hashCode()

    // endregion
}

fun XpmModuleUri.resolve(project: Project): Sequence<VirtualFile> {
    return project.sourceFolders()
        .filter { folder -> folder.file != null && folder.rootType === JavaSourceRootType.SOURCE }
        .map { folder -> folder.file?.findFileByRelativePath(path) }
        .filterNotNull()
}