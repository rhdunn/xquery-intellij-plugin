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
    // region Character Classes

    companion object {
        private const val END_OF_BUFFER: Int = -1
        private const val MESSAGE: Int = -2
        private const val NEW_LINE: Int = -3
        private const val DIGIT: Int = -4

        private const val HYPHEN_MINUS: Int = 7

        private const val DIG = DIGIT
        private const val MIN = HYPHEN_MINUS
        private const val MSG = MESSAGE
        private const val NEL = NEW_LINE

        private val characterClasses = intArrayOf(
            //////// x0   x1   x2   x3   x4   x5   x6   x7   x8   x9   xA   xB   xC   xD   xE   xF
            /* 0x */ MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, NEL, MSG, MSG, NEL, MSG, MSG,
            /* 1x */ MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG,
            /* 2x */ MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MIN, MSG, MSG,
            /* 3x */ DIG, DIG, DIG, DIG, DIG, DIG, DIG, DIG, DIG, DIG, MSG, MSG, MSG, MSG, MSG, MSG,
            /* 4x */ MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG,
            /* 5x */ MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG,
            /* 6x */ MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG,
            /* 7x */ MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG
        )

        fun getCharacterClass(c: Int): Int {
            if (c < characterClasses.size) { // 0x0000-0x0079
                return if (c == CodePointRange.END_OF_BUFFER) END_OF_BUFFER else characterClasses[c]
            }
            return MESSAGE
        }
    }

    // endregion
    // region States

    private fun stateDefault() {
        var cc = getCharacterClass(mTokenRange.codePoint)
        mType = when (cc) {
            END_OF_BUFFER -> null
            NEW_LINE -> {
                while (cc == NEW_LINE) {
                    mTokenRange.match()
                    cc = getCharacterClass(mTokenRange.codePoint)
                }
                MarkLogicErrorLogTokenType.WHITE_SPACE
            }
            DIGIT -> {
                while (cc == DIGIT || cc == HYPHEN_MINUS) {
                    mTokenRange.match()
                    cc = getCharacterClass(mTokenRange.codePoint)
                }
                MarkLogicErrorLogTokenType.DATE
            }
            else -> {
                while (cc != NEW_LINE && cc != END_OF_BUFFER) {
                    mTokenRange.match()
                    cc = getCharacterClass(mTokenRange.codePoint)
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
