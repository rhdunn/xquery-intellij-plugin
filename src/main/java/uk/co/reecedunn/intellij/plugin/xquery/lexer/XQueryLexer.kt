/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.core.lexer.CharacterClass
import uk.co.reecedunn.intellij.plugin.core.lexer.CodePointRange

import java.util.EmptyStackException
import java.util.HashMap
import java.util.Stack

private const val STATE_DEFAULT = 0
private const val STATE_STRING_LITERAL_QUOTE = 1
private const val STATE_STRING_LITERAL_APOSTROPHE = 2
private const val STATE_DOUBLE_EXPONENT = 3
const val STATE_XQUERY_COMMENT = 4
private const val STATE_XML_COMMENT = 5
private const val STATE_UNEXPECTED_END_OF_BLOCK = 6
private const val STATE_CDATA_SECTION = 7
private const val STATE_PRAGMA_PRE_QNAME = 8
private const val STATE_PRAGMA_QNAME = 9
private const val STATE_PRAGMA_CONTENTS = 10
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
private const val STATE_BRACED_URI_LITERAL = 26
private const val STATE_STRING_CONSTRUCTOR_CONTENTS = 27
private const val STATE_DEFAULT_STRING_INTERPOLATION = 28
const val STATE_MAYBE_DIR_ELEM_CONSTRUCTOR = 29
const val STATE_START_DIR_ELEM_CONSTRUCTOR = 30
private const val STATE_BRACED_URI_LITERAL_PRAGMA = 31

class XQueryLexer : LexerBase() {
    private val mTokenRange: CodePointRange
    private var mState: Int = 0
    private val mStates = Stack<Int>()
    private var mType: IElementType? = null

    init {
        mTokenRange = CodePointRange()
    }

    // region States

    private fun pushState(state: Int) {
        mStates.push(state)
    }

    private fun popState() {
        try {
            mStates.pop()
        } catch (e: EmptyStackException) {
            //
        }

    }

