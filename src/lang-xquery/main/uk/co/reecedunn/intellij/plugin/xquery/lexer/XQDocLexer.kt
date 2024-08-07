/*
 * Copyright (C) 2016-2022 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.lexer

import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.core.lexer.*
import xqt.platform.xml.lexer.AlphaNumeric
import xqt.platform.xml.lexer.NameChar
import xqt.platform.xml.lexer.NameStartChar
import xqt.platform.xml.lexer.S
import xqt.platform.xml.model.XmlChar

@Suppress("DuplicatedCode")
class XQDocLexer : LexerImpl(STATE_CONTENTS) {
    companion object {
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

        private val ContentCharExcludedChars = setOf(
            XmlCharReader.EndOfBuffer,
            LineFeed,
            CarriageReturn,
            LessThanSign,
            Ampersand
        )
    }

    private fun matchEntityReference(): IElementType = when (characters.matchEntityReference()) {
        EntityReferenceType.EmptyEntityReference -> XQDocTokenType.EMPTY_ENTITY_REFERENCE
        EntityReferenceType.PartialEntityReference -> XQDocTokenType.PARTIAL_ENTITY_REFERENCE
        EntityReferenceType.CharacterReference -> XQDocTokenType.CHARACTER_REFERENCE
        else -> XQDocTokenType.PREDEFINED_ENTITY_REFERENCE
    }

    private fun stateDefault(): IElementType? = when (characters.currentChar) {
        XmlCharReader.EndOfBuffer -> null
        Tilde -> {
            characters.advance()
            pushState(STATE_CONTENTS)
            pushState(STATE_TRIM)
            XQDocTokenType.XQDOC_COMMENT_MARKER
        }

        else -> {
            pushState(STATE_XQUERY_CONTENTS)
            pushState(STATE_XQUERY_CONTENTS_TRIM)
            advance()
            tokenType
        }
    }

    private fun stateContents(): IElementType? = when (characters.currentChar) {
        XmlCharReader.EndOfBuffer -> null
        LessThanSign -> {
            characters.advance()
            pushState(STATE_ELEM_CONSTRUCTOR)
            XQDocTokenType.OPEN_XML_TAG
        }

        LineFeed, CarriageReturn -> {
            pushState(STATE_TRIM)
            stateTrim(STATE_TRIM)
        }

        Ampersand -> matchEntityReference() // XML PredefinedEntityRef and CharRef
        else -> run {
            characters.advanceUntil { it in ContentCharExcludedChars }
            if (characters.currentChar == LineFeed || characters.currentChar == CarriageReturn) {
                pushState(STATE_TRIM)
            }
            XQDocTokenType.CONTENTS
        }
    }

    private fun stateXQueryContents(): IElementType? = when (characters.currentChar) {
        XmlCharReader.EndOfBuffer -> null
        LineFeed, CarriageReturn -> {
            pushState(STATE_XQUERY_CONTENTS_TRIM)
            stateTrim(STATE_XQUERY_CONTENTS_TRIM)
        }

        else -> {
            characters.advanceUntil { it == XmlCharReader.EndOfBuffer || it == LineFeed || it == CarriageReturn }
            if (characters.currentChar == LineFeed || characters.currentChar == CarriageReturn) {
                pushState(STATE_XQUERY_CONTENTS_TRIM)
            }
            XQDocTokenType.CONTENTS
        }
    }

    private fun stateTaggedContents(): IElementType? = when (characters.currentChar) {
        in AlphaNumeric -> {
            characters.advanceWhile { it in AlphaNumeric }
            val tokenType = TAG_NAMES[tokenText] ?: XQDocTokenType.TAG
            if (tokenType === XQDocTokenType.T_PARAM) {
                popState()
                pushState(STATE_PARAM_TAG_CONTENTS_START)
            }
            tokenType
        }

        Space, CharacterTabulation -> {
            characters.advanceWhile { it == Space || it == CharacterTabulation }
            popState()
            XQDocTokenType.WHITE_SPACE
        }

        else -> {
            popState()
            stateContents()
        }
    }

    private fun stateParamTagContentsStart(): IElementType? = when (characters.currentChar) {
        Space, CharacterTabulation -> {
            characters.advanceWhile { it == Space || it == CharacterTabulation }
            XQDocTokenType.WHITE_SPACE
        }

        DollarSign -> {
            characters.advance()
            popState()
            pushState(STATE_PARAM_TAG_VARNAME)
            XQDocTokenType.VARIABLE_INDICATOR
        }

        else -> {
            popState()
            stateContents()
        }
    }

    private fun stateParamTagVarName(): IElementType? = when (characters.currentChar) {
        in S -> {
            characters.advanceWhile { it in S }
            popState()
            XQDocTokenType.WHITE_SPACE
        }

        // XQuery/XML NCName token rules.
        in NameStartChar -> {
            characters.advanceWhile { it in NameChar }
            XQDocTokenType.NCNAME
        }

        else -> {
            popState()
            stateContents()
        }
    }

    private fun stateElemConstructor(state: Int): IElementType? = when (characters.currentChar) {
        XmlCharReader.EndOfBuffer -> null
        in AlphaNumeric -> {
            characters.advanceWhile { it in AlphaNumeric }
            XQDocTokenType.XML_TAG
        }

        in S -> {
            characters.advanceWhile { it in S }
            XQDocTokenType.WHITE_SPACE
        }

        EqualsSign -> {
            characters.advance()
            XQDocTokenType.XML_EQUAL
        }

        QuotationMark -> {
            characters.advance()
            pushState(STATE_ATTRIBUTE_VALUE_QUOTE)
            XQDocTokenType.XML_ATTRIBUTE_VALUE_START
        }

        Apostrophe -> {
            characters.advance()
            pushState(STATE_ATTRIBUTE_VALUE_APOS)
            XQDocTokenType.XML_ATTRIBUTE_VALUE_START
        }

        Solidus -> {
            characters.advance()
            if (characters.currentChar == GreaterThanSign) {
                characters.advance()
                popState()
                XQDocTokenType.SELF_CLOSING_XML_TAG
            } else {
                XQDocTokenType.INVALID
            }
        }

        GreaterThanSign -> {
            characters.advance()
            popState()
            if (state == STATE_ELEM_CONSTRUCTOR) {
                pushState(STATE_ELEM_CONTENTS)
            }
            XQDocTokenType.END_XML_TAG
        }

        else -> {
            characters.advance()
            XQDocTokenType.INVALID
        }
    }

    private fun stateElemContents(): IElementType? = when (characters.currentChar) {
        XmlCharReader.EndOfBuffer -> null
        LessThanSign -> {
            characters.advance()
            if (characters.currentChar == Solidus) {
                characters.advance()
                popState()
                pushState(STATE_ELEM_CONSTRUCTOR_CLOSING)
                XQDocTokenType.CLOSE_XML_TAG
            } else {
                pushState(STATE_ELEM_CONSTRUCTOR)
                XQDocTokenType.OPEN_XML_TAG
            }
        }

        Ampersand -> matchEntityReference() // XML PredefinedEntityRef and CharRef
        else -> {
            characters.advance()
            characters.advanceUntil { it == XmlCharReader.EndOfBuffer || it == LessThanSign || it == Ampersand }
            XQDocTokenType.XML_ELEMENT_CONTENTS
        }
    }

    private fun stateAttributeValue(endChar: XmlChar): IElementType? = when (characters.currentChar) {
        XmlCharReader.EndOfBuffer -> null
        endChar -> {
            characters.advance()
            popState()
            XQDocTokenType.XML_ATTRIBUTE_VALUE_END
        }

        else -> {
            characters.advanceWhile { it != XmlCharReader.EndOfBuffer && it != endChar }
            XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS
        }
    }

    private fun stateTrim(state: Int): IElementType? = when (characters.currentChar) {
        XmlCharReader.EndOfBuffer -> null
        Space, CharacterTabulation -> {
            characters.advanceWhile { it == Space || it == CharacterTabulation }
            XQDocTokenType.WHITE_SPACE
        }

        LineFeed, CarriageReturn -> {
            val pc = characters.currentChar
            characters.advance()
            if (pc == CarriageReturn && characters.currentChar == LineFeed) {
                characters.advance()
            }

            characters.advanceWhile { it == Space || it == CharacterTabulation }

            if (characters.currentChar == Colon) {
                characters.advance()
            }

            XQDocTokenType.TRIM
        }

        CommercialAt -> {
            if (state == STATE_TRIM) {
                characters.advance()
                popState()
                pushState(STATE_TAGGED_CONTENTS)
                XQDocTokenType.TAG_MARKER
            } else {
                popState()
                advance()
                tokenType
            }
        }

        else -> {
            popState()
            advance()
            tokenType
        }
    }

    override fun advance(state: Int): IElementType? = when (state) {
        STATE_DEFAULT -> stateDefault()
        STATE_CONTENTS -> stateContents()
        STATE_TAGGED_CONTENTS -> stateTaggedContents()
        STATE_ELEM_CONSTRUCTOR, STATE_ELEM_CONSTRUCTOR_CLOSING -> stateElemConstructor(state)
        STATE_ELEM_CONTENTS -> stateElemContents()
        STATE_ATTRIBUTE_VALUE_QUOTE -> stateAttributeValue(QuotationMark)
        STATE_ATTRIBUTE_VALUE_APOS -> stateAttributeValue(Apostrophe)
        STATE_TRIM, STATE_XQUERY_CONTENTS_TRIM -> stateTrim(state)
        STATE_PARAM_TAG_CONTENTS_START -> stateParamTagContentsStart()
        STATE_PARAM_TAG_VARNAME -> stateParamTagVarName()
        STATE_XQUERY_CONTENTS -> stateXQueryContents()
        else -> throw AssertionError("Invalid state: $state")
    }
}
