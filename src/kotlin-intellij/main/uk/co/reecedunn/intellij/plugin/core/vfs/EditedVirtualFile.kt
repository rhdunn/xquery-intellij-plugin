/*
 * Copyright (C) 2021 Reece H. Dunn
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

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class EditedVirtualFile(
    val original: VirtualFile,
    val contents: String,
    private val modificationStampValue: Long
): VirtualFile() {
    override fun getName(): String = original.name

    override fun getFileSystem(): VirtualFileSystem = original.fileSystem

    override fun getPath(): String = original.path

    override fun isWritable(): Boolean = false

    override fun isDirectory(): Boolean = original.isDirectory

    override fun isValid(): Boolean = original.isValid

    override fun getParent(): VirtualFile? = original.parent

    override fun getChildren(): Array<VirtualFile>? = original.children

    @Throws(IOException::class)
    override fun getOutputStream(requestor: Any, newModificationStamp: Long, newTimeStamp: Long): OutputStream {
        throw UnsupportedOperationException()
    }

    @Throws(IOException::class)
    override fun contentsToByteArray(): ByteArray = contents.toByteArray(charset)

    override fun getTimeStamp(): Long = original.timeStamp

    override fun getModificationStamp(): Long = modificationStampValue

    override fun getLength(): Long = contents.length.toLong()

    override fun refresh(asynchronous: Boolean, recursive: Boolean, postRunnable: Runnable?) {
        original.refresh(asynchronous, recursive, postRunnable)
    }

    @Throws(IOException::class)
    override fun getInputStream(): InputStream = contents.byteInputStream(charset)
}
