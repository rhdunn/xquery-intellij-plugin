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
package uk.co.reecedunn.intellij.plugin.core.vfs

import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.core.zip.toZipByteArray
import uk.co.reecedunn.intellij.plugin.core.zip.unzip
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.ref.WeakReference
import java.util.zip.ZipEntry

class ZipFileSystem private constructor() : VirtualFileSystemImpl("zip") {
    internal val entries = ArrayList<ZipFile>()
    internal val directories = HashMap<String, ZipFile>()

    constructor(zip: InputStream) : this() {
        val fs = WeakReference(this)
        val contents = ByteArrayOutputStream(DEFAULT_BUFFER_SIZE)
        zip.unzip { entry, stream ->
            stream.copyTo(contents)
            val file = ZipFile(entry, contents.toByteArray(), fs)
            contents.reset()
            entries.add(file)
            mkdirs(file, fs)
        }
    }

    constructor(zip: ByteArray) : this(ByteArrayInputStream(zip))

    constructor(zip: VirtualFile) : this(zip.contentsToByteArray())

    private fun mkdirs(dir: ZipFile, fs: WeakReference<ZipFileSystem>) {
        if (directories[dir.parentPath] != null) return

        val entry = ZipEntry(dir.parentPath)
        entry.size = 0
        val parent = ZipFile(entry, DIR_CONTENTS, fs)

        directories[dir.parentPath] = parent
        mkdirs(parent, fs)
    }

    fun save(): ByteArray = entries.asSequence().map { it.toPair() }.toZipByteArray()

    // region VirtualFileSystem

    override fun findFileByPath(path: String): VirtualFile? {
        val entry = directories[path] ?: entries.find { it.path == path }
        if (entry != null) return entry
        if (!path.endsWith('/')) return findFileByPath("$path/")
        return null
    }

    override fun refresh(asynchronous: Boolean) {}

    // endregion

    companion object {
        val DIR_CONTENTS: ByteArray = ByteArray(0)
    }
}
