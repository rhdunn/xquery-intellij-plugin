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
package uk.co.reecedunn.intellij.plugin.core.tests.lexer

import com.intellij.lexer.Lexer
import com.intellij.psi.tree.IElementType

import org.hamcrest.core.Is.`is`
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat

abstract class LexerTestCase {
    protected fun matchToken(lexer: Lexer, text: String, state: Int, start: Int, end: Int, type: IElementType?) {
        assertThat(lexer.tokenText, `is`(text))
        assertThat(lexer.state, `is`(state))
        assertThat(lexer.tokenStart, `is`(start))
        assertThat(lexer.tokenEnd, `is`(end))
        assertThat(lexer.tokenType, `is`(type))

        if (lexer.tokenType == null) {
            assertThat(lexer.bufferEnd, `is`(start))
            assertThat(lexer.bufferEnd, `is`(end))
        }

        lexer.advance()
    }

    protected fun matchSingleToken(lexer: Lexer, text: String, state: Int, type: IElementType) {
        val length = text.length
        lexer.start(text)
        matchToken(lexer, text, 0, 0, length, type)
        matchToken(lexer, "", state, length, length, null)
    }

    protected fun matchSingleToken(lexer: Lexer, text: String, type: IElementType) {
        matchSingleToken(lexer, text, 0, type)
    }
}
