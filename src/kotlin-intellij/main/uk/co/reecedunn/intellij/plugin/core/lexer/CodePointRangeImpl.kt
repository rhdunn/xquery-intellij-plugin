/*
 * Copyright (C) 2016, 2019 Reece H. Dunn
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

class CodePointRangeImpl : CodePointRange {
    override var bufferSequence: CharSequence = ""
        private set

    override var start: Int = 0
        private set

    override var end: Int = 0
        private set

    private var mSaved: Int = 0

    override var bufferEnd: Int = 0
        private set

    override val codePoint
        get(): Int {
            if (end == bufferEnd)
                return CodePointRange.END_OF_BUFFER
            val high = bufferSequence[end]
            if (Character.isHighSurrogate(high) && end + 1 != bufferEnd) {
                val low = bufferSequence[end + 1]
                if (Character.isLowSurrogate(low)) {
                    return Character.toCodePoint(high, low)
                }
            }
            return high.toInt()
        }

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int) {
        bufferSequence = buffer
        end = startOffset
        start = end
        bufferEnd = endOffset
    }

    override fun flush() {
        start = end
    }

    override fun match() {
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

    override fun seek(position: Int) {
        end = position
    }

    override fun save() {
        mSaved = end
    }

    override fun restore() {
        end = mSaved
    }
}
