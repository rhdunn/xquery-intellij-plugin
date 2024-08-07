// Copyright (C) 2022 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.intellij.tests.lang

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.lexer.Space
import uk.co.reecedunn.intellij.plugin.core.lexer.XmlCharReader
import xqt.platform.xml.lexer.Digits
import xqt.platform.xml.model.XmlChar

@DisplayName("An XmlCharReader can be initialized")
class AnXmlCharReaderCanBeInitialized {
    @Test
    @DisplayName("from the default constructor")
    fun from_the_default_constructor() {
        val reader = XmlCharReader()

        assertEquals("", reader.buffer)
        assertEquals(0, reader.currentOffset)
        assertEquals(0, reader.bufferEndOffset)
    }

    @Test
    @DisplayName("from reset with a buffer")
    fun from_reset_with_a_buffer() {
        val reader = XmlCharReader()
        reader.reset("lorem ipsum dolor")

        assertEquals("lorem ipsum dolor", reader.buffer)
        assertEquals(0, reader.currentOffset)
        assertEquals(17, reader.bufferEndOffset)
    }

    @Test
    @DisplayName("from reset with a buffer and start offset")
    fun from_reset_with_a_buffer_and_start_offset() {
        val reader = XmlCharReader()
        reader.reset("lorem ipsum dolor", 6)

        assertEquals("lorem ipsum dolor", reader.buffer)
        assertEquals(6, reader.currentOffset)
        assertEquals(17, reader.bufferEndOffset)
    }

    @Test
    @DisplayName("from reset with a buffer, an end offset, and start offset")
    fun from_reset_with_a_buffer_an_end_offset_and_start_offset() {
        val reader = XmlCharReader()
        reader.reset("lorem ipsum dolor", 6, 11)

        assertEquals("lorem ipsum dolor", reader.buffer)
        assertEquals(6, reader.currentOffset)
        assertEquals(11, reader.bufferEndOffset)
    }
}

@DisplayName("An XmlCharReader can read XmlChar values")
class AnXmlCharReaderCanReadXmlCharValues {
    @Test
    @DisplayName("from an empty buffer")
    fun from_an_empty_buffer() {
        val reader = XmlCharReader()
        reader.reset("")

        assertEquals(0, reader.currentOffset)
        assertEquals(XmlCharReader.EndOfBuffer, reader.currentChar)
        assertEquals(XmlCharReader.EndOfBuffer, reader.nextChar)

        reader.advance()
        assertEquals(0, reader.currentOffset)
        assertEquals(XmlCharReader.EndOfBuffer, reader.currentChar)
        assertEquals(XmlCharReader.EndOfBuffer, reader.nextChar)
    }

    @Test
    @DisplayName("from a sequence of ASCII characters")
    fun from_a_sequence_of_ascii_characters() {
        val reader = XmlCharReader()
        reader.reset("lorem")

        assertEquals(0, reader.currentOffset)
        assertEquals(XmlChar('l'), reader.currentChar)
        assertEquals(XmlChar('o'), reader.nextChar)

        reader.advance()
        assertEquals(1, reader.currentOffset)
        assertEquals(XmlChar('o'), reader.currentChar)
        assertEquals(XmlChar('r'), reader.nextChar)

        reader.advance()
        assertEquals(2, reader.currentOffset)
        assertEquals(XmlChar('r'), reader.currentChar)
        assertEquals(XmlChar('e'), reader.nextChar)

        reader.advance()
        assertEquals(3, reader.currentOffset)
        assertEquals(XmlChar('e'), reader.currentChar)
        assertEquals(XmlChar('m'), reader.nextChar)

        reader.advance()
        assertEquals(4, reader.currentOffset)
        assertEquals(XmlChar('m'), reader.currentChar)
        assertEquals(XmlCharReader.EndOfBuffer, reader.nextChar)

        reader.advance()
        assertEquals(5, reader.currentOffset)
        assertEquals(XmlCharReader.EndOfBuffer, reader.currentChar)
        assertEquals(XmlCharReader.EndOfBuffer, reader.nextChar)

        reader.advance()
        assertEquals(5, reader.currentOffset)
        assertEquals(XmlCharReader.EndOfBuffer, reader.currentChar)
        assertEquals(XmlCharReader.EndOfBuffer, reader.nextChar)
    }

