/*
 * Copyright (C) 2019 Reece H. Dunn
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
import com.intellij.openapi.vfs.VirtualFileSystem
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.ref.WeakReference
import java.util.zip.ZipEntry

data class ZipFile(
    private val entry: ZipEntry,
    private val contents: ByteArray,
    private val fileSystem: WeakReference<ZipFileSystem>
) : VirtualFile() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ZipFile

        if (entry != other.entry) return false
        if (!contents.contentEquals(other.contents)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = entry.hashCode()
        result = 31 * result + contents.contentHashCode()
        return result
    }

    internal fun toPair(): Pair<ZipEntry, ByteArray> = entry to contents

    // region VirtualFile

    private val filename: String by lazy {
        if (isDirectory)
            path.split("/").dropLast(1).last()
        else
            path.split("/").last()
    }

    override fun getName(): String = filename

    override fun getFileSystem(): VirtualFileSystem = fileSystem.get()!!

    override fun getPath(): String = entry.name

    override fun isWritable(): Boolean = false

    override fun isDirectory(): Boolean = path.endsWith('/')

    override fun isValid(): Boolean = true

    override fun getParent(): VirtualFile? = TODO()

    override fun getChildren(): Array<VirtualFile> = TODO()

    override fun getOutputStream(requestor: Any?, newModificationStamp: Long, newTimeStamp: Long): OutputStream = TODO()

    override fun contentsToByteArray(): ByteArray {
        return if (isDirectory) throw UnsupportedOperationException() else contents
    }

    override fun getTimeStamp(): Long = entry.time

    override fun getModificationStamp(): Long = 0

    override fun getLength(): Long = entry.size

    override fun refresh(asynchronous: Boolean, recursive: Boolean, postRunnable: Runnable?) = TODO()

    override fun getInputStream(): InputStream {
        return if (isDirectory) throw UnsupportedOperationException() else ByteArrayInputStream(contents)
    }

    // endregion
}
