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
import uk.co.reecedunn.intellij.plugin.core.lexer.XmlCharReader
import uk.co.reecedunn.intellij.plugin.marklogic.log.lang.MarkLogicErrorLogFormat
import xqt.platform.xml.lexer.*

class MarkLogicErrorLogLexer(val format: MarkLogicErrorLogFormat) : LexerImpl(STATE_DEFAULT) {
    companion object {
        private const val STATE_TIME = 1
        private const val STATE_LOG_LEVEL = 2
        private const val STATE_SERVER = 3
        private const val STATE_MESSAGE = 4
        private const val STATE_SIMPLE_MESSAGE = 5
    }

    private fun stateDefault(): IElementType? = when (characters.currentChar) {
        XmlCharReader.EndOfBuffer -> null
        LineFeed, CarriageReturn -> {
            characters.advanceWhile { it == LineFeed || it == CarriageReturn }
            MarkLogicErrorLogTokenType.WHITE_SPACE
        }

        in Digits -> {
            characters.advanceWhile { it in Digits || it == HyphenMinus }
            pushState(STATE_TIME)
            MarkLogicErrorLogTokenType.DATE
        }

        Space, CharacterTabulation -> {
            pushState(STATE_SIMPLE_MESSAGE)
            stateLogLevelOrServer(STATE_SIMPLE_MESSAGE)
        }

        else -> {
            pushState(STATE_MESSAGE)
            stateLogLevelOrServer(STATE_DEFAULT)
        }
    }

    private fun stateTime(): IElementType? = when (characters.currentChar) {
        XmlCharReader.EndOfBuffer -> null
        LineFeed, CarriageReturn -> {
            popState()
            stateDefault()
        }

        Space, CharacterTabulation -> {
            characters.advanceWhile { it == Space || it == CharacterTabulation }
            MarkLogicErrorLogTokenType.WHITE_SPACE
        }

        in Digits -> {
            characters.advanceWhile { it in Digits || it == Colon || it == FullStop }
            popState()
            pushState(STATE_LOG_LEVEL)
            MarkLogicErrorLogTokenType.TIME
        }

        else -> {
            popState()
            stateDefault()
        }
    }

    private fun stateLogLevelOrServer(state: Int): IElementType? {
        var c = characters.currentChar
        return when {
            c == XmlCharReader.EndOfBuffer -> null
            c == LineFeed || c == CarriageReturn -> {
                popState()
                stateDefault()
            }

            (c == Space || c == CharacterTabulation) && state != STATE_SIMPLE_MESSAGE -> {
                characters.advanceWhile { it == Space || it == CharacterTabulation }
                MarkLogicErrorLogTokenType.WHITE_SPACE
            }

            c == Colon -> {
                characters.advance()
                MarkLogicErrorLogTokenType.COLON
            }

            c == PlusSign -> {
                characters.advance()
                MarkLogicErrorLogTokenType.CONTINUATION
            }

            else -> {
                var seenWhitespace = false
                while (c != LineFeed && c != CarriageReturn && c != XmlCharReader.EndOfBuffer) {
                    when (c) {
                        Colon -> if (!seenWhitespace) {
                            c = characters.nextChar
                            when {
                                c != Space && c != CharacterTabulation && c != PlusSign -> seenWhitespace = true
                                state == STATE_DEFAULT -> {
                                    if (tokenSequence == "WARNING") {
                                        return MarkLogicErrorLogTokenType.LogLevel.WARNING
                                    }
                                }

                                state == STATE_LOG_LEVEL -> {
                                    popState()
                                    pushState(if (format.haveServer) STATE_SERVER else STATE_MESSAGE)
                                    return MarkLogicErrorLogTokenType.LogLevel.token(tokenSequence)
                                }

                                state == STATE_SERVER -> {
                                    popState()
                                    pushState(STATE_MESSAGE)
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
                    characters.advance()
                    c = characters.currentChar
                }
                MarkLogicErrorLogTokenType.MESSAGE
            }
        }
    }

    override fun advance(state: Int): IElementType? = when (state) {
        STATE_DEFAULT -> stateDefault()
        STATE_TIME -> stateTime()
        STATE_LOG_LEVEL -> stateLogLevelOrServer(state)
        STATE_SERVER -> stateLogLevelOrServer(state)
        STATE_MESSAGE -> stateLogLevelOrServer(state)
        STATE_SIMPLE_MESSAGE -> stateLogLevelOrServer(state)
        else -> throw AssertionError("Invalid state: $state")
    }
}