    @Test
    @DisplayName("from a sequence of Basic Multilingual Plane characters")
    fun from_a_sequence_of_basic_multilingual_plane_characters() {
        val reader = XmlCharReader()
        reader.reset("\u0301\u0303\u0304")

        assertEquals(0, reader.currentOffset)
        assertEquals(XmlChar(0x0301), reader.currentChar)
        assertEquals(XmlChar(0x0303), reader.nextChar)

        reader.advance()
        assertEquals(1, reader.currentOffset)
        assertEquals(XmlChar(0x0303), reader.currentChar)
        assertEquals(XmlChar(0x0304), reader.nextChar)

        reader.advance()
        assertEquals(2, reader.currentOffset)
        assertEquals(XmlChar(0x0304), reader.currentChar)
        assertEquals(XmlCharReader.EndOfBuffer, reader.nextChar)

        reader.advance()
        assertEquals(3, reader.currentOffset)
        assertEquals(XmlCharReader.EndOfBuffer, reader.currentChar)
        assertEquals(XmlCharReader.EndOfBuffer, reader.nextChar)

        reader.advance()
        assertEquals(3, reader.currentOffset)
        assertEquals(XmlCharReader.EndOfBuffer, reader.currentChar)
        assertEquals(XmlCharReader.EndOfBuffer, reader.nextChar)
    }

    @Test
    @DisplayName("from a sequence of Supplementary Multilingual Plane characters")
    fun from_a_sequence_of_supplementary_multilingual_plane_characters() {
        val reader = XmlCharReader()
        reader.reset("\uD83D\uDE01\uD83D\uDE03\uD83D\uDE04")

        assertEquals(0, reader.currentOffset)
        assertEquals(XmlChar(0x1F601), reader.currentChar)
        assertEquals(XmlChar(0x1F603), reader.nextChar)

        reader.advance()
        assertEquals(2, reader.currentOffset)
        assertEquals(XmlChar(0x1F603), reader.currentChar)
        assertEquals(XmlChar(0x1F604), reader.nextChar)

        reader.advance()
        assertEquals(4, reader.currentOffset)
        assertEquals(XmlChar(0x1F604), reader.currentChar)
        assertEquals(XmlCharReader.EndOfBuffer, reader.nextChar)

        reader.advance()
        assertEquals(6, reader.currentOffset)
        assertEquals(XmlCharReader.EndOfBuffer, reader.currentChar)
        assertEquals(XmlCharReader.EndOfBuffer, reader.nextChar)

        reader.advance()
        assertEquals(6, reader.currentOffset)
        assertEquals(XmlCharReader.EndOfBuffer, reader.currentChar)
        assertEquals(XmlCharReader.EndOfBuffer, reader.nextChar)
    }

    @Test
    @DisplayName("from a sequence of high surrogate codepoints")
    fun from_a_sequence_of_high_surrogate_codepoints() {
        val reader = XmlCharReader()
        reader.reset("\uD801\uD803\uD804")

        assertEquals(0, reader.currentOffset)
        assertEquals(XmlChar(0xD801), reader.currentChar)
        assertEquals(XmlChar(0xD803), reader.nextChar)

        reader.advance()
        assertEquals(1, reader.currentOffset)
        assertEquals(XmlChar(0xD803), reader.currentChar)
        assertEquals(XmlChar(0xD804), reader.nextChar)

        reader.advance()
        assertEquals(2, reader.currentOffset)
        assertEquals(XmlChar(0xD804), reader.currentChar)
        assertEquals(XmlCharReader.EndOfBuffer, reader.nextChar)

        reader.advance()
        assertEquals(3, reader.currentOffset)
        assertEquals(XmlCharReader.EndOfBuffer, reader.currentChar)
        assertEquals(XmlCharReader.EndOfBuffer, reader.nextChar)

        reader.advance()
        assertEquals(3, reader.currentOffset)
        assertEquals(XmlCharReader.EndOfBuffer, reader.currentChar)
        assertEquals(XmlCharReader.EndOfBuffer, reader.nextChar)
    }

    @Test
    @DisplayName("from a sequence of low surrogate codepoints")
    fun from_a_sequence_of_low_surrogate_codepoints() {
        val reader = XmlCharReader()
        reader.reset("\uDC01\uDC03\uDC04")

        assertEquals(0, reader.currentOffset)
        assertEquals(XmlChar(0xDC01), reader.currentChar)
        assertEquals(XmlChar(0xDC03), reader.nextChar)

        reader.advance()
        assertEquals(1, reader.currentOffset)
        assertEquals(XmlChar(0xDC03), reader.currentChar)
        assertEquals(XmlChar(0xDC04), reader.nextChar)

        reader.advance()
        assertEquals(2, reader.currentOffset)
        assertEquals(XmlChar(0xDC04), reader.currentChar)
        assertEquals(XmlCharReader.EndOfBuffer, reader.nextChar)

        reader.advance()
        assertEquals(3, reader.currentOffset)
        assertEquals(XmlCharReader.EndOfBuffer, reader.currentChar)
        assertEquals(XmlCharReader.EndOfBuffer, reader.nextChar)

        reader.advance()
        assertEquals(3, reader.currentOffset)
        assertEquals(XmlCharReader.EndOfBuffer, reader.currentChar)
        assertEquals(XmlCharReader.EndOfBuffer, reader.nextChar)
    }

