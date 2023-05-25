/*
 * Copyright (C) 2016-2018, 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.tests.lexer

import com.intellij.lexer.Lexer
import com.intellij.psi.tree.IElementType
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat

import org.hamcrest.core.Is.`is`

abstract class LexerTestCase {
    abstract val lexer: Lexer

    private fun endOfBuffer(state: Int, end: Int) {
        assertThat(lexer.tokenText, `is`(""))
        assertThat(lexer.state, `is`(state))
        assertThat(lexer.tokenStart, `is`(end))
        assertThat(lexer.tokenEnd, `is`(end))
        assertThat(lexer.tokenType, `is`(nullValue()))
        assertThat(lexer.bufferEnd, `is`(end))

        lexer.advance()
    }

    fun tokenize(text: CharSequence, start: Int, end: Int, state: Int, test: LexerTokens.() -> Unit = {}) {
        val tokens = LexerTokens(lexer, start, state)
        lexer.start(text, start, end, state)
        tokens.test()
        endOfBuffer(tokens.state, end)
    }

    fun tokenize(text: CharSequence, test: LexerTokens.() -> Unit = {}) {
        return tokenize(text, 0, text.length, 0, test)
    }

    fun tokenize(vararg lines: CharSequence, test: LexerTokens.() -> Unit = {}) {
        return tokenize(lines.joinToString("\n"), test)
    }

    fun token(text: String, endState: Int, type: IElementType) = tokenize(text) {
        token(text, type)
        state(endState)
    }

    fun token(text: String, type: IElementType) = token(text, 0, type)

    class LexerTokens(val lexer: Lexer, start: Int, internal var state: Int) {
        internal var end: Int = start

        fun state(state: Int) {
            this.state = state
        }

        fun token(text: String, type: IElementType) {
            val tokenEnd = end + text.length
            assertThat(lexer.tokenText, `is`(text))
            assertThat(lexer.state, `is`(state))
            assertThat(lexer.tokenStart, `is`(end))
            assertThat(lexer.tokenEnd, `is`(tokenEnd))
            assertThat(lexer.tokenType, `is`(type))

            end = tokenEnd
            lexer.advance()
        }
    }
}
