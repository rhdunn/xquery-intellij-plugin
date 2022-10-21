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

import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.core.lexer.CodePointRange
import uk.co.reecedunn.intellij.plugin.core.lexer.LexerImpl
import uk.co.reecedunn.intellij.plugin.core.lexer.STATE_DEFAULT
import uk.co.reecedunn.intellij.plugin.marklogic.log.lang.MarkLogicErrorLogFormat

class MarkLogicErrorLogLexer(val format: MarkLogicErrorLogFormat) : LexerImpl(STATE_DEFAULT) {
    // region Character Classes

    companion object {
        private const val END_OF_BUFFER: Int = -1
        private const val MESSAGE: Int = -2
        private const val NEW_LINE: Int = -3
        private const val DIGIT: Int = -4
        private const val WHITE_SPACE: Int = -5

        private const val HYPHEN_MINUS: Int = 1
        private const val DOT: Int = 2
        private const val COLON: Int = 3
        private const val PLUS: Int = 4

        private const val CLN = COLON
        private const val DIG = DIGIT
        private const val MIN = HYPHEN_MINUS
        private const val MSG = MESSAGE
        private const val NEL = NEW_LINE
        private const val PLS = PLUS
        private const val WSP = WHITE_SPACE

        private val characterClasses = intArrayOf(
            //////// x0   x1   x2   x3   x4   x5   x6   x7   x8   x9   xA   xB   xC   xD   xE   xF
            /* 0x */ MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, WSP, NEL, MSG, MSG, NEL, MSG, MSG,
            /* 1x */ MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG,
            /* 2x */ WSP, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, PLS, MSG, MIN, DOT, MSG,
            /* 3x */ DIG, DIG, DIG, DIG, DIG, DIG, DIG, DIG, DIG, DIG, CLN, MSG, MSG, MSG, MSG, MSG,
            /* 4x */ MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG,
            /* 5x */ MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG,
            /* 6x */ MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG,
            /* 7x */ MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG, MSG
        )

        fun getCharacterClass(c: Int): Int {
            if (c < characterClasses.size) { // 0x0000-0x0079
                return if (c == CodePointRange.END_OF_BUFFER.codepoint) END_OF_BUFFER else characterClasses[c]
            }
            return MESSAGE
        }
    }

    // endregion
    // region States

    object State {
        const val Default = 0
        const val Time = 1
        const val LogLevel = 2
        const val Server = 3
        const val Message = 4
        const val SimpleMessage = 5
    }

    private fun stateDefault(): IElementType? {
        var cc = getCharacterClass(mTokenRange.codePoint.codepoint)
        return when (cc) {
            END_OF_BUFFER -> null
            NEW_LINE -> {
                while (cc == NEW_LINE) {
                    mTokenRange.match()
                    cc = getCharacterClass(mTokenRange.codePoint.codepoint)
                }
                MarkLogicErrorLogTokenType.WHITE_SPACE
            }
            DIGIT -> {
                while (cc == DIGIT || cc == HYPHEN_MINUS) {
                    mTokenRange.match()
                    cc = getCharacterClass(mTokenRange.codePoint.codepoint)
                }
                pushState(State.Time)
                MarkLogicErrorLogTokenType.DATE
            }
            WHITE_SPACE -> {
                pushState(State.SimpleMessage)
                return stateLogLevelOrServer(State.SimpleMessage)
            }
            else -> {
                pushState(State.Message)
                return stateLogLevelOrServer(State.Default)
            }
        }
    }

    private fun stateTime(): IElementType? {
        var cc = getCharacterClass(mTokenRange.codePoint.codepoint)
        return when (cc) {
            END_OF_BUFFER -> null
            NEW_LINE -> {
                popState()
                stateDefault()
            }
            WHITE_SPACE -> {
                while (cc == WHITE_SPACE) {
                    mTokenRange.match()
                    cc = getCharacterClass(mTokenRange.codePoint.codepoint)
                }
                MarkLogicErrorLogTokenType.WHITE_SPACE
            }
            DIGIT -> {
                while (cc == DIGIT || cc == COLON || cc == DOT) {
                    mTokenRange.match()
                    cc = getCharacterClass(mTokenRange.codePoint.codepoint)
                }
                popState()
                pushState(State.LogLevel)
                MarkLogicErrorLogTokenType.TIME
            }
            else -> {
                popState()
                stateDefault()
            }
        }
    }

    private fun stateLogLevelOrServer(state: Int): IElementType? {
        var cc = getCharacterClass(mTokenRange.codePoint.codepoint)
        return when {
            cc == END_OF_BUFFER -> null
            cc == NEW_LINE -> {
                popState()
                stateDefault()
            }
            cc == WHITE_SPACE && state != State.SimpleMessage -> {
                while (cc == WHITE_SPACE) {
                    mTokenRange.match()
                    cc = getCharacterClass(mTokenRange.codePoint.codepoint)
                }
                MarkLogicErrorLogTokenType.WHITE_SPACE
            }
            cc == COLON -> {
                mTokenRange.match()
                MarkLogicErrorLogTokenType.COLON
            }
            cc == PLUS -> {
                mTokenRange.match()
                MarkLogicErrorLogTokenType.CONTINUATION
            }
            else -> {
                var seenWhitespace = false
                while (cc != NEW_LINE && cc != END_OF_BUFFER) {
                    when (cc) {
                        COLON -> if (!seenWhitespace) {
                            mTokenRange.save()
                            mTokenRange.match()
                            cc = getCharacterClass(mTokenRange.codePoint.codepoint)
                            mTokenRange.restore()
                            when {
                                cc != WHITE_SPACE && cc != PLUS -> seenWhitespace = true
                                state == State.Default -> {
                                    val text = mTokenRange.bufferSequence.substring(mTokenRange.start, mTokenRange.end)
                                    if (text == "WARNING") {
                                        return MarkLogicErrorLogTokenType.LogLevel.WARNING
                                    }
                                }
                                state == State.LogLevel -> {
                                    popState()
                                    pushState(if (format.haveServer) State.Server else State.Message)

                                    val text = mTokenRange.bufferSequence.substring(mTokenRange.start, mTokenRange.end)
                                    return MarkLogicErrorLogTokenType.LogLevel.token(text)
                                }
                                state == State.Server -> {
                                    popState()
                                    pushState(State.Message)
                                    return MarkLogicErrorLogTokenType.SERVER
                                }
                                else -> {
                                }
                            }
                        }
                        WHITE_SPACE -> seenWhitespace = true
                        else -> {
                        }
                    }
                    mTokenRange.match()
                    cc = getCharacterClass(mTokenRange.codePoint.codepoint)
                }
                MarkLogicErrorLogTokenType.MESSAGE
            }
        }
    }

    // endregion
    // region Lexer

    override fun advance(state: Int) {
        mType = when (state) {
            State.Default -> stateDefault()
            State.Time -> stateTime()
            State.LogLevel,
            State.Server,
            State.Message,
            State.SimpleMessage -> stateLogLevelOrServer(state)
            else -> throw AssertionError("Invalid state: $state")
        }
    }

    // endregion
}