    private fun stateDefault(mState: Int) {
        var c = mTokenRange.codePoint
        var cc = CharacterClass.getCharClass(c)
        when (cc) {
            CharacterClass.WHITESPACE -> {
                mTokenRange.match()
                while (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.WHITESPACE)
                    mTokenRange.match()
                mType = XQueryTokenType.WHITE_SPACE
            }
            CharacterClass.DOT -> {
                mTokenRange.save()
                mTokenRange.match()
                cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                if (cc == CharacterClass.DOT) {
                    mTokenRange.match()
                    mType = XQueryTokenType.PARENT_SELECTOR
                    return
                } else if (cc != CharacterClass.DIGIT) {
                    mType = XQueryTokenType.DOT
                    return
                } else {
                    mTokenRange.restore()
                    cc = CharacterClass.DOT
                    // Fall Through
                }
                mTokenRange.match()
                while (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.DIGIT)
                    mTokenRange.match()
                if (cc != CharacterClass.DOT && CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.DOT) {
                    mTokenRange.match()
                    while (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.DIGIT)
                        mTokenRange.match()
                    mType = XQueryTokenType.DECIMAL_LITERAL
                } else {
                    mType = if (cc == CharacterClass.DOT) XQueryTokenType.DECIMAL_LITERAL else XQueryTokenType.INTEGER_LITERAL
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
                        mType = XQueryTokenType.DOUBLE_LITERAL
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
                if (cc != CharacterClass.DOT && CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.DOT) {
                    mTokenRange.match()
                    while (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.DIGIT)
                        mTokenRange.match()
                    mType = XQueryTokenType.DECIMAL_LITERAL
                } else {
                    mType = if (cc == CharacterClass.DOT) XQueryTokenType.DECIMAL_LITERAL else XQueryTokenType.INTEGER_LITERAL
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
                        mType = XQueryTokenType.DOUBLE_LITERAL
                    } else {
                        pushState(STATE_DOUBLE_EXPONENT)
                        mTokenRange.restore()
                    }
                }
            }
            CharacterClass.END_OF_BUFFER -> mType = null
            CharacterClass.QUOTE, CharacterClass.APOSTROPHE -> {
                mTokenRange.match()
                mType = XQueryTokenType.STRING_LITERAL_START
                pushState(if (cc == CharacterClass.QUOTE) STATE_STRING_LITERAL_QUOTE else STATE_STRING_LITERAL_APOSTROPHE)
            }
            CharacterClass.NAME_START_CHAR -> {
                mTokenRange.match()
                cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                if (c == 'Q'.toInt() && cc == CharacterClass.CURLY_BRACE_OPEN) {
                    mTokenRange.match()
                    mType = XQueryTokenType.BRACED_URI_LITERAL_START
                    pushState(STATE_BRACED_URI_LITERAL)
                } else {
                    while (cc == CharacterClass.NAME_START_CHAR ||
                            cc == CharacterClass.DIGIT ||
                            cc == CharacterClass.DOT ||
                            cc == CharacterClass.HYPHEN_MINUS ||
                            cc == CharacterClass.NAME_CHAR) {
                        mTokenRange.match()
                        cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                    }
                    mType = (sKeywords as java.util.Map<String, IElementType>).getOrDefault(tokenText, XQueryTokenType.NCNAME)
                }
            }
            CharacterClass.PARENTHESIS_OPEN -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                if (c == ':'.toInt()) {
                    mTokenRange.match()
                    mType = XQueryTokenType.COMMENT_START_TAG
                    pushState(STATE_XQUERY_COMMENT)
                } else if (c == '#'.toInt()) {
                    mTokenRange.match()
                    mType = XQueryTokenType.PRAGMA_BEGIN
                    pushState(STATE_PRAGMA_PRE_QNAME)
                } else {
                    mType = XQueryTokenType.PARENTHESIS_OPEN
                }
            }
            CharacterClass.PARENTHESIS_CLOSE -> {
                mTokenRange.match()
                mType = XQueryTokenType.PARENTHESIS_CLOSE
            }
            CharacterClass.COLON -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                if (c == ')'.toInt()) {
                    mTokenRange.match()
                    mType = XQueryTokenType.COMMENT_END_TAG
                } else if (c == ':'.toInt()) {
                    mTokenRange.match()
                    mType = XQueryTokenType.AXIS_SEPARATOR
                } else if (c == '='.toInt()) {
                    mTokenRange.match()
                    mType = XQueryTokenType.ASSIGN_EQUAL
                } else {
                    mType = XQueryTokenType.QNAME_SEPARATOR
                }
            }
            CharacterClass.HASH -> {
                mTokenRange.match()
                if (mTokenRange.codePoint == ')'.toInt()) {
                    mTokenRange.match()
                    mType = XQueryTokenType.PRAGMA_END
                } else {
                    mType = XQueryTokenType.FUNCTION_REF_OPERATOR
                }
            }
            CharacterClass.EXCLAMATION_MARK -> {
                mTokenRange.match()
                if (mTokenRange.codePoint == '='.toInt()) {
                    mTokenRange.match()
                    mType = XQueryTokenType.NOT_EQUAL
                } else {
                    mType = XQueryTokenType.MAP_OPERATOR // XQuery 3.0
                }
            }
            CharacterClass.DOLLAR -> {
                mTokenRange.match()
                mType = XQueryTokenType.VARIABLE_INDICATOR
            }
            CharacterClass.ASTERISK -> {
                mTokenRange.match()
                mType = XQueryTokenType.STAR
            }
            CharacterClass.PLUS -> {
                mTokenRange.match()
                mType = XQueryTokenType.PLUS
            }
            CharacterClass.COMMA -> {
                mTokenRange.match()
                mType = XQueryTokenType.COMMA
            }
            CharacterClass.HYPHEN_MINUS -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                if (c == '-'.toInt()) {
                    mTokenRange.save()
                    mTokenRange.match()
                    if (mTokenRange.codePoint == '>'.toInt()) {
                        mTokenRange.match()
                        mType = XQueryTokenType.XML_COMMENT_END_TAG
                    } else {
                        mTokenRange.restore()
                        mType = XQueryTokenType.MINUS
                    }
                } else {
                    mType = XQueryTokenType.MINUS
                }
            }
            CharacterClass.SEMICOLON -> {
                mTokenRange.match()
                mType = XQueryTokenType.SEPARATOR
            }
            CharacterClass.LESS_THAN -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                if (c == '/'.toInt()) {
                    mTokenRange.match()
                    mType = XQueryTokenType.CLOSE_XML_TAG
                } else if (c == '<'.toInt()) {
                    val position = mTokenRange.end
                    mTokenRange.match()
                    matchOpenXmlTag()
                    if (mType === XQueryTokenType.DIRELEM_OPEN_XML_TAG) {
                        // For when adding a DirElemConstructor before another one -- i.e. <<a/>
                        mTokenRange.seek(position)
                        mType = XQueryTokenType.LESS_THAN
                    } else {
                        mTokenRange.seek(position)
                        mTokenRange.match()
                        mType = XQueryTokenType.NODE_BEFORE
                    }
                } else if (c == '='.toInt()) {
                    mTokenRange.match()
                    mType = XQueryTokenType.LESS_THAN_OR_EQUAL
                } else if (c == '?'.toInt()) {
                    mTokenRange.match()
                    mType = XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN
                    pushState(STATE_PROCESSING_INSTRUCTION)
                } else if (c == '!'.toInt()) {
                    mTokenRange.match()
                    if (mTokenRange.codePoint == '-'.toInt()) {
                        mTokenRange.match()
                        if (mTokenRange.codePoint == '-'.toInt()) {
                            mTokenRange.match()
                            mType = XQueryTokenType.XML_COMMENT_START_TAG
                            pushState(STATE_XML_COMMENT)
                        } else {
                            mType = XQueryTokenType.INVALID
                        }
                    } else if (mTokenRange.codePoint == '['.toInt()) {
                        mTokenRange.match()
                        if (mTokenRange.codePoint == 'C'.toInt()) {
                            mTokenRange.match()
                            if (mTokenRange.codePoint == 'D'.toInt()) {
                                mTokenRange.match()
                                if (mTokenRange.codePoint == 'A'.toInt()) {
                                    mTokenRange.match()
                                    if (mTokenRange.codePoint == 'T'.toInt()) {
                                        mTokenRange.match()
                                        if (mTokenRange.codePoint == 'A'.toInt()) {
                                            mTokenRange.match()
                                            if (mTokenRange.codePoint == '['.toInt()) {
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
                    if (mState == STATE_MAYBE_DIR_ELEM_CONSTRUCTOR) {
                        mType = XQueryTokenType.LESS_THAN
                    } else {
                        matchOpenXmlTag()
                    }
                }
            }
            CharacterClass.GREATER_THAN -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                if (c == '>'.toInt()) {
                    mTokenRange.match()
                    mType = XQueryTokenType.NODE_AFTER
                } else if (c == '='.toInt()) {
                    mTokenRange.match()
                    mType = XQueryTokenType.GREATER_THAN_OR_EQUAL
                } else {
                    mType = XQueryTokenType.GREATER_THAN
                }
            }
            CharacterClass.EQUAL -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                if (c == '>'.toInt()) {
                    mTokenRange.match()
                    mType = XQueryTokenType.ARROW
                } else {
                    mType = XQueryTokenType.EQUAL
                }
            }
            CharacterClass.CURLY_BRACE_OPEN -> {
                mTokenRange.match()
                mType = XQueryTokenType.BLOCK_OPEN
                pushState(mState)
            }
            CharacterClass.CURLY_BRACE_CLOSE -> {
                mTokenRange.match()
                if (mTokenRange.codePoint == '`'.toInt() && mState == STATE_DEFAULT_STRING_INTERPOLATION) {
                    mTokenRange.match()
                    mType = XQueryTokenType.STRING_INTERPOLATION_CLOSE
                } else {
                    mType = XQueryTokenType.BLOCK_CLOSE
                }
                popState()
            }
            CharacterClass.VERTICAL_BAR -> {
                mTokenRange.match()
                if (mTokenRange.codePoint == '|'.toInt()) {
                    mTokenRange.match()
                    mType = XQueryTokenType.CONCATENATION
                } else {
                    mType = XQueryTokenType.UNION
                }
            }
            CharacterClass.FORWARD_SLASH -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                if (c == '/'.toInt()) {
                    mTokenRange.match()
                    mType = XQueryTokenType.ALL_DESCENDANTS_PATH
                } else if (c == '>'.toInt()) {
                    mTokenRange.match()
                    mType = XQueryTokenType.SELF_CLOSING_XML_TAG
                } else {
                    mType = XQueryTokenType.DIRECT_DESCENDANTS_PATH
                }
            }
            CharacterClass.AT_SIGN -> {
                mTokenRange.match()
                mType = XQueryTokenType.ATTRIBUTE_SELECTOR
            }
            CharacterClass.SQUARE_BRACE_OPEN -> {
                mTokenRange.match()
                mType = XQueryTokenType.SQUARE_OPEN
            }
            CharacterClass.SQUARE_BRACE_CLOSE -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                if (c == ']'.toInt()) {
                    mTokenRange.save()
                    mTokenRange.match()
                    if (mTokenRange.codePoint == '>'.toInt()) {
                        mTokenRange.match()
                        mType = XQueryTokenType.CDATA_SECTION_END_TAG
                    } else {
                        mTokenRange.restore()
                        mType = XQueryTokenType.SQUARE_CLOSE
                    }
                } else if (c == '`'.toInt()) {
                    mTokenRange.match()
                    if (mTokenRange.codePoint == '`'.toInt()) {
                        mTokenRange.match()
                        mType = XQueryTokenType.STRING_CONSTRUCTOR_END
                    } else {
                        mType = XQueryTokenType.INVALID
                    }
                } else {
                    mType = XQueryTokenType.SQUARE_CLOSE
                }
            }
            CharacterClass.QUESTION_MARK -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                if (c == '>'.toInt()) {
                    mTokenRange.match()
                    mType = XQueryTokenType.PROCESSING_INSTRUCTION_END
                } else {
                    mType = XQueryTokenType.OPTIONAL
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
                if (c == '`'.toInt()) {
                    mTokenRange.match()
                    if (mTokenRange.codePoint == '['.toInt()) {
                        mTokenRange.match()
                        mType = XQueryTokenType.STRING_CONSTRUCTOR_START
                        pushState(STATE_STRING_CONSTRUCTOR_CONTENTS)
                    } else {
                        mType = XQueryTokenType.INVALID
                    }
                } else if (c == '{'.toInt()) {
                    mTokenRange.match()
                    pushState(mState)
                    mType = XQueryTokenType.STRING_INTERPOLATION_OPEN
                } else {
                    mType = XQueryTokenType.INVALID
                }
            }
            else -> {
                mTokenRange.match()
                mType = XQueryTokenType.BAD_CHARACTER
            }
        }
    }

    private fun stateStringLiteral(type: Char) {
        var c = mTokenRange.codePoint
        if (c == type.toInt()) {
            mTokenRange.match()
            if (mTokenRange.codePoint == type.toInt() && type != '}') {
                mTokenRange.match()
                mType = XQueryTokenType.ESCAPED_CHARACTER
            } else {
                mType = if (type == '}') XQueryTokenType.BRACED_URI_LITERAL_END else XQueryTokenType.STRING_LITERAL_END
                popState()
            }
        } else if (c == '&'.toInt()) {
            matchEntityReference(if (type == '"') STATE_STRING_LITERAL_QUOTE else STATE_STRING_LITERAL_APOSTROPHE)
        } else if (c == '{'.toInt() && type == '}') {
            mTokenRange.match()
            mType = XQueryTokenType.BAD_CHARACTER
        } else if (c == CodePointRange.END_OF_BUFFER) {
            mType = null
        } else {
            while (c != type.toInt() && c != CodePointRange.END_OF_BUFFER && c != '&'.toInt() && !(type == '}' && c == '{'.toInt())) {
                mTokenRange.match()
                c = mTokenRange.codePoint
            }
            mType = XQueryTokenType.STRING_LITERAL_CONTENTS
        }
    }

    private fun stateDoubleExponent() {
        mTokenRange.match()
        val c = mTokenRange.codePoint
        if (c == '+'.toInt() || c == '-'.toInt()) {
            mTokenRange.match()
        }
        mType = XQueryTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT
        popState()
    }

    private fun stateXQueryComment() {
        var c = mTokenRange.codePoint
        if (c == CodePointRange.END_OF_BUFFER) {
            mType = null
            return
        } else if (c == ':'.toInt()) {
            mTokenRange.save()
            mTokenRange.match()
            if (mTokenRange.codePoint == ')'.toInt()) {
                mTokenRange.match()
                mType = XQueryTokenType.COMMENT_END_TAG
                popState()
                return
            } else {
                mTokenRange.restore()
            }
        }

        var depth = 1
        while (true) {
            if (c == CodePointRange.END_OF_BUFFER) {
                mTokenRange.match()
                mType = XQueryTokenType.COMMENT
                popState()
                pushState(STATE_UNEXPECTED_END_OF_BLOCK)
                return
            } else if (c == '('.toInt()) {
                mTokenRange.match()
                if (mTokenRange.codePoint == ':'.toInt()) {
                    mTokenRange.match()
                    ++depth
                }
            } else if (c == ':'.toInt()) {
                mTokenRange.save()
                mTokenRange.match()
                if (mTokenRange.codePoint == ')'.toInt()) {
                    mTokenRange.match()
                    if (--depth == 0) {
                        mTokenRange.restore()
                        mType = XQueryTokenType.COMMENT
                        return
                    }
                }
            } else {
                mTokenRange.match()
            }
            c = mTokenRange.codePoint
        }
    }

    private fun stateXmlComment() {
        var c = mTokenRange.codePoint
        if (c == CodePointRange.END_OF_BUFFER) {
            mType = null
            return
        } else if (c == '-'.toInt()) {
            mTokenRange.save()
            mTokenRange.match()
            if (mTokenRange.codePoint == '-'.toInt()) {
                mTokenRange.match()
                if (mTokenRange.codePoint == '>'.toInt()) {
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
            } else if (c == '-'.toInt()) {
                mTokenRange.save()
                mTokenRange.match()
                if (mTokenRange.codePoint == '-'.toInt()) {
                    mTokenRange.match()
                    if (mTokenRange.codePoint == '>'.toInt()) {
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

    private fun stateUnexpectedEndOfBlock() {
        mType = XQueryTokenType.UNEXPECTED_END_OF_BLOCK
        popState()
    }

    private fun stateCDataSection() {
        var c = mTokenRange.codePoint
        if (c == CodePointRange.END_OF_BUFFER) {
            mType = null
            return
        } else if (c == ']'.toInt()) {
            mTokenRange.save()
            mTokenRange.match()
            if (mTokenRange.codePoint == ']'.toInt()) {
                mTokenRange.match()
                if (mTokenRange.codePoint == '>'.toInt()) {
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
            } else if (c == ']'.toInt()) {
                mTokenRange.save()
                mTokenRange.match()
                if (mTokenRange.codePoint == ']'.toInt()) {
                    mTokenRange.match()
                    if (mTokenRange.codePoint == '>'.toInt()) {
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

    private fun statePragmaPreQName() {
        val c = mTokenRange.codePoint
        var cc = CharacterClass.getCharClass(c)
        when (cc) {
            CharacterClass.WHITESPACE -> {
                mTokenRange.match()
                while (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.WHITESPACE)
                    mTokenRange.match()
                mType = XQueryTokenType.WHITE_SPACE
            }
            CharacterClass.COLON -> {
                mTokenRange.match()
                mType = XQueryTokenType.QNAME_SEPARATOR
                popState()
                pushState(STATE_PRAGMA_QNAME)
            }
            CharacterClass.NAME_START_CHAR -> {
                mTokenRange.match()
                cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                if (c == 'Q'.toInt() && cc == CharacterClass.CURLY_BRACE_OPEN) {
                    mTokenRange.match()
                    mType = XQueryTokenType.BRACED_URI_LITERAL_START
                    popState()
                    pushState(STATE_PRAGMA_QNAME)
                    pushState(STATE_BRACED_URI_LITERAL_PRAGMA)
                } else {
                    while (cc == CharacterClass.NAME_START_CHAR ||
                            cc == CharacterClass.DIGIT ||
                            cc == CharacterClass.DOT ||
                            cc == CharacterClass.HYPHEN_MINUS ||
                            cc == CharacterClass.NAME_CHAR) {
                        mTokenRange.match()
                        cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                    }
                    mType = XQueryTokenType.NCNAME
                    popState()
                    pushState(STATE_PRAGMA_QNAME)
                }
            }
            else -> {
                popState()
                pushState(STATE_PRAGMA_CONTENTS)
                statePragmaContents()
            }
        }
    }

    private fun statePragmaQName() {
        val c = mTokenRange.codePoint
        var cc = CharacterClass.getCharClass(c)
        when (cc) {
            CharacterClass.WHITESPACE -> {
                mTokenRange.match()
                while (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.WHITESPACE)
                    mTokenRange.match()
                mType = XQueryTokenType.WHITE_SPACE
                popState()
                pushState(STATE_PRAGMA_CONTENTS)
            }
            CharacterClass.COLON -> {
                mTokenRange.match()
                mType = XQueryTokenType.QNAME_SEPARATOR
            }
            CharacterClass.NAME_START_CHAR -> {
                mTokenRange.match()
                cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                while (cc == CharacterClass.NAME_START_CHAR ||
                        cc == CharacterClass.DIGIT ||
                        cc == CharacterClass.DOT ||
                        cc == CharacterClass.HYPHEN_MINUS ||
                        cc == CharacterClass.NAME_CHAR) {
                    mTokenRange.match()
                    cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                }
                mType = XQueryTokenType.NCNAME
            }
            else -> {
                popState()
                pushState(STATE_PRAGMA_CONTENTS)
                statePragmaContents()
            }
        }
    }

    private fun statePragmaContents() {
        var c = mTokenRange.codePoint
        if (c == CodePointRange.END_OF_BUFFER) {
            mType = null
            return
        } else if (c == '#'.toInt()) {
            mTokenRange.save()
            mTokenRange.match()
            if (mTokenRange.codePoint == ')'.toInt()) {
                mTokenRange.match()
                mType = XQueryTokenType.PRAGMA_END
                popState()
                return
            } else {
                mTokenRange.restore()
            }
        }

        while (true) {
            if (c == CodePointRange.END_OF_BUFFER) {
                mTokenRange.match()
                mType = XQueryTokenType.PRAGMA_CONTENTS
                popState()
                pushState(STATE_UNEXPECTED_END_OF_BLOCK)
                return
            } else if (c == '#'.toInt()) {
                mTokenRange.save()
                mTokenRange.match()
                if (mTokenRange.codePoint == ')'.toInt()) {
                    mTokenRange.restore()
                    mType = XQueryTokenType.PRAGMA_CONTENTS
                    return
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
                mType = if (state == STATE_DIR_ATTRIBUTE_LIST) XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR else XQueryTokenType.XML_TAG_QNAME_SEPARATOR
            }
            CharacterClass.NAME_START_CHAR -> {
                mTokenRange.match()
                cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                while (cc == CharacterClass.NAME_START_CHAR ||
                        cc == CharacterClass.DIGIT ||
                        cc == CharacterClass.DOT ||
                        cc == CharacterClass.HYPHEN_MINUS ||
                        cc == CharacterClass.NAME_CHAR) {
                    mTokenRange.match()
                    cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                }
                mType = if (state == STATE_DIR_ATTRIBUTE_LIST) XQueryTokenType.XML_ATTRIBUTE_NCNAME else XQueryTokenType.XML_TAG_NCNAME
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
                if (c == '>'.toInt()) {
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
                mType = XQueryTokenType.BAD_CHARACTER
            }
        }
    }

    private fun stateDirAttributeValue(type: Char) {
        var c = mTokenRange.codePoint
        if (c == type.toInt()) {
            mTokenRange.match()
            if (mTokenRange.codePoint == type.toInt()) {
                mTokenRange.match()
                mType = XQueryTokenType.XML_ESCAPED_CHARACTER
            } else {
                mType = XQueryTokenType.XML_ATTRIBUTE_VALUE_END
                popState()
            }
        } else if (c == '{'.toInt()) {
            mTokenRange.match()
            if (mTokenRange.codePoint == '{'.toInt()) {
                mTokenRange.match()
                mType = XQueryTokenType.XML_ESCAPED_CHARACTER
            } else {
                mType = XQueryTokenType.BLOCK_OPEN
                pushState(if (type == '"') STATE_DEFAULT_ATTRIBUTE_QUOT else STATE_DEFAULT_ATTRIBUTE_APOSTROPHE)
            }
        } else if (c == '}'.toInt()) {
            mTokenRange.match()
            if (mTokenRange.codePoint == '}'.toInt()) {
                mTokenRange.match()
                mType = XQueryTokenType.XML_ESCAPED_CHARACTER
            } else {
                mType = XQueryTokenType.BLOCK_CLOSE
            }
        } else if (c == '<'.toInt()) {
            mTokenRange.match()
            mType = XQueryTokenType.BAD_CHARACTER
        } else if (c == '&'.toInt()) {
            matchEntityReference(if (type == '"') STATE_DIR_ATTRIBUTE_VALUE_QUOTE else STATE_DIR_ATTRIBUTE_VALUE_APOSTROPHE)
        } else if (c == CodePointRange.END_OF_BUFFER) {
            mType = null
        } else {
            while (true) {
                when (c) {
                    CodePointRange.END_OF_BUFFER, '{'.toInt(), '}'.toInt(), '<'.toInt(), '&'.toInt() -> {
                        mType = XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS
                        return
                    }
                    else -> if (c == type.toInt()) {
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
        if (c == '{'.toInt()) {
            mTokenRange.match()
            if (mTokenRange.codePoint == '{'.toInt()) {
                mTokenRange.match()
                mType = XQueryTokenType.ESCAPED_CHARACTER
            } else {
                mType = XQueryTokenType.BLOCK_OPEN
                pushState(STATE_DEFAULT_ELEM_CONTENT)
            }
        } else if (c == '}'.toInt()) {
            mTokenRange.match()
            if (mTokenRange.codePoint == '}'.toInt()) {
                mTokenRange.match()
                mType = XQueryTokenType.ESCAPED_CHARACTER
            } else {
                mType = XQueryTokenType.BLOCK_CLOSE
            }
        } else if (c == '<'.toInt()) {
            mTokenRange.match()
            c = mTokenRange.codePoint
            if (c == '/'.toInt()) {
                mTokenRange.match()
                mType = XQueryTokenType.CLOSE_XML_TAG
                popState()
                pushState(STATE_DIR_ELEM_CONSTRUCTOR_CLOSING)
            } else if (CharacterClass.getCharClass(mTokenRange.codePoint) == CharacterClass.NAME_START_CHAR) {
                mType = XQueryTokenType.OPEN_XML_TAG
                pushState(STATE_DIR_ELEM_CONSTRUCTOR)
            } else if (c == '?'.toInt()) {
                mTokenRange.match()
                mType = XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN
                pushState(STATE_PROCESSING_INSTRUCTION_ELEM_CONTENT)
            } else if (c == '!'.toInt()) {
                mTokenRange.match()
                if (mTokenRange.codePoint == '-'.toInt()) {
                    mTokenRange.match()
                    if (mTokenRange.codePoint == '-'.toInt()) {
                        mTokenRange.match()
                        mType = XQueryTokenType.XML_COMMENT_START_TAG
                        pushState(STATE_XML_COMMENT_ELEM_CONTENT)
                    } else {
                        mType = XQueryTokenType.INVALID
                    }
                } else if (mTokenRange.codePoint == '['.toInt()) {
                    mTokenRange.match()
                    if (mTokenRange.codePoint == 'C'.toInt()) {
                        mTokenRange.match()
                        if (mTokenRange.codePoint == 'D'.toInt()) {
                            mTokenRange.match()
                            if (mTokenRange.codePoint == 'A'.toInt()) {
                                mTokenRange.match()
                                if (mTokenRange.codePoint == 'T'.toInt()) {
                                    mTokenRange.match()
                                    if (mTokenRange.codePoint == 'A'.toInt()) {
                                        mTokenRange.match()
                                        if (mTokenRange.codePoint == '['.toInt()) {
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
                mType = XQueryTokenType.BAD_CHARACTER
            }
        } else if (c == '&'.toInt()) {
            matchEntityReference(STATE_DIR_ELEM_CONTENT)
        } else if (c == CodePointRange.END_OF_BUFFER) {
            mType = null
        } else {
            while (true) {
                when (c) {
                    CodePointRange.END_OF_BUFFER, '{'.toInt(), '}'.toInt(), '<'.toInt(), '&'.toInt() -> {
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
                mType = XQueryTokenType.WHITE_SPACE
                popState()
                pushState(if (state == STATE_PROCESSING_INSTRUCTION) STATE_PROCESSING_INSTRUCTION_CONTENTS else STATE_PROCESSING_INSTRUCTION_CONTENTS_ELEM_CONTENT)
            }
            CharacterClass.NAME_START_CHAR -> {
                mTokenRange.match()
                cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                while (cc == CharacterClass.NAME_START_CHAR ||
                        cc == CharacterClass.DIGIT ||
                        cc == CharacterClass.DOT ||
                        cc == CharacterClass.HYPHEN_MINUS ||
                        cc == CharacterClass.NAME_CHAR) {
                    mTokenRange.match()
                    cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                }
                mType = XQueryTokenType.NCNAME
            }
            CharacterClass.QUESTION_MARK -> {
                mTokenRange.match()
                if (mTokenRange.codePoint == '>'.toInt()) {
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
                mType = XQueryTokenType.BAD_CHARACTER
            }
        }
    }

    private fun stateProcessingInstructionContents() {
        var c = mTokenRange.codePoint
        if (c == CodePointRange.END_OF_BUFFER) {
            mType = null
            return
        } else if (c == '?'.toInt()) {
            mTokenRange.save()
            mTokenRange.match()
            if (mTokenRange.codePoint == '>'.toInt()) {
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
            } else if (c == '?'.toInt()) {
                mTokenRange.save()
                mTokenRange.match()
                if (mTokenRange.codePoint == '>'.toInt()) {
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
        if (c == '`'.toInt()) {
            mTokenRange.save()
            mTokenRange.match()
            if (mTokenRange.codePoint == '{'.toInt()) {
                mTokenRange.match()
                pushState(STATE_DEFAULT_STRING_INTERPOLATION)
                mType = XQueryTokenType.STRING_INTERPOLATION_OPEN
                return
            } else {
                mTokenRange.restore()
            }
        }
        while (c != CodePointRange.END_OF_BUFFER) {
            if (c == ']'.toInt()) {
                mTokenRange.save()
                mTokenRange.match()
                if (mTokenRange.codePoint == '`'.toInt()) {
                    mTokenRange.match()
                    if (mTokenRange.codePoint == '`'.toInt()) {
                        mTokenRange.restore()
                        popState()
                        mType = XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS
                        return
                    }
                }
            } else if (c == '`'.toInt()) {
                mTokenRange.save()
                mTokenRange.match()
                if (mTokenRange.codePoint == '{'.toInt()) {
                    mTokenRange.restore()
                    mType = XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS
                    return
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

        while (cc == CharacterClass.NAME_START_CHAR ||
                cc == CharacterClass.DIGIT ||
                cc == CharacterClass.DOT ||
                cc == CharacterClass.HYPHEN_MINUS ||
                cc == CharacterClass.NAME_CHAR) {
            mTokenRange.match()
            cc = CharacterClass.getCharClass(mTokenRange.codePoint)
        }
        return true
    }

    private fun matchQName(): Boolean {
        if (!matchNCName())
            return false

        if (mTokenRange.codePoint == ':'.toInt()) {
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
            mType = XQueryTokenType.LESS_THAN
            return
        }

        mType = XQueryTokenType.DIRELEM_OPEN_XML_TAG
        matchWhiteSpace()

        when (CharacterClass.getCharClass(mTokenRange.codePoint)) {
            CharacterClass.FORWARD_SLASH -> {
                mTokenRange.save()
                mTokenRange.match()
                if (mTokenRange.codePoint == '>'.toInt()) {
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
        val isAttributeValue = state == STATE_DIR_ATTRIBUTE_VALUE_QUOTE || state == STATE_DIR_ATTRIBUTE_VALUE_APOSTROPHE
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
                mType = if (isAttributeValue) XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE else XQueryTokenType.PREDEFINED_ENTITY_REFERENCE
            } else {
                mType = if (isAttributeValue) XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE else XQueryTokenType.PARTIAL_ENTITY_REFERENCE
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
                        mType = if (isAttributeValue) XQueryTokenType.XML_CHARACTER_REFERENCE else XQueryTokenType.CHARACTER_REFERENCE
                    } else {
                        mType = if (isAttributeValue) XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE else XQueryTokenType.PARTIAL_ENTITY_REFERENCE
                    }
                } else if (c == ';'.toInt()) {
                    mTokenRange.match()
                    mType = if (isAttributeValue) XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE else XQueryTokenType.EMPTY_ENTITY_REFERENCE
                } else {
                    mType = if (isAttributeValue) XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE else XQueryTokenType.PARTIAL_ENTITY_REFERENCE
                }
            } else if (c >= '0'.toInt() && c <= '9'.toInt()) {
                mTokenRange.match()
                while (c >= '0'.toInt() && c <= '9'.toInt()) {
                    mTokenRange.match()
                    c = mTokenRange.codePoint
                }
                if (c == ';'.toInt()) {
                    mTokenRange.match()
                    mType = if (isAttributeValue) XQueryTokenType.XML_CHARACTER_REFERENCE else XQueryTokenType.CHARACTER_REFERENCE
                } else {
                    mType = if (isAttributeValue) XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE else XQueryTokenType.PARTIAL_ENTITY_REFERENCE
                }
            } else if (c == ';'.toInt()) {
                mTokenRange.match()
                mType = if (isAttributeValue) XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE else XQueryTokenType.EMPTY_ENTITY_REFERENCE
            } else {
                mType = if (isAttributeValue) XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE else XQueryTokenType.PARTIAL_ENTITY_REFERENCE
            }
        } else if (cc == CharacterClass.SEMICOLON) {
            mTokenRange.match()
            mType = if (isAttributeValue) XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE else XQueryTokenType.EMPTY_ENTITY_REFERENCE
        } else {
            mType = if (isAttributeValue) XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE else XQueryTokenType.PARTIAL_ENTITY_REFERENCE
        }
    }

    // endregion
    // region Lexer

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        mTokenRange.start(buffer, startOffset, endOffset)
        mStates.clear()

        if (initialState != STATE_DEFAULT) {
            pushState(STATE_DEFAULT)
        }
        pushState(initialState)
        advance()
    }

    override fun advance() {
        mTokenRange.flush()
        try {
            mState = mStates.peek()
        } catch (e: EmptyStackException) {
            mState = STATE_DEFAULT
        }

        when (mState) {
            STATE_DEFAULT, STATE_DEFAULT_ATTRIBUTE_QUOT, STATE_DEFAULT_ATTRIBUTE_APOSTROPHE, STATE_DEFAULT_ELEM_CONTENT, STATE_DEFAULT_STRING_INTERPOLATION, STATE_MAYBE_DIR_ELEM_CONSTRUCTOR -> stateDefault(mState)
            STATE_STRING_LITERAL_QUOTE -> stateStringLiteral('"')
            STATE_STRING_LITERAL_APOSTROPHE -> stateStringLiteral('\'')
            STATE_DOUBLE_EXPONENT -> stateDoubleExponent()
            STATE_XQUERY_COMMENT -> stateXQueryComment()
            STATE_XML_COMMENT, STATE_XML_COMMENT_ELEM_CONTENT -> stateXmlComment()
            STATE_UNEXPECTED_END_OF_BLOCK -> stateUnexpectedEndOfBlock()
            STATE_CDATA_SECTION, STATE_CDATA_SECTION_ELEM_CONTENT -> stateCDataSection()
            STATE_PRAGMA_PRE_QNAME -> statePragmaPreQName()
            STATE_PRAGMA_QNAME -> statePragmaQName()
            STATE_PRAGMA_CONTENTS -> statePragmaContents()
            STATE_DIR_ELEM_CONSTRUCTOR, STATE_DIR_ELEM_CONSTRUCTOR_CLOSING, STATE_DIR_ATTRIBUTE_LIST -> stateDirElemConstructor(mState)
            STATE_DIR_ATTRIBUTE_VALUE_QUOTE -> stateDirAttributeValue('"')
            STATE_DIR_ATTRIBUTE_VALUE_APOSTROPHE -> stateDirAttributeValue('\'')
            STATE_DIR_ELEM_CONTENT -> stateDirElemContent()
            STATE_PROCESSING_INSTRUCTION, STATE_PROCESSING_INSTRUCTION_ELEM_CONTENT -> stateProcessingInstruction(mState)
            STATE_PROCESSING_INSTRUCTION_CONTENTS, STATE_PROCESSING_INSTRUCTION_CONTENTS_ELEM_CONTENT -> stateProcessingInstructionContents()
            STATE_BRACED_URI_LITERAL, STATE_BRACED_URI_LITERAL_PRAGMA -> stateStringLiteral('}')
            STATE_STRING_CONSTRUCTOR_CONTENTS -> stateStringConstructorContents()
            STATE_START_DIR_ELEM_CONSTRUCTOR -> stateStartDirElemConstructor()
            else -> throw AssertionError("Invalid state: " + mState)
        }
    }

    override fun getState(): Int {
        return mState
    }

    override fun getTokenType(): IElementType? {
        return mType
    }

    override fun getTokenStart(): Int {
        return mTokenRange.start
    }

    override fun getTokenEnd(): Int {
        return mTokenRange.end
    }

    override// jacoco Code Coverage reports an unchecked branch when @NotNull is used.
    fun getBufferSequence(): CharSequence {
        return mTokenRange.bufferSequence
    }

    override fun getBufferEnd(): Int {
        return mTokenRange.bufferEnd
    }

    // endregion
    // region Keywords

    companion object {
        private val sKeywords = HashMap<String, IElementType>()
        init {
            sKeywords["after"] = XQueryTokenType.K_AFTER // Update Facility 1.0
            sKeywords["all"] = XQueryTokenType.K_ALL // Full Text 1.0
            sKeywords["allowing"] = XQueryTokenType.K_ALLOWING // XQuery 3.0
            sKeywords["ancestor"] = XQueryTokenType.K_ANCESTOR
            sKeywords["ancestor-or-self"] = XQueryTokenType.K_ANCESTOR_OR_SELF
            sKeywords["and"] = XQueryTokenType.K_AND
            sKeywords["any"] = XQueryTokenType.K_ANY // Full Text 1.0
            sKeywords["array"] = XQueryTokenType.K_ARRAY // XQuery 3.1
            sKeywords["array-node"] = XQueryTokenType.K_ARRAY_NODE // MarkLogic 8.0
            sKeywords["as"] = XQueryTokenType.K_AS
            sKeywords["ascending"] = XQueryTokenType.K_ASCENDING
            sKeywords["assignable"] = XQueryTokenType.K_ASSIGNABLE // Scripting Extension 1.0
            sKeywords["at"] = XQueryTokenType.K_AT
            sKeywords["attribute"] = XQueryTokenType.K_ATTRIBUTE
            sKeywords["attribute-decl"] = XQueryTokenType.K_ATTRIBUTE_DECL // MarkLogic 7.0
            sKeywords["base-uri"] = XQueryTokenType.K_BASE_URI
            sKeywords["before"] = XQueryTokenType.K_BEFORE // Update Facility 1.0
            sKeywords["binary"] = XQueryTokenType.K_BINARY // MarkLogic 6.0
            sKeywords["block"] = XQueryTokenType.K_BLOCK // Scripting Extension 1.0
            sKeywords["boolean-node"] = XQueryTokenType.K_BOOLEAN_NODE // MarkLogic 8.0
            sKeywords["boundary-space"] = XQueryTokenType.K_BOUNDARY_SPACE
            sKeywords["by"] = XQueryTokenType.K_BY
            sKeywords["case"] = XQueryTokenType.K_CASE
            sKeywords["cast"] = XQueryTokenType.K_CAST
            sKeywords["castable"] = XQueryTokenType.K_CASTABLE
            sKeywords["catch"] = XQueryTokenType.K_CATCH // XQuery 3.0
            sKeywords["child"] = XQueryTokenType.K_CHILD
            sKeywords["collation"] = XQueryTokenType.K_COLLATION
            sKeywords["comment"] = XQueryTokenType.K_COMMENT
            sKeywords["complex-type"] = XQueryTokenType.K_COMPLEX_TYPE // MarkLogic 7.0
            sKeywords["construction"] = XQueryTokenType.K_CONSTRUCTION
            sKeywords["contains"] = XQueryTokenType.K_CONTAINS // Full Text 1.0
            sKeywords["content"] = XQueryTokenType.K_CONTENT // Full Text 1.0
            sKeywords["context"] = XQueryTokenType.K_CONTEXT // XQuery 3.0
            sKeywords["copy"] = XQueryTokenType.K_COPY // Update Facility 1.0
            sKeywords["copy-namespaces"] = XQueryTokenType.K_COPY_NAMESPACES
            sKeywords["count"] = XQueryTokenType.K_COUNT // XQuery 3.0
            sKeywords["decimal-format"] = XQueryTokenType.K_DECIMAL_FORMAT // XQuery 3.0
            sKeywords["decimal-separator"] = XQueryTokenType.K_DECIMAL_SEPARATOR // XQuery 3.0
            sKeywords["declare"] = XQueryTokenType.K_DECLARE
            sKeywords["default"] = XQueryTokenType.K_DEFAULT
            sKeywords["delete"] = XQueryTokenType.K_DELETE // Update Facility 1.0
            sKeywords["descendant"] = XQueryTokenType.K_DESCENDANT
            sKeywords["descendant-or-self"] = XQueryTokenType.K_DESCENDANT_OR_SELF
            sKeywords["descending"] = XQueryTokenType.K_DESCENDING
            sKeywords["diacritics"] = XQueryTokenType.K_DIACRITICS // Full Text 1.0
            sKeywords["different"] = XQueryTokenType.K_DIFFERENT // Full Text 1.0
            sKeywords["digit"] = XQueryTokenType.K_DIGIT // XQuery 3.0
            sKeywords["distance"] = XQueryTokenType.K_DISTANCE // Full Text 1.0
            sKeywords["div"] = XQueryTokenType.K_DIV
            sKeywords["document"] = XQueryTokenType.K_DOCUMENT
            sKeywords["document-node"] = XQueryTokenType.K_DOCUMENT_NODE
            sKeywords["element"] = XQueryTokenType.K_ELEMENT
            sKeywords["element-decl"] = XQueryTokenType.K_ELEMENT_DECL // MarkLogic 7.0
            sKeywords["else"] = XQueryTokenType.K_ELSE
            sKeywords["empty"] = XQueryTokenType.K_EMPTY
            sKeywords["empty-sequence"] = XQueryTokenType.K_EMPTY_SEQUENCE
            sKeywords["encoding"] = XQueryTokenType.K_ENCODING
            sKeywords["end"] = XQueryTokenType.K_END // XQuery 3.0
            sKeywords["entire"] = XQueryTokenType.K_ENTIRE // Full Text 1.0
            sKeywords["eq"] = XQueryTokenType.K_EQ
            sKeywords["every"] = XQueryTokenType.K_EVERY
            sKeywords["exactly"] = XQueryTokenType.K_EXACTLY // Full Text 1.0
            sKeywords["except"] = XQueryTokenType.K_EXCEPT
            sKeywords["exit"] = XQueryTokenType.K_EXIT // Scripting Extension 1.0
            sKeywords["exponent-separator"] = XQueryTokenType.K_EXPONENT_SEPARATOR // XQuery 3.1
            sKeywords["external"] = XQueryTokenType.K_EXTERNAL
            sKeywords["first"] = XQueryTokenType.K_FIRST // Update Facility 1.0
            sKeywords["following"] = XQueryTokenType.K_FOLLOWING
            sKeywords["following-sibling"] = XQueryTokenType.K_FOLLOWING_SIBLING
            sKeywords["for"] = XQueryTokenType.K_FOR
            sKeywords["from"] = XQueryTokenType.K_FROM // Full Text 1.0
            sKeywords["ft-option"] = XQueryTokenType.K_FT_OPTION // Full Text 1.0
            sKeywords["ftand"] = XQueryTokenType.K_FTAND // Full Text 1.0
            sKeywords["ftnot"] = XQueryTokenType.K_FTNOT // Full Text 1.0
            sKeywords["ftor"] = XQueryTokenType.K_FTOR // Full Text 1.0
            sKeywords["function"] = XQueryTokenType.K_FUNCTION
            sKeywords["fuzzy"] = XQueryTokenType.K_FUZZY // BaseX 6.1
            sKeywords["ge"] = XQueryTokenType.K_GE
            sKeywords["greatest"] = XQueryTokenType.K_GREATEST
            sKeywords["group"] = XQueryTokenType.K_GROUP // XQuery 3.0
            sKeywords["grouping-separator"] = XQueryTokenType.K_GROUPING_SEPARATOR // XQuery 3.0
            sKeywords["gt"] = XQueryTokenType.K_GT
            sKeywords["idiv"] = XQueryTokenType.K_IDIV
            sKeywords["if"] = XQueryTokenType.K_IF
            sKeywords["import"] = XQueryTokenType.K_IMPORT
            sKeywords["in"] = XQueryTokenType.K_IN
            sKeywords["infinity"] = XQueryTokenType.K_INFINITY // XQuery 3.0
            sKeywords["inherit"] = XQueryTokenType.K_INHERIT
            sKeywords["insensitive"] = XQueryTokenType.K_INSENSITIVE // Full Text 1.0
            sKeywords["insert"] = XQueryTokenType.K_INSERT // Update Facility 1.0
            sKeywords["instance"] = XQueryTokenType.K_INSTANCE
            sKeywords["intersect"] = XQueryTokenType.K_INTERSECT
            sKeywords["into"] = XQueryTokenType.K_INTO // Update Facility 1.0
            sKeywords["invoke"] = XQueryTokenType.K_INVOKE // Update Facility 3.0
            sKeywords["is"] = XQueryTokenType.K_IS
            sKeywords["item"] = XQueryTokenType.K_ITEM
            sKeywords["language"] = XQueryTokenType.K_LANGUAGE // Full Text 1.0
            sKeywords["last"] = XQueryTokenType.K_LAST // Update Facility 1.0
            sKeywords["lax"] = XQueryTokenType.K_LAX
            sKeywords["le"] = XQueryTokenType.K_LE
            sKeywords["least"] = XQueryTokenType.K_LEAST
            sKeywords["let"] = XQueryTokenType.K_LET
            sKeywords["levels"] = XQueryTokenType.K_LEVELS // Full Text 1.0
            sKeywords["lowercase"] = XQueryTokenType.K_LOWERCASE // Full Text 1.0
            sKeywords["lt"] = XQueryTokenType.K_LT
            sKeywords["map"] = XQueryTokenType.K_MAP // XQuery 3.1
            sKeywords["minus-sign"] = XQueryTokenType.K_MINUS_SIGN // XQuery 3.0
            sKeywords["mod"] = XQueryTokenType.K_MOD
            sKeywords["modify"] = XQueryTokenType.K_MODIFY // Update Facility 1.0
            sKeywords["module"] = XQueryTokenType.K_MODULE
            sKeywords["most"] = XQueryTokenType.K_MOST // Full Text 1.0
            sKeywords["namespace"] = XQueryTokenType.K_NAMESPACE
            sKeywords["namespace-node"] = XQueryTokenType.K_NAMESPACE_NODE // XQuery 3.0
            sKeywords["NaN"] = XQueryTokenType.K_NAN // XQuery 3.0
            sKeywords["ne"] = XQueryTokenType.K_NE
            sKeywords["next"] = XQueryTokenType.K_NEXT // XQuery 3.0
            sKeywords["no"] = XQueryTokenType.K_NO // Full Text 1.0
            sKeywords["no-inherit"] = XQueryTokenType.K_NO_INHERIT
            sKeywords["no-preserve"] = XQueryTokenType.K_NO_PRESERVE
            sKeywords["node"] = XQueryTokenType.K_NODE
            sKeywords["nodes"] = XQueryTokenType.K_NODES // Update Facility 1.0
            sKeywords["not"] = XQueryTokenType.K_NOT // Full Text 1.0
            sKeywords["null-node"] = XQueryTokenType.K_NULL_NODE // MarkLogic 8.0
            sKeywords["number-node"] = XQueryTokenType.K_NUMBER_NODE // MarkLogic 8.0
            sKeywords["object-node"] = XQueryTokenType.K_OBJECT_NODE // MarkLogic 8.0
            sKeywords["occurs"] = XQueryTokenType.K_OCCURS // Full Text 1.0
            sKeywords["of"] = XQueryTokenType.K_OF
            sKeywords["only"] = XQueryTokenType.K_ONLY // XQuery 3.0
            sKeywords["option"] = XQueryTokenType.K_OPTION
            sKeywords["or"] = XQueryTokenType.K_OR
            sKeywords["order"] = XQueryTokenType.K_ORDER
            sKeywords["ordered"] = XQueryTokenType.K_ORDERED
            sKeywords["ordering"] = XQueryTokenType.K_ORDERING
            sKeywords["paragraph"] = XQueryTokenType.K_PARAGRAPH // Full Text 1.0
            sKeywords["paragraphs"] = XQueryTokenType.K_PARAGRAPHS // Full Text 1.0
            sKeywords["parent"] = XQueryTokenType.K_PARENT
            sKeywords["pattern-separator"] = XQueryTokenType.K_PATTERN_SEPARATOR // XQuery 3.0
            sKeywords["per-mille"] = XQueryTokenType.K_PER_MILLE // XQuery 3.0
            sKeywords["percent"] = XQueryTokenType.K_PERCENT // XQuery 3.0
            sKeywords["phrase"] = XQueryTokenType.K_PHRASE // Full Text 1.0
            sKeywords["preceding"] = XQueryTokenType.K_PRECEDING
            sKeywords["preceding-sibling"] = XQueryTokenType.K_PRECEDING_SIBLING
            sKeywords["preserve"] = XQueryTokenType.K_PRESERVE
            sKeywords["previous"] = XQueryTokenType.K_PREVIOUS // XQuery 3.0
            sKeywords["private"] = XQueryTokenType.K_PRIVATE // MarkLogic 6.0
            sKeywords["processing-instruction"] = XQueryTokenType.K_PROCESSING_INSTRUCTION
            sKeywords["property"] = XQueryTokenType.K_PROPERTY // MarkLogic 6.0
            sKeywords["public"] = XQueryTokenType.K_PUBLIC // XQuery 3.0 (§4.15 -- Annotations)
            sKeywords["relationship"] = XQueryTokenType.K_RELATIONSHIP // Full Text 1.0
            sKeywords["rename"] = XQueryTokenType.K_RENAME // Update Facility 1.0
            sKeywords["replace"] = XQueryTokenType.K_REPLACE // Update Facility 1.0
            sKeywords["return"] = XQueryTokenType.K_RETURN
            sKeywords["returning"] = XQueryTokenType.K_RETURNING // Scripting Extension 1.0
            sKeywords["revalidation"] = XQueryTokenType.K_REVALIDATION // Update Facility 1.0
            sKeywords["same"] = XQueryTokenType.K_SAME // Full Text 1.0
            sKeywords["satisfies"] = XQueryTokenType.K_SATISFIES
            sKeywords["schema"] = XQueryTokenType.K_SCHEMA
            sKeywords["schema-attribute"] = XQueryTokenType.K_SCHEMA_ATTRIBUTE
            sKeywords["schema-component"] = XQueryTokenType.K_SCHEMA_COMPONENT // MarkLogic 7.0
            sKeywords["schema-element"] = XQueryTokenType.K_SCHEMA_ELEMENT
            sKeywords["schema-facet"] = XQueryTokenType.K_SCHEMA_FACET // MarkLogic 8.0
            sKeywords["schema-particle"] = XQueryTokenType.K_SCHEMA_PARTICLE // MarkLogic 7.0
            sKeywords["schema-root"] = XQueryTokenType.K_SCHEMA_ROOT // MarkLogic 7.0
            sKeywords["schema-type"] = XQueryTokenType.K_SCHEMA_TYPE // MarkLogic 7.0
            sKeywords["score"] = XQueryTokenType.K_SCORE // Full Text 1.0
            sKeywords["self"] = XQueryTokenType.K_SELF
            sKeywords["sensitive"] = XQueryTokenType.K_SENSITIVE // Full Text 1.0
            sKeywords["sentence"] = XQueryTokenType.K_SENTENCE // Full Text 1.0
            sKeywords["sentences"] = XQueryTokenType.K_SENTENCES // Full Text 1.0
            sKeywords["sequential"] = XQueryTokenType.K_SEQUENTIAL // Scripting Extension 1.0
            sKeywords["simple"] = XQueryTokenType.K_SIMPLE // Scripting Extension 1.0
            sKeywords["simple-type"] = XQueryTokenType.K_SIMPLE_TYPE // MarkLogic 7.0
            sKeywords["skip"] = XQueryTokenType.K_SKIP // Update Facility 1.0
            sKeywords["sliding"] = XQueryTokenType.K_SLIDING // XQuery 3.0
            sKeywords["some"] = XQueryTokenType.K_SOME
            sKeywords["stable"] = XQueryTokenType.K_STABLE
            sKeywords["start"] = XQueryTokenType.K_START // XQuery 3.0
            sKeywords["stemming"] = XQueryTokenType.K_STEMMING // Full Text 1.0
            sKeywords["stop"] = XQueryTokenType.K_STOP // Full Text 1.0
            sKeywords["strict"] = XQueryTokenType.K_STRICT
            sKeywords["strip"] = XQueryTokenType.K_STRIP
            sKeywords["stylesheet"] = XQueryTokenType.K_STYLESHEET // MarkLogic 6.0
            sKeywords["switch"] = XQueryTokenType.K_SWITCH // XQuery 3.0
            sKeywords["text"] = XQueryTokenType.K_TEXT
            sKeywords["then"] = XQueryTokenType.K_THEN
            sKeywords["thesaurus"] = XQueryTokenType.K_THESAURUS // Full Text 1.0
            sKeywords["times"] = XQueryTokenType.K_TIMES // Full Text 1.0
            sKeywords["to"] = XQueryTokenType.K_TO
            sKeywords["transform"] = XQueryTokenType.K_TRANSFORM // Update Facility 3.0
            sKeywords["treat"] = XQueryTokenType.K_TREAT
            sKeywords["try"] = XQueryTokenType.K_TRY // XQuery 3.0
            sKeywords["tumbling"] = XQueryTokenType.K_TUMBLING // XQuery 3.0
            sKeywords["tuple"] = XQueryTokenType.K_TUPLE // Saxon 9.8
            sKeywords["type"] = XQueryTokenType.K_TYPE // XQuery 3.0
            sKeywords["typeswitch"] = XQueryTokenType.K_TYPESWITCH
            sKeywords["unassignable"] = XQueryTokenType.K_UNASSIGNABLE // Scripting Extension 1.0
            sKeywords["union"] = XQueryTokenType.K_UNION
            sKeywords["unordered"] = XQueryTokenType.K_UNORDERED
            sKeywords["update"] = XQueryTokenType.K_UPDATE // BaseX 7.8
            sKeywords["updating"] = XQueryTokenType.K_UPDATING // Update Facility 1.0
            sKeywords["uppercase"] = XQueryTokenType.K_UPPERCASE // Full Text 1.0
            sKeywords["using"] = XQueryTokenType.K_USING // Full Text 1.0
            sKeywords["validate"] = XQueryTokenType.K_VALIDATE
            sKeywords["value"] = XQueryTokenType.K_VALUE // Update Facility 1.0
            sKeywords["variable"] = XQueryTokenType.K_VARIABLE
            sKeywords["version"] = XQueryTokenType.K_VERSION
            sKeywords["weight"] = XQueryTokenType.K_WEIGHT // Full Text 1.0
            sKeywords["when"] = XQueryTokenType.K_WHEN // XQuery 3.0
            sKeywords["where"] = XQueryTokenType.K_WHERE
            sKeywords["while"] = XQueryTokenType.K_WHILE // Scripting Extension 1.0
            sKeywords["wildcards"] = XQueryTokenType.K_WILDCARDS // Full Text 1.0
            sKeywords["window"] = XQueryTokenType.K_WINDOW // XQuery 3.0; Full Text 1.0
            sKeywords["with"] = XQueryTokenType.K_WITH // Update Facility 1.0
            sKeywords["without"] = XQueryTokenType.K_WITHOUT // Full Text 1.0
            sKeywords["word"] = XQueryTokenType.K_WORD // Full Text 1.0
            sKeywords["words"] = XQueryTokenType.K_WORDS // Full Text 1.0
            sKeywords["xquery"] = XQueryTokenType.K_XQUERY
            sKeywords["zero-digit"] = XQueryTokenType.K_ZERO_DIGIT // XQuery 3.0
        }
    }

    // endregion
}