    @Test
    @DisplayName("from a given range")
    fun from_a_given_range() {
        val reader = XmlCharReader()
        reader.reset("lorem ipsum dolor", 6, 11)

        assertEquals(6, reader.currentOffset)
        assertEquals(XmlChar('i'), reader.currentChar)
        assertEquals(XmlChar('p'), reader.nextChar)

        reader.advance()
        assertEquals(7, reader.currentOffset)
        assertEquals(XmlChar('p'), reader.currentChar)
        assertEquals(XmlChar('s'), reader.nextChar)

        reader.advance()
        assertEquals(8, reader.currentOffset)
        assertEquals(XmlChar('s'), reader.currentChar)
        assertEquals(XmlChar('u'), reader.nextChar)

        reader.advance()
        assertEquals(9, reader.currentOffset)
        assertEquals(XmlChar('u'), reader.currentChar)
        assertEquals(XmlChar('m'), reader.nextChar)

        reader.advance()
        assertEquals(10, reader.currentOffset)
        assertEquals(XmlChar('m'), reader.currentChar)
        assertEquals(XmlCharReader.EndOfBuffer, reader.nextChar)

        reader.advance()
        assertEquals(11, reader.currentOffset)
        assertEquals(XmlCharReader.EndOfBuffer, reader.currentChar)
        assertEquals(XmlCharReader.EndOfBuffer, reader.nextChar)

        reader.advance()
        assertEquals(11, reader.currentOffset)
        assertEquals(XmlCharReader.EndOfBuffer, reader.currentChar)
        assertEquals(XmlCharReader.EndOfBuffer, reader.nextChar)
    }
}

@DisplayName("An XmlCharReader can peek at XmlChar values")
class AnXmlCharReaderCanPeekAtXmlCharValues {
    @Test
    @DisplayName("from an empty buffer")
    fun from_an_empty_buffer() {
        val reader = XmlCharReader()
        reader.reset("")

        assertEquals(XmlCharReader.EndOfBuffer, reader.currentChar)
        assertEquals(XmlCharReader.EndOfBuffer, reader.peek(0))
        assertEquals(XmlCharReader.EndOfBuffer, reader.peek(1))

        // The current state has not been modified.
        assertEquals(0, reader.currentOffset)
        assertEquals(XmlCharReader.EndOfBuffer, reader.currentChar)
    }

    @Test
    @DisplayName("from a sequence of ASCII characters")
    fun from_a_sequence_of_ascii_characters() {
        val reader = XmlCharReader()
        reader.reset("lorem")

        assertEquals(XmlChar('l'), reader.currentChar)
        assertEquals(XmlChar('o'), reader.peek(0))
        assertEquals(XmlChar('r'), reader.peek(1))
        assertEquals(XmlChar('e'), reader.peek(2))
        assertEquals(XmlChar('m'), reader.peek(3))

        assertEquals(XmlCharReader.EndOfBuffer, reader.peek(5))
        assertEquals(XmlCharReader.EndOfBuffer, reader.peek(6))

        // The current state has not been modified.
        assertEquals(0, reader.currentOffset)
        assertEquals(XmlChar('l'), reader.currentChar)
    }

    @Test
    @DisplayName("from a sequence of Basic Multilingual Plane characters")
    fun from_a_sequence_of_basic_multilingual_plane_characters() {
        val reader = XmlCharReader()
        reader.reset("\u0301\u0303\u0304")

        assertEquals(XmlChar(0x0301), reader.currentChar)
        assertEquals(XmlChar(0x0303), reader.peek(0))
        assertEquals(XmlChar(0x0304), reader.peek(1))

        assertEquals(XmlCharReader.EndOfBuffer, reader.peek(2))
        assertEquals(XmlCharReader.EndOfBuffer, reader.peek(3))

        // The current state has not been modified.
        assertEquals(0, reader.currentOffset)
        assertEquals(XmlChar(0x0301), reader.currentChar)
    }

