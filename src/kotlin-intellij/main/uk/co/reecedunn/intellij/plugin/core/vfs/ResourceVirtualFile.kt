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
package uk.co.reecedunn.intellij.plugin.core.vfs

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import uk.co.reecedunn.intellij.plugin.core.vfs.impl.ResourceVirtualFileImpl
import java.io.*

import java.lang.ref.WeakReference
import java.net.JarURLConnection

object ResourceVirtualFile {
    fun create(loader: ClassLoader, resource: String, fileSystem: VirtualFileSystem? = null): VirtualFile {
        return create(loader, resource, fileSystem?.let { WeakReference(it) })
    }

    internal fun create(
        loader: ClassLoader,
        resource: String,
        fileSystem: WeakReference<VirtualFileSystem>?
    ): VirtualFile {
        return createIfValid(loader, resource, fileSystem)
            ?: ResourceVirtualFileImpl(loader, resource, fileSystem, null, "")
    }

    fun createIfValid(
        loader: ClassLoader,
        resource: String,
        fileSystem: VirtualFileSystem? = null
    ): VirtualFile? {
        return createIfValid(loader, resource, fileSystem?.let { WeakReference(it) })
    }

    private fun createIfValid(
        loader: ClassLoader,
        resource: String,
        fileSystem: WeakReference<VirtualFileSystem>?
    ): VirtualFile? {
        return loader.getResource(resource)?.let {
            when (it.protocol) {
                "file" -> {
                    val file = File(it.toURI())
                    ResourceVirtualFileImpl(loader, resource, fileSystem, file, file.path)
                }
                "jar" -> {
                    val connection = it.openConnection() as JarURLConnection
                    val file = File(connection.jarFileURL.toURI())
                    ResourceVirtualFileImpl(loader, resource, fileSystem, file, "${file.path}!/$resource")
                }
                else -> null
            }
        }
    }
}
