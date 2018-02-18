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
import uk.co.reecedunn.intellij.plugin.core.lexer.CodePointRange
import uk.co.reecedunn.intellij.plugin.core.lexer.LexerImpl
import uk.co.reecedunn.intellij.plugin.core.lexer.STATE_DEFAULT

// region State Constants

const val STATE_NCNAME = 1

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
                XmlSchemaDataTypeTokenType.WHITE_SPACE
            }
            CharacterClass.NAME_START_CHAR -> {
                mTokenRange.match()
                var cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                while (cc == CharacterClass.NAME_START_CHAR ||
                        cc == CharacterClass.DIGIT ||
                        cc == CharacterClass.DOT ||
                        cc == CharacterClass.HYPHEN_MINUS ||
                        cc == CharacterClass.NAME_CHAR) {
                    mTokenRange.match()
                    cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                }
                XmlSchemaDataTypeTokenType.NCNAME
            }
            CharacterClass.AMPERSAND -> matchEntityReference()
            else -> {
                mTokenRange.match()
                var cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                while (cc != CharacterClass.WHITESPACE && cc != CharacterClass.AMPERSAND && cc != CharacterClass.END_OF_BUFFER) {
                    mTokenRange.match()
                    cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                }
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
            if (cc == CharacterClass.SEMICOLON) {
                mTokenRange.match()
                return XmlSchemaDataTypeTokenType.PREDEFINED_ENTITY_REFERENCE
            } else {
                return XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE
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
                    if (c == ';'.toInt()) {
                        mTokenRange.match()
                        return XmlSchemaDataTypeTokenType.CHARACTER_REFERENCE
                    } else {
                        return XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE
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
                if (c == ';'.toInt()) {
                    mTokenRange.match()
                    return XmlSchemaDataTypeTokenType.CHARACTER_REFERENCE
                } else {
                    return XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE
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
            STATE_NCNAME -> stateNCName()
            else -> throw AssertionError("Invalid state: $state")
        }
    }

    // endregion
}
