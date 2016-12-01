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
package uk.co.reecedunn.intellij.plugin.xqdoc.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import uk.co.reecedunn.intellij.plugin.core.lexer.CharacterClass;
import uk.co.reecedunn.intellij.plugin.core.lexer.CodePointRange;
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class XQDocLexer extends LexerBase {
    private CodePointRange mTokenRange;
    private int mState;
    private final Stack<Integer> mStates = new Stack<>();
    private IElementType mType;

    public XQDocLexer() {
        mTokenRange = new CodePointRange();
    }

    // region States

    private void pushState(int state) {
        mStates.push(state);
    }

    private void popState() {
        try {
            mStates.pop();
        } catch (EmptyStackException e) {
            //
        }
    }

    private static final int STATE_DEFAULT = 0;

    private void stateDefault() {
    }

    // endregion
    // region Lexer

    @Override
    public final void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        mTokenRange.start(buffer, startOffset, endOffset);
        mStates.clear();
        pushState(initialState);
        advance();
    }

    @Override
    public final void advance() {
        mTokenRange.flush();
        try {
            mState = mStates.peek();
        } catch (EmptyStackException e) {
            mState = STATE_DEFAULT;
        }
        switch (mState) {
            case STATE_DEFAULT:
                stateDefault();
                break;
            default:
                throw new AssertionError("Invalid state: " + mState);
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

    @Override
    @SuppressWarnings("NullableProblems") // jacoco Code Coverage reports an unchecked branch when @NotNull is used.
    public CharSequence getBufferSequence() {
        return mTokenRange.getBufferSequence();
    }

    @Override
    public final int getBufferEnd() {
        return mTokenRange.getBufferEnd();
    }

    // endregion
}
