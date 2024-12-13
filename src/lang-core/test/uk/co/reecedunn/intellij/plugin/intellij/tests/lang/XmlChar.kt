// Copyright (C) 2022 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.intellij.tests.lang

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.lexer.XmlChar

@DisplayName("An XmlChar value can be created from a codepoint")
class AnXmlCharValueCanBeCreatedFromACodePoint {
    @Test
    @DisplayName("in the ASCII range")
    fun in_the_ascii_range() {
        val c = XmlChar(0x20) // Basic Latin
        assertEquals(0x20, c.codepoint)
        assertEquals(" ", c.toString())

        assertEquals(XmlChar(0x20), c)
    }

    @Test
    @DisplayName("in the Basic Multilingual Plane")
    fun in_the_basic_multilingual_plane() {
        val c = XmlChar(0x0153) // Latin Extended-A
        assertEquals(0x0153, c.codepoint)
        assertEquals("\u0153", c.toString())

        assertEquals(XmlChar(0x0153), c)
    }

    @Test
    @DisplayName("in the Supplementary Multilingual Plane")
    fun in_the_supplementary_multilingual_plane() {
        val c = XmlChar(0x1F601) // Emoticons
        assertEquals(0x1F601, c.codepoint)
        assertEquals("\uD83D\uDE01", c.toString())

        assertEquals(XmlChar(0x1F601), c)
    }
}

@DisplayName("An XmlChar value")
class AnXmlCharValue {
    @Test
    @DisplayName("can be created from a Char value")
    fun can_be_created_from_a_char_value() {
        val c = XmlChar(' ')
        assertEquals(0x20, c.codepoint)
        assertEquals(" ", c.toString())

        assertEquals(XmlChar(0x20), c)
    }

    @Test
    @DisplayName("can be created from a surrogate pair")
    fun can_be_created_from_a_surrogate_pair() {
        val c = XmlChar(0xD83D.toChar(), 0xDE01.toChar())
        assertEquals(0x1F601, c.codepoint)
        assertEquals("\uD83D\uDE01", c.toString())

        assertEquals(XmlChar(0x1F601), c)
    }

    @Test
    @DisplayName("can compare to other XmlChar values")
    fun can_compare_to_other_xml_char_values() {
        assertEquals(1, XmlChar(0x50).compareTo(XmlChar(0x40)))

        assertEquals(0, XmlChar(0x50).compareTo(XmlChar(0x50)))

        assertEquals(-1, XmlChar(0x50).compareTo(XmlChar(0x60)))
    }
}
