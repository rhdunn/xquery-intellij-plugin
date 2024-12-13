// Copyright (C) 2022 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.intellij.tests.lang

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.lexer.*

@DisplayName("Character classes support testing if an XmlChar belongs to")
class CharacterClassesSupportTestingIfAnXmlCharBelongsTo {
    @Test
    @DisplayName("the Digit character class")
    fun the_digit_character_class() {
        assertTrue(XmlChar('0') in Digits)
        assertTrue(XmlChar('5') in Digits)
        assertTrue(XmlChar('9') in Digits)

        assertFalse(XmlChar('a') in Digits)
        assertFalse(XmlChar('A') in Digits)
        assertFalse(XmlChar('=') in Digits)
    }

    @Test
    @DisplayName("the HexDigit character class")
    fun the_hex_digit_character_class() {
        assertTrue(XmlChar('0') in HexDigits)
        assertTrue(XmlChar('5') in HexDigits)
        assertTrue(XmlChar('9') in HexDigits)

        assertTrue(XmlChar('a') in HexDigits)
        assertTrue(XmlChar('c') in HexDigits)
        assertTrue(XmlChar('f') in HexDigits)
        assertFalse(XmlChar('g') in HexDigits)
        assertFalse(XmlChar('z') in HexDigits)

        assertTrue(XmlChar('A') in HexDigits)
        assertTrue(XmlChar('C') in HexDigits)
        assertTrue(XmlChar('F') in HexDigits)
        assertFalse(XmlChar('G') in HexDigits)
        assertFalse(XmlChar('Z') in HexDigits)

        assertFalse(XmlChar('=') in HexDigits)
    }

    @Test
    @DisplayName("the AlphaNumeric character class")
    fun the_alpha_numeric_character_class() {
        assertTrue(XmlChar('0') in AlphaNumeric)
        assertTrue(XmlChar('5') in AlphaNumeric)
        assertTrue(XmlChar('9') in AlphaNumeric)

        assertTrue(XmlChar('a') in AlphaNumeric)
        assertTrue(XmlChar('g') in AlphaNumeric)
        assertTrue(XmlChar('z') in AlphaNumeric)

        assertTrue(XmlChar('A') in AlphaNumeric)
        assertTrue(XmlChar('G') in AlphaNumeric)
        assertTrue(XmlChar('Z') in AlphaNumeric)

        assertFalse(XmlChar('=') in AlphaNumeric)
    }

    @Test
    @DisplayName("the Character character class")
    fun the_character_character_class() {
        // [#x9] | [#xA] | [#xD]
        assertFalse(XmlChar(0x01) in Character)
        assertTrue(XmlChar('\t') in Character)
        assertTrue(XmlChar('\r') in Character)
        assertTrue(XmlChar('\n') in Character)
        assertFalse(XmlChar(0x1F) in Character)

        // [#x20-#xD7FF]
        assertTrue(XmlChar(0x0020) in Character)
        assertTrue(XmlChar(0x0075) in Character)
        assertTrue(XmlChar(0x012E) in Character)
        assertTrue(XmlChar(0xD7FF) in Character)

        // [#xD800-#xDFFF] -- exclude surrogate pair codepoints
        assertFalse(XmlChar(0xD800) in Character)
        assertFalse(XmlChar(0xDBB0) in Character)
        assertFalse(XmlChar(0xDFFF) in Character)

        // [#xE000-#xFFFD]
        assertTrue(XmlChar(0xE000) in Character)
        assertTrue(XmlChar(0xF0A5) in Character)
        assertTrue(XmlChar(0xFFFD) in Character)

        // [#xFFFE-#xFFFF]
        assertFalse(XmlChar(0xFFFE) in Character)
        assertFalse(XmlChar(0xFFFF) in Character)

        // [#x10000-#x10FFFF]
        assertTrue(XmlChar(0x010000) in Character)
        assertTrue(XmlChar(0x0A96B2) in Character)
        assertTrue(XmlChar(0x10FFFF) in Character)

        assertFalse(XmlChar(0x110000) in Character)
        assertFalse(XmlChar(0x16B37C) in Character)
        assertFalse(XmlChar(0xFFFFFF) in Character)
    }

