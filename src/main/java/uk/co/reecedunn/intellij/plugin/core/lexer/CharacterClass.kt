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

object CharacterClass {
    const val INVALID = 0
    const val END_OF_BUFFER = -1
    const val WHITESPACE = -2
    const val DIGIT = -3
    const val NAME_START_CHAR = -4 // Excluding ':'
    const val NAME_CHAR = -5 // Excluding NameStartChar, '-', '.' and Digit
    const val CHAR = -6

    const val DOT = 1
    const val QUOTE = 2
    const val APOSTROPHE = 3
    const val SEMICOLON = 4
    const val HASH = 5
    const val COLON = 6
    const val HYPHEN_MINUS = 7
    const val PARENTHESIS_OPEN = 8
    const val PARENTHESIS_CLOSE = 9
    const val EXCLAMATION_MARK = 10
    const val EQUAL = 11
    const val DOLLAR = 12
    const val ASTERISK = 13
    const val PLUS = 14
    const val LESS_THAN = 15
    const val COMMA = 16
    const val CURLY_BRACE_OPEN = 17
    const val CURLY_BRACE_CLOSE = 18
    const val GREATER_THAN = 19
    const val VERTICAL_BAR = 20
    const val QUESTION_MARK = 21
    const val FORWARD_SLASH = 22
    const val AT_SIGN = 23
    const val SQUARE_BRACE_OPEN = 24
    const val SQUARE_BRACE_CLOSE = 25
    const val PERCENT = 26
    const val AMPERSAND = 27
    const val BACK_TICK = 28

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
            /* 7x */ NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, CBO, VTB, CBC, CHR, INV)

    // TODO: Classify any non-conforming XML Char value as INVALID, like with xdm.model.XmlChar.
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
}
