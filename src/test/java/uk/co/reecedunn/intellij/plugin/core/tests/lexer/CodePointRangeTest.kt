/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.tests.lexer

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.lexer.CodePointRange
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat

class CodePointRangeTest {
    @Test
    fun testEmptyBuffer() {
        val range = CodePointRange()

        val sequence = ""
        range.start(sequence, 0, 0)
        assertThat(range.bufferSequence, `is`(sequence))
        assertThat(range.bufferEnd, `is`(0))
        assertThat(range.start, `is`(0))
        assertThat(range.end, `is`(0))
    }

    @Test
    fun testMatchingCodePoints() {
        val range = CodePointRange()

        range.start("a\u1255\uD392", 0, 3)
        assertThat(range.bufferEnd, `is`(3))

        assertThat(range.start, `is`(0))
        assertThat(range.end, `is`(0))
        assertThat(range.codePoint, `is`('a'.toInt()))

        range.match()
        assertThat(range.start, `is`(0))
        assertThat(range.end, `is`(1))
        assertThat(range.codePoint, `is`(0x1255))

        range.match()
        assertThat(range.start, `is`(0))
        assertThat(range.end, `is`(2))
        assertThat(range.codePoint, `is`(0xD392))

        range.match()
        assertThat(range.start, `is`(0))
        assertThat(range.end, `is`(3))
        assertThat(range.codePoint, `is`(CodePointRange.END_OF_BUFFER))

        range.match()
        assertThat(range.start, `is`(0))
        assertThat(range.end, `is`(3))
        assertThat(range.codePoint, `is`(CodePointRange.END_OF_BUFFER))
    }

    @Test
    fun testMatchingIncompleteSurrogatePairs() {
        val range = CodePointRange()

        range.start("\uD802\uD803", 0, 2)
        assertThat(range.bufferEnd, `is`(2))

        assertThat(range.start, `is`(0))
        assertThat(range.end, `is`(0))
        assertThat(range.codePoint, `is`(0xD802))

        range.match()
        assertThat(range.start, `is`(0))
        assertThat(range.end, `is`(1))
        assertThat(range.codePoint, `is`(0xD803))

        range.match()
        assertThat(range.start, `is`(0))
        assertThat(range.end, `is`(2))
        assertThat(range.codePoint, `is`(CodePointRange.END_OF_BUFFER))
    }

    @Test
    fun testMatchingSurrogatePairs() {
        val range = CodePointRange()

        range.start("\uD802\uDD07\uD802\uDDA3", 0, 4)
        assertThat(range.bufferEnd, `is`(4))

        assertThat(range.start, `is`(0))
        assertThat(range.end, `is`(0))
        assertThat(range.codePoint, `is`(0x10907))

        range.match()
        assertThat(range.start, `is`(0))
        assertThat(range.end, `is`(2))
        assertThat(range.codePoint, `is`(0x109A3))

        range.match()
        assertThat(range.start, `is`(0))
        assertThat(range.end, `is`(4))
        assertThat(range.codePoint, `is`(CodePointRange.END_OF_BUFFER))

        range.match()
        assertThat(range.start, `is`(0))
        assertThat(range.end, `is`(4))
        assertThat(range.codePoint, `is`(CodePointRange.END_OF_BUFFER))

        range.match()
        assertThat(range.start, `is`(0))
        assertThat(range.end, `is`(4))
        assertThat(range.codePoint, `is`(CodePointRange.END_OF_BUFFER))
    }

    @Test
    fun testFlush() {
        val range = CodePointRange()

        range.start("abcd", 0, 4)
        range.match()
        range.match()

        assertThat(range.start, `is`(0))
        assertThat(range.end, `is`(2))
        assertThat(range.bufferEnd, `is`(4))

        range.flush()

        assertThat(range.start, `is`(2))
        assertThat(range.end, `is`(2))
        assertThat(range.bufferEnd, `is`(4))
    }

    @Test
    fun testSaveRestore() {
        val range = CodePointRange()

        range.start("abcd", 0, 4)
        range.match()

        assertThat(range.start, `is`(0))
        assertThat(range.end, `is`(1))
        assertThat(range.bufferEnd, `is`(4))

        range.save()

        assertThat(range.start, `is`(0))
        assertThat(range.end, `is`(1))
        assertThat(range.bufferEnd, `is`(4))

        range.match()

        assertThat(range.start, `is`(0))
        assertThat(range.end, `is`(2))
        assertThat(range.bufferEnd, `is`(4))

        range.restore()

        assertThat(range.start, `is`(0))
        assertThat(range.end, `is`(1))
        assertThat(range.bufferEnd, `is`(4))
    }
}
