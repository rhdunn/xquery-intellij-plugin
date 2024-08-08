// Copyright (C) 2021-2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.vfs.impl

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
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
    override fun contentsToByteArray(): ByteArray = inputStream.readBytes()

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
