/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.log.lexer

import uk.co.reecedunn.intellij.plugin.core.lexer.CodePointRange
import uk.co.reecedunn.intellij.plugin.core.lexer.CodePointRangeImpl
import uk.co.reecedunn.intellij.plugin.core.lexer.LexerImpl
import uk.co.reecedunn.intellij.plugin.core.lexer.STATE_DEFAULT

class MarkLogicErrorLogLexer : LexerImpl(STATE_DEFAULT, CodePointRangeImpl()) {
    // region States

    private fun stateDefault() {
        var c = mTokenRange.codePoint
        mType = when (c) {
            CodePointRange.END_OF_BUFFER -> null
            '\r'.toInt(), '\n'.toInt() -> {
                while (c == '\r'.toInt() || c == '\n'.toInt()) {
                    mTokenRange.match()
                    c = mTokenRange.codePoint
                }
                MarkLogicErrorLogTokenType.WHITE_SPACE
            }
            else -> {
                while (c != '\r'.toInt() && c != '\n'.toInt() && c != CodePointRange.END_OF_BUFFER) {
                    mTokenRange.match()
                    c = mTokenRange.codePoint
                }
                MarkLogicErrorLogTokenType.MESSAGE
            }
        }
    }

    // endregion
    // region Lexer

    override fun advance(state: Int) = when (state) {
        STATE_DEFAULT -> stateDefault()
        else -> throw AssertionError("Invalid state: $state")
    }

    // endregion
}
