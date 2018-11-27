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

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType
import java.util.*

const val STATE_DEFAULT = 0

abstract class LexerImpl(private val baseState: Int) : LexerBase() {
    protected val mTokenRange: CodePointRange =
        CodePointRange()
    protected var mType: IElementType? = null

    // region States

    private var mState: Int = 0
    private val mStates = Stack<Int>()

    protected fun pushState(state: Int) {
        mStates.push(state)
    }

    protected fun popState() {
        try {
            mStates.pop()
        } catch (e: EmptyStackException) {
            //
        }

    }

    protected fun nextState(): Int {
        mTokenRange.flush()
        mState = try { mStates.peek() } catch (e: EmptyStackException) {
            STATE_DEFAULT
        }
        return mState
    }

    // endregion
    // region Lexer

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        mTokenRange.start(buffer, startOffset, endOffset)
        mStates.clear()
        if (initialState != STATE_DEFAULT) {
            pushState(baseState)
        }
        pushState(initialState)
        advance()
    }

    override fun getState(): Int = mState

    override fun getTokenType(): IElementType? = mType

    override fun getTokenStart(): Int = mTokenRange.start

    override fun getTokenEnd(): Int = mTokenRange.end

    override fun getBufferSequence(): CharSequence = mTokenRange.bufferSequence

    override fun getBufferEnd(): Int = mTokenRange.bufferEnd

    // endregion
}
