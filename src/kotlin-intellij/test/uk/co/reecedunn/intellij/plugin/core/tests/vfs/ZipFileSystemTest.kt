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
package uk.co.reecedunn.intellij.plugin.core.tests.vfs

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ZipFileSystem
import uk.co.reecedunn.intellij.plugin.core.zip.toZipByteArray
import uk.co.reecedunn.intellij.plugin.core.zip.unzip
import java.util.zip.ZipEntry

@DisplayName("IntelliJ - Base Platform - Files - Virtual File System - Zip File System")
class ZipFileSystemTest {
    @Nested
    @DisplayName("from ByteArray")
    internal inner class FromByteArray {
        @Test
        @DisplayName("empty")
        fun empty() {
            val zip = sequenceOf<Pair<ZipEntry, ByteArray>>().toZipByteArray()
            val fs = ZipFileSystem(zip)

            val entries = fs.save().unzip().toList()
            assertThat(entries.size, `is`(0))

            assertThat(fs.findFileByPath("lorem-ipsum.txt"), `is`(nullValue()))
            assertThat(fs.findFileByPath("hello.txt"), `is`(nullValue()))
            assertThat(fs.findFileByPath("test.txt"), `is`(nullValue()))
        }

        @Test
        @DisplayName("files")
        fun files() {
            val zip = sequenceOf(
                ZipEntry("lorem-ipsum.txt") to "Lorem ipsum dolor sed emit...".toByteArray(),
                ZipEntry("hello.txt") to "Hello, world!".toByteArray()
            ).toZipByteArray()
            val fs = ZipFileSystem(zip)

            val entries = fs.save().unzip().toList()
            assertThat(entries.size, `is`(2))
            assertThat(entries[0].first.name, `is`("lorem-ipsum.txt"))
            assertThat(entries[0].second, `is`("Lorem ipsum dolor sed emit...".toByteArray()))
            assertThat(entries[1].first.name, `is`("hello.txt"))
            assertThat(entries[1].second, `is`("Hello, world!".toByteArray()))

            assertThat(fs.findFileByPath("lorem-ipsum.txt"), `is`(not(nullValue())))
            assertThat(fs.findFileByPath("hello.txt"), `is`(not(nullValue())))
            assertThat(fs.findFileByPath("test.txt"), `is`(nullValue()))
        }
    }
}
