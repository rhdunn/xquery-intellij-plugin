/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xdm.datatype

/**
 * Represents an XML [Char](https://www.w3.org/TR/REC-xml/#NT-Char).
 */
data class XmlChar(val codepoint: Int) {
    override fun toString(): String {
        // 1. Invalid Unicode Codepoint
        if (!isValid) return REPLACEMENT_CODEPOINT
        // 2. Basic Multilingual Plane Codepoint
        if (codepoint <= 0xFFFF) return codepoint.toChar().toString()
        // 3. Other Codepoint => Surrogate Pair
        val hi = 0xD800 + Math.floor((codepoint - 0x10000).toDouble() / 0x400)
        val lo = 0xDC00 + (codepoint - 0x10000) % 0x400
        return charArrayOf(hi.toChar(), lo.toChar()).toString()
    }

    /**
     * Is this a valid codepoint?
     *
     * Any value outside the range U+0000 to U+10FFFF is an invalid codepoint.
     * Invalid codepoints are not allowed in the XML `Char` regular expression.
     */
    val isValid: Boolean = codepoint in 0..0x10FFFF

    /**
     * Is the codepoint an unsupported control character?
     *
     * The XML `Char` regular expression excludes C0 control characters except
     * HT, CR, and LF, while the note in the spec discourages DEL, and the C1
     * control characters except NEL.
     *
     * The C0 control characters are U+0000 to U+001F, and the C1 control
     * characters are in the range U+0080 to U+009F. The named codepoints
     * are:
     *   -  U+0009 HT (HORIZONTAL TAB)
     *   -  U+000D CR (CARRIAGE RETURN)
     *   -  U+000A LF (LINE FEED)
     *   -  U+007F DEL (DELETE)
     *   -  U+0085 NEL (NEXT LINE)
     */
    val isUnsupportedControlCharacter get(): Boolean {
        return (codepoint in 0x00..0x1F || codepoint in 0x7F..0x9F) && codepoint !in VALID_CONTROL_CHARACTERS
    }

    /**
     * Is the codepoint a UTF-16 surrogate?
     *
     * Surrogates are invalid when not used as a pair in a UTF-16 byte sequence.
     *
     * The XML `Char` regular expression excludes these codepoints.
     */
    val isSurrogate: Boolean = codepoint in 0xD800..0xDFFF

    /**
     * Is the codepoint permanently unassigned?
     *
     * The XML `Char` type regular expression excludes the Basic Multilingual
     * Plane U+FFFE and U+FFFF codepoints, while the note in the spec
     * discourages the equivalent codepoints from the other Unicode planes.
     * Unicode classifies these as `Cn` (unassigned) characters.
     *
     * The Unicode codepoints U+FDD0 to U+FDEF are intended for internal
     * processing purposes, and as such are also unassigned codepoints.
     *
     * Other Unicode codepoints with the `Cn `(unassigned) general category
     * may be assigned at some point in the future, so using those is not
     * invalid.
     */
    val isPermanentlyUnassigned get(): Boolean {
        return (codepoint and 0x00FFFF) in 0xFFFE..0xFFFF || codepoint in 0xFDD0..0xFDEF
    }

    companion object {
        const val REPLACEMENT_CODEPOINT = "\uFFFD"
        private val VALID_CONTROL_CHARACTERS = arrayOf(0x09, 0x0A, 0x0D, 0x85) // TAB, LF, CR, NEL
    }
}