    @Test
    @DisplayName("the S character class")
    fun the_s_character_class() {
        assertTrue(XmlChar(' ') in S)
        assertTrue(XmlChar('\t') in S)
        assertTrue(XmlChar('\r') in S)
        assertTrue(XmlChar('\n') in S)

        assertFalse(XmlChar('0') in S)
        assertFalse(XmlChar('a') in S)
        assertFalse(XmlChar('A') in S)
        assertFalse(XmlChar('=') in S)
    }

    @Test
    @DisplayName("the NameStartChar character class")
    fun the_name_start_char_character_class() {
        assertTrue(XmlChar(':') in NameStartChar)
        assertTrue(XmlChar('_') in NameStartChar)

        assertFalse(XmlChar('-') in NameStartChar)
        assertFalse(XmlChar('.') in NameStartChar)
        assertFalse(XmlChar('+') in NameStartChar)

        assertFalse(XmlChar('0') in NameStartChar)
        assertFalse(XmlChar('5') in NameStartChar)
        assertFalse(XmlChar('9') in NameStartChar)

        assertTrue(XmlChar('A') in NameStartChar)
        assertTrue(XmlChar('M') in NameStartChar)
        assertTrue(XmlChar('Z') in NameStartChar)

        assertTrue(XmlChar('a') in NameStartChar)
        assertTrue(XmlChar('m') in NameStartChar)
        assertTrue(XmlChar('z') in NameStartChar)

        assertFalse(XmlChar(0xB7) in NameStartChar)

        // [#xC0-#xD6] | [#xD8-#xF6] | [#xF8-#x2FF]
        assertTrue(XmlChar(0xC0) in NameStartChar)
        assertTrue(XmlChar(0xC8) in NameStartChar)
        assertTrue(XmlChar(0xD6) in NameStartChar)
        assertFalse(XmlChar(0xD7) in NameStartChar)
        assertTrue(XmlChar(0xD8) in NameStartChar)
        assertTrue(XmlChar(0xF2) in NameStartChar)
        assertTrue(XmlChar(0xF6) in NameStartChar)
        assertFalse(XmlChar(0xF7) in NameStartChar)
        assertTrue(XmlChar(0xF8) in NameStartChar)
        assertTrue(XmlChar(0x179) in NameStartChar)
        assertTrue(XmlChar(0x2FF) in NameStartChar)

        assertFalse(XmlChar(0x300) in NameStartChar)
        assertFalse(XmlChar(0x36F) in NameStartChar)

        // [#x370-#x37D] | [#x37F-#x1FFF]
        assertTrue(XmlChar(0x370) in NameStartChar)
        assertTrue(XmlChar(0x379) in NameStartChar)
        assertTrue(XmlChar(0x37D) in NameStartChar)
        assertFalse(XmlChar(0x37E) in NameStartChar)
        assertTrue(XmlChar(0x37F) in NameStartChar)
        assertTrue(XmlChar(0x596) in NameStartChar)
        assertTrue(XmlChar(0x1FFF) in NameStartChar)

        // [#x200C-#x200D]
        assertTrue(XmlChar(0x200C) in NameStartChar)
        assertTrue(XmlChar(0x200D) in NameStartChar)

        assertFalse(XmlChar(0x203F) in NameStartChar)
        assertFalse(XmlChar(0x2040) in NameStartChar)

        // [#x2070-#x218F]
        assertTrue(XmlChar(0x2070) in NameStartChar)
        assertTrue(XmlChar(0x209C) in NameStartChar)
        assertTrue(XmlChar(0x218F) in NameStartChar)

        // [#x2C00-#x2FEF]
        assertTrue(XmlChar(0x2C00) in NameStartChar)
        assertTrue(XmlChar(0x2DD8) in NameStartChar)
        assertTrue(XmlChar(0x2FEF) in NameStartChar)

        // [#x3001-#xD7FF]
        assertTrue(XmlChar(0x3001) in NameStartChar)
        assertTrue(XmlChar(0x8864) in NameStartChar)
        assertTrue(XmlChar(0xD7FF) in NameStartChar)

        // [#xF900-#xFDCF]
        assertTrue(XmlChar(0xF900) in NameStartChar)
        assertTrue(XmlChar(0xFB05) in NameStartChar)
        assertTrue(XmlChar(0xFDCF) in NameStartChar)

        // [#xFDF0-#xFFFD]
        assertTrue(XmlChar(0xFDF0) in NameStartChar)
        assertTrue(XmlChar(0xFE09) in NameStartChar)
        assertTrue(XmlChar(0xFFFD) in NameStartChar)

        // [#x10000-#xEFFFF]
        assertTrue(XmlChar(0x10000) in NameStartChar)
        assertTrue(XmlChar(0x3B955) in NameStartChar)
        assertTrue(XmlChar(0xEFFFF) in NameStartChar)
    }

