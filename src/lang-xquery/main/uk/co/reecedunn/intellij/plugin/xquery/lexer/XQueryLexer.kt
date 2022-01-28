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
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

@Suppress("DuplicatedCode")
class XQueryLexer : XPathLexer() {
    // region States

    override fun ncnameToKeyword(name: CharSequence): IKeywordOrNCNameType? {
        return KEYWORDS[name] ?: super.ncnameToKeyword(name)
    }

    override fun stateDefault(state: Int) {
        var c = mTokenRange.codePoint
        var cc = CharacterClass.getCharClass(c)
        when (cc) {
            CharacterClass.WHITESPACE -> {
                mTokenRange.match()
                while (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.WHITESPACE)
                    mTokenRange.match()
                mType = XPathTokenType.WHITE_SPACE
            }
            CharacterClass.DOT -> {
                mTokenRange.save()
                mTokenRange.match()
                cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                when {
                    cc == CharacterClass.DOT -> {
                        mTokenRange.match()
                        mType = if (mTokenRange.codePoint == '.'.code) {
                            mTokenRange.match()
                            XPathTokenType.ELLIPSIS
                        } else {
                            XPathTokenType.PARENT_SELECTOR
                        }
                        return
                    }
                    cc == CharacterClass.CURLY_BRACE_OPEN -> {
                        mTokenRange.match()
                        mType = XPathTokenType.CONTEXT_FUNCTION
                        return
                    }
                    cc != CharacterClass.DIGIT -> {
                        mType = XPathTokenType.DOT
                        return
                    }
                    else -> {
                        mTokenRange.restore()
                        mType = XPathTokenType.DECIMAL_LITERAL
                    }
                }
                mTokenRange.match()
                while (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.DIGIT)
                    mTokenRange.match()
                c = mTokenRange.codePoint
                if (c == 'e'.code || c == 'E'.code) {
                    mTokenRange.save()
                    mTokenRange.match()
                    c = mTokenRange.codePoint
                    if (c == '+'.code || c == '-'.code) {
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
                if (c == 'e'.code || c == 'E'.code) {
                    mTokenRange.save()
                    mTokenRange.match()
                    c = mTokenRange.codePoint
                    if (c == '+'.code || c == '-'.code) {
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
            CharacterClass.END_OF_BUFFER -> mType = null
            CharacterClass.QUOTE, CharacterClass.APOSTROPHE -> {
                mTokenRange.match()
                mType = XPathTokenType.STRING_LITERAL_START
                pushState(if (cc == CharacterClass.QUOTE) STATE_STRING_LITERAL_QUOTE else STATE_STRING_LITERAL_APOSTROPHE)
            }
            CharacterClass.NAME_START_CHAR -> {
                mTokenRange.match()
                cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                if (c == 'Q'.code && cc == CharacterClass.CURLY_BRACE_OPEN) {
                    mTokenRange.match()
                    mType = XPathTokenType.BRACED_URI_LITERAL_START
                    pushState(STATE_BRACED_URI_LITERAL)
                } else if (c == '_'.code && cc == CharacterClass.CURLY_BRACE_OPEN) {
                    mTokenRange.match()
                    mType = XPathTokenType.LAMBDA_FUNCTION
                } else {
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
                    mType = ncnameToKeyword(tokenText) ?: XPathTokenType.NCNAME
                }
            }
            CharacterClass.PARENTHESIS_OPEN -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                when (c) {
                    ':'.code -> {
                        mTokenRange.match()
                        mType = XPathTokenType.COMMENT_START_TAG
                        pushState(STATE_XQUERY_COMMENT)
                    }
                    '#'.code -> {
                        mTokenRange.match()
                        mType = XPathTokenType.PRAGMA_BEGIN
                        pushState(STATE_PRAGMA_PRE_QNAME)
                    }
                    else -> mType = XPathTokenType.PARENTHESIS_OPEN
                }
            }
            CharacterClass.PARENTHESIS_CLOSE -> {
                mTokenRange.match()
                mType = XPathTokenType.PARENTHESIS_CLOSE
            }
            CharacterClass.COLON -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                mType = when (c) {
                    ')'.code -> {
                        mTokenRange.match()
                        XPathTokenType.COMMENT_END_TAG
                    }
                    ':'.code -> {
                        mTokenRange.match()
                        XPathTokenType.AXIS_SEPARATOR
                    }
                    '='.code -> {
                        mTokenRange.match()
                        XPathTokenType.ASSIGN_EQUAL
                    }
                    else -> XPathTokenType.QNAME_SEPARATOR
                }
            }
            CharacterClass.HASH -> {
                mTokenRange.match()
                mType = if (mTokenRange.codePoint == ')'.code) {
                    mTokenRange.match()
                    XPathTokenType.PRAGMA_END
                } else {
                    XPathTokenType.FUNCTION_REF_OPERATOR
                }
            }
            CharacterClass.EXCLAMATION_MARK -> {
                mTokenRange.match()
                mType = when (mTokenRange.codePoint) {
                    '='.code -> {
                        mTokenRange.match()
                        XPathTokenType.NOT_EQUAL
                    }
                    '!'.code -> {
                        mTokenRange.match()
                        XPathTokenType.TERNARY_ELSE // EXPath XPath/XQuery NG Proposal
                    }
                    else -> XPathTokenType.MAP_OPERATOR // XQuery 3.0
                }
            }
            CharacterClass.DOLLAR -> {
                mTokenRange.match()
                mType = XPathTokenType.VARIABLE_INDICATOR
            }
            CharacterClass.ASTERISK -> {
                mTokenRange.match()
                mType = XPathTokenType.STAR
            }
            CharacterClass.PLUS -> {
                mTokenRange.match()
                mType = XPathTokenType.PLUS
            }
            CharacterClass.COMMA -> {
                mTokenRange.match()
                mType = XPathTokenType.COMMA
            }
            CharacterClass.HYPHEN_MINUS -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                mType = if (c == '-'.code) {
                    mTokenRange.save()
                    mTokenRange.match()
                    if (mTokenRange.codePoint == '>'.code) {
                        mTokenRange.match()
                        XQueryTokenType.XML_COMMENT_END_TAG
                    } else {
                        mTokenRange.restore()
                        XPathTokenType.MINUS
                    }
                } else if (c == '>'.code) {
                    mTokenRange.match()
                    XPathTokenType.THIN_ARROW
                } else {
                    XPathTokenType.MINUS
                }
            }
            CharacterClass.SEMICOLON -> {
                mTokenRange.match()
                mType = XQueryTokenType.SEPARATOR
            }
            CharacterClass.LESS_THAN -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                if (c == '/'.code) {
                    mTokenRange.match()
                    mType = XQueryTokenType.CLOSE_XML_TAG
                } else if (c == '<'.code) {
                    val position = mTokenRange.end
                    mTokenRange.match()
                    matchOpenXmlTag()
                    mType = if (mType === XQueryTokenType.DIRELEM_OPEN_XML_TAG) {
                        // For when adding a DirElemConstructor before another one -- i.e. <<a/>
                        mTokenRange.seek(position)
                        XPathTokenType.LESS_THAN
                    } else {
                        mTokenRange.seek(position)
                        mTokenRange.match()
                        XPathTokenType.NODE_BEFORE
                    }
                } else if (c == '='.code) {
                    mTokenRange.match()
                    mType = XPathTokenType.LESS_THAN_OR_EQUAL
                } else if (c == '?'.code) {
                    mTokenRange.match()
                    mType = XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN
                    pushState(STATE_PROCESSING_INSTRUCTION)
                } else if (c == '!'.code) {
                    mTokenRange.match()
                    if (mTokenRange.codePoint == '-'.code) {
                        mTokenRange.match()
                        if (mTokenRange.codePoint == '-'.code) {
                            mTokenRange.match()
                            mType = XQueryTokenType.XML_COMMENT_START_TAG
                            pushState(STATE_XML_COMMENT)
                        } else {
                            mType = XQueryTokenType.INVALID
                        }
                    } else if (mTokenRange.codePoint == '['.code) {
                        mTokenRange.match()
                        if (mTokenRange.codePoint == 'C'.code) {
                            mTokenRange.match()
                            if (mTokenRange.codePoint == 'D'.code) {
                                mTokenRange.match()
                                if (mTokenRange.codePoint == 'A'.code) {
                                    mTokenRange.match()
                                    if (mTokenRange.codePoint == 'T'.code) {
                                        mTokenRange.match()
                                        if (mTokenRange.codePoint == 'A'.code) {
                                            mTokenRange.match()
                                            if (mTokenRange.codePoint == '['.code) {
                                                mTokenRange.match()
                                                mType = XQueryTokenType.CDATA_SECTION_START_TAG
                                                pushState(STATE_CDATA_SECTION)
                                            } else {
                                                mType = XQueryTokenType.INVALID
                                            }
                                        } else {
                                            mType = XQueryTokenType.INVALID
                                        }
                                    } else {
                                        mType = XQueryTokenType.INVALID
                                    }
                                } else {
                                    mType = XQueryTokenType.INVALID
                                }
                            } else {
                                mType = XQueryTokenType.INVALID
                            }
                        } else {
                            mType = XQueryTokenType.INVALID
                        }
                    } else {
                        mType = XQueryTokenType.INVALID
                    }
                } else {
                    if (state == STATE_MAYBE_DIR_ELEM_CONSTRUCTOR) {
                        mType = XPathTokenType.LESS_THAN
                    } else {
                        matchOpenXmlTag()
                    }
                }
            }
            CharacterClass.GREATER_THAN -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                mType = when (c) {
                    '>'.code -> {
                        mTokenRange.match()
                        XPathTokenType.NODE_AFTER
                    }
                    '='.code -> {
                        mTokenRange.match()
                        XPathTokenType.GREATER_THAN_OR_EQUAL
                    }
                    else -> XPathTokenType.GREATER_THAN
                }
            }
            CharacterClass.EQUAL -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                mType = if (c == '>'.code) {
                    mTokenRange.match()
                    XPathTokenType.ARROW
                } else {
                    XPathTokenType.EQUAL
                }
            }
            CharacterClass.CURLY_BRACE_OPEN -> {
                mTokenRange.match()
                mType = XPathTokenType.BLOCK_OPEN
                pushState(state)
            }
            CharacterClass.CURLY_BRACE_CLOSE -> {
                mTokenRange.match()
                mType = if (mTokenRange.codePoint == '`'.code && state == STATE_DEFAULT_STRING_INTERPOLATION) {
                    mTokenRange.match()
                    XQueryTokenType.STRING_INTERPOLATION_CLOSE
                } else {
                    XPathTokenType.BLOCK_CLOSE
                }
                popState()
            }
            CharacterClass.VERTICAL_BAR -> {
                mTokenRange.match()
                mType = if (mTokenRange.codePoint == '|'.code) {
                    mTokenRange.match()
                    XPathTokenType.CONCATENATION
                } else {
                    XPathTokenType.UNION
                }
            }
            CharacterClass.FORWARD_SLASH -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                mType = when (c) {
                    '/'.code -> {
                        mTokenRange.match()
                        XPathTokenType.ALL_DESCENDANTS_PATH
                    }
                    '>'.code -> {
                        mTokenRange.match()
                        XQueryTokenType.SELF_CLOSING_XML_TAG
                    }
                    else -> XPathTokenType.DIRECT_DESCENDANTS_PATH
                }
            }
            CharacterClass.AT_SIGN -> {
                mTokenRange.match()
                mType = XPathTokenType.ATTRIBUTE_SELECTOR
            }
            CharacterClass.SQUARE_BRACE_OPEN -> {
                mTokenRange.match()
                mType = XPathTokenType.SQUARE_OPEN
            }
            CharacterClass.SQUARE_BRACE_CLOSE -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                when (c) {
                    ']'.code -> {
                        mTokenRange.save()
                        mTokenRange.match()
                        mType = if (mTokenRange.codePoint == '>'.code) {
                            mTokenRange.match()
                            XQueryTokenType.CDATA_SECTION_END_TAG
                        } else {
                            mTokenRange.restore()
                            XPathTokenType.SQUARE_CLOSE
                        }
                    }
                    '`'.code -> {
                        mTokenRange.match()
                        mType = if (mTokenRange.codePoint == '`'.code) {
                            mTokenRange.match()
                            XQueryTokenType.STRING_CONSTRUCTOR_END
                        } else {
                            XQueryTokenType.INVALID
                        }
                    }
                    else -> mType = XPathTokenType.SQUARE_CLOSE
                }
            }
            CharacterClass.QUESTION_MARK -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                mType = when (c) {
                    '>'.code -> {
                        mTokenRange.match()
                        XQueryTokenType.PROCESSING_INSTRUCTION_END
                    }
                    '?'.code -> {
                        mTokenRange.match()
                        XPathTokenType.TERNARY_IF
                    }
                    ':'.code -> {
                        mTokenRange.match()
                        XPathTokenType.ELVIS // EXPath XPath/XQuery NG Proposal
                    }
                    else -> XPathTokenType.OPTIONAL
                }
            }
            CharacterClass.PERCENT -> {
                mTokenRange.match()
                mType = XQueryTokenType.ANNOTATION_INDICATOR
            }
            CharacterClass.AMPERSAND -> {
                mTokenRange.match()
                mType = XQueryTokenType.ENTITY_REFERENCE_NOT_IN_STRING
            }
            CharacterClass.BACK_TICK -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                if (c == '`'.code) {
                    mTokenRange.match()
                    if (mTokenRange.codePoint == '['.code) {
                        mTokenRange.match()
                        mType = XQueryTokenType.STRING_CONSTRUCTOR_START
                        pushState(STATE_STRING_CONSTRUCTOR_CONTENTS)
                    } else {
                        mType = XQueryTokenType.INVALID
                    }
                } else {
                    mType = XQueryTokenType.INVALID
                }
            }
            CharacterClass.TILDE -> {
                mTokenRange.match()
                mType = XPathTokenType.TYPE_ALIAS
            }
            else -> {
                mTokenRange.match()
                mType = XPathTokenType.BAD_CHARACTER
            }
        }
    }

    override fun stateStringLiteral(type: Char) {
        var c = mTokenRange.codePoint
        if (c == type.code) {
            mTokenRange.match()
            if (mTokenRange.codePoint == type.code && type != '}') {
                mTokenRange.match()
                mType = XPathTokenType.ESCAPED_CHARACTER
            } else {
                mType = if (type == '}') XPathTokenType.BRACED_URI_LITERAL_END else XPathTokenType.STRING_LITERAL_END
                popState()
            }
        } else if (c == '&'.code) {
            matchEntityReference(if (type == '"') STATE_STRING_LITERAL_QUOTE else STATE_STRING_LITERAL_APOSTROPHE)
        } else if (c == '{'.code && type == '}') {
            mTokenRange.match()
            mType = XPathTokenType.BAD_CHARACTER
        } else if (c == CodePointRange.END_OF_BUFFER) {
            mType = null
        } else {
            while (c != type.code && c != CodePointRange.END_OF_BUFFER && c != '&'.code && !(type == '}' && c == '{'.code)) {
                mTokenRange.match()
                c = mTokenRange.codePoint
            }
            mType = XPathTokenType.STRING_LITERAL_CONTENTS
        }
    }

    private fun stateXmlComment() {
        var c = mTokenRange.codePoint
        if (c == CodePointRange.END_OF_BUFFER) {
            mType = null
            return
        } else if (c == '-'.code) {
            mTokenRange.save()
            mTokenRange.match()
            if (mTokenRange.codePoint == '-'.code) {
                mTokenRange.match()
                if (mTokenRange.codePoint == '>'.code) {
                    mTokenRange.match()
                    mType = XQueryTokenType.XML_COMMENT_END_TAG
                    popState()
                    return
                } else {
                    mTokenRange.restore()
                }
            } else {
                mTokenRange.restore()
            }
        }

        while (true) {
            if (c == CodePointRange.END_OF_BUFFER) {
                mTokenRange.match()
                mType = XQueryTokenType.XML_COMMENT
                popState()
                pushState(STATE_UNEXPECTED_END_OF_BLOCK)
                return
            } else if (c == '-'.code) {
                mTokenRange.save()
                mTokenRange.match()
                if (mTokenRange.codePoint == '-'.code) {
                    mTokenRange.match()
                    if (mTokenRange.codePoint == '>'.code) {
                        mTokenRange.restore()
                        mType = XQueryTokenType.XML_COMMENT
                        return
                    }
                }
            } else {
                mTokenRange.match()
            }
            c = mTokenRange.codePoint
        }
    }

    private fun stateCDataSection() {
        var c = mTokenRange.codePoint
        if (c == CodePointRange.END_OF_BUFFER) {
            mType = null
            return
        } else if (c == ']'.code) {
            mTokenRange.save()
            mTokenRange.match()
            if (mTokenRange.codePoint == ']'.code) {
                mTokenRange.match()
                if (mTokenRange.codePoint == '>'.code) {
                    mTokenRange.match()
                    mType = XQueryTokenType.CDATA_SECTION_END_TAG
                    popState()
                    return
                } else {
                    mTokenRange.restore()
                }
            } else {
                mTokenRange.restore()
            }
        }

        while (true) {
            if (c == CodePointRange.END_OF_BUFFER) {
                mTokenRange.match()
                mType = XQueryTokenType.CDATA_SECTION
                popState()
                pushState(STATE_UNEXPECTED_END_OF_BLOCK)
                return
            } else if (c == ']'.code) {
                mTokenRange.save()
                mTokenRange.match()
                if (mTokenRange.codePoint == ']'.code) {
                    mTokenRange.match()
                    if (mTokenRange.codePoint == '>'.code) {
                        mTokenRange.restore()
                        mType = XQueryTokenType.CDATA_SECTION
                        return
                    }
                }
            } else {
                mTokenRange.match()
            }
            c = mTokenRange.codePoint
        }
    }

    private fun stateDirElemConstructor(state: Int) {
        var cc = CharacterClass.getCharClass(mTokenRange.codePoint)
        val c: Int
        when (cc) {
            CharacterClass.WHITESPACE -> {
                mTokenRange.match()
                while (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.WHITESPACE)
                    mTokenRange.match()
                mType = XQueryTokenType.XML_WHITE_SPACE
                if (state == STATE_DIR_ELEM_CONSTRUCTOR) {
                    popState()
                    pushState(STATE_DIR_ATTRIBUTE_LIST)
                }
            }
            CharacterClass.COLON -> {
                mTokenRange.match()
                mType = if (state == STATE_DIR_ATTRIBUTE_LIST)
                    XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR
                else
                    XQueryTokenType.XML_TAG_QNAME_SEPARATOR
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
                mType = if (state == STATE_DIR_ATTRIBUTE_LIST)
                    if (tokenText == "xmlns")
                        XQueryTokenType.XML_ATTRIBUTE_XMLNS
                    else
                        XQueryTokenType.XML_ATTRIBUTE_NCNAME
                else
                    XQueryTokenType.XML_TAG_NCNAME
            }
            CharacterClass.GREATER_THAN -> {
                mTokenRange.match()
                mType = XQueryTokenType.END_XML_TAG
                popState()
                if (state == STATE_DIR_ELEM_CONSTRUCTOR || state == STATE_DIR_ATTRIBUTE_LIST) {
                    pushState(STATE_DIR_ELEM_CONTENT)
                }
            }
            CharacterClass.FORWARD_SLASH -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                if (c == '>'.code) {
                    mTokenRange.match()
                    mType = XQueryTokenType.SELF_CLOSING_XML_TAG
                    popState()
                } else {
                    mType = XQueryTokenType.INVALID
                }
            }
            CharacterClass.EQUAL -> {
                mTokenRange.match()
                mType = XQueryTokenType.XML_EQUAL
            }
            CharacterClass.QUOTE, CharacterClass.APOSTROPHE -> {
                mTokenRange.match()
                mType = XQueryTokenType.XML_ATTRIBUTE_VALUE_START
                pushState(if (cc == CharacterClass.QUOTE) STATE_DIR_ATTRIBUTE_VALUE_QUOTE else STATE_DIR_ATTRIBUTE_VALUE_APOSTROPHE)
            }
            CharacterClass.END_OF_BUFFER -> mType = null
            else -> {
                mTokenRange.match()
                mType = XPathTokenType.BAD_CHARACTER
            }
        }
    }

    private fun stateDirAttributeValue(type: Char) {
        var c = mTokenRange.codePoint
        if (c == type.code) {
            mTokenRange.match()
            if (mTokenRange.codePoint == type.code) {
                mTokenRange.match()
                mType = XQueryTokenType.XML_ESCAPED_CHARACTER
            } else {
                mType = XQueryTokenType.XML_ATTRIBUTE_VALUE_END
                popState()
            }
        } else if (c == '{'.code) {
            mTokenRange.match()
            if (mTokenRange.codePoint == '{'.code) {
                mTokenRange.match()
                mType = XQueryTokenType.XML_ESCAPED_CHARACTER
            } else {
                mType = XPathTokenType.BLOCK_OPEN
                pushState(if (type == '"') STATE_DEFAULT_ATTRIBUTE_QUOT else STATE_DEFAULT_ATTRIBUTE_APOSTROPHE)
            }
        } else if (c == '}'.code) {
            mTokenRange.match()
            mType = if (mTokenRange.codePoint == '}'.code) {
                mTokenRange.match()
                XQueryTokenType.XML_ESCAPED_CHARACTER
            } else {
                XPathTokenType.BLOCK_CLOSE
            }
        } else if (c == '<'.code) {
            mTokenRange.match()
            mType = XPathTokenType.BAD_CHARACTER
        } else if (c == '&'.code) {
            matchEntityReference(if (type == '"') STATE_DIR_ATTRIBUTE_VALUE_QUOTE else STATE_DIR_ATTRIBUTE_VALUE_APOSTROPHE)
        } else if (c == CodePointRange.END_OF_BUFFER) {
            mType = null
        } else {
            while (true) {
                when (c) {
                    CodePointRange.END_OF_BUFFER, '{'.code, '}'.code, '<'.code, '&'.code -> {
                        mType = XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS
                        return
                    }
                    else -> if (c == type.code) {
                        mType = XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS
                        return
                    } else {
                        mTokenRange.match()
                        c = mTokenRange.codePoint
                    }
                }
            }
        }
    }

    private fun stateDirElemContent() {
        var c = mTokenRange.codePoint
        if (c == '{'.code) {
            mTokenRange.match()
            if (mTokenRange.codePoint == '{'.code) {
                mTokenRange.match()
                mType = XPathTokenType.ESCAPED_CHARACTER
            } else {
                mType = XPathTokenType.BLOCK_OPEN
                pushState(STATE_DEFAULT_ELEM_CONTENT)
            }
        } else if (c == '}'.code) {
            mTokenRange.match()
            mType = if (mTokenRange.codePoint == '}'.code) {
                mTokenRange.match()
                XPathTokenType.ESCAPED_CHARACTER
            } else {
                XPathTokenType.BLOCK_CLOSE
            }
        } else if (c == '<'.code) {
            mTokenRange.match()
            c = mTokenRange.codePoint
            if (c == '/'.code) {
                mTokenRange.match()
                mType = XQueryTokenType.CLOSE_XML_TAG
                popState()
                pushState(STATE_DIR_ELEM_CONSTRUCTOR_CLOSING)
            } else if (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.NAME_START_CHAR) {
                mType = XQueryTokenType.OPEN_XML_TAG
                pushState(STATE_DIR_ELEM_CONSTRUCTOR)
            } else if (c == '?'.code) {
                mTokenRange.match()
                mType = XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN
                pushState(STATE_PROCESSING_INSTRUCTION_ELEM_CONTENT)
            } else if (c == '!'.code) {
                mTokenRange.match()
                if (mTokenRange.codePoint == '-'.code) {
                    mTokenRange.match()
                    if (mTokenRange.codePoint == '-'.code) {
                        mTokenRange.match()
                        mType = XQueryTokenType.XML_COMMENT_START_TAG
                        pushState(STATE_XML_COMMENT_ELEM_CONTENT)
                    } else {
                        mType = XQueryTokenType.INVALID
                    }
                } else if (mTokenRange.codePoint == '['.code) {
                    mTokenRange.match()
                    if (mTokenRange.codePoint == 'C'.code) {
                        mTokenRange.match()
                        if (mTokenRange.codePoint == 'D'.code) {
                            mTokenRange.match()
                            if (mTokenRange.codePoint == 'A'.code) {
                                mTokenRange.match()
                                if (mTokenRange.codePoint == 'T'.code) {
                                    mTokenRange.match()
                                    if (mTokenRange.codePoint == 'A'.code) {
                                        mTokenRange.match()
                                        if (mTokenRange.codePoint == '['.code) {
                                            mTokenRange.match()
                                            mType = XQueryTokenType.CDATA_SECTION_START_TAG
                                            pushState(STATE_CDATA_SECTION_ELEM_CONTENT)
                                        } else {
                                            mType = XQueryTokenType.INVALID
                                        }
                                    } else {
                                        mType = XQueryTokenType.INVALID
                                    }
                                } else {
                                    mType = XQueryTokenType.INVALID
                                }
                            } else {
                                mType = XQueryTokenType.INVALID
                            }
                        } else {
                            mType = XQueryTokenType.INVALID
                        }
                    } else {
                        mType = XQueryTokenType.INVALID
                    }
                } else {
                    mType = XQueryTokenType.INVALID
                }
            } else {
                mType = XPathTokenType.BAD_CHARACTER
            }
        } else if (c == '&'.code) {
            matchEntityReference(STATE_DIR_ELEM_CONTENT)
        } else if (c == CodePointRange.END_OF_BUFFER) {
            mType = null
        } else {
            while (true) {
                when (c) {
                    CodePointRange.END_OF_BUFFER, '{'.code, '}'.code, '<'.code, '&'.code -> {
                        mType = XQueryTokenType.XML_ELEMENT_CONTENTS
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

    private fun stateProcessingInstruction(state: Int) {
        var cc = CharacterClass.getCharClass(mTokenRange.codePoint)
        when (cc) {
            CharacterClass.WHITESPACE -> {
                mTokenRange.match()
                while (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.WHITESPACE)
                    mTokenRange.match()
                mType = XQueryTokenType.XML_WHITE_SPACE
                popState()
                pushState(if (state == STATE_PROCESSING_INSTRUCTION) STATE_PROCESSING_INSTRUCTION_CONTENTS else STATE_PROCESSING_INSTRUCTION_CONTENTS_ELEM_CONTENT)
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
                mType = XQueryTokenType.XML_PI_TARGET_NCNAME
            }
            CharacterClass.COLON -> {
                mTokenRange.match()
                mType = XQueryTokenType.XML_TAG_QNAME_SEPARATOR
            }
            CharacterClass.QUESTION_MARK -> {
                mTokenRange.match()
                if (mTokenRange.codePoint == '>'.code) {
                    mTokenRange.match()
                    mType = XQueryTokenType.PROCESSING_INSTRUCTION_END
                    popState()
                } else {
                    mType = XQueryTokenType.INVALID
                }
            }
            CharacterClass.END_OF_BUFFER -> mType = null
            else -> {
                mTokenRange.match()
                mType = XPathTokenType.BAD_CHARACTER
            }
        }
    }

    private fun stateProcessingInstructionContents() {
        var c = mTokenRange.codePoint
        if (c == CodePointRange.END_OF_BUFFER) {
            mType = null
            return
        } else if (c == '?'.code) {
            mTokenRange.save()
            mTokenRange.match()
            if (mTokenRange.codePoint == '>'.code) {
                mTokenRange.match()
                mType = XQueryTokenType.PROCESSING_INSTRUCTION_END
                popState()
                return
            } else {
                mTokenRange.restore()
            }
        }

        while (true) {
            if (c == CodePointRange.END_OF_BUFFER) {
                mTokenRange.match()
                mType = XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS
                popState()
                pushState(STATE_UNEXPECTED_END_OF_BLOCK)
                return
            } else if (c == '?'.code) {
                mTokenRange.save()
                mTokenRange.match()
                if (mTokenRange.codePoint == '>'.code) {
                    mTokenRange.restore()
                    mType = XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS
                    return
                }
            } else {
                mTokenRange.match()
            }
            c = mTokenRange.codePoint
        }
    }

    private fun stateStringConstructorContents() {
        var c = mTokenRange.codePoint
        if (c == '`'.code) {
            mTokenRange.save()
            mTokenRange.match()
            if (mTokenRange.codePoint == '{'.code) {
                mTokenRange.match()
                pushState(STATE_DEFAULT_STRING_INTERPOLATION)
                mType = XQueryTokenType.STRING_INTERPOLATION_OPEN
                return
            } else {
                mTokenRange.restore()
            }
        }
        while (c != CodePointRange.END_OF_BUFFER) {
            if (c == ']'.code) {
                mTokenRange.save()
                mTokenRange.match()
                if (mTokenRange.codePoint == '`'.code) {
                    mTokenRange.match()
                    if (mTokenRange.codePoint == '`'.code) {
                        mTokenRange.restore()
                        popState()
                        mType = XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS
                        return
                    }
                }
            } else if (c == '`'.code) {
                mTokenRange.save()
                mTokenRange.match()
                if (mTokenRange.codePoint == '{'.code) {
                    mTokenRange.restore()
                    mType = XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS
                    return
                } else {
                    mTokenRange.restore()
                }
            }
            mTokenRange.match()
            c = mTokenRange.codePoint
        }
        popState()
        pushState(STATE_UNEXPECTED_END_OF_BLOCK)
        mType = XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS
    }

    private fun stateStartDirElemConstructor() {
        when (CharacterClass.getCharClass(mTokenRange.codePoint)) {
            CharacterClass.LESS_THAN -> {
                mTokenRange.match()
                mType = XQueryTokenType.OPEN_XML_TAG
                if (CharacterClass.getCharClass(mTokenRange.codePoint) != CharacterClass.WHITESPACE) {
                    popState()
                    pushState(STATE_DIR_ELEM_CONSTRUCTOR)
                }
            }
            CharacterClass.WHITESPACE -> {
                mType = XQueryTokenType.XML_WHITE_SPACE
                matchWhiteSpace()
                popState()
                pushState(STATE_DIR_ELEM_CONSTRUCTOR)
            }
        }
    }

    // endregion
    // region Helper Functions

    private fun matchNCName(): Boolean {
        var cc = CharacterClass.getCharClass(mTokenRange.codePoint)
        if (cc != CharacterClass.NAME_START_CHAR)
            return false

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
        return true
    }

    private fun matchQName(): Boolean {
        if (!matchNCName())
            return false

        if (mTokenRange.codePoint == ':'.code) {
            mTokenRange.match()
            matchNCName()
        }
        return true
    }

    private fun matchWhiteSpace() {
        var cc = CharacterClass.getCharClass(mTokenRange.codePoint)
        while (cc == CharacterClass.WHITESPACE) {
            mTokenRange.match()
            cc = CharacterClass.getCharClass(mTokenRange.codePoint)
        }
    }

    private fun matchOpenXmlTag() {
        // Whitespace between the '<' and the NCName/QName is invalid. The lexer
        // allows this to provide better error reporting in the parser.
        mTokenRange.save()
        matchWhiteSpace()

        if (!matchQName()) {
            mTokenRange.restore()
            mType = XPathTokenType.LESS_THAN
            return
        }

        mType = XQueryTokenType.DIRELEM_OPEN_XML_TAG
        matchWhiteSpace()

        when (CharacterClass.getCharClass(mTokenRange.codePoint)) {
            CharacterClass.FORWARD_SLASH -> {
                mTokenRange.save()
                mTokenRange.match()
                if (mTokenRange.codePoint == '>'.code) {
                    mTokenRange.match()
                    return
                }
                mType = XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG
                mTokenRange.restore()
            }
            CharacterClass.GREATER_THAN -> {
                mTokenRange.match()
                pushState(STATE_DIR_ELEM_CONTENT)
            }
            CharacterClass.NAME_START_CHAR -> pushState(STATE_DIR_ATTRIBUTE_LIST)
            else -> mType = XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG
        }
    }

    private fun matchEntityReference(state: Int) {
        mType = if (state == STATE_DIR_ATTRIBUTE_VALUE_QUOTE || state == STATE_DIR_ATTRIBUTE_VALUE_APOSTROPHE) {
            when (mTokenRange.matchEntityReference()) {
                EntityReferenceType.CharacterReference -> XQueryTokenType.XML_CHARACTER_REFERENCE
                EntityReferenceType.EmptyEntityReference -> XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE
                EntityReferenceType.PartialEntityReference -> XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE
                else -> XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE
            }
        } else {
            when (mTokenRange.matchEntityReference()) {
                EntityReferenceType.CharacterReference -> XQueryTokenType.CHARACTER_REFERENCE
                EntityReferenceType.EmptyEntityReference -> XQueryTokenType.EMPTY_ENTITY_REFERENCE
                EntityReferenceType.PartialEntityReference -> XQueryTokenType.PARTIAL_ENTITY_REFERENCE
                else -> XQueryTokenType.PREDEFINED_ENTITY_REFERENCE
            }
        }
    }

    // endregion
    // region Lexer

    override fun advance(state: Int): Unit = when (state) {
        STATE_DEFAULT,
        STATE_DEFAULT_ATTRIBUTE_QUOT,
        STATE_DEFAULT_ATTRIBUTE_APOSTROPHE,
        STATE_DEFAULT_ELEM_CONTENT,
        STATE_DEFAULT_STRING_INTERPOLATION,
        STATE_MAYBE_DIR_ELEM_CONSTRUCTOR ->
            stateDefault(state)
        STATE_XML_COMMENT,
        STATE_XML_COMMENT_ELEM_CONTENT ->
            stateXmlComment()
        STATE_CDATA_SECTION,
        STATE_CDATA_SECTION_ELEM_CONTENT ->
            stateCDataSection()
        STATE_DIR_ELEM_CONSTRUCTOR,
        STATE_DIR_ELEM_CONSTRUCTOR_CLOSING,
        STATE_DIR_ATTRIBUTE_LIST ->
            stateDirElemConstructor(state)
        STATE_DIR_ATTRIBUTE_VALUE_QUOTE ->
            stateDirAttributeValue('"')
        STATE_DIR_ATTRIBUTE_VALUE_APOSTROPHE ->
            stateDirAttributeValue('\'')
        STATE_DIR_ELEM_CONTENT ->
            stateDirElemContent()
        STATE_PROCESSING_INSTRUCTION,
        STATE_PROCESSING_INSTRUCTION_ELEM_CONTENT ->
            stateProcessingInstruction(state)
        STATE_PROCESSING_INSTRUCTION_CONTENTS,
        STATE_PROCESSING_INSTRUCTION_CONTENTS_ELEM_CONTENT ->
            stateProcessingInstructionContents()
        STATE_STRING_CONSTRUCTOR_CONTENTS ->
            stateStringConstructorContents()
        STATE_START_DIR_ELEM_CONSTRUCTOR ->
            stateStartDirElemConstructor()
        else -> super.advance(state)
    }

    // endregion

    companion object {
        // region State Constants

        private const val STATE_XML_COMMENT = 5
        private const val STATE_CDATA_SECTION = 7
        private const val STATE_DIR_ELEM_CONSTRUCTOR = 11
        private const val STATE_DIR_ELEM_CONSTRUCTOR_CLOSING = 12
        private const val STATE_DIR_ATTRIBUTE_VALUE_QUOTE = 13
        private const val STATE_DIR_ATTRIBUTE_VALUE_APOSTROPHE = 14
        private const val STATE_DEFAULT_ATTRIBUTE_QUOT = 15
        private const val STATE_DEFAULT_ATTRIBUTE_APOSTROPHE = 16
        private const val STATE_DIR_ELEM_CONTENT = 17
        private const val STATE_DEFAULT_ELEM_CONTENT = 18
        private const val STATE_XML_COMMENT_ELEM_CONTENT = 19
        private const val STATE_CDATA_SECTION_ELEM_CONTENT = 20
        private const val STATE_PROCESSING_INSTRUCTION = 21
        private const val STATE_PROCESSING_INSTRUCTION_CONTENTS = 22
        private const val STATE_PROCESSING_INSTRUCTION_ELEM_CONTENT = 23
        private const val STATE_PROCESSING_INSTRUCTION_CONTENTS_ELEM_CONTENT = 24
        private const val STATE_DIR_ATTRIBUTE_LIST = 25
        private const val STATE_STRING_CONSTRUCTOR_CONTENTS = 27
        private const val STATE_DEFAULT_STRING_INTERPOLATION = 28
        const val STATE_MAYBE_DIR_ELEM_CONSTRUCTOR: Int = 29
        const val STATE_START_DIR_ELEM_CONSTRUCTOR: Int = 30

        // endregion

        private val KEYWORDS = mapOf(
            "after" to XQueryTokenType.K_AFTER, // Update Facility 1.0
            "allowing" to XQueryTokenType.K_ALLOWING, // XQuery 3.0
            "ascending" to XQueryTokenType.K_ASCENDING,
            "assignable" to XQueryTokenType.K_ASSIGNABLE, // Scripting Extension 1.0
            "attribute-decl" to XQueryTokenType.K_ATTRIBUTE_DECL, // MarkLogic 7.0
            "base-uri" to XQueryTokenType.K_BASE_URI,
            "before" to XQueryTokenType.K_BEFORE, // Update Facility 1.0
            "binary" to XQueryTokenType.K_BINARY, // MarkLogic 6.0
            "block" to XQueryTokenType.K_BLOCK, // Scripting Extension 1.0
            "boundary-space" to XQueryTokenType.K_BOUNDARY_SPACE,
            "by" to XQueryTokenType.K_BY,
            "catch" to XQueryTokenType.K_CATCH, // XQuery 3.0
            "collation" to XQueryTokenType.K_COLLATION,
            "complex-type" to XQueryTokenType.K_COMPLEX_TYPE, // MarkLogic 7.0
            "construction" to XQueryTokenType.K_CONSTRUCTION,
            "context" to XQueryTokenType.K_CONTEXT, // XQuery 3.0
            "copy" to XQueryTokenType.K_COPY, // Update Facility 1.0
            "copy-namespaces" to XQueryTokenType.K_COPY_NAMESPACES,
            "count" to XQueryTokenType.K_COUNT, // XQuery 3.0
            "decimal-format" to XQueryTokenType.K_DECIMAL_FORMAT, // XQuery 3.0
            "decimal-separator" to XQueryTokenType.K_DECIMAL_SEPARATOR, // XQuery 3.0
            "declare" to XQueryTokenType.K_DECLARE,
            "delete" to XQueryTokenType.K_DELETE, // Update Facility 1.0
            "descending" to XQueryTokenType.K_DESCENDING,
            "digit" to XQueryTokenType.K_DIGIT, // XQuery 3.0
            "document" to XQueryTokenType.K_DOCUMENT,
            "element-decl" to XQueryTokenType.K_ELEMENT_DECL, // MarkLogic 7.0
            "encoding" to XQueryTokenType.K_ENCODING,
            "exit" to XQueryTokenType.K_EXIT, // Scripting Extension 1.0
            "exponent-separator" to XQueryTokenType.K_EXPONENT_SEPARATOR, // XQuery 3.1
            "external" to XQueryTokenType.K_EXTERNAL,
            "first" to XQueryTokenType.K_FIRST, // Update Facility 1.0
            "fn" to XPathTokenType.K_FN, // Saxon 9.8
            "ft-option" to XQueryTokenType.K_FT_OPTION, // Full Text 1.0
            "full" to XQueryTokenType.K_FULL, // MarkLogic 6.0
            "fuzzy" to XQueryTokenType.K_FUZZY, // BaseX 6.1
            "greatest" to XQueryTokenType.K_GREATEST,
            "group" to XQueryTokenType.K_GROUP, // XQuery 3.0
            "grouping-separator" to XQueryTokenType.K_GROUPING_SEPARATOR, // XQuery 3.0
            "import" to XQueryTokenType.K_IMPORT,
            "infinity" to XQueryTokenType.K_INFINITY, // XQuery 3.0
            "inherit" to XQueryTokenType.K_INHERIT,
            "insert" to XQueryTokenType.K_INSERT, // Update Facility 1.0
            "into" to XQueryTokenType.K_INTO, // Update Facility 1.0
            "invoke" to XQueryTokenType.K_INVOKE, // Update Facility 3.0
            "item-type" to XQueryTokenType.K_ITEM_TYPE, // XQuery 4.0 ED
            "last" to XQueryTokenType.K_LAST, // Update Facility 1.0
            "lax" to XQueryTokenType.K_LAX,
            "minus-sign" to XQueryTokenType.K_MINUS_SIGN, // XQuery 3.0
            "modify" to XQueryTokenType.K_MODIFY, // Update Facility 1.0
            "module" to XQueryTokenType.K_MODULE,
            "model-group" to XQueryTokenType.K_MODEL_GROUP, // MarkLogic 7.0
            "NaN" to XQueryTokenType.K_NAN, // XQuery 3.0
            "next" to XQueryTokenType.K_NEXT, // XQuery 3.0
            "no-inherit" to XQueryTokenType.K_NO_INHERIT,
            "no-preserve" to XQueryTokenType.K_NO_PRESERVE,
            "nodes" to XQueryTokenType.K_NODES, // Update Facility 1.0
            "non-deterministic" to XQueryTokenType.K_NON_DETERMINISTIC, // BaseX 8.4
            "only" to XQueryTokenType.K_ONLY, // XQuery 3.0
            "order" to XQueryTokenType.K_ORDER,
            "ordering" to XQueryTokenType.K_ORDERING,
            "pattern-separator" to XQueryTokenType.K_PATTERN_SEPARATOR, // XQuery 3.0
            "per-mille" to XQueryTokenType.K_PER_MILLE, // XQuery 3.0
            "percent" to XQueryTokenType.K_PERCENT, // XQuery 3.0
            "preserve" to XQueryTokenType.K_PRESERVE,
            "previous" to XQueryTokenType.K_PREVIOUS, // XQuery 3.0
            "private" to XQueryTokenType.K_PRIVATE, // MarkLogic 6.0
            "public" to XQueryTokenType.K_PUBLIC, // XQuery 3.0 Annotations
            "rename" to XQueryTokenType.K_RENAME, // Update Facility 1.0
            "replace" to XQueryTokenType.K_REPLACE, // Update Facility 1.0
            "returning" to XQueryTokenType.K_RETURNING, // Scripting Extension 1.0
            "revalidation" to XQueryTokenType.K_REVALIDATION, // Update Facility 1.0
            "schema" to XQueryTokenType.K_SCHEMA,
            "schema-component" to XQueryTokenType.K_SCHEMA_COMPONENT, // MarkLogic 7.0
            "schema-facet" to XQueryTokenType.K_SCHEMA_FACET, // MarkLogic 7.0
            "schema-particle" to XQueryTokenType.K_SCHEMA_PARTICLE, // MarkLogic 7.0
            "schema-root" to XQueryTokenType.K_SCHEMA_ROOT, // MarkLogic 7.0
            "schema-type" to XQueryTokenType.K_SCHEMA_TYPE, // MarkLogic 7.0
            "schema-wildcard" to XQueryTokenType.K_SCHEMA_WILDCARD, // MarkLogic 7.0
            "sequential" to XQueryTokenType.K_SEQUENTIAL, // Scripting Extension 1.0
            "simple" to XQueryTokenType.K_SIMPLE, // Scripting Extension 1.0
            "simple-type" to XQueryTokenType.K_SIMPLE_TYPE, // MarkLogic 7.0
            "skip" to XQueryTokenType.K_SKIP, // Update Facility 1.0
            "sliding" to XQueryTokenType.K_SLIDING, // XQuery 3.0
            "stable" to XQueryTokenType.K_STABLE,
            "strict" to XQueryTokenType.K_STRICT,
            "strip" to XQueryTokenType.K_STRIP,
            "stylesheet" to XQueryTokenType.K_STYLESHEET, // MarkLogic 6.0
            "switch" to XQueryTokenType.K_SWITCH, // XQuery 3.0
            "transform" to XQueryTokenType.K_TRANSFORM, // Update Facility 3.0
            "try" to XQueryTokenType.K_TRY, // XQuery 3.0
            "tumbling" to XQueryTokenType.K_TUMBLING, // XQuery 3.0
            "tuple" to XPathTokenType.K_TUPLE, // Saxon 9.8
            "typeswitch" to XQueryTokenType.K_TYPESWITCH,
            "unassignable" to XQueryTokenType.K_UNASSIGNABLE, // Scripting Extension 1.0
            "unordered" to XQueryTokenType.K_UNORDERED,
            "update" to XQueryTokenType.K_UPDATE, // BaseX 7.8
            "updating" to XQueryTokenType.K_UPDATING, // Update Facility 1.0
            "validate" to XQueryTokenType.K_VALIDATE,
            "value" to XQueryTokenType.K_VALUE, // Update Facility 1.0
            "variable" to XQueryTokenType.K_VARIABLE,
            "version" to XQueryTokenType.K_VERSION,
            "when" to XQueryTokenType.K_WHEN, // XQuery 3.0
            "where" to XQueryTokenType.K_WHERE,
            "while" to XQueryTokenType.K_WHILE, // Scripting Extension 1.0
            "xquery" to XQueryTokenType.K_XQUERY,
            "zero-digit" to XQueryTokenType.K_ZERO_DIGIT // XQuery 3.0
        )
    }
}
