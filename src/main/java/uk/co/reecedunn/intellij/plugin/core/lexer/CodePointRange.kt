/*
 * Copyright (C) 2016 Reece H. Dunn
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

class CodePointRange {
    var bufferSequence: CharSequence = ""
        private set

    var start: Int = 0
        private set

    var end: Int = 0
        private set

    private var mSaved: Int = 0

    var bufferEnd: Int = 0
        private set

    val codePoint get(): Int {
        if (end == bufferEnd)
            return END_OF_BUFFER
        val high = bufferSequence[end]
        if (Character.isHighSurrogate(high) && end + 1 != bufferEnd) {
            val low = bufferSequence[end + 1]
            if (Character.isLowSurrogate(low)) {
                return Character.toCodePoint(high, low)
            }
        }
        return high.toInt()
    }

    fun start(buffer: CharSequence, startOffset: Int, endOffset: Int) {
        bufferSequence = buffer
        end = startOffset
        start = end
        bufferEnd = endOffset
    }

    fun flush() {
        start = end
    }

    fun match() {
        if (end != bufferEnd) {
            if (Character.isHighSurrogate(bufferSequence[end])) {
                end += 1
                if (end != bufferEnd && Character.isLowSurrogate(bufferSequence[end]))
                    end += 1
            } else {
                end += 1
            }
        }
    }

    fun seek(position: Int) {
        end = position
    }

    fun save() {
        mSaved = end
    }

    fun restore() {
        end = mSaved
    }

    companion object {
        const val END_OF_BUFFER = -1
    }
}
