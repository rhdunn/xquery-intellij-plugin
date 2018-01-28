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
    val INVALID = 0
    val END_OF_BUFFER = -1
    val WHITESPACE = -2
    val DIGIT = -3
    val NAME_START_CHAR = -4 // Excluding ':'
    val NAME_CHAR = -5 // Excluding NameStartChar, '-', '.' and Digit
    val CHAR = -6

    val DOT = 1
    val QUOTE = 2
    val APOSTROPHE = 3
    val SEMICOLON = 4
    val HASH = 5
    val COLON = 6
    val HYPHEN_MINUS = 7
    val PARENTHESIS_OPEN = 8
    val PARENTHESIS_CLOSE = 9
    val EXCLAMATION_MARK = 10
    val EQUAL = 11
    val DOLLAR = 12
    val ASTERISK = 13
    val PLUS = 14
    val LESS_THAN = 15
    val COMMA = 16
    val CURLY_BRACE_OPEN = 17
    val CURLY_BRACE_CLOSE = 18
    val GREATER_THAN = 19
    val VERTICAL_BAR = 20
    val QUESTION_MARK = 21
    val FORWARD_SLASH = 22
    val AT_SIGN = 23
    val SQUARE_BRACE_OPEN = 24
    val SQUARE_BRACE_CLOSE = 25
    val PERCENT = 26
    val AMPERSAND = 27
    val BACK_TICK = 28

    private val AMP = AMPERSAND
    private val APO = APOSTROPHE
    private val AST = ASTERISK
    private val ATS = AT_SIGN
    private val BTK = BACK_TICK
    private val CBC = CURLY_BRACE_CLOSE
    private val CBO = CURLY_BRACE_OPEN
    private val CHR = CHAR
    private val CLN = COLON
    private val COM = COMMA
    private val DIG = DIGIT
    private val DOL = DOLLAR
    private val EQL = EQUAL
    private val EMK = EXCLAMATION_MARK
    private val FSL = FORWARD_SLASH
    private val GTN = GREATER_THAN
    private val HSH = HASH
    private val INV = INVALID
    private val LTN = LESS_THAN
    private val MIN = HYPHEN_MINUS
    private val NSC = NAME_START_CHAR
    private val PCT = PERCENT
    private val PLS = PLUS
    private val PNC = PARENTHESIS_CLOSE
    private val PNO = PARENTHESIS_OPEN
    private val QMK = QUESTION_MARK
    private val QUO = QUOTE
    private val SBC = SQUARE_BRACE_CLOSE
    private val SBO = SQUARE_BRACE_OPEN
    private val SMC = SEMICOLON
    private val VTB = VERTICAL_BAR
    private val WSP = WHITESPACE

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
