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

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class XQueryLexer extends LexerBase {
    private XQueryCodePointRange mTokenRange;
    private int mState;
    private int mNextState;
    private IElementType mType;

    // Character Classes

    private static final int WHITESPACE = 1;
    private static final int NUMBER = 2;
    private static final int DOT = 3;
    private static final int QUOTE = 4;
    private static final int APOSTROPHE = 5;
    private static final int END_OF_BUFFER = -1;

    private static final int mCharacterClasses[] = {
        //////// x0 x1 x2 x3 x4 x5 x6 x7 x8 x9 xA xB xC xD xE xF
        /* 0x */ -1,0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0,
        /* 1x */ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        /* 2x */ 1, 0, 4, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 3, 0,
        /* 3x */ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0,
        /* 4x */ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        /* 5x */ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        /* 6x */ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        /* 7x */ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        /* 8x */ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        /* 9x */ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        /* Ax */ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        /* Bx */ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        /* Cx */ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        /* Dx */ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        /* Ex */ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        /* Fx */ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    private int getCharClass(int c) {
        if (c <= 0xFF) {
            return mCharacterClasses[c];
        }
        return 0;
    }

    // States

    private static final int STATE_DEFAULT = 0;
    private static final int STATE_STRING_LITERAL_QUOTE = 1;
    private static final int STATE_STRING_LITERAL_APOSTROPHE = 2;

    private void stateDefault() {
        int initialClass = getCharClass(mTokenRange.getCodePoint());
        int c;
        switch (initialClass) {
            case WHITESPACE:
                mTokenRange.match();
                while (getCharClass(mTokenRange.getCodePoint()) == WHITESPACE)
                    mTokenRange.match();
                mType = XQueryTokenType.WHITE_SPACE;
                break;
            case DOT:
            case NUMBER:
                mTokenRange.match();
                while (getCharClass(mTokenRange.getCodePoint()) == NUMBER)
                    mTokenRange.match();
                if (initialClass != DOT && getCharClass(mTokenRange.getCodePoint()) == DOT) {
                    mTokenRange.match();
                    while (getCharClass(mTokenRange.getCodePoint()) == NUMBER)
                        mTokenRange.match();
                    mType = XQueryTokenType.DECIMAL_LITERAL;
                } else {
                    mType = initialClass == DOT ? XQueryTokenType.DECIMAL_LITERAL : XQueryTokenType.INTEGER_LITERAL;
                }
                c = mTokenRange.getCodePoint();
                if (c == 'e' || c == 'E') {
                    mTokenRange.save();
                    mTokenRange.match();
                    c = mTokenRange.getCodePoint();
                    if ((c == '+') || (c == '-')) {
                        mTokenRange.match();
                        c = mTokenRange.getCodePoint();
                    }
                    if (getCharClass(c) == NUMBER) {
                        mTokenRange.match();
                        while (getCharClass(mTokenRange.getCodePoint()) == NUMBER)
                            mTokenRange.match();
                        mType = XQueryTokenType.DOUBLE_LITERAL;
                    } else {
                        mTokenRange.restore();
                    }
                }
                break;
            case END_OF_BUFFER:
                mType = null;
                break;
            case QUOTE:
            case APOSTROPHE:
                mTokenRange.match();
                mType = XQueryTokenType.STRING_LITERAL_START;
                mNextState = (initialClass == QUOTE) ? STATE_STRING_LITERAL_QUOTE : STATE_STRING_LITERAL_APOSTROPHE;
                break;
            default:
                mTokenRange.match();
                mType = XQueryTokenType.BAD_CHARACTER;
                break;
        }
    }

    private void stateStringLiteral(char type) {
        int c = mTokenRange.getCodePoint();
        if (c == type) {
            mTokenRange.match();
            mType = XQueryTokenType.STRING_LITERAL_END;
            mNextState = STATE_DEFAULT;
        } else if (c == '\0') {
            mType = null;
        } else {
            while (c != type && c != '\0') {
                mTokenRange.match();
                c = mTokenRange.getCodePoint();
            }
            mType = XQueryTokenType.STRING_LITERAL_CONTENTS;
        }
    }

    // Lexer implementation

    public XQueryLexer() {
        mTokenRange = new XQueryCodePointRange();
    }

    @Override
    public final void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        mTokenRange.start(buffer, startOffset, endOffset);
        mNextState = initialState;
        advance();
    }

    @Override
    public final void advance() {
        mTokenRange.flush();
        mState = mNextState;
        switch (mState) {
            case STATE_DEFAULT:
                stateDefault();
                break;
            case STATE_STRING_LITERAL_QUOTE:
                stateStringLiteral('"');
                break;
            case STATE_STRING_LITERAL_APOSTROPHE:
                stateStringLiteral('\'');
                break;
        }
    }

    @Override
    public final int getState() {
        return mState;
    }

    @Override
    public final IElementType getTokenType() {
        return mType;
    }

    @Override
    public final int getTokenStart() {
        return mTokenRange.getStart();
    }

    @Override
    public final int getTokenEnd() {
        return mTokenRange.getEnd();
    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return mTokenRange.getBufferSequence();
    }

    @Override
    public final int getBufferEnd() {
        return mTokenRange.getBufferEnd();
    }
}
