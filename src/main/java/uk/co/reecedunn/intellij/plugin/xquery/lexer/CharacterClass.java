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
package uk.co.reecedunn.intellij.plugin.xquery.lexer;

public final class CharacterClass {
    // The CharacterClass constructor is not used, but make sure code coverage reports it as covered.
    private static CharacterClass INSTANCE = new CharacterClass();

    public static final int INVALID = 0;
    public static final int END_OF_BUFFER = -1;
    public static final int WHITESPACE = -2;
    public static final int DIGIT = -3;
    public static final int NAME_START_CHAR = -4; // Excluding ':'
    public static final int NAME_CHAR = -5; // Excluding NameStartChar, '-', '.' and Digit
    public static final int CHAR = -6;

    public static final int DOT = 1;
    public static final int QUOTE = 2;
    public static final int APOSTROPHE = 3;
    public static final int SEMICOLON = 4;
    public static final int HASH = 5;
    public static final int COLON = 6;
    public static final int HYPHEN_MINUS = 7;

    private static final int APO = APOSTROPHE;
    private static final int CHR = CHAR;
    private static final int CLN = COLON;
    private static final int DIG = DIGIT;
    private static final int EOB = END_OF_BUFFER;
    private static final int HSH = HASH;
    private static final int INV = INVALID;
    private static final int MIN = HYPHEN_MINUS;
    private static final int NSC = NAME_START_CHAR;
    private static final int QUO = QUOTE;
    private static final int SMC = SEMICOLON;
    private static final int WSP = WHITESPACE;

    private static final int mCharacterClasses[] = {
        //////// x0   x1   x2   x3   x4   x5   x6   x7   x8   x9   xA   xB   xC   xD   xE   xF
        /* 0x */ EOB, INV, INV, INV, INV, INV, INV, INV, INV, WSP, WSP, INV, INV, WSP, INV, INV,
        /* 1x */ INV, INV, INV, INV, INV, INV, INV, INV, INV, INV, INV, INV, INV, INV, INV, INV,
        /* 2x */ WSP, CHR, QUO, HSH, CHR, CHR, CHR, APO, CHR, CHR, CHR, CHR, CHR, MIN, DOT, CHR,
        /* 3x */ DIG, DIG, DIG, DIG, DIG, DIG, DIG, DIG, DIG, DIG, CLN, SMC, CHR, CHR, CHR, CHR,
        /* 4x */ CHR, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC,
        /* 5x */ NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, CHR, CHR, CHR, CHR, NSC,
        /* 6x */ CHR, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC,
        /* 7x */ NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, NSC, CHR, CHR, CHR, CHR, INV,
    };

    public static int getCharClass(int c) {
        if (c < mCharacterClasses.length) // 0x0000-0x0079
            return mCharacterClasses[c];
        if (c <= 0xD7FF) { // 0x0080-0xD7FF
            if (c == 0xB7)
                return NAME_CHAR;
            if (c >= 0xC0 && c <= 0x2FF)
                return (c == 0xD7 || c == 0xF7) ? CHAR : NAME_START_CHAR;
            if (c >= 0x300 && c <= 0x36F)
                return NAME_CHAR;
            if (c >= 0x370 && c <= 0x1FFF)
                return (c == 0x37E) ? CHAR : NAME_START_CHAR;
            if (c == 0x200C || c == 0x200D)
                return NAME_START_CHAR;
            if (c >= 0x2070 && c <= 0x218F)
                return NAME_START_CHAR;
            if (c >= 0x2C00 && c <= 0x2FEF)
                return NAME_START_CHAR;
            if (c >= 0x3001)
                return NAME_START_CHAR;
            return CHAR;
        }
        if (c <= 0xDFFF) // 0xD800-0xDFFF
            return INVALID; // Surrogate Block
        if (c <= 0xFFFD) { // 0xE000-0xFFFD
            if (c >= 0xF900 && c <= 0xFDCF)
                return NAME_START_CHAR;
            if (c >= 0xFDF0)
                return NAME_START_CHAR;
            return CHAR;
        }
        if (c == 0xFFFE || c == 0xFFFF)
            return INVALID;
        if (c <= 0x10FFFF) { // 0x010000-0x10FFFF
            if (c <= 0xEFFFF)
                return NAME_START_CHAR;
            return CHAR;
        }
        return INVALID;
    }
}
