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
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import uk.co.reecedunn.intellij.plugin.core.vfs.ZipFileSystem
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.core.zip.toZipByteArray
import uk.co.reecedunn.intellij.plugin.core.zip.unzip
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry

@DisplayName("IntelliJ - Base Platform - Files - Virtual File System - Zip File System")
class ZipFileSystemTest {
    @Nested
    @DisplayName("from ByteArray")
    @Suppress("RedundantInnerClassModifier")
    internal inner class FromByteArray {
        @Test
        @DisplayName("empty")
        fun empty() {
            val zip = sequenceOf<Pair<ZipEntry, ByteArray>>().toZipByteArray()
            val fs = ZipFileSystem(zip)

            val entries = fs.save().unzip()
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

            val entries = fs.save().unzip()
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

    @Test
    @DisplayName("file in root directory")
    fun fileInRootDirectory() {
        val zip = sequenceOf(
            ZipEntry("lorem-ipsum.txt") to "Lorem ipsum dolor sed emit...".toByteArray(),
            ZipEntry("hello.txt") to "Hello, world!".toByteArray()
        ).toZipByteArray()
        val fs = ZipFileSystem(zip)

        val entry = fs.findFileByPath("lorem-ipsum.txt")!!
        entry.charset = StandardCharsets.UTF_8

        assertThat(entry.fileSystem, `is`(sameInstance(fs)))
        assertThat(entry.path, `is`("lorem-ipsum.txt"))
        assertThat(entry.name, `is`("lorem-ipsum.txt"))
        assertThat(entry.isWritable, `is`(false))
        assertThat(entry.isDirectory, `is`(false))
        assertThat(entry.isValid, `is`(true))
        assertThat(entry.timeStamp, `is`(not(0L)))
        assertThat(entry.modificationStamp, Is.`is`(0L))
        assertThat(entry.length, `is`(29L))
        assertThat(entry.contentsToByteArray(), `is`("Lorem ipsum dolor sed emit...".toByteArray()))
        assertThat(entry.decode(), `is`("Lorem ipsum dolor sed emit..."))

        assertThat(entry.children.size, `is`(0))

        val parent = entry.parent!!
        assertThat(parent.path, `is`(""))
        assertThat(parent.isDirectory, `is`(true))
        assertThat(parent.children.find { it.path == entry.path }, `is`(sameInstance(entry)))
    }

    @Test
    @DisplayName("file in directory with implicit directory entry")
    fun fileInDirectoryWithImplicitDirectoryEntry() {
        val zip = sequenceOf(
            ZipEntry("contents/lorem-ipsum.txt") to "Lorem ipsum dolor sed emit...".toByteArray(),
            ZipEntry("contents/hello.txt") to "Hello, world!".toByteArray()
        ).toZipByteArray()
        val fs = ZipFileSystem(zip)

        val entry = fs.findFileByPath("contents/lorem-ipsum.txt")!!
        entry.charset = StandardCharsets.UTF_8

        assertThat(entry.fileSystem, `is`(sameInstance(fs)))
        assertThat(entry.path, `is`("contents/lorem-ipsum.txt"))
        assertThat(entry.name, `is`("lorem-ipsum.txt"))
        assertThat(entry.isWritable, `is`(false))
        assertThat(entry.isDirectory, `is`(false))
        assertThat(entry.isValid, `is`(true))
        assertThat(entry.timeStamp, `is`(not(0L)))
        assertThat(entry.modificationStamp, Is.`is`(0L))
        assertThat(entry.length, `is`(29L))
        assertThat(entry.contentsToByteArray(), `is`("Lorem ipsum dolor sed emit...".toByteArray()))
        assertThat(entry.decode(), `is`("Lorem ipsum dolor sed emit..."))

        assertThat(entry.children.size, `is`(0))

        val parent = entry.parent!!
        assertThat(parent.path, `is`("contents/"))
        assertThat(parent.isDirectory, `is`(true))
        assertThat(parent.children.find { it.path == entry.path }, `is`(sameInstance(entry)))
    }

    @Test
    @DisplayName("file in directory with explicit directory entry")
    fun fileInDirectoryWithExplicitDirectoryEntry() {
        val zip = sequenceOf(
            ZipEntry("contents/") to ZipFileSystem.DIR_CONTENTS,
            ZipEntry("contents/lorem-ipsum.txt") to "Lorem ipsum dolor sed emit...".toByteArray(),
            ZipEntry("contents/hello.txt") to "Hello, world!".toByteArray()
        ).toZipByteArray()
        val fs = ZipFileSystem(zip)

        val entry = fs.findFileByPath("contents/lorem-ipsum.txt")!!
        entry.charset = StandardCharsets.UTF_8

        assertThat(entry.fileSystem, `is`(sameInstance(fs)))
        assertThat(entry.path, `is`("contents/lorem-ipsum.txt"))
        assertThat(entry.name, `is`("lorem-ipsum.txt"))
        assertThat(entry.isWritable, `is`(false))
        assertThat(entry.isDirectory, `is`(false))
        assertThat(entry.isValid, `is`(true))
        assertThat(entry.timeStamp, `is`(not(0L)))
        assertThat(entry.modificationStamp, Is.`is`(0L))
        assertThat(entry.length, `is`(29L))
        assertThat(entry.contentsToByteArray(), `is`("Lorem ipsum dolor sed emit...".toByteArray()))
        assertThat(entry.decode(), `is`("Lorem ipsum dolor sed emit..."))

        assertThat(entry.children.size, `is`(0))

        val parent = entry.parent!!
        assertThat(parent.path, `is`("contents/"))
        assertThat(parent.isDirectory, `is`(true))
        assertThat(parent.children.find { it.path == entry.path }, `is`(sameInstance(entry)))
    }

    @Test
    @DisplayName("directory with trailing slash")
    fun directoryWithTrailingSlash() {
        val zip = sequenceOf(
            ZipEntry("contents/") to ZipFileSystem.DIR_CONTENTS,
            ZipEntry("contents/lorem-ipsum.txt") to "Lorem ipsum dolor sed emit...".toByteArray(),
            ZipEntry("contents/hello.txt") to "Hello, world!".toByteArray(),
            ZipEntry("contents/inner/") to ZipFileSystem.DIR_CONTENTS,
            ZipEntry("contents/inner/one.txt") to "lorem".toByteArray(),
            ZipEntry("contents/inner/two.txt") to "ipsum".toByteArray()
        ).toZipByteArray()
        val fs = ZipFileSystem(zip)

        val entry = fs.findFileByPath("contents/")!!
        entry.charset = StandardCharsets.UTF_8

        assertThat(entry.fileSystem, `is`(sameInstance(fs)))
        assertThat(entry.path, `is`("contents/"))
        assertThat(entry.name, `is`("contents"))
        assertThat(entry.isWritable, `is`(false))
        assertThat(entry.isDirectory, `is`(true))
        assertThat(entry.isValid, `is`(true))
        assertThat(entry.timeStamp, `is`(not(0L)))
        assertThat(entry.modificationStamp, Is.`is`(0L))
        assertThat(entry.length, `is`(0L))
        assertThrows<UnsupportedOperationException> { entry.contentsToByteArray() }
        assertThrows<UnsupportedOperationException> { entry.decode() }

        val children = entry.children
        assertThat(children.size, `is`(3))

        assertThat(children[0].path, `is`("contents/lorem-ipsum.txt"))
        assertThat(children[0].isDirectory, `is`(false))
        assertThat(children[0].parent, `is`(sameInstance(entry)))

        assertThat(children[1].path, `is`("contents/hello.txt"))
        assertThat(children[1].isDirectory, `is`(false))
        assertThat(children[1].parent, `is`(sameInstance(entry)))

        assertThat(children[2].path, `is`("contents/inner/"))
        assertThat(children[2].isDirectory, `is`(true))
        assertThat(children[2].parent, `is`(sameInstance(entry)))

        val parent = entry.parent!!
        assertThat(parent.path, `is`(""))
        assertThat(parent.isDirectory, `is`(true))
        assertThat(parent.children.find { it.path == entry.path }, `is`(sameInstance(entry)))
    }

    @Test
    @DisplayName("directory without trailing slash")
    fun directoryWithoutTrailingSlash() {
        val zip = sequenceOf(
            ZipEntry("contents/") to ZipFileSystem.DIR_CONTENTS,
            ZipEntry("contents/lorem-ipsum.txt") to "Lorem ipsum dolor sed emit...".toByteArray(),
            ZipEntry("contents/hello.txt") to "Hello, world!".toByteArray(),
            ZipEntry("contents/inner/") to ZipFileSystem.DIR_CONTENTS,
            ZipEntry("contents/inner/one.txt") to "lorem".toByteArray(),
            ZipEntry("contents/inner/two.txt") to "ipsum".toByteArray()
        ).toZipByteArray()
        val fs = ZipFileSystem(zip)

        val entry = fs.findFileByPath("contents")!!
        entry.charset = StandardCharsets.UTF_8

        assertThat(entry.fileSystem, `is`(sameInstance(fs)))
        assertThat(entry.path, `is`("contents/"))
        assertThat(entry.name, `is`("contents"))
        assertThat(entry.isWritable, `is`(false))
        assertThat(entry.isDirectory, `is`(true))
        assertThat(entry.isValid, `is`(true))
        assertThat(entry.timeStamp, `is`(not(0L)))
        assertThat(entry.modificationStamp, Is.`is`(0L))
        assertThat(entry.length, `is`(0L))
        assertThrows<UnsupportedOperationException> { entry.contentsToByteArray() }
        assertThrows<UnsupportedOperationException> { entry.decode() }

        val children = entry.children
        assertThat(children.size, `is`(3))

        assertThat(children[0].path, `is`("contents/lorem-ipsum.txt"))
        assertThat(children[0].isDirectory, `is`(false))
        assertThat(children[0].parent, `is`(sameInstance(entry)))

        assertThat(children[1].path, `is`("contents/hello.txt"))
        assertThat(children[1].isDirectory, `is`(false))
        assertThat(children[1].parent, `is`(sameInstance(entry)))

        assertThat(children[2].path, `is`("contents/inner/"))
        assertThat(children[2].isDirectory, `is`(true))
        assertThat(children[2].parent, `is`(sameInstance(entry)))

        val parent = entry.parent!!
        assertThat(parent.path, `is`(""))
        assertThat(parent.isDirectory, `is`(true))
        assertThat(parent.children.find { it.path == entry.path }, `is`(sameInstance(entry)))
    }

    @Test
    @DisplayName("nested directory with trailing slash")
    fun nestedDirectoryWithTrailingSlash() {
        val zip = sequenceOf(
            ZipEntry("contents/") to ZipFileSystem.DIR_CONTENTS,
            ZipEntry("contents/test/") to ZipFileSystem.DIR_CONTENTS,
            ZipEntry("contents/test/lorem-ipsum.txt") to "Lorem ipsum dolor sed emit...".toByteArray(),
            ZipEntry("contents/test/hello.txt") to "Hello, world!".toByteArray(),
            ZipEntry("contents/test/inner/") to ZipFileSystem.DIR_CONTENTS,
            ZipEntry("contents/test/inner/one.txt") to "lorem".toByteArray(),
            ZipEntry("contents/test/inner/two.txt") to "ipsum".toByteArray()
        ).toZipByteArray()
        val fs = ZipFileSystem(zip)

        val entry = fs.findFileByPath("contents/test/")!!
        entry.charset = StandardCharsets.UTF_8

        assertThat(entry.fileSystem, `is`(sameInstance(fs)))
        assertThat(entry.path, `is`("contents/test/"))
        assertThat(entry.name, `is`("test"))
        assertThat(entry.isWritable, `is`(false))
        assertThat(entry.isDirectory, `is`(true))
        assertThat(entry.isValid, `is`(true))
        assertThat(entry.timeStamp, `is`(not(0L)))
        assertThat(entry.modificationStamp, Is.`is`(0L))
        assertThat(entry.length, `is`(0L))
        assertThrows<UnsupportedOperationException> { entry.contentsToByteArray() }
        assertThrows<UnsupportedOperationException> { entry.decode() }

        val children = entry.children
        assertThat(children.size, `is`(3))

        assertThat(children[0].path, `is`("contents/test/lorem-ipsum.txt"))
        assertThat(children[0].isDirectory, `is`(false))
        assertThat(children[0].parent, `is`(sameInstance(entry)))

        assertThat(children[1].path, `is`("contents/test/hello.txt"))
        assertThat(children[1].isDirectory, `is`(false))
        assertThat(children[1].parent, `is`(sameInstance(entry)))

        assertThat(children[2].path, `is`("contents/test/inner/"))
        assertThat(children[2].isDirectory, `is`(true))
        assertThat(children[2].parent, `is`(sameInstance(entry)))

        val parent = entry.parent!!
        assertThat(parent.path, `is`("contents/"))
        assertThat(parent.isDirectory, `is`(true))
        assertThat(parent.children.find { it.path == entry.path }, `is`(sameInstance(entry)))
    }

    @Test
    @DisplayName("root directory with explicit directory entry")
    fun rootDirectoryWithExplicitDirectoryEntry() {
        val zip = sequenceOf(
            ZipEntry("lorem-ipsum.txt") to "Lorem ipsum dolor sed emit...".toByteArray(),
            ZipEntry("hello.txt") to "Hello, world!".toByteArray(),
            ZipEntry("contents/") to ZipFileSystem.DIR_CONTENTS,
            ZipEntry("contents/one.txt") to "lorem".toByteArray(),
            ZipEntry("contents/two.txt") to "ipsum".toByteArray()
        ).toZipByteArray()
        val fs = ZipFileSystem(zip)

        val entry = fs.findFileByPath("")!!
        entry.charset = StandardCharsets.UTF_8

        assertThat(entry.fileSystem, `is`(sameInstance(fs)))
        assertThat(entry.path, `is`(""))
        assertThat(entry.name, `is`(""))
        assertThat(entry.isWritable, `is`(false))
        assertThat(entry.isDirectory, `is`(true))
        assertThat(entry.isValid, `is`(true))
        assertThat(entry.timeStamp, `is`(not(0L)))
        assertThat(entry.modificationStamp, Is.`is`(0L))
        assertThat(entry.length, `is`(0L))
        assertThrows<UnsupportedOperationException> { entry.contentsToByteArray() }
        assertThrows<UnsupportedOperationException> { entry.decode() }

        val children = entry.children
        assertThat(children.size, `is`(3))

        assertThat(children[0].path, `is`("lorem-ipsum.txt"))
        assertThat(children[0].isDirectory, `is`(false))
        assertThat(children[0].parent, `is`(sameInstance(entry)))

        assertThat(children[1].path, `is`("hello.txt"))
        assertThat(children[1].isDirectory, `is`(false))
        assertThat(children[1].parent, `is`(sameInstance(entry)))

        assertThat(children[2].path, `is`("contents/"))
        assertThat(children[2].isDirectory, `is`(true))
        assertThat(children[2].parent, `is`(sameInstance(entry)))

        assertThat(entry.parent, `is`(nullValue()))
    }

    @Test
    @DisplayName("root directory with implicit directory entry")
    fun rootDirectoryWithImplicitDirectoryEntry() {
        val zip = sequenceOf(
            ZipEntry("lorem-ipsum.txt") to "Lorem ipsum dolor sed emit...".toByteArray(),
            ZipEntry("hello.txt") to "Hello, world!".toByteArray(),
            ZipEntry("contents/one.txt") to "lorem".toByteArray(),
            ZipEntry("contents/two.txt") to "ipsum".toByteArray()
        ).toZipByteArray()
        val fs = ZipFileSystem(zip)

        val entry = fs.findFileByPath("")!!
        entry.charset = StandardCharsets.UTF_8

        assertThat(entry.fileSystem, `is`(sameInstance(fs)))
        assertThat(entry.path, `is`(""))
        assertThat(entry.name, `is`(""))
        assertThat(entry.isWritable, `is`(false))
        assertThat(entry.isDirectory, `is`(true))
        assertThat(entry.isValid, `is`(true))
        assertThat(entry.timeStamp, `is`(not(0L)))
        assertThat(entry.modificationStamp, Is.`is`(0L))
        assertThat(entry.length, `is`(0L))
        assertThrows<UnsupportedOperationException> { entry.contentsToByteArray() }
        assertThrows<UnsupportedOperationException> { entry.decode() }

        val children = entry.children
        assertThat(children.size, `is`(3))

        assertThat(children[0].path, `is`("lorem-ipsum.txt"))
        assertThat(children[0].isDirectory, `is`(false))
        assertThat(children[0].parent, `is`(sameInstance(entry)))

        assertThat(children[1].path, `is`("hello.txt"))
        assertThat(children[1].isDirectory, `is`(false))
        assertThat(children[1].parent, `is`(sameInstance(entry)))

        assertThat(children[2].path, `is`("contents/"))
        assertThat(children[2].isDirectory, `is`(true))
        assertThat(children[2].parent, `is`(sameInstance(entry)))

        assertThat(entry.parent, `is`(nullValue()))
    }
}
