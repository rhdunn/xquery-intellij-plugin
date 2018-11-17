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

class XPathLexer : LexerImpl(STATE_DEFAULT) {
    // region States

    private fun stateDefault() {
        val c = mTokenRange.codePoint
        val cc = CharacterClass.getCharClass(c)
        when (cc) {
            CharacterClass.DOT -> {
                mTokenRange.match()
                while (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.DIGIT)
                    mTokenRange.match()
                mType = XPathTokenType.DECIMAL_LITERAL
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

    // endregion
    // region Lexer

    override fun advance() {
        when (nextState()) {
            STATE_DEFAULT -> stateDefault()
            else -> throw AssertionError("Invalid state: $state")
        }
    }

    // endregion
}
