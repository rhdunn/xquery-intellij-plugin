// Copyright (C) 2022 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.lexer

import kotlin.jvm.JvmInline

/**
 * Represents an XML character.
 *
 * @param codepoint The unicode codepoint for this XML character.
 *
 * @see <a href="http://www.w3.org/TR/REC-xml/#NT-Char">XML 1.0 (REC) Char</a>
 */
@JvmInline
value class XmlChar(val codepoint: Int) : Comparable<XmlChar> {
    /**
     * Creates an XML character from a Kotlin character.
     */
    constructor(c: Char) : this(c.code)

    /**
     * Creates an XML character from a surrogate pair.
     *
     * @param high The high-surrogate value.
     * @param low The low-surrogate value.
     */
    constructor(high: Char, low: Char) : this(toCodePoint(high, low))

    /**
     * Returns the UTF-16 representation of the XML character.
     */
    override fun toString(): String = when {
        codepoint <= 0xFFFF -> codepoint.toChar().toString()
        else -> {
            // Surrogate Pair
            val base = codepoint - 0x10000
            val hi = 0xD800 + base.floorDiv(0x400)
            val lo = 0xDC00 + base % 0x400
            hi.toChar().toString() + lo.toChar().toString()
        }
    }

    /**
     * Compares this object with the specified object for order.
     *
     * @return zero if this object is equal to the specified `other` object,
     *         a negative number if it's less than `other`,
     *         or a positive number if it's greater than `other`.
     */
    override fun compareTo(other: XmlChar): Int = codepoint.compareTo(other.codepoint)

    companion object {
        private fun toCodePoint(high: Char, low: Char): Int {
            val hi = (high.code - 0xD800) * 0x400
            val lo = low.code - 0xDC00
            return 0x10000 + hi + lo
        }
    }
}
