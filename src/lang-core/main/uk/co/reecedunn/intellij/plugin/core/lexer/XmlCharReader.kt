// Copyright (C) 2022 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.lexer

/**
 * Creates a reader that enumerates the codepoints in a character sequence.
 */
class XmlCharReader {
    /**
     * Reset the reader to the specified character sequence.
     *
     * @param buffer The character sequence to read from.
     * @param startOffset The offset of the first codepoint in the buffer to read from.
     * @param bufferEndOffset The offset of the last codepoint in the buffer to read up to.
     */
    fun reset(buffer: CharSequence, startOffset: Int, bufferEndOffset: Int) {
        this.buffer = buffer
        this.nextOffset = startOffset
        this.bufferEndOffset = bufferEndOffset
        advance()
    }

    /**
     * Reset the reader to the specified character sequence.
     *
     * @param buffer The character sequence to read from.
     * @param startOffset The offset of the first codepoint in the buffer to read from.
     */
    fun reset(buffer: CharSequence, startOffset: Int = 0): Unit = reset(buffer, startOffset, buffer.length)

    /**
     * Advance to the next XmlChar in the buffer.
     */
    fun advance() {
        currentOffset = nextOffset
    }

    /**
     * Advances to the next XmlChar in the buffer that does not match the predicate.
     *
     * This can be used to implement `T+` and `T*` constructs in lexical tokens.
     * For example, the lexical token:
     *
     *     Digits := [0-9]+
     *
     * can be implemented as:
     *
     *     if (reader.currentChar in Digit) {
     *         reader.advanceWhile { it in Digit }
     *     }
     */
    fun advanceWhile(predicate: (XmlChar) -> Boolean) {
        while (predicate(currentChar))
            advance()
    }

    /**
     * Advances to the next XmlChar in the buffer that matches the predicate.
     *
     * This can be used to implement `Char - T` constructs in lexical tokens.
     */
    fun advanceUntil(predicate: (XmlChar) -> Boolean) {
        while (!predicate(currentChar))
            advance()
    }

    /**
     * The underlying UTF-16 character sequence.
     */
    var buffer: CharSequence = ""
        private set

    /**
     * The end of the buffer, or the point to read up to.
     */
    var bufferEndOffset: Int = 0
        private set

    /**
     * The offset of the current XmlChar in the buffer.
     */
    var currentOffset: Int = 0
        set(offset) {
            field = offset
            updateState(offset)
        }

    /**
     * The value of the current XmlChar in the buffer.
     */
    var currentChar: XmlChar = EndOfBuffer
        private set

    /**
     * Returns the next character that would be read from the advance method.
     *
     * This method does not update the current offset.
     */
    val nextChar: XmlChar
        get() = peek(0)

    /**
     * Returns the character at the given offset from the current position.
     *
     * This method does not update the current offset.
     */
    fun peek(offset: Int): XmlChar {
        val peekOffset = nextOffset + offset
        if (peekOffset >= bufferEndOffset) {
            return EndOfBuffer
        }

        val high = buffer[peekOffset]
        if (high.code in HighSurrogate && peekOffset + 1 != bufferEndOffset) {
            val low = buffer[peekOffset + 1]
            if (low.code in LowSurrogate) {
                return XmlChar(high, low)
            }
        }

        return XmlChar(high)
    }

    private var nextOffset: Int = 0

    private fun updateState(offset: Int) {
        if (offset >= bufferEndOffset) {
            currentChar = EndOfBuffer
            nextOffset = bufferEndOffset
            return
        }

        val high = buffer[offset]
        if (high.code in HighSurrogate && offset + 1 != bufferEndOffset) {
            val low = buffer[offset + 1]
            if (low.code in LowSurrogate) {
                currentChar = XmlChar(high, low)
                nextOffset = offset + 2
                return
            }
        }

        currentChar = XmlChar(high)
        nextOffset = offset + 1
    }

    companion object {
        /**
         * Represents the end of the character sequence.
         */
        val EndOfBuffer: XmlChar = XmlChar(-1)

        private val HighSurrogate = 0xD800..0xDBFF
        private val LowSurrogate = 0xDC00..0xDFFF
    }
}