    @Test
    @DisplayName("the NameChar character class")
    fun the_name_char_character_class() {
        assertTrue(XmlChar(':') in NameChar)
        assertTrue(XmlChar('_') in NameChar)

        assertTrue(XmlChar('-') in NameChar)
        assertTrue(XmlChar('.') in NameChar)

        assertFalse(XmlChar('+') in NameChar)

        assertTrue(XmlChar('0') in NameChar)
        assertTrue(XmlChar('5') in NameChar)
        assertTrue(XmlChar('9') in NameChar)

        assertTrue(XmlChar('A') in NameChar)
        assertTrue(XmlChar('M') in NameChar)
        assertTrue(XmlChar('Z') in NameChar)

        assertTrue(XmlChar('a') in NameChar)
        assertTrue(XmlChar('m') in NameChar)
        assertTrue(XmlChar('z') in NameChar)

        assertTrue(XmlChar(0xB7) in NameChar)

        // [#xC0-#xD6] | [#xD8-#xF6] | [#xF8-#x37D] | [#x37F-#x1FFF]
        assertTrue(XmlChar(0xC0) in NameChar)
        assertTrue(XmlChar(0xC8) in NameChar)
        assertTrue(XmlChar(0xD6) in NameChar)
        assertFalse(XmlChar(0xD7) in NameChar)
        assertTrue(XmlChar(0xD8) in NameChar)
        assertTrue(XmlChar(0xF2) in NameChar)
        assertTrue(XmlChar(0xF6) in NameChar)
        assertFalse(XmlChar(0xF7) in NameChar)
        assertTrue(XmlChar(0xF8) in NameChar)
        assertTrue(XmlChar(0x179) in NameChar)
        assertTrue(XmlChar(0x2FF) in NameChar)
        assertTrue(XmlChar(0x300) in NameChar)
        assertTrue(XmlChar(0x36F) in NameChar)
        assertTrue(XmlChar(0x370) in NameChar)
        assertTrue(XmlChar(0x379) in NameChar)
        assertTrue(XmlChar(0x37D) in NameChar)
        assertFalse(XmlChar(0x37E) in NameChar)
        assertTrue(XmlChar(0x37F) in NameChar)
        assertTrue(XmlChar(0x596) in NameChar)
        assertTrue(XmlChar(0x1FFF) in NameChar)

        // [#x200C-#x200D]
        assertTrue(XmlChar(0x200C) in NameChar)
        assertTrue(XmlChar(0x200D) in NameChar)

        // [#x203F-#x2040]
        assertTrue(XmlChar(0x203F) in NameChar)
        assertTrue(XmlChar(0x2040) in NameChar)

        // [#x2070-#x218F]
        assertTrue(XmlChar(0x2070) in NameChar)
        assertTrue(XmlChar(0x209C) in NameChar)
        assertTrue(XmlChar(0x218F) in NameChar)

        // [#x2C00-#x2FEF]
        assertTrue(XmlChar(0x2C00) in NameChar)
        assertTrue(XmlChar(0x2DD8) in NameChar)
        assertTrue(XmlChar(0x2FEF) in NameChar)

        // [#x3001-#xD7FF]
        assertTrue(XmlChar(0x3001) in NameChar)
        assertTrue(XmlChar(0x8864) in NameChar)
        assertTrue(XmlChar(0xD7FF) in NameChar)

        // [#xF900-#xFDCF]
        assertTrue(XmlChar(0xF900) in NameChar)
        assertTrue(XmlChar(0xFB05) in NameChar)
        assertTrue(XmlChar(0xFCDF) in NameChar)

        // [#xFDF0-#xFFFD]
        assertTrue(XmlChar(0xFDF0) in NameChar)
        assertTrue(XmlChar(0xFE09) in NameChar)
        assertTrue(XmlChar(0xFFFD) in NameChar)

        // [#x10000-#xEFFFF]
        assertTrue(XmlChar(0x10000) in NameChar)
        assertTrue(XmlChar(0x3B955) in NameChar)
        assertTrue(XmlChar(0xEFFFF) in NameChar)
    }
}