    @Test
    @DisplayName("from a sequence of Supplementary Multilingual Plane characters")
    fun from_a_sequence_of_supplementary_multilingual_plane_characters() {
        val reader = XmlCharReader()
        reader.reset("\uD83D\uDE01\uD83D\uDE03\uD83D\uDE04")

        assertEquals(XmlChar(0x1F601), reader.currentChar)
        assertEquals(XmlChar(0x1F603), reader.peek(0))
        assertEquals(XmlChar(0x1F604), reader.peek(2))

        assertEquals(XmlCharReader.EndOfBuffer, reader.peek(4))
        assertEquals(XmlCharReader.EndOfBuffer, reader.peek(6))

        // The current state has not been modified.
        assertEquals(0, reader.currentOffset)
        assertEquals(XmlChar(0x1F601), reader.currentChar)
    }

    @Test
    @DisplayName("from a sequence of high surrogate codepoints")
    fun from_a_sequence_of_high_surrogate_codepoints() {
        val reader = XmlCharReader()
        reader.reset("\uD801\uD803\uD804")

        assertEquals(XmlChar(0xD801), reader.currentChar)
        assertEquals(XmlChar(0xD803), reader.peek(0))
        assertEquals(XmlChar(0xD804), reader.peek(1))

        assertEquals(XmlCharReader.EndOfBuffer, reader.peek(2))
        assertEquals(XmlCharReader.EndOfBuffer, reader.peek(3))

        // The current state has not been modified.
        assertEquals(0, reader.currentOffset)
        assertEquals(XmlChar(0xD801), reader.currentChar)
    }

    @Test
    @DisplayName("from a sequence of low surrogate codepoints")
    fun from_a_sequence_of_low_surrogate_codepoints() {
        val reader = XmlCharReader()
        reader.reset("\uDC01\uDC03\uDC04")

        assertEquals(XmlChar(0xDC01), reader.currentChar)
        assertEquals(XmlChar(0xDC03), reader.peek(0))
        assertEquals(XmlChar(0xDC04), reader.peek(1))

        assertEquals(XmlCharReader.EndOfBuffer, reader.peek(2))
        assertEquals(XmlCharReader.EndOfBuffer, reader.peek(3))

        // The current state has not been modified.
        assertEquals(0, reader.currentOffset)
        assertEquals(XmlChar(0xDC01), reader.currentChar)
    }

    @Test
    @DisplayName("from a given range")
    fun from_a_given_range() {
        val reader = XmlCharReader()
        reader.reset("lorem ipsum dolor", 6, 11)

        assertEquals(XmlChar('i'), reader.currentChar)
        assertEquals(XmlChar('p'), reader.peek(0))
        assertEquals(XmlChar('s'), reader.peek(1))
        assertEquals(XmlChar('u'), reader.peek(2))
        assertEquals(XmlChar('m'), reader.peek(3))

        assertEquals(XmlCharReader.EndOfBuffer, reader.peek(4))
        assertEquals(XmlCharReader.EndOfBuffer, reader.peek(5))

        // The current state has not been modified.
        assertEquals(6, reader.currentOffset)
        assertEquals(XmlChar('i'), reader.currentChar)
    }
}

@DisplayName("An XmlCharReader can")
class AnXmlCharReaderCan {
    @Test
    @DisplayName("read all characters matching a predicate")
    fun read_all_characters_matching_a_predicate() {
        val reader = XmlCharReader()
        reader.reset("123 456")

        assertEquals(0, reader.currentOffset)
        assertEquals(XmlChar('1'), reader.currentChar)

        reader.advanceWhile { it in Digits }
        assertEquals(3, reader.currentOffset)
        assertEquals(XmlChar(' '), reader.currentChar)
    }

    @Test
    @DisplayName("read all characters not matching a predicate")
    fun read_all_characters_not_matching_a_predicate() {
        val reader = XmlCharReader()
        reader.reset("123 456")

        assertEquals(0, reader.currentOffset)
        assertEquals(XmlChar('1'), reader.currentChar)

        reader.advanceUntil { it == Space }
        assertEquals(3, reader.currentOffset)
        assertEquals(XmlChar(' '), reader.currentChar)
    }

    @Test
    @DisplayName("seek to a given offset")
    fun seek_to_a_given_offset() {
        val reader = XmlCharReader()
        reader.reset("123 456")
        reader.currentOffset = 4

        assertEquals(4, reader.currentOffset)
        assertEquals(XmlChar('4'), reader.currentChar)

        reader.advance()
        assertEquals(5, reader.currentOffset)
        assertEquals(XmlChar('5'), reader.currentChar)

        reader.advance()
        assertEquals(6, reader.currentOffset)
        assertEquals(XmlChar('6'), reader.currentChar)

        reader.advance()
        assertEquals(7, reader.currentOffset)
        assertEquals(XmlCharReader.EndOfBuffer, reader.currentChar)
    }
}
