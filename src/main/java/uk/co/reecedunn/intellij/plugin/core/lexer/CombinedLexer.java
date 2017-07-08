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
package uk.co.reecedunn.intellij.plugin.core.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CombinedLexer extends LexerBase {
    class State {
        final Lexer lexer;
        final int state;
        final int parentState;
        final int childState;
        final IElementType transition;

        State(Lexer lexer, int state, int parentState, int childState, IElementType transition) {
            this.lexer = lexer;
            this.state = state;
            this.parentState = parentState;
            this.childState = childState;
            this.transition = transition;
        }
    }

    private final Lexer mLanguage;
    private final Map<Integer, State> mStates = new HashMap<>();
    private final Map<IElementType, State> mTransitions = new HashMap<>();
    private int mStateMask;

    private Lexer mActiveLexer;
    private int mState;

    public CombinedLexer(Lexer language) {
        mLanguage = language;
        mStateMask = 0;
    }

    public void addState(Lexer lexer, int stateId, int parentStateId, IElementType transition) {
        addState(lexer, stateId, parentStateId, 0, transition);
    }

    public void addState(Lexer lexer, int stateId, int parentStateId, int childStateId, IElementType transition) {
        State state = new State(lexer, stateId, parentStateId, childStateId, transition);
        mStates.put(stateId, state);
        mTransitions.put(transition, state);
        mStateMask |= stateId;
    }

    // region Lexer

    @Override
    public final void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        mState = initialState & mStateMask;
        State state = mStates.getOrDefault(mState, null);
        if (state != null) {
            mActiveLexer = state.lexer;
            mLanguage.start(buffer, startOffset, endOffset, state.parentState);
            mActiveLexer.start(mLanguage.getBufferSequence(), mLanguage.getTokenStart(), mLanguage.getTokenEnd(), initialState & ~mStateMask);
        } else {
            mLanguage.start(buffer, startOffset, endOffset, initialState & ~mStateMask);
            state = mTransitions.getOrDefault(mLanguage.getTokenType(), null);
            if (state != null) {
                mActiveLexer = state.lexer;
                mActiveLexer.start(mLanguage.getBufferSequence(), mLanguage.getTokenStart(), mLanguage.getTokenEnd(), state.childState);
                mState = state.state;
            } else {
                mActiveLexer = mLanguage;
            }
        }
    }

    @Override
    public void advance() {
        if (mActiveLexer != mLanguage) {
            mActiveLexer.advance();
            if (mActiveLexer.getTokenType() == null) {
                mLanguage.advance();
                mState = 0;
                mActiveLexer = mLanguage;
            }
        } else {
            mLanguage.advance();
            State state = mTransitions.getOrDefault(mLanguage.getTokenType(), null);
            if (state != null) {
                mActiveLexer = state.lexer;
                mActiveLexer.start(mLanguage.getBufferSequence(), mLanguage.getTokenStart(), mLanguage.getTokenEnd(), state.childState);
                mState = state.state;
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
