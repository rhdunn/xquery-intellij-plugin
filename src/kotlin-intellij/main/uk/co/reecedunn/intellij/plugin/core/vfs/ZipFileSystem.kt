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

import uk.co.reecedunn.intellij.plugin.core.zip.entries
import uk.co.reecedunn.intellij.plugin.core.zip.toZipByteArray
import java.io.ByteArrayInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class ZipFileSystem {
    private val entries = ArrayList<Pair<ZipEntry, ByteArray>>()

    constructor(zip: ByteArray) {
        return ByteArrayInputStream(zip).use { stream ->
            ZipInputStream(stream).use { zip ->
                zip.entries.forEach { (entry, contents) ->
                    entries.add(entry to contents)
                }
            }
        }
    }

    fun save(): ByteArray = entries.asSequence().toZipByteArray()
}
