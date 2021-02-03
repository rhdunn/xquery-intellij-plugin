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
package uk.co.reecedunn.intellij.plugin.core.vfs.impl

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import org.apache.commons.compress.utils.IOUtils
import java.io.*
import java.lang.ref.WeakReference
import java.net.URLConnection

class URLConnectionVirtualFileImpl(
    private val connection: URLConnection,
    private val fileSystem: WeakReference<VirtualFileSystem>?
) : VirtualFile() {
    private val path = connection.url.path.substringAfter('!')
    private val name = path.substring(path.indexOf('/'))

    override fun getName(): String = name

    override fun getFileSystem(): VirtualFileSystem = fileSystem?.get() ?: throw UnsupportedOperationException()

    override fun getPath(): String = path

    override fun isWritable(): Boolean = false

    override fun isDirectory(): Boolean = false

    override fun isValid(): Boolean = true

    override fun getParent(): VirtualFile? = null

    override fun getChildren(): Array<VirtualFile>? {
        TODO()
    }

    @Throws(IOException::class)
    override fun getOutputStream(requestor: Any, newModificationStamp: Long, newTimeStamp: Long): OutputStream {
        throw UnsupportedOperationException()
    }

    @Throws(IOException::class)
    override fun contentsToByteArray(): ByteArray = IOUtils.toByteArray(inputStream)

    override fun getTimeStamp(): Long = 0

    override fun getModificationStamp(): Long = 0

    override fun getLength(): Long = connection.contentLength.toLong()

    override fun refresh(asynchronous: Boolean, recursive: Boolean, postRunnable: Runnable?): Unit = TODO()

    @Throws(IOException::class)
    override fun getInputStream(): InputStream = when (isDirectory) {
        true -> throw UnsupportedOperationException()
        else -> connection.getInputStream()
    }
}
