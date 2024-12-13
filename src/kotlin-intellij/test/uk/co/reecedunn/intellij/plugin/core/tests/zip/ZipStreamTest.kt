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
package uk.co.reecedunn.intellij.plugin.core.tests.zip

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.zip.toZipByteArray
import uk.co.reecedunn.intellij.plugin.core.zip.unzip
import java.util.zip.ZipEntry

@DisplayName("IntelliJ - Base Platform - Files - Virtual File System - Zip Streams")
class ZipStreamTest {
    @Test
    @DisplayName("empty")
    fun empty() {
        val zip = sequenceOf<Pair<ZipEntry, ByteArray>>().toZipByteArray()
        assertThat(zip.size, `is`(22))

        val entries = zip.unzip()
        assertThat(entries.size, `is`(0))
    }

    @Test
    @DisplayName("single file")
    fun singleFile() {
        val zip = sequenceOf(
            ZipEntry("text.txt") to "one, one, two, one".toByteArray()
        ).toZipByteArray()
        assertThat(zip.size, `is`(144))

        val entries = zip.unzip()
        assertThat(entries.size, `is`(1))

        assertThat(entries[0].first.name, `is`("text.txt"))
        assertThat(entries[0].first.isDirectory, `is`(false))
        assertThat(entries[0].first.compressedSize, `is`(14L))
        assertThat(entries[0].first.size, `is`(18L))
        assertThat(entries[0].second, `is`("one, one, two, one".toByteArray()))
    }

    @Test
    @DisplayName("single directory")
    fun singleDirectory() {
        val zip = sequenceOf(
            ZipEntry("contents/") to ByteArray(0)
        ).toZipByteArray()
        assertThat(zip.size, `is`(134))

        val entries = zip.unzip()
        assertThat(entries.size, `is`(1))

        assertThat(entries[0].first.name, `is`("contents/"))
        assertThat(entries[0].first.isDirectory, `is`(true))
        assertThat(entries[0].first.compressedSize, `is`(2L))
        assertThat(entries[0].first.size, `is`(0L))
        assertThat(entries[0].second, `is`(ByteArray(0)))
    }

    @Test
    @DisplayName("multiple files")
    fun multipleFiles() {
        val zip = sequenceOf(
            ZipEntry("lorem-ipsum.txt") to "Lorem ipsum dolor sed emit...".toByteArray(),
            ZipEntry("hello.txt") to "Hello, world!".toByteArray()
        ).toZipByteArray()
        assertThat(zip.size, `is`(300))

        val entries = zip.unzip()
        assertThat(entries.size, `is`(2))

        assertThat(entries[0].first.name, `is`("lorem-ipsum.txt"))
        assertThat(entries[0].first.isDirectory, `is`(false))
        assertThat(entries[0].first.compressedSize, `is`(31L))
        assertThat(entries[0].first.size, `is`(29L))
        assertThat(entries[0].second, `is`("Lorem ipsum dolor sed emit...".toByteArray()))

        assertThat(entries[1].first.name, `is`("hello.txt"))
        assertThat(entries[1].first.isDirectory, `is`(false))
        assertThat(entries[1].first.compressedSize, `is`(15L))
        assertThat(entries[1].first.size, `is`(13L))
        assertThat(entries[1].second, `is`("Hello, world!".toByteArray()))
    }
}
