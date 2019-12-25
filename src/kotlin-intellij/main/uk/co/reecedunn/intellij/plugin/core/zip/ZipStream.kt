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
package uk.co.reecedunn.intellij.plugin.core.zip

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

private class ZipEntryIterator(private val zip: ZipInputStream) : Iterator<Pair<ZipEntry, ByteArray>> {
    private var entry: ZipEntry? = zip.nextEntry

    override fun hasNext(): Boolean = entry != null

    override fun next(): Pair<ZipEntry, ByteArray> {
        val ret = Pair(entry!!, zip.readAllBytes())
        entry = zip.nextEntry
        return ret
    }
}

val ZipInputStream.entries: Sequence<Pair<ZipEntry, ByteArray>> get() = ZipEntryIterator(this).asSequence()

fun Sequence<Pair<ZipEntry, ByteArray>>.toZipByteArray(): ByteArray {
    return ByteArrayOutputStream().use { stream ->
        ZipOutputStream(stream).use { zip ->
            forEach { (entry, contents) ->
                zip.putNextEntry(entry)
                zip.write(contents)
                zip.closeEntry()
            }
        }
        stream.toByteArray()
    }
}

fun ByteArray.unzip(): Sequence<Pair<ZipEntry, ByteArray>> {
    return ByteArrayInputStream(this).use { stream ->
        ZipInputStream(stream).use { zip ->
            zip.entries.toList().asSequence()
        }
    }
}
