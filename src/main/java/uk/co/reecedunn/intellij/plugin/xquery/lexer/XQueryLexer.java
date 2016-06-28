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
    private CharSequence mBuffer;
    private int mTokenStart;
    private int mTokenEnd;
    private int mEndOfBuffer;
    private int mState;
    private IElementType mType;

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
