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
import uk.co.reecedunn.intellij.plugin.xquery.XQueryTokenType;

public class XQueryLexer extends LexerBase {
    private CharSequence mBuffer;
    private int mTokenStart;
    private int mTokenEnd;
    private int mEndOfBuffer;
    private int mState;
    private IElementType mType;

    private static final int WHITESPACE = 1;
    private static final int END_OF_BUFFER = -1;

    private static final int mCharacterClasses[] = {
        //////// x0 x1 x2 x3 x4 x5 x6 x7 x8 x9 xA xB xC xD xE xF
        /* 0x */ 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0,
        /* 1x */ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        /* 2x */ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        /* 3x */ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
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

    private int getCharClass(int offset) {
        if (offset >= mEndOfBuffer)
            return END_OF_BUFFER;
        return mCharacterClasses[mBuffer.charAt(offset)];
    }

    @Override
    public final void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        mBuffer = buffer;
        mTokenStart = startOffset;
        mTokenEnd = startOffset;
        mEndOfBuffer = endOffset;
        mState = initialState;
        mType = null;
    }

    @Override
    public final void advance() {
        mTokenStart = mTokenEnd;
        int mTokenNext = mTokenStart;
        switch (getCharClass(mTokenNext)) {
            case WHITESPACE:
                while (getCharClass(mTokenNext + 1) == WHITESPACE)
                    mTokenNext += 1;
                mTokenEnd = mTokenNext + 1;
                mType = XQueryTokenType.WHITE_SPACE;
                break;
            case END_OF_BUFFER:
                mType = null;
                break;
            default:
                mTokenEnd = mTokenStart + 1;
                mType = XQueryTokenType.ERROR_ELEMENT;
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
        return mTokenStart;
    }

    @Override
    public final int getTokenEnd() {
        return mTokenEnd;
    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return mBuffer;
    }

    @Override
    public final int getBufferEnd() {
        return mEndOfBuffer;
    }
}
