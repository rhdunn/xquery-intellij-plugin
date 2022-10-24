/*
 * Copyright (C) 2021-2022 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.lexer.LexerImpl
import uk.co.reecedunn.intellij.plugin.core.lexer.STATE_DEFAULT
import uk.co.reecedunn.intellij.plugin.marklogic.log.lang.MarkLogicErrorLogFormat
import xqt.platform.xml.lexer.*
import xqt.platform.xml.model.XmlCharReader

class MarkLogicErrorLogLexer(val format: MarkLogicErrorLogFormat) : LexerImpl(STATE_DEFAULT) {
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
        return when (mTokenRange.codePoint) {
            XmlCharReader.EndOfBuffer -> null
            LineFeed, CarriageReturn -> {
                while (mTokenRange.codePoint == LineFeed || mTokenRange.codePoint == CarriageReturn) {
                    mTokenRange.match()
                }
                MarkLogicErrorLogTokenType.WHITE_SPACE
            }

            in Digit -> {
                while (mTokenRange.codePoint in Digit || mTokenRange.codePoint == HyphenMinus) {
                    mTokenRange.match()
                }
                pushState(State.Time)
                MarkLogicErrorLogTokenType.DATE
            }

            Space, CharacterTabulation -> {
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
        return when (mTokenRange.codePoint) {
            XmlCharReader.EndOfBuffer -> null
            LineFeed, CarriageReturn -> {
                popState()
                stateDefault()
            }

            Space, CharacterTabulation -> {
                while (mTokenRange.codePoint == Space || mTokenRange.codePoint == CharacterTabulation) {
                    mTokenRange.match()
                }
                MarkLogicErrorLogTokenType.WHITE_SPACE
            }

            in Digit -> {
                var c = mTokenRange.codePoint
                while (c in Digit || c == Colon || c == FullStop) {
                    mTokenRange.match()
                    c = mTokenRange.codePoint
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
        var c = mTokenRange.codePoint
        return when {
            c == XmlCharReader.EndOfBuffer -> null
            c == LineFeed || c == CarriageReturn -> {
                popState()
                stateDefault()
            }

            (c == Space || c == CharacterTabulation) && state != State.SimpleMessage -> {
                while (c == Space || c == CharacterTabulation) {
                    mTokenRange.match()
                    c = mTokenRange.codePoint
                }
                MarkLogicErrorLogTokenType.WHITE_SPACE
            }

            c == Colon -> {
                mTokenRange.match()
                MarkLogicErrorLogTokenType.COLON
            }

            c == PlusSign -> {
                mTokenRange.match()
                MarkLogicErrorLogTokenType.CONTINUATION
            }

            else -> {
                var seenWhitespace = false
                while (c != LineFeed && c != CarriageReturn && c != XmlCharReader.EndOfBuffer) {
                    when (c) {
                        Colon -> if (!seenWhitespace) {
                            mTokenRange.save()
                            mTokenRange.match()
                            c = mTokenRange.codePoint
                            mTokenRange.restore()
                            when {
                                c != Space && c != CharacterTabulation && c != PlusSign -> seenWhitespace = true
                                state == State.Default -> {
                                    if (tokenSequence == "WARNING") {
                                        return MarkLogicErrorLogTokenType.LogLevel.WARNING
                                    }
                                }

                                state == State.LogLevel -> {
                                    popState()
                                    pushState(if (format.haveServer) State.Server else State.Message)
                                    return MarkLogicErrorLogTokenType.LogLevel.token(tokenSequence)
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

                        Space, CharacterTabulation -> seenWhitespace = true
                        else -> {
                        }
                    }
                    mTokenRange.match()
                    c = mTokenRange.codePoint
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
