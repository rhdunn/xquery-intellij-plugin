/*
 * Copyright (C) 2016, 2018-2021 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import java.io.*

import java.lang.ref.WeakReference

class ResourceVirtualFileImpl internal constructor(
    private val mLoader: ClassLoader,
    private val mResource: String,
    private val mFileSystem: WeakReference<VirtualFileSystem>?,
    private var mFile: File?,
    private var mPath: String
) : VirtualFile() {
    private var mParent: String? = null
    private var mName: String? = null

    init {
        val idx = mResource.lastIndexOf('/')
        if (idx == -1) {
            mParent = null
            mName = mResource
        } else {
            mParent = mResource.substring(0, idx)
            mName = mResource.substring(idx + 1)
        }
    }

    override fun getName(): String = mName!!

    override fun getFileSystem(): VirtualFileSystem = mFileSystem?.get() ?: throw UnsupportedOperationException()

    override fun getPath(): String = mPath

    override fun isWritable(): Boolean = false

    override fun isDirectory(): Boolean = mFile != null && mFile!!.isDirectory

    override fun isValid(): Boolean = mFile != null

    override fun getParent(): VirtualFile? = mParent?.let { ResourceVirtualFile.create(mLoader, it, mFileSystem) }

    override fun getChildren(): Array<VirtualFile>? {
        val children = mFile?.list() ?: return null
        return children.map { child ->
            ResourceVirtualFile.create(mLoader, "$mResource/$child", mFileSystem)
        }.toTypedArray()
    }

    @Throws(IOException::class)
    override fun getOutputStream(requestor: Any, newModificationStamp: Long, newTimeStamp: Long): OutputStream {
        throw UnsupportedOperationException()
    }

    @Throws(IOException::class)
    override fun contentsToByteArray(): ByteArray = IOUtils.toByteArray(inputStream)

    override fun getTimeStamp(): Long = mFile!!.lastModified()

    override fun getModificationStamp(): Long = 0

    override fun getLength(): Long {
        return if (mFile == null || mFile!!.isDirectory) 0 else mFile!!.length()
    }

    override fun refresh(asynchronous: Boolean, recursive: Boolean, postRunnable: Runnable?): Unit = TODO()

    @Throws(IOException::class)
    override fun getInputStream(): InputStream = when (isDirectory) {
        true -> throw UnsupportedOperationException()
        else -> mLoader.getResourceAsStream(mResource) ?: throw FileNotFoundException()
    }
}
