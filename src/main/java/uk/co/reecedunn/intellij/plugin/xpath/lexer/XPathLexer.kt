/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.lexer

import uk.co.reecedunn.intellij.plugin.core.lexer.CharacterClass
import uk.co.reecedunn.intellij.plugin.core.lexer.LexerImpl
import uk.co.reecedunn.intellij.plugin.core.lexer.STATE_DEFAULT

// region State Constants

const val STATE_DOUBLE_EXPONENT = 3

// endregion

open class XPathLexer : LexerImpl(STATE_DEFAULT) {
    // region States

    private fun stateDefault() {
        var c = mTokenRange.codePoint
        var cc = CharacterClass.getCharClass(c)
        when (cc) {
            CharacterClass.DOT -> {
                mTokenRange.match()
                while (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.DIGIT)
                    mTokenRange.match()
                mType = XPathTokenType.DECIMAL_LITERAL
                c = mTokenRange.codePoint
                if (c == 'e'.toInt() || c == 'E'.toInt()) {
                    mTokenRange.save()
                    mTokenRange.match()
                    c = mTokenRange.codePoint
                    if (c == '+'.toInt() || c == '-'.toInt()) {
                        mTokenRange.match()
                        c = mTokenRange.codePoint
                    }
                    if (CharacterClass.getCharClass(c) == CharacterClass.DIGIT) {
                        mTokenRange.match()
                        while (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.DIGIT)
                            mTokenRange.match()
                        mType = XPathTokenType.DOUBLE_LITERAL
                    } else {
                        pushState(STATE_DOUBLE_EXPONENT)
                        mTokenRange.restore()
                    }
                }
            }
            CharacterClass.DIGIT -> {
                mTokenRange.match()
                while (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.DIGIT)
                    mTokenRange.match()
                mType = if (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.DOT) {
                    mTokenRange.match()
                    while (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.DIGIT)
                        mTokenRange.match()
                    XPathTokenType.DECIMAL_LITERAL
                } else {
                    XPathTokenType.INTEGER_LITERAL
                }
                c = mTokenRange.codePoint
                if (c == 'e'.toInt() || c == 'E'.toInt()) {
                    mTokenRange.save()
                    mTokenRange.match()
                    c = mTokenRange.codePoint
                    if (c == '+'.toInt() || c == '-'.toInt()) {
                        mTokenRange.match()
                        c = mTokenRange.codePoint
                    }
                    if (CharacterClass.getCharClass(c) == CharacterClass.DIGIT) {
                        mTokenRange.match()
                        while (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.DIGIT)
                            mTokenRange.match()
                        mType = XPathTokenType.DOUBLE_LITERAL
                    } else {
                        pushState(STATE_DOUBLE_EXPONENT)
                        mTokenRange.restore()
                    }
                }
            }
            CharacterClass.NAME_START_CHAR -> {
                mTokenRange.match()
                cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                while (
                    cc == CharacterClass.NAME_START_CHAR ||
                    cc == CharacterClass.DIGIT ||
                    cc == CharacterClass.DOT ||
                    cc == CharacterClass.HYPHEN_MINUS ||
                    cc == CharacterClass.NAME_CHAR
                ) {
                    mTokenRange.match()
                    cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                }
                mType = XPathTokenType.NCNAME
            }
            CharacterClass.WHITESPACE -> {
                mTokenRange.match()
                while (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.WHITESPACE)
                    mTokenRange.match()
                mType = XPathTokenType.WHITE_SPACE
            }
            CharacterClass.END_OF_BUFFER -> mType = null
            else -> {
                mTokenRange.match()
                mType = XPathTokenType.BAD_CHARACTER
            }
        }
    }

    private fun stateDoubleExponent() {
        mTokenRange.match()
        val c = mTokenRange.codePoint
        if (c == '+'.toInt() || c == '-'.toInt()) {
            mTokenRange.match()
        }
        mType = XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT
        popState()
    }

    // endregion
    // region Lexer

    override fun advance() {
        when (nextState()) {
            STATE_DEFAULT -> stateDefault()
            STATE_DOUBLE_EXPONENT -> stateDoubleExponent()
            else -> throw AssertionError("Invalid state: $state")
        }
    }

    // endregion
}
