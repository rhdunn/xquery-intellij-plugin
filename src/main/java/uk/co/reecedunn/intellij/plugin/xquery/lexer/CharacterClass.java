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

    public static final int WHITESPACE = 1;
    public static final int NUMBER = 2;
    public static final int DOT = 3;
    public static final int QUOTE = 4;
    public static final int APOSTROPHE = 5;
    public static final int SEMICOLON = 6;
    public static final int LETTER = 7;
    public static final int HASH = 8;
    public static final int END_OF_BUFFER = -1;

    private static final int APO = APOSTROPHE;
    private static final int EOB = END_OF_BUFFER;
    private static final int HSH = HASH;
    private static final int LET = LETTER;
    private static final int NUM = NUMBER;
    private static final int QUO = QUOTE;
    private static final int SMC = SEMICOLON;
    private static final int WSP = WHITESPACE;

    private static final int mCharacterClasses[] = {
        //////// x0   x1   x2   x3   x4   x5   x6   x7   x8   x9   xA   xB   xC   xD   xE   xF
        /* 0x */ EOB, 0,   0,   0,   0,   0,   0,   0,   0,   WSP, WSP, 0,   0,   WSP, 0,   0,
        /* 1x */ 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
        /* 2x */ WSP, 0,   QUO, HSH, 0,   0,   0,   APO, 0,   0,   0,   0,   0,   0,   DOT, 0,
        /* 3x */ NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, 0,   SMC, 0,   0,   0,   0,
        /* 4x */ 0,   LET, LET, LET, LET, LET, LET, LET, LET, LET, LET, LET, LET, LET, LET, LET,
        /* 5x */ LET, LET, LET, LET, LET, LET, LET, LET, LET, LET, LET, 0,   0,   0,   0,   0,
        /* 6x */ 0,   LET, LET, LET, LET, LET, LET, LET, LET, LET, LET, LET, LET, LET, LET, LET,
        /* 7x */ LET, LET, LET, LET, LET, LET, LET, LET, LET, LET, LET, 0,   0,   0,   0,   0,
    };

    public static int getCharClass(int c) {
        if (c < mCharacterClasses.length) {
            return mCharacterClasses[c];
        }
        return 0;
    }
}
