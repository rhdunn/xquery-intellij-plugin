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
import uk.co.reecedunn.intellij.plugin.core.zip.entries
import uk.co.reecedunn.intellij.plugin.core.zip.toZipByteArray
import java.io.ByteArrayInputStream
import java.lang.ref.WeakReference
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class ZipFileSystem private constructor() : VirtualFileSystemImpl("zip") {
    private val entries = ArrayList<ZipFile>()

    constructor(zip: ByteArray) : this() {
        return ByteArrayInputStream(zip).use { stream ->
            ZipInputStream(stream).use { zip ->
                val fs = WeakReference(this)
                zip.entries.forEach { (entry, contents) ->
                    entries.add(ZipFile(entry, contents, fs))
                }
            }
        }
    }

    fun save(): ByteArray = entries.asSequence().map { it.toPair() }.toZipByteArray()

    // region VirtualFileSystem

    override fun findFileByPath(path: String): VirtualFile? {
        val entry = entries.find { it.path == path }
        if (entry != null) return entry
        if (!path.endsWith('/')) return findFileByPath("$path/")
        return null
    }

    override fun refresh(asynchronous: Boolean) {}

    // endregion
}
