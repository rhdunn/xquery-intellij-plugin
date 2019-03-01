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
package uk.co.reecedunn.intellij.plugin.xqdoc.lexer

import uk.co.reecedunn.intellij.plugin.core.lexer.*

// region State Constants

private const val STATE_CONTENTS = 1
private const val STATE_TAGGED_CONTENTS = 2
private const val STATE_ELEM_CONSTRUCTOR = 3
private const val STATE_ELEM_CONTENTS = 4
private const val STATE_ELEM_CONSTRUCTOR_CLOSING = 5
private const val STATE_ATTRIBUTE_VALUE_QUOTE = 6
private const val STATE_ATTRIBUTE_VALUE_APOS = 7
private const val STATE_TRIM = 8
private const val STATE_PARAM_TAG_CONTENTS_START = 9
private const val STATE_PARAM_TAG_VARNAME = 10
private const val STATE_XQUERY_CONTENTS = 11
private const val STATE_XQUERY_CONTENTS_TRIM = 12

// endregion
// region Special Tag Names

private val TAG_NAMES = mapOf(
    "author" to XQDocTokenType.T_AUTHOR,
    "deprecated" to XQDocTokenType.T_DEPRECATED,
    "error" to XQDocTokenType.T_ERROR,
    "param" to XQDocTokenType.T_PARAM,
    "return" to XQDocTokenType.T_RETURN,
    "see" to XQDocTokenType.T_SEE,
    "since" to XQDocTokenType.T_SINCE,
    "version" to XQDocTokenType.T_VERSION
)

// endregion

class XQDocLexer : LexerImpl(STATE_CONTENTS, CodePointRangeImpl()) {
    // region States

