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
package uk.co.reecedunn.intellij.plugin.core.zip

import uk.co.reecedunn.intellij.plugin.core.io.NonClosingInputStream
import uk.co.reecedunn.intellij.plugin.core.progress.forEachCancellable
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

private class ZipEntryIterator(private val zip: ZipInputStream) : Iterator<ZipEntry> {
    private var entry: ZipEntry? = zip.nextEntry
    private var consumedNext = false

    override fun hasNext(): Boolean {
        if (consumedNext) {
            consumedNext = false
            entry = zip.nextEntry
        }
        return entry != null
    }

    override fun next(): ZipEntry {
        consumedNext = true
        return entry!!
    }
}

val ZipInputStream.entries: Sequence<ZipEntry>
    get() = ZipEntryIterator(this).asSequence()

fun Sequence<Pair<ZipEntry, ByteArray>>.toZipByteArray(): ByteArray = ByteArrayOutputStream().use { stream ->
    ZipOutputStream(stream).use { zip ->
        forEach { (entry, contents) ->
            zip.putNextEntry(entry)
            zip.write(contents)
            zip.closeEntry()
        }
    }
    stream.toByteArray()
}

fun InputStream.unzip(f: (ZipEntry, InputStream) -> Unit): Unit = use { stream ->
    ZipInputStream(stream).use { zip ->
        val input = NonClosingInputStream(zip)
        zip.entries.forEachCancellable(10) { entry -> f(entry, input) }
    }
}

fun ByteArray.unzip(): List<Pair<ZipEntry, ByteArray>> {
    val contents = ByteArrayOutputStream(DEFAULT_BUFFER_SIZE)
    val ret = ArrayList<Pair<ZipEntry, ByteArray>>()
    ByteArrayInputStream(this).unzip { entry, zip ->
        zip.copyTo(contents)
        ret.add(Pair(entry, contents.toByteArray()))
        contents.reset()
    }
    return ret
}
