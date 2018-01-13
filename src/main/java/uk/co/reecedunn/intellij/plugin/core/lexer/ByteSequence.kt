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
package uk.co.reecedunn.intellij.plugin.core.lexer

import kotlin.experimental.and

class ByteSequence private constructor(private val mData: ByteArray,
                                       private val mOffset: Int,
                                       private val mLength: Int) : CharSequence {

    constructor(data: ByteArray) : this(data, 0, data.size)

    override val length get(): Int = mLength

    override fun get(index: Int): Char {
        if (index < 0 || index >= mLength) {
            throw IndexOutOfBoundsException(index.toString())
        }

        return (mData[mOffset + index] and 0xFF.toByte()).toChar()
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        if (startIndex < 0 || startIndex > mLength) {
            throw IndexOutOfBoundsException(startIndex.toString())
        }

        if (endIndex > mLength) {
            throw IndexOutOfBoundsException(endIndex.toString())
        }

        val length = endIndex - startIndex
        if (length < 0) {
            throw IndexOutOfBoundsException(length.toString())
        }

        return ByteSequence(mData, mOffset + startIndex, length)
    }

    override fun toString(): String = String(mData, mOffset, mLength)
}
