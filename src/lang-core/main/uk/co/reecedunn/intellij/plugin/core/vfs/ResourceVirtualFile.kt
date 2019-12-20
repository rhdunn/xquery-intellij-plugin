/*
 * Copyright (C) 2016, 2018-2019 Reece H. Dunn
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
import org.apache.commons.compress.utils.IOUtils

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.JarURLConnection

class ResourceVirtualFile private constructor(
    private val mLoader: ClassLoader,
    private val mResource: String,
    private val mFileSystem: VirtualFileSystem?,
    private var mFile: File?,
    private var mPath: String?
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

    override fun getFileSystem(): VirtualFileSystem = mFileSystem ?: throw UnsupportedOperationException()

    override fun getPath(): String = mPath ?: ""

    override fun isWritable(): Boolean = false

    override fun isDirectory(): Boolean = mFile != null && mFile!!.isDirectory

    override fun isValid(): Boolean = mFile != null

    override fun getParent(): VirtualFile? {
        return if (mParent == null) null else create(mLoader, mParent!!, mFileSystem)
    }

    override fun getChildren(): Array<VirtualFile>? {
        if (mFile == null) {
            return null
        }

        val children = mFile!!.list() ?: return null
        return children.map { child -> create(mLoader, "$mResource/$child", mFileSystem) }.toTypedArray()
    }

    @Throws(IOException::class)
    override fun getOutputStream(requestor: Any, newModificationStamp: Long, newTimeStamp: Long): OutputStream {
        throw UnsupportedOperationException()
    }

    @Throws(IOException::class)
    override fun contentsToByteArray(): ByteArray {
        val input = inputStream ?: throw UnsupportedOperationException()
        return IOUtils.toByteArray(input)
    }

    override fun getTimeStamp(): Long = mFile!!.lastModified()

    override fun getModificationStamp(): Long = 0

    override fun getLength(): Long {
        return if (mFile == null || mFile!!.isDirectory) 0 else mFile!!.length()
    }

    override fun refresh(asynchronous: Boolean, recursive: Boolean, postRunnable: Runnable?) = TODO()

    @Throws(IOException::class)
    override fun getInputStream(): InputStream? {
        return if (isDirectory) null else mLoader.getResourceAsStream(mResource)
    }

    companion object {
        fun create(loader: ClassLoader, resource: String, fileSystem: VirtualFileSystem? = null): ResourceVirtualFile {
            return createIfValid(loader, resource, fileSystem)
                ?: ResourceVirtualFile(loader, resource, fileSystem, null, null)
        }

        fun createIfValid(
            loader: ClassLoader,
            resource: String,
            fileSystem: VirtualFileSystem? = null
        ): ResourceVirtualFile? {
            return loader.getResource(resource)?.let {
                when (it.protocol) {
                    "file" -> {
                        val file = File(it.toURI())
                        ResourceVirtualFile(loader, resource, fileSystem, file, file.path)
                    }
                    "jar" -> {
                        val connection = it.openConnection() as JarURLConnection
                        val file = File(connection.jarFileURL.toURI())
                        ResourceVirtualFile(loader, resource, fileSystem, file, "${file.path}!/$resource")
                    }
                    else -> null
                }
            }
        }
    }
}
