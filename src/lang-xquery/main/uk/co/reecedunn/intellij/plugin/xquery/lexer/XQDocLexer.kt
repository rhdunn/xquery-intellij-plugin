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

import uk.co.reecedunn.intellij.plugin.core.lexer.*
import xqt.platform.xml.lexer.*
import xqt.platform.xml.model.XmlChar
import xqt.platform.xml.model.XmlCharReader

@Suppress("DuplicatedCode")
class XQDocLexer : LexerImpl(STATE_CONTENTS) {
    // region States

    private fun matchEntityReference() {
        mType = when (characters.matchEntityReference()) {
            EntityReferenceType.EmptyEntityReference -> XQDocTokenType.EMPTY_ENTITY_REFERENCE
            EntityReferenceType.PartialEntityReference -> XQDocTokenType.PARTIAL_ENTITY_REFERENCE
            EntityReferenceType.CharacterReference -> XQDocTokenType.CHARACTER_REFERENCE
            else -> XQDocTokenType.PREDEFINED_ENTITY_REFERENCE
        }
    }

    private fun stateDefault() {
        when (characters.currentChar) {
            XmlCharReader.EndOfBuffer -> mType = null
            Tilde -> {
                characters.advance()
                mType = XQDocTokenType.XQDOC_COMMENT_MARKER
                pushState(STATE_CONTENTS)
                pushState(STATE_TRIM)
            }

            else -> {
                pushState(STATE_XQUERY_CONTENTS)
                pushState(STATE_XQUERY_CONTENTS_TRIM)
                advance()
            }
        }
    }

    private fun stateContents() {
        when (characters.currentChar) {
            XmlCharReader.EndOfBuffer -> mType = null
            LessThanSign -> {
                characters.advance()
                mType = XQDocTokenType.OPEN_XML_TAG
                pushState(STATE_ELEM_CONSTRUCTOR)
            }

            LineFeed, CarriageReturn -> {
                pushState(STATE_TRIM)
                stateTrim(STATE_TRIM)
            }

            Ampersand -> matchEntityReference() // XML PredefinedEntityRef and CharRef
            else -> while (true) {
                when (characters.currentChar) {
                    LineFeed, CarriageReturn -> {
                        pushState(STATE_TRIM)
                        mType = XQDocTokenType.CONTENTS
                        return
                    }

                    XmlCharReader.EndOfBuffer, LessThanSign, Ampersand -> {
                        mType = XQDocTokenType.CONTENTS
                        return
                    }

                    else -> {
                        characters.advance()
                    }
                }
            }
        }
    }

    private fun stateXQueryContents() {
        when (characters.currentChar) {
            XmlCharReader.EndOfBuffer -> mType = null
            LineFeed, CarriageReturn -> {
                pushState(STATE_XQUERY_CONTENTS_TRIM)
                stateTrim(STATE_XQUERY_CONTENTS_TRIM)
            }

            else -> while (true) {
                when (characters.currentChar) {
                    LineFeed, CarriageReturn -> {
                        pushState(STATE_XQUERY_CONTENTS_TRIM)
                        mType = XQDocTokenType.CONTENTS
                        return
                    }

                    XmlCharReader.EndOfBuffer -> {
                        mType = XQDocTokenType.CONTENTS
                        return
                    }

                    else -> {
                        characters.advance()
                    }
                }
            }
        }
    }

    private fun stateTaggedContents() {
        when (characters.currentChar) {
            in AlphaNumeric -> {
                while (characters.currentChar in AlphaNumeric) {
                    characters.advance()
                }
                mType = TAG_NAMES[tokenText] ?: XQDocTokenType.TAG
                if (mType === XQDocTokenType.T_PARAM) {
                    popState()
                    pushState(STATE_PARAM_TAG_CONTENTS_START)
                }
            }

            Space, CharacterTabulation -> {
                while (characters.currentChar == Space || characters.currentChar == CharacterTabulation) {
                    characters.advance()
                }
                mType = XQDocTokenType.WHITE_SPACE
                popState()
            }

            else -> {
                popState()
                stateContents()
            }
        }
    }

    private fun stateParamTagContentsStart() {
        when (characters.currentChar) {
            Space, CharacterTabulation -> {
                while (characters.currentChar == Space || characters.currentChar == CharacterTabulation) {
                    characters.advance()
                }
                mType = XQDocTokenType.WHITE_SPACE
            }

            DollarSign -> {
                characters.advance()
                mType = XQDocTokenType.VARIABLE_INDICATOR
                popState()
                pushState(STATE_PARAM_TAG_VARNAME)
            }

            else -> {
                popState()
                stateContents()
            }
        }
    }

