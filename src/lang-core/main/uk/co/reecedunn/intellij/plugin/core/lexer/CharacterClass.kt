/*
 * Copyright (C) 2016, 2020-2022 Reece H. Dunn
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

object CharacterClass {
    const val INVALID: Int = 0
    const val END_OF_BUFFER: Int = -1
    const val WHITESPACE: Int = -2
    const val DIGIT: Int = -3

    // XML 1.0 5ed (4) NameStartChar
    const val NAME_START_CHAR: Int = -4 // Excluding ':'

    // XML 1.0 5ed (4a) NameChar
    const val NAME_CHAR: Int = -5 // Excluding NameStartChar, '-', '.' and Digit

    // XML 1.0 5ed (2) Char
    const val CHAR: Int = -6

    const val DOT: Int = 1
    const val QUOTE: Int = 2
    const val APOSTROPHE: Int = 3
    const val SEMICOLON: Int = 4
    const val HASH: Int = 5
    const val COLON: Int = 6
    const val HYPHEN_MINUS: Int = 7
    const val PARENTHESIS_OPEN: Int = 8
    const val PARENTHESIS_CLOSE: Int = 9
    const val EXCLAMATION_MARK: Int = 10
    const val EQUAL: Int = 11
    const val DOLLAR: Int = 12
    const val ASTERISK: Int = 13
    const val PLUS: Int = 14
    const val LESS_THAN: Int = 15
    const val COMMA: Int = 16
    const val CURLY_BRACE_OPEN: Int = 17
    const val CURLY_BRACE_CLOSE: Int = 18
    const val GREATER_THAN: Int = 19
    const val VERTICAL_BAR: Int = 20
    const val QUESTION_MARK: Int = 21
    const val FORWARD_SLASH: Int = 22
    const val AT_SIGN: Int = 23
    const val SQUARE_BRACE_OPEN: Int = 24
    const val SQUARE_BRACE_CLOSE: Int = 25
    const val PERCENT: Int = 26
    const val AMPERSAND: Int = 27
    const val BACK_TICK: Int = 28
    const val TILDE: Int = 29

    private const val AMP = AMPERSAND
    private const val APO = APOSTROPHE
    private const val AST = ASTERISK
    private const val ATS = AT_SIGN
    private const val BTK = BACK_TICK
    private const val CBC = CURLY_BRACE_CLOSE
    private const val CBO = CURLY_BRACE_OPEN
    private const val CHR = CHAR
    private const val CLN = COLON
    private const val COM = COMMA
    private const val DIG = DIGIT
    private const val DOL = DOLLAR
    private const val EQL = EQUAL
    private const val EMK = EXCLAMATION_MARK
    private const val FSL = FORWARD_SLASH
    private const val GTN = GREATER_THAN
    private const val HSH = HASH
    private const val INV = INVALID
    private const val LTN = LESS_THAN
    private const val MIN = HYPHEN_MINUS
    private const val NSC = NAME_START_CHAR
    private const val PCT = PERCENT
    private const val PLS = PLUS
    private const val PNC = PARENTHESIS_CLOSE
    private const val PNO = PARENTHESIS_OPEN
    private const val QMK = QUESTION_MARK
    private const val QUO = QUOTE
    private const val SBC = SQUARE_BRACE_CLOSE
    private const val SBO = SQUARE_BRACE_OPEN
    private const val SMC = SEMICOLON
    private const val TLD = TILDE
    private const val VTB = VERTICAL_BAR
    private const val WSP = WHITESPACE

    private val mCharacterClasses = intArrayOf(
        //////// x0   x1   x2   x3   x4   x5   x6   x7   x8   x9   xA   xB   xC   xD   xE   xF
        /* 0x */ INV, INV, INV, INV, INV, INV, INV, INV, INV, WSP, WSP, INV, INV, WSP, INV, INV,
        /* 1x */ INV, INV, INV, INV, INV, INV, INV, INV, INV, INV, INV, INV, INV, INV, INV, INV,
        /* 2x */ WSP, EMK, QUO, HSH, DOL, PCT, AMP, APO, PNO, PNC, AST, PLS, COM, MIN, DOT, FSL,
        /* 3x */ DIG, DIG, DIG, DIG, DIG, DIG, DIG, DIG, DIG, DIG, CLN, SMC, LTN, EQL, GTN, QMK,
        /* 4x */ ATS, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC,
        /* 5x */ NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, SBO, CHR, SBC, CHR, NSC,
        /* 6x */ BTK, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC,
        /* 7x */ NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, CBO, VTB, CBC, TLD, INV
    )

    fun getCharClass(c: Int): Int {
        if (c < mCharacterClasses.size) { // 0x0000-0x0079
            return if (c == CodePointRange.END_OF_BUFFER) END_OF_BUFFER else mCharacterClasses[c]
        }
        if (c <= 0xD7FF) { // 0x0080-0xD7FF
            if (c == 0xB7)
                return NAME_CHAR
            if (c in 0xC0..0x2FF)
                return if (c == 0xD7 || c == 0xF7) CHAR else NAME_START_CHAR
            if (c in 0x300..0x36F)
                return NAME_CHAR
            if (c in 0x370..0x1FFF)
                return if (c == 0x37E) CHAR else NAME_START_CHAR
            if (c == 0x200C || c == 0x200D)
                return NAME_START_CHAR
            if (c in 0x2070..0x218F)
                return NAME_START_CHAR
            if (c in 0x2C00..0x2FEF)
                return NAME_START_CHAR
            return if (c >= 0x3001) NAME_START_CHAR else CHAR
        }
        if (c <= 0xDFFF) // 0xD800-0xDFFF
            return INVALID // Surrogate Block
        if (c <= 0xFFFD) { // 0xE000-0xFFFD
            if (c in 0xF900..0xFDCF)
                return NAME_START_CHAR
            return if (c >= 0xFDF0) NAME_START_CHAR else CHAR
        }
        if (c == 0xFFFE || c == 0xFFFF)
            return INVALID
        return if (c <= 0x10FFFF) { // 0x010000-0x10FFFF
            if (c <= 0xEFFFF) NAME_START_CHAR else CHAR
        } else INVALID
    }

    // region Character Class Tests

    object HexDigit {
        operator fun contains(c: Int): Boolean {
            @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
            return (
                c >= '0'.code && c <= '9'.code ||
                c >= 'a'.code && c <= 'f'.code ||
                c >= 'A'.code && c <= 'F'.code
            )
        }
    }

    object AlphaNumeric {
        operator fun contains(c: Int): Boolean {
            @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
            return (
                c >= '0'.code && c <= '9'.code ||
                c >= 'a'.code && c <= 'z'.code ||
                c >= 'A'.code && c <= 'z'.code
            )
        }
    }

    // endregion
}
