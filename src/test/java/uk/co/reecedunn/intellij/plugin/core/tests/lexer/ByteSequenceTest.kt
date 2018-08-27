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
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.lexer.ByteSequence

class ByteSequenceTest {
    @Test
    fun testConstruction() {
        val data = byteArrayOf(0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39)
        val b = ByteSequence(data)

        assertThat(b.length, `is`(10))
        assertThat(b.toString(), `is`("0123456789"))

        assertThat(b[0], `is`('0'))
        assertThat(b[9], `is`('9'))
    }

    @Test
    fun testSubSequence() {
        val data = byteArrayOf(0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39)
        val b = ByteSequence(data)
        val c = b.subSequence(2, 8)

        assertThat(c.length, `is`(6))
        assertThat(c.toString(), `is`("234567"))

        assertThat(c[0], `is`('2'))
        assertThat(c[5], `is`('7'))

        assertThat(b.subSequence(10, 10).length, `is`(0))
        assertThat(b.subSequence(10, 10).toString(), `is`(""))
    }

    @Test
    fun testSubSubSequence() {
        val data = byteArrayOf(0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39)
        val b = ByteSequence(data)
        val c = b.subSequence(2, 8)
        val d = c.subSequence(2, 5)

        assertThat(d.length, `is`(3))
        assertThat(d.toString(), `is`("456"))

        assertThat(d[0], `is`('4'))
        assertThat(d[2], `is`('6'))
    }

    @Test
    fun testSubSequence_OutOfBounds() {
        val data = byteArrayOf(0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39)
        val b = ByteSequence(data)

        val e1 = assertThrows(IndexOutOfBoundsException::class.java) { b.subSequence(-1, 0) }
        assertThat<String>(e1.message, `is`("-1"))

        val e2 = assertThrows(IndexOutOfBoundsException::class.java) { b.subSequence(11, 11) }
        assertThat<String>(e2.message, `is`("11"))

        val e3 = assertThrows(IndexOutOfBoundsException::class.java) { b.subSequence(0, 11) }
        assertThat<String>(e3.message, `is`("11"))

        val e5 = assertThrows(IndexOutOfBoundsException::class.java) { b.subSequence(1, 11) }
        assertThat<String>(e5.message, `is`("11"))

        val e4 = assertThrows(IndexOutOfBoundsException::class.java) { b.subSequence(6, 4) }
        assertThat<String>(e4.message, `is`("-2"))
    }

    @Test
    fun testCharAt_OutOfBounds() {
        val data = byteArrayOf(0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39)
        val b = ByteSequence(data)

        val e1 = assertThrows(IndexOutOfBoundsException::class.java) { b[-1] }
        assertThat<String>(e1.message, `is`("-1"))

        val e2 = assertThrows(IndexOutOfBoundsException::class.java) { b[10] }
        assertThat<String>(e2.message, `is`("10"))
    }

    @Test
    fun testCharAt_SubSequence_OutOfBounds() {
        val data = byteArrayOf(0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39)
        val b = ByteSequence(data)
        val c = b.subSequence(2, 8)

        val e1 = assertThrows(IndexOutOfBoundsException::class.java) { c[-1] }
        assertThat<String>(e1.message, `is`("-1"))

        val e2 = assertThrows(IndexOutOfBoundsException::class.java) { c[6] }
        assertThat<String>(e2.message, `is`("6"))
    }
}