    private fun stateParamTagVarName() {
        when (characters.currentChar) {
            in S -> {
                characters.advance()
                while (characters.currentChar in S)
                    characters.advance()
                mType = XQDocTokenType.WHITE_SPACE
                popState()
            }

            // XQuery/XML NCName token rules.
            in NameStartChar -> {
                characters.advance()
                while (characters.currentChar in NameChar) {
                    characters.advance()
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
        when (characters.currentChar) {
            XmlCharReader.EndOfBuffer -> mType = null
            in AlphaNumeric -> {
                while (characters.currentChar in AlphaNumeric) {
                    characters.advance()
                }
                mType = XQDocTokenType.XML_TAG
            }

            in S -> {
                while (characters.currentChar in S) {
                    characters.advance()
                }
                mType = XQDocTokenType.WHITE_SPACE
            }

            EqualsSign -> {
                characters.advance()
                mType = XQDocTokenType.XML_EQUAL
            }

            QuotationMark -> {
                characters.advance()
                mType = XQDocTokenType.XML_ATTRIBUTE_VALUE_START
                pushState(STATE_ATTRIBUTE_VALUE_QUOTE)
            }

            Apostrophe -> {
                characters.advance()
                mType = XQDocTokenType.XML_ATTRIBUTE_VALUE_START
                pushState(STATE_ATTRIBUTE_VALUE_APOS)
            }

            Solidus -> {
                characters.advance()
                if (characters.currentChar == GreaterThanSign) {
                    characters.advance()
                    mType = XQDocTokenType.SELF_CLOSING_XML_TAG
                    popState()
                } else {
                    mType = XQDocTokenType.INVALID
                }
            }

            GreaterThanSign -> {
                characters.advance()
                mType = XQDocTokenType.END_XML_TAG
                popState()
                if (state == STATE_ELEM_CONSTRUCTOR) {
                    pushState(STATE_ELEM_CONTENTS)
                }
            }

            else -> {
                characters.advance()
                mType = XQDocTokenType.INVALID
            }
        }
    }

    private fun stateElemContents() {
        when (characters.currentChar) {
            XmlCharReader.EndOfBuffer -> mType = null
            LessThanSign -> {
                characters.advance()
                if (characters.currentChar == Solidus) {
                    characters.advance()
                    mType = XQDocTokenType.CLOSE_XML_TAG
                    popState()
                    pushState(STATE_ELEM_CONSTRUCTOR_CLOSING)
                } else {
                    mType = XQDocTokenType.OPEN_XML_TAG
                    pushState(STATE_ELEM_CONSTRUCTOR)
                }
            }

            Ampersand -> matchEntityReference() // XML PredefinedEntityRef and CharRef
            else -> {
                characters.advance()
                while (true) {
                    when (characters.currentChar) {
                        XmlCharReader.EndOfBuffer, LessThanSign, Ampersand -> {
                            mType = XQDocTokenType.XML_ELEMENT_CONTENTS
                            return
                        }

                        else -> {
                            characters.advance()
                        }
                    }
                }
            }
        }
    }

    private fun stateAttributeValue(endChar: XmlChar) {
        when (characters.currentChar) {
            XmlCharReader.EndOfBuffer -> mType = null
            endChar -> {
                characters.advance()
                mType = XQDocTokenType.XML_ATTRIBUTE_VALUE_END
                popState()
            }

            else -> {
                while (characters.currentChar != XmlCharReader.EndOfBuffer && characters.currentChar != endChar) {
                    characters.advance()
                }
                mType = XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS
            }
        }
    }

    private fun stateTrim(state: Int) {
        when (characters.currentChar) {
            XmlCharReader.EndOfBuffer -> mType = null
            Space, CharacterTabulation -> {
                while (characters.currentChar == Space || characters.currentChar == CharacterTabulation) {
                    characters.advance()
                }
                mType = XQDocTokenType.WHITE_SPACE
            }

            LineFeed, CarriageReturn -> {
                val pc = characters.currentChar
                characters.advance()
                if (pc == CarriageReturn && characters.currentChar == LineFeed) {
                    characters.advance()
                }

                while (characters.currentChar == Space || characters.currentChar == CharacterTabulation) {
                    characters.advance()
                }

                if (characters.currentChar == Colon) {
                    characters.advance()
                }

                mType = XQDocTokenType.TRIM
            }

            CommercialAt -> {
                if (state == STATE_TRIM) {
                    characters.advance()
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

    override fun advance(state: Int): Unit = when (state) {
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

    // endregion

    companion object {
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
    }
}
