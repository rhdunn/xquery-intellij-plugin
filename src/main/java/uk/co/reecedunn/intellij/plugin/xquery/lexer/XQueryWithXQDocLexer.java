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

import com.intellij.lexer.Lexer;
import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocLexer;

public class XQueryWithXQDocLexer extends LexerBase {
    private final Lexer mLanguage;
    private final Lexer mXQDoc;
    private Lexer mActiveLexer;
    private int mState;

    private static final int STATE_LEXER_XQDOC = 0x70000000;

    public XQueryWithXQDocLexer() {
        mLanguage = new XQueryLexer();
        mXQDoc = new XQDocLexer();
    }

    // region Lexer

    @Override
    public final void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        mState = initialState & STATE_LEXER_XQDOC;
        if (mState == STATE_LEXER_XQDOC) {
            mLanguage.start(buffer, startOffset, endOffset, XQueryLexer.STATE_XQUERY_COMMENT);
            mXQDoc.start(mLanguage.getBufferSequence(), mLanguage.getTokenStart(), mLanguage.getTokenEnd(), initialState & ~STATE_LEXER_XQDOC);
            mActiveLexer = mXQDoc;
        } else {
            mLanguage.start(buffer, startOffset, endOffset, initialState & ~STATE_LEXER_XQDOC);
            mActiveLexer = mLanguage;
        }
    }

    @Override
    public void advance() {
        if (mState == STATE_LEXER_XQDOC) {
            mXQDoc.advance();
            if (mXQDoc.getTokenType() == null) {
                mLanguage.advance();
                mState = 0;
                mActiveLexer = mLanguage;
            }
        } else {
            mLanguage.advance();
            if (mLanguage.getTokenType() == XQueryTokenType.COMMENT) {
                mXQDoc.start(mLanguage.getBufferSequence(), mLanguage.getTokenStart(), mLanguage.getTokenEnd(), 0);
                mState = STATE_LEXER_XQDOC;
                mActiveLexer = mXQDoc;
            }
        }
    }

    @Override
    public int getState() {
        return mActiveLexer.getState() | mState;
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        return mActiveLexer.getTokenType();
    }

    @Override
    public int getTokenStart() {
        return mActiveLexer.getTokenStart();
    }

    @Override
    public int getTokenEnd() {
        return mActiveLexer.getTokenEnd();
    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return mLanguage.getBufferSequence();
    }

    @Override
    public int getBufferEnd() {
        return mLanguage.getBufferEnd();
    }

    // endregion
}