    private fun matchEntityReference() {
        mTokenRange.match()
        var cc = CharacterClass.getCharClass(mTokenRange.codePoint)
        if (cc == CharacterClass.NAME_START_CHAR) {
            mTokenRange.match()
            cc = CharacterClass.getCharClass(mTokenRange.codePoint)
            while (cc == CharacterClass.NAME_START_CHAR || cc == CharacterClass.DIGIT) {
                mTokenRange.match()
                cc = CharacterClass.getCharClass(mTokenRange.codePoint)
            }
            mType = if (cc == CharacterClass.SEMICOLON) {
                mTokenRange.match()
                XQDocTokenType.PREDEFINED_ENTITY_REFERENCE
            } else {
                XQDocTokenType.PARTIAL_ENTITY_REFERENCE
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
                    mType = if (c == ';'.toInt()) {
                        mTokenRange.match()
                        XQDocTokenType.CHARACTER_REFERENCE
                    } else {
                        XQDocTokenType.PARTIAL_ENTITY_REFERENCE
                    }
                } else if (c == ';'.toInt()) {
                    mTokenRange.match()
                    mType = XQDocTokenType.EMPTY_ENTITY_REFERENCE
                } else {
                    mType = XQDocTokenType.PARTIAL_ENTITY_REFERENCE
                }
            } else if (c >= '0'.toInt() && c <= '9'.toInt()) {
                mTokenRange.match()
                while (c >= '0'.toInt() && c <= '9'.toInt()) {
                    mTokenRange.match()
                    c = mTokenRange.codePoint
                }
                mType = if (c == ';'.toInt()) {
                    mTokenRange.match()
                    XQDocTokenType.CHARACTER_REFERENCE
                } else {
                    XQDocTokenType.PARTIAL_ENTITY_REFERENCE
                }
            } else if (c == ';'.toInt()) {
                mTokenRange.match()
                mType = XQDocTokenType.EMPTY_ENTITY_REFERENCE
            } else {
                mType = XQDocTokenType.PARTIAL_ENTITY_REFERENCE
            }
        } else if (cc == CharacterClass.SEMICOLON) {
            mTokenRange.match()
            mType = XQDocTokenType.EMPTY_ENTITY_REFERENCE
        } else {
            mType = XQDocTokenType.PARTIAL_ENTITY_REFERENCE
        }
    }

    private fun stateDefault() {
        val c = mTokenRange.codePoint
        when (c) {
            CodePointRange.END_OF_BUFFER -> mType = null
            '~'.toInt() -> {
                mTokenRange.match()
                mType = XQDocTokenType.XQDOC_COMMENT_MARKER
                pushState(STATE_CONTENTS)
                pushState(STATE_TRIM)
            }
            else -> {
                pushState(STATE_XQUERY_CONTENTS)
                pushState(STATE_TRIM)
                advance()
            }
        }
    }

    private fun stateContents() {
        var c = mTokenRange.codePoint
        when (c) {
            CodePointRange.END_OF_BUFFER -> mType = null
            '<'.toInt() -> {
                mTokenRange.match()
                mType = XQDocTokenType.OPEN_XML_TAG
                pushState(STATE_ELEM_CONSTRUCTOR)
            }
            '\n'.toInt(), '\r'.toInt() -> { // U+000A, U+000D
                pushState(STATE_TRIM)
                stateTrim(STATE_TRIM)
            }
            '&'.toInt() -> matchEntityReference() // XML PredefinedEntityRef and CharRef
            else -> while (true)
                when (c) {
                    '\n'.toInt(), '\r'.toInt() -> { // U+000A, U+000D
                        pushState(STATE_TRIM)
                        mType = XQDocTokenType.CONTENTS
                        return
                    }
                    CodePointRange.END_OF_BUFFER, '<'.toInt(), '&'.toInt() -> {
                        mType = XQDocTokenType.CONTENTS
                        return
                    }
                    else -> {
                        mTokenRange.match()
                        c = mTokenRange.codePoint
                    }
                }
        }
    }

    private fun stateXQueryContents() {
        var c = mTokenRange.codePoint
        when (c) {
            CodePointRange.END_OF_BUFFER -> mType = null
            '\n'.toInt(), '\r'.toInt() -> { // U+000A, U+000D
                pushState(STATE_XQUERY_CONTENTS_TRIM)
                stateTrim(STATE_XQUERY_CONTENTS_TRIM)
            }
            else -> while (true)
                when (c) {
                    '\n'.toInt(), '\r'.toInt() -> { // U+000A, U+000D
                        pushState(STATE_XQUERY_CONTENTS_TRIM)
                        mType = XQDocTokenType.CONTENTS
                        return
                    }
                    CodePointRange.END_OF_BUFFER -> {
                        mType = XQDocTokenType.CONTENTS
                        return
                    }
                    else -> {
                        mTokenRange.match()
                        c = mTokenRange.codePoint
                    }
                }
        }
    }

    private fun stateTaggedContents() {
        var c = mTokenRange.codePoint
        if (c >= 'a'.toInt() && c <= 'z'.toInt() || c >= 'A'.toInt() && c <= 'Z'.toInt() || c >= '0'.toInt() && c <= '9'.toInt()) {
            while (c >= 'a'.toInt() && c <= 'z'.toInt() || c >= 'A'.toInt() && c <= 'Z'.toInt() || c >= '0'.toInt() && c <= '9'.toInt()) {
                mTokenRange.match()
                c = mTokenRange.codePoint
            }
            mType = TAG_NAMES[tokenText] ?: XQDocTokenType.TAG
            if (mType === XQDocTokenType.T_PARAM) {
                popState()
                pushState(STATE_PARAM_TAG_CONTENTS_START)
            }
        } else if (c == ' '.toInt() || c == '\t'.toInt()) {
            while (c == ' '.toInt() || c == '\t'.toInt()) {
                mTokenRange.match()
                c = mTokenRange.codePoint
            }
            mType = XQDocTokenType.WHITE_SPACE
            popState()
        } else {
            popState()
            stateContents()
        }
    }

    private fun stateParamTagContentsStart() {
        var c = mTokenRange.codePoint
        if (c == ' '.toInt() || c == '\t'.toInt()) {
            while (c == ' '.toInt() || c == '\t'.toInt()) {
                mTokenRange.match()
                c = mTokenRange.codePoint
            }
            mType = XQDocTokenType.WHITE_SPACE
        } else if (c == '$'.toInt()) {
            mTokenRange.match()
            mType = XQDocTokenType.VARIABLE_INDICATOR
            popState()
            pushState(STATE_PARAM_TAG_VARNAME)
        } else {
            popState()
            stateContents()
        }
    }

    private fun stateParamTagVarName() {
        var cc = CharacterClass.getCharClass(mTokenRange.codePoint)
        when (cc) {
            CharacterClass.WHITESPACE -> {
                mTokenRange.match()
                while (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.WHITESPACE)
                    mTokenRange.match()
                mType = XQDocTokenType.WHITE_SPACE
                popState()
            }
            CharacterClass.NAME_START_CHAR // XQuery/XML NCName token rules.
            -> {
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
                mType = XQDocTokenType.NCNAME
            }
            else -> {
                popState()
                stateContents()
            }
        }
    }

    private fun stateElemConstructor(state: Int) {
        var c = mTokenRange.codePoint
        when (c) {
            CodePointRange.END_OF_BUFFER -> mType = null
            '0'.toInt(), '1'.toInt(), '2'.toInt(), '3'.toInt(), '4'.toInt(), '5'.toInt(), '6'.toInt(), '7'.toInt(),
            '8'.toInt(), '9'.toInt(), 'a'.toInt(), 'b'.toInt(), 'c'.toInt(), 'd'.toInt(), 'e'.toInt(), 'f'.toInt(),
            'g'.toInt(), 'h'.toInt(), 'i'.toInt(), 'j'.toInt(), 'k'.toInt(), 'l'.toInt(), 'm'.toInt(), 'n'.toInt(),
            'o'.toInt(), 'p'.toInt(), 'q'.toInt(), 'r'.toInt(), 's'.toInt(), 't'.toInt(), 'u'.toInt(), 'v'.toInt(),
            'w'.toInt(), 'x'.toInt(), 'y'.toInt(), 'z'.toInt(), 'A'.toInt(), 'B'.toInt(), 'C'.toInt(), 'D'.toInt(),
            'E'.toInt(), 'F'.toInt(), 'G'.toInt(), 'H'.toInt(), 'I'.toInt(), 'J'.toInt(), 'K'.toInt(), 'L'.toInt(),
            'M'.toInt(), 'N'.toInt(), 'O'.toInt(), 'P'.toInt(), 'Q'.toInt(), 'R'.toInt(), 'S'.toInt(), 'T'.toInt(),
            'U'.toInt(), 'V'.toInt(), 'W'.toInt(), 'X'.toInt(), 'Y'.toInt(), 'Z'.toInt() -> {
                while (c >= 'a'.toInt() && c <= 'z'.toInt() || c >= 'A'.toInt() && c <= 'Z'.toInt() || c >= '0'.toInt() && c <= '9'.toInt()) {
                    mTokenRange.match()
                    c = mTokenRange.codePoint
                }
                mType = XQDocTokenType.XML_TAG
            }
            ' '.toInt(), '\t'.toInt(), '\r'.toInt(), '\n'.toInt() -> {
                while (c == ' '.toInt() || c == '\t'.toInt() || c == '\r'.toInt() || c == '\n'.toInt()) {
                    mTokenRange.match()
                    c = mTokenRange.codePoint
                }
                mType = XQDocTokenType.WHITE_SPACE
            }
            '='.toInt() -> {
                mTokenRange.match()
                mType = XQDocTokenType.XML_EQUAL
            }
            '"'.toInt() -> {
                mTokenRange.match()
                mType = XQDocTokenType.XML_ATTRIBUTE_VALUE_START
                pushState(STATE_ATTRIBUTE_VALUE_QUOTE)
            }
            '\''.toInt() -> {
                mTokenRange.match()
                mType = XQDocTokenType.XML_ATTRIBUTE_VALUE_START
                pushState(STATE_ATTRIBUTE_VALUE_APOS)
            }
            '/'.toInt() -> {
                mTokenRange.match()
                if (mTokenRange.codePoint == '>'.toInt()) {
                    mTokenRange.match()
                    mType = XQDocTokenType.SELF_CLOSING_XML_TAG
                    popState()
                } else {
                    mType = XQDocTokenType.INVALID
                }
            }
            '>'.toInt() -> {
                mTokenRange.match()
                mType = XQDocTokenType.END_XML_TAG
                popState()
                if (state == STATE_ELEM_CONSTRUCTOR) {
                    pushState(STATE_ELEM_CONTENTS)
                }
            }
            else -> {
                mTokenRange.match()
                mType = XQDocTokenType.INVALID
            }
        }
    }

    private fun stateElemContents() {
        var c = mTokenRange.codePoint
        when (c) {
            CodePointRange.END_OF_BUFFER -> mType = null
            '<'.toInt() -> {
                mTokenRange.match()
                if (mTokenRange.codePoint == '/'.toInt()) {
                    mTokenRange.match()
                    mType = XQDocTokenType.CLOSE_XML_TAG
                    popState()
                    pushState(STATE_ELEM_CONSTRUCTOR_CLOSING)
                } else {
                    mType = XQDocTokenType.OPEN_XML_TAG
                    pushState(STATE_ELEM_CONSTRUCTOR)
                }
            }
            '&'.toInt() -> matchEntityReference() // XML PredefinedEntityRef and CharRef
            else -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                while (true)
                    when (c) {
                        CodePointRange.END_OF_BUFFER, '<'.toInt(), '&'.toInt() -> {
                            mType = XQDocTokenType.XML_ELEMENT_CONTENTS
                            return
                        }
                        else -> {
                            mTokenRange.match()
                            c = mTokenRange.codePoint
                        }
                    }
            }
        }
    }

    private fun stateAttributeValue(endChar: Int) {
        var c = mTokenRange.codePoint
        when (c) {
            CodePointRange.END_OF_BUFFER -> mType = null
            endChar -> {
                mTokenRange.match()
                mType = XQDocTokenType.XML_ATTRIBUTE_VALUE_END
                popState()
            }
            else -> {
                while (c != CodePointRange.END_OF_BUFFER && c != endChar) {
                    mTokenRange.match()
                    c = mTokenRange.codePoint
                }
                mType = XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS
            }
        }
    }

    private fun stateTrim(state: Int) {
        var c = mTokenRange.codePoint
        when (c) {
            CodePointRange.END_OF_BUFFER -> mType = null
            ' '.toInt(), '\t'.toInt() -> {
                while (c == ' '.toInt() || c == '\t'.toInt()) {
                    mTokenRange.match()
                    c = mTokenRange.codePoint
                }
                mType = XQDocTokenType.WHITE_SPACE
            }
            '\r'.toInt(), '\n'.toInt() -> { // U+000D, U+000A
                mTokenRange.match()
                if (c == '\r'.toInt() && mTokenRange.codePoint == '\n'.toInt()) {
                    mTokenRange.match()
                }

                c = mTokenRange.codePoint
                while (c == ' '.toInt() || c == '\t'.toInt()) { // U+0020 || U+0009
                    mTokenRange.match()
                    c = mTokenRange.codePoint
                }

                if (c == ':'.toInt()) {
                    mTokenRange.match()
                }

                mType = XQDocTokenType.TRIM
            }
            '@'.toInt() -> {
                if (state == STATE_TRIM) {
                    mTokenRange.match()
                    mType = XQDocTokenType.TAG_MARKER
                    popState()
                    pushState(STATE_TAGGED_CONTENTS)
                } else {
                    popState()
                    advance()
                }
            }
            else -> {
                popState()
                advance()
            }
        }
    }

    // endregion
    // region Lexer

    override fun advance() {
        when (nextState()) {
            STATE_DEFAULT -> stateDefault()
            STATE_CONTENTS -> stateContents()
            STATE_TAGGED_CONTENTS -> stateTaggedContents()
            STATE_ELEM_CONSTRUCTOR, STATE_ELEM_CONSTRUCTOR_CLOSING -> stateElemConstructor(state)
            STATE_ELEM_CONTENTS -> stateElemContents()
            STATE_ATTRIBUTE_VALUE_QUOTE -> stateAttributeValue('"'.toInt())
            STATE_ATTRIBUTE_VALUE_APOS -> stateAttributeValue('\''.toInt())
            STATE_TRIM, STATE_XQUERY_CONTENTS_TRIM -> stateTrim(state)
            STATE_PARAM_TAG_CONTENTS_START -> stateParamTagContentsStart()
            STATE_PARAM_TAG_VARNAME -> stateParamTagVarName()
            STATE_XQUERY_CONTENTS -> stateXQueryContents()
            else -> throw AssertionError("Invalid state: $state")
        }
    }

    // endregion
}
