/*
 * Copyright (C) 2016, 2019-2020, 2022 Reece H. Dunn
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

import xqt.platform.xml.model.XmlChar
import xqt.platform.xml.model.XmlCharReader

class CodePointRange {
    private val characters = XmlCharReader()

    val bufferSequence: CharSequence
        get() = characters.buffer

    var start: Int = 0
        private set

    val end: Int
        get() = characters.currentOffset

    private var mSaved: Int = 0

    val bufferEnd: Int
        get() = characters.bufferEndOffset

    val codePoint: XmlChar
        get() = characters.currentChar

    fun start(buffer: CharSequence, startOffset: Int, endOffset: Int) {
        characters.reset(buffer, startOffset, endOffset)
        start = characters.currentOffset
    }

    fun flush() {
        start = characters.currentOffset
    }

    fun match() {
        characters.advance()
    }

    fun seek(position: Int) {
        characters.currentOffset = position
    }

    fun save() {
        mSaved = characters.currentOffset
    }

    fun restore() {
        characters.currentOffset = mSaved
    }

    companion object {
        val END_OF_BUFFER: XmlChar = XmlChar(-1)
    }
}
