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
package uk.co.reecedunn.intellij.plugin.xdm.lexer

import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.core.lexer.CharacterClass
import uk.co.reecedunn.intellij.plugin.core.lexer.LexerImpl
import uk.co.reecedunn.intellij.plugin.core.lexer.STATE_DEFAULT

// region State Constants

const val STATE_NCNAME = 1
private const val STATE_NCNAME_CONTINUATION = 2
private const val STATE_NCNAME_INVALID = 3

// endregion

class XmlSchemaDataTypeLexer : LexerImpl(STATE_DEFAULT) {
    // region States

    private fun stateDefault() {
        mType = null
    }

    private fun stateNCName() {
        val c = mTokenRange.codePoint
        mType = when (CharacterClass.getCharClass(c)) {
            CharacterClass.END_OF_BUFFER -> null
            CharacterClass.WHITESPACE -> {
                while (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.WHITESPACE) {
                    mTokenRange.match()
                }
                if (state != STATE_NCNAME)
                    popState()
                XmlSchemaDataTypeTokenType.WHITE_SPACE
            }
            CharacterClass.NAME_START_CHAR,
            CharacterClass.DIGIT,
            CharacterClass.DOT,
            CharacterClass.HYPHEN_MINUS,
            CharacterClass.NAME_CHAR -> {
                var cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                val isNCNameStart = cc == CharacterClass.NAME_START_CHAR
                while (cc == CharacterClass.NAME_START_CHAR ||
                        cc == CharacterClass.DIGIT ||
                        cc == CharacterClass.DOT ||
                        cc == CharacterClass.HYPHEN_MINUS ||
                        cc == CharacterClass.NAME_CHAR) {
                    mTokenRange.match()
                    cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                }
                when (state) {
                    STATE_NCNAME -> {
                        if (cc == CharacterClass.AMPERSAND)
                            pushState(STATE_NCNAME_CONTINUATION)
                        if (isNCNameStart) XmlSchemaDataTypeTokenType.NCNAME else XmlSchemaDataTypeTokenType.UNKNOWN
                    }
                    STATE_NCNAME_INVALID -> XmlSchemaDataTypeTokenType.UNKNOWN
                    else -> XmlSchemaDataTypeTokenType.NCNAME
                }
            }
            CharacterClass.AMPERSAND -> matchEntityReference()
            else -> {
                mTokenRange.match()
                var cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                while (cc != CharacterClass.WHITESPACE && cc != CharacterClass.AMPERSAND && cc != CharacterClass.END_OF_BUFFER) {
                    mTokenRange.match()
                    cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                }
                if (cc == CharacterClass.AMPERSAND && state == STATE_NCNAME)
                    pushState(STATE_NCNAME_INVALID)
                XmlSchemaDataTypeTokenType.UNKNOWN
            }
        }
    }

    // endregion
    // region Helper Functions

    private fun matchEntityReference(): IElementType {
        mTokenRange.match()
        var cc = CharacterClass.getCharClass(mTokenRange.codePoint)
        if (cc == CharacterClass.NAME_START_CHAR) {
            mTokenRange.match()
            cc = CharacterClass.getCharClass(mTokenRange.codePoint)
            while (cc == CharacterClass.NAME_START_CHAR || cc == CharacterClass.DIGIT) {
                mTokenRange.match()
                cc = CharacterClass.getCharClass(mTokenRange.codePoint)
            }
            return if (cc == CharacterClass.SEMICOLON) {
                mTokenRange.match()
                XmlSchemaDataTypeTokenType.PREDEFINED_ENTITY_REFERENCE
            } else {
                XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE
            }
        } else if (cc == CharacterClass.HASH) {
            mTokenRange.match()
            var c = mTokenRange.codePoint
            if (c == 'x'.toInt()) {
                mTokenRange.match()
                c = mTokenRange.codePoint
                if (c >= '0'.toInt() && c <= '9'.toInt() || c >= 'a'.toInt() && c <= 'f'.toInt() || c >= 'A'.toInt() && c <= 'F'.toInt()) {
                    while (c >= '0'.toInt() && c <= '9'.toInt() || c >= 'a'.toInt() && c <= 'f'.toInt() || c >= 'A'.toInt() && c <= 'F'.toInt()) {
                        mTokenRange.match()
                        c = mTokenRange.codePoint
                    }
                    return if (c == ';'.toInt()) {
                        mTokenRange.match()
                        XmlSchemaDataTypeTokenType.CHARACTER_REFERENCE
                    } else {
                        XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE
                    }
                } else if (c == ';'.toInt()) {
                    mTokenRange.match()
                    return XmlSchemaDataTypeTokenType.EMPTY_ENTITY_REFERENCE
                } else {
                    return XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE
                }
            } else if (c >= '0'.toInt() && c <= '9'.toInt()) {
                mTokenRange.match()
                while (c >= '0'.toInt() && c <= '9'.toInt()) {
                    mTokenRange.match()
                    c = mTokenRange.codePoint
                }
                return if (c == ';'.toInt()) {
                    mTokenRange.match()
                    XmlSchemaDataTypeTokenType.CHARACTER_REFERENCE
                } else {
                    XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE
                }
            } else if (c == ';'.toInt()) {
                mTokenRange.match()
                return XmlSchemaDataTypeTokenType.EMPTY_ENTITY_REFERENCE
            } else {
                return XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE
            }
        } else if (cc == CharacterClass.SEMICOLON) {
            mTokenRange.match()
            return XmlSchemaDataTypeTokenType.EMPTY_ENTITY_REFERENCE
        } else {
            return XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE
        }
    }

    // endregion
    // region Lexer

    override fun advance() {
        when (nextState()) {
            STATE_DEFAULT -> stateDefault()
            STATE_NCNAME, STATE_NCNAME_CONTINUATION, STATE_NCNAME_INVALID -> stateNCName()
            else -> throw AssertionError("Invalid state: $state")
        }
    }

    // endregion
}
