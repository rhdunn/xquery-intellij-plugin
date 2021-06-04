/*
 * Copyright (C) 2019, 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.lexer

import uk.co.reecedunn.intellij.plugin.core.lexer.CodePointRange
import uk.co.reecedunn.intellij.plugin.core.lexer.CodePointRangeImpl
import uk.co.reecedunn.intellij.plugin.core.lexer.EntityReferenceType
import uk.co.reecedunn.intellij.plugin.core.lexer.matchEntityReference

class XmlCodePointRangeImpl : CodePointRange {
    private val range: CodePointRange = CodePointRangeImpl()

    override val bufferSequence: CharSequence
        get() = range.bufferSequence

    override val start: Int
        get() = range.start

    override val end: Int
        get() = range.end

    override val bufferEnd: Int
        get() = range.bufferEnd

    override val codePoint: Int
        get() {
            val c = range.codePoint
            if (c == '&'.toInt()) {
                val savedEnd = end
                range.match()
                val ret = when (range.matchEntityReference()) {
                    EntityReferenceType.PredefinedEntityReference -> {
                        when (bufferSequence.subSequence(savedEnd, end)) {
                            "&amp;" -> 38
                            "&apos;" -> 39
                            "&gt;" -> 62
                            "&lt;" -> 60
                            "&quot;" -> 34
                            else -> 0xFFFE
                        }
                    }
                    else -> 38
                }
                seek(savedEnd)
                return ret
            }
            return c
        }

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int) {
        range.start(buffer, startOffset, endOffset)
    }

    override fun flush() {
        range.flush()
    }

    override fun match() {
        val c = range.codePoint
        if (c == '&'.toInt()) {
            range.match()
            val savedEnd = end
            if (range.matchEntityReference() !== EntityReferenceType.PredefinedEntityReference) {
                seek(savedEnd)
            }
        } else {
            range.match()
        }
    }

    override fun seek(position: Int) {
        range.seek(position)
    }

    override fun save() {
        range.save()
    }

    override fun restore() {
        range.restore()
    }
}
