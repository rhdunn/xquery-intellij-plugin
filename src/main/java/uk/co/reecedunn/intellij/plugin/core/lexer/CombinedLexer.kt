/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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

import com.intellij.lexer.Lexer
import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType
import java.util.*

class CombinedLexer(private val mLanguage: Lexer) : LexerBase() {
    private val mStates = HashMap<Int, State>()
    private val mTransitions = HashMap<IElementType, State>()
    private var mStateMask: Int = 0

    private var mActiveLexer: Lexer? = null
    private var mState: Int = 0

    internal inner class State(val lexer: Lexer,
                               val state: Int,
                               val parentState: Int,
                               val childState: Int)

    fun addState(lexer: Lexer, stateId: Int, parentStateId: Int, transition: IElementType) {
        addState(lexer, stateId, parentStateId, 0, transition)
    }

    fun addState(lexer: Lexer, stateId: Int, parentStateId: Int, childStateId: Int, transition: IElementType) {
        val state = State(lexer, stateId, parentStateId, childStateId)
        mStates[stateId] = state
        mTransitions[transition] = state
        mStateMask = mStateMask or stateId
    }

    // region Lexer

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        mState = initialState and mStateMask
        var state: State? = mStates[mState]
        if (state != null) {
            mActiveLexer = state.lexer
            mLanguage.start(buffer, startOffset, endOffset, state.parentState)
            mActiveLexer!!.start(mLanguage.bufferSequence, mLanguage.tokenStart, mLanguage.tokenEnd, initialState and mStateMask.inv())
        } else {
            mLanguage.start(buffer, startOffset, endOffset, initialState and mStateMask.inv())
            state = mTransitions[mLanguage.tokenType]
            if (state != null) {
                mActiveLexer = state.lexer
                mActiveLexer!!.start(mLanguage.bufferSequence, mLanguage.tokenStart, mLanguage.tokenEnd, state.childState)
                mState = state.state
            } else {
                mActiveLexer = mLanguage
            }
        }
    }

    override fun advance() {
        if (mActiveLexer !== mLanguage) {
            mActiveLexer!!.advance()
            if (mActiveLexer!!.tokenType == null) {
                mLanguage.advance()
                val state = mTransitions[mLanguage.tokenType]
                if (state != null) {
                    mActiveLexer = state.lexer
                    mActiveLexer!!.start(mLanguage.bufferSequence, mLanguage.tokenStart, mLanguage.tokenEnd, state.childState)
                    mState = state.state
                } else {
                    mActiveLexer = mLanguage
                    mState = 0
                }
            }
        } else {
            mLanguage.advance()
            val state = mTransitions[mLanguage.tokenType]
            if (state != null) {
                mActiveLexer = state.lexer
                mActiveLexer!!.start(mLanguage.bufferSequence, mLanguage.tokenStart, mLanguage.tokenEnd, state.childState)
                mState = state.state
            }
        }
    }

    override fun getState(): Int = mActiveLexer!!.state or mState

    override fun getTokenType(): IElementType? = mActiveLexer!!.tokenType

    override fun getTokenStart(): Int = mActiveLexer!!.tokenStart

    override fun getTokenEnd(): Int = mActiveLexer!!.tokenEnd

    override fun getBufferSequence(): CharSequence = mLanguage.bufferSequence

    override fun getBufferEnd(): Int = mLanguage.bufferEnd

    // endregion
}
