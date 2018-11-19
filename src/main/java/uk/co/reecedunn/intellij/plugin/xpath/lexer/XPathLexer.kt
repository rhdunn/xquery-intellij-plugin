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
import uk.co.reecedunn.intellij.plugin.core.lexer.CodePointRange
import uk.co.reecedunn.intellij.plugin.core.lexer.LexerImpl
import uk.co.reecedunn.intellij.plugin.core.lexer.STATE_DEFAULT

// region State Constants

const val STATE_STRING_LITERAL_QUOTE = 1
const val STATE_STRING_LITERAL_APOSTROPHE = 2
const val STATE_DOUBLE_EXPONENT = 3
const val STATE_XQUERY_COMMENT = 4
const val STATE_UNEXPECTED_END_OF_BLOCK = 6
const val STATE_BRACED_URI_LITERAL = 26

// endregion

private val KEYWORDS = mapOf(
    "ancestor" to XPathTokenType.K_ANCESTOR, // XPath 2.0
    "ancestor-or-self" to XPathTokenType.K_ANCESTOR_OR_SELF, // XPath 2.0
    "and" to XPathTokenType.K_AND, // XPath 2.0
    "as" to XPathTokenType.K_AS, // XPath 2.0
    "attribute" to XPathTokenType.K_ATTRIBUTE, // XPath 2.0
    "cast" to XPathTokenType.K_CAST, // XPath 2.0
    "castable" to XPathTokenType.K_CASTABLE, // XPath 2.0
    "child" to XPathTokenType.K_CHILD, // XPath 2.0
    "comment" to XPathTokenType.K_COMMENT, // XPath 2.0
    "descendant" to XPathTokenType.K_DESCENDANT, // XPath 2.0
    "descendant-or-self" to XPathTokenType.K_DESCENDANT_OR_SELF, // XPath 2.0
    "div" to XPathTokenType.K_DIV, // XPath 2.0
    "document-node" to XPathTokenType.K_DOCUMENT_NODE, // XPath 2.0
    "element" to XPathTokenType.K_ELEMENT, // XPath 2.0
    "else" to XPathTokenType.K_ELSE, // XPath 2.0
    "empty-sequence" to XPathTokenType.K_EMPTY_SEQUENCE, // XPath 2.0
    "eq" to XPathTokenType.K_EQ, // XPath 2.0
    "every" to XPathTokenType.K_EVERY, // XPath 2.0
    "except" to XPathTokenType.K_EXCEPT, // XPath 2.0
    "following" to XPathTokenType.K_FOLLOWING, // XPath 2.0
    "following-sibling" to XPathTokenType.K_FOLLOWING_SIBLING, // XPath 2.0
    "for" to XPathTokenType.K_FOR, // XPath 2.0
    "ge" to XPathTokenType.K_GE, // XPath 2.0
    "gt" to XPathTokenType.K_GT, // XPath 2.0
    "idiv" to XPathTokenType.K_IDIV, // XPath 2.0
    "if" to XPathTokenType.K_IF, // XPath 2.0
    "in" to XPathTokenType.K_IN, // XPath 2.0
    "instance" to XPathTokenType.K_INSTANCE, // XPath 2.0
    "intersect" to XPathTokenType.K_INTERSECT, // XPath 2.0
    "is" to XPathTokenType.K_IS, // XPath 2.0
    "item" to XPathTokenType.K_ITEM, // XPath 2.0
    "le" to XPathTokenType.K_LE, // XPath 2.0
    "lt" to XPathTokenType.K_LT, // XPath 2.0
    "mod" to XPathTokenType.K_MOD, // XPath 2.0
    "namespace" to XPathTokenType.K_NAMESPACE, // XPath 2.0
    "ne" to XPathTokenType.K_NE, // XPath 2.0
    "node" to XPathTokenType.K_NODE, // XPath 2.0
    "of" to XPathTokenType.K_OF, // XPath 2.0
    "or" to XPathTokenType.K_OR, // XPath 2.0
    "parent" to XPathTokenType.K_PARENT, // XPath 2.0
    "preceding" to XPathTokenType.K_PRECEDING, // XPath 2.0
    "preceding-sibling" to XPathTokenType.K_PRECEDING_SIBLING, // XPath 2.0
    "processing-instruction" to XPathTokenType.K_PROCESSING_INSTRUCTION, // XPath 2.0
    "return" to XPathTokenType.K_RETURN, // XPath 2.0
    "satisfies" to XPathTokenType.K_SATISFIES, // XPath 2.0
    "schema-attribute" to XPathTokenType.K_SCHEMA_ATTRIBUTE, // XPath 2.0
    "self" to XPathTokenType.K_SELF, // XPath 2.0
    "some" to XPathTokenType.K_SOME, // XPath 2.0
    "text" to XPathTokenType.K_TEXT, // XPath 2.0
    "then" to XPathTokenType.K_THEN, // XPath 2.0
    "to" to XPathTokenType.K_TO, // XPath 2.0
    "treat" to XPathTokenType.K_TREAT, // XPath 2.0
    "union" to XPathTokenType.K_UNION // XPath 2.0
)

open class XPathLexer : LexerImpl(STATE_DEFAULT) {
    // region States

    protected open fun ncnameToKeyword(name: CharSequence): IKeywordOrNCNameType? {
        return KEYWORDS[name]
    }

    private fun stateDefault() {
        var c = mTokenRange.codePoint
        var cc = CharacterClass.getCharClass(c)
        when (cc) {
            CharacterClass.ASTERISK -> {
                mTokenRange.match()
                mType = XPathTokenType.STAR
            }
            CharacterClass.AT_SIGN -> {
                mTokenRange.match()
                mType = XPathTokenType.ATTRIBUTE_SELECTOR
            }
            CharacterClass.COLON -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                mType = when (c) {
                    ')'.toInt() -> {
                        mTokenRange.match()
                        XPathTokenType.COMMENT_END_TAG
                    }
                    ':'.toInt() -> {
                        mTokenRange.match()
                        XPathTokenType.AXIS_SEPARATOR
                    }
                    else -> XPathTokenType.QNAME_SEPARATOR
                }
            }
            CharacterClass.COMMA -> {
                mTokenRange.match()
                mType = XPathTokenType.COMMA
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
            CharacterClass.DOLLAR -> {
                mTokenRange.match()
                mType = XPathTokenType.VARIABLE_INDICATOR
            }
            CharacterClass.DOT -> {
                mTokenRange.match()
                cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                when (cc) {
                    CharacterClass.DOT -> {
                        mTokenRange.match()
                        mType = XPathTokenType.PARENT_SELECTOR
                        return
                    }
                    CharacterClass.DIGIT -> mType = XPathTokenType.DECIMAL_LITERAL
                    else -> {
                        mType = XPathTokenType.DOT
                        return
                    }
                }

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
            CharacterClass.EQUAL -> {
                mTokenRange.match()
                mType = XPathTokenType.EQUAL
            }
            CharacterClass.EXCLAMATION_MARK -> {
                mTokenRange.match()
                if (mTokenRange.codePoint == '='.toInt()) {
                    mTokenRange.match()
                    mType = XPathTokenType.NOT_EQUAL
                }
            }
            CharacterClass.FORWARD_SLASH -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                mType = if (c == '/'.toInt()) {
                    mTokenRange.match()
                    XPathTokenType.ALL_DESCENDANTS_PATH
                } else {
                    XPathTokenType.DIRECT_DESCENDANTS_PATH
                }
            }
            CharacterClass.GREATER_THAN -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                mType = when (c) {
                    '>'.toInt() -> {
                        mTokenRange.match()
                        XPathTokenType.NODE_AFTER
                    }
                    '='.toInt() -> {
                        mTokenRange.match()
                        XPathTokenType.GREATER_THAN_OR_EQUAL
                    }
                    else -> XPathTokenType.GREATER_THAN
                }
            }
            CharacterClass.HYPHEN_MINUS -> {
                mTokenRange.match()
                mType = XPathTokenType.MINUS
            }
            CharacterClass.LESS_THAN -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                mType = when (c) {
                    '<'.toInt() -> {
                        mTokenRange.match()
                        XPathTokenType.NODE_BEFORE
                    }
                    '='.toInt() -> {
                        mTokenRange.match()
                        XPathTokenType.LESS_THAN_OR_EQUAL
                    }
                    else -> XPathTokenType.LESS_THAN
                }
            }
            CharacterClass.NAME_START_CHAR -> {
                mTokenRange.match()
                cc = CharacterClass.getCharClass(mTokenRange.codePoint)
                if (c == 'Q'.toInt() && cc == CharacterClass.CURLY_BRACE_OPEN) {
                    mTokenRange.match()
                    mType = XPathTokenType.BRACED_URI_LITERAL_START
                    pushState(STATE_BRACED_URI_LITERAL)
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
            CharacterClass.PARENTHESIS_CLOSE -> {
                mTokenRange.match()
                mType = XPathTokenType.PARENTHESIS_CLOSE
            }
            CharacterClass.PARENTHESIS_OPEN -> {
                mTokenRange.match()
                c = mTokenRange.codePoint
                if (c == ':'.toInt()) {
                    mTokenRange.match()
                    mType = XPathTokenType.COMMENT_START_TAG
                    pushState(STATE_XQUERY_COMMENT)
                } else {
                    mType = XPathTokenType.PARENTHESIS_OPEN
                }
            }
            CharacterClass.PLUS -> {
                mTokenRange.match()
                mType = XPathTokenType.PLUS
            }
            CharacterClass.QUESTION_MARK -> {
                mTokenRange.match()
                mType = XPathTokenType.OPTIONAL
            }
            CharacterClass.QUOTE, CharacterClass.APOSTROPHE -> {
                mTokenRange.match()
                mType = XPathTokenType.STRING_LITERAL_START
                pushState(if (cc == CharacterClass.QUOTE) STATE_STRING_LITERAL_QUOTE else STATE_STRING_LITERAL_APOSTROPHE)
            }
            CharacterClass.SQUARE_BRACE_CLOSE -> {
                mTokenRange.match()
                mType = XPathTokenType.SQUARE_CLOSE
            }
            CharacterClass.SQUARE_BRACE_OPEN -> {
                mTokenRange.match()
                mType = XPathTokenType.SQUARE_OPEN
            }
            CharacterClass.VERTICAL_BAR -> {
                mTokenRange.match()
                mType = XPathTokenType.UNION
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

    protected open fun stateStringLiteral(type: Char) {
        var c = mTokenRange.codePoint
        if (c == type.toInt()) {
            mTokenRange.match()
            if (mTokenRange.codePoint == type.toInt() && type != '}') {
                mTokenRange.match()
                mType = XPathTokenType.ESCAPED_CHARACTER
            } else {
                mType = if (type == '}') XPathTokenType.BRACED_URI_LITERAL_END else XPathTokenType.STRING_LITERAL_END
                popState()
            }
        } else if (c == '{'.toInt() && type == '}') {
            mTokenRange.match()
            mType = XPathTokenType.BAD_CHARACTER
        } else if (c == CodePointRange.END_OF_BUFFER) {
            mType = null
        } else {
            while (c != type.toInt() && c != CodePointRange.END_OF_BUFFER && c != '&'.toInt() && !(type == '}' && c == '{'.toInt())) {
                mTokenRange.match()
                c = mTokenRange.codePoint
            }
            mType = XPathTokenType.STRING_LITERAL_CONTENTS
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
                mType = XPathTokenType.COMMENT_END_TAG
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
                mType = XPathTokenType.COMMENT
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
                        mType = XPathTokenType.COMMENT
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
        mType = XPathTokenType.UNEXPECTED_END_OF_BLOCK
        popState()
    }

    // endregion
    // region Lexer

    override fun advance() {
        when (nextState()) {
            STATE_DEFAULT -> stateDefault()
            STATE_STRING_LITERAL_QUOTE -> stateStringLiteral('"')
            STATE_STRING_LITERAL_APOSTROPHE -> stateStringLiteral('\'')
            STATE_DOUBLE_EXPONENT -> stateDoubleExponent()
            STATE_XQUERY_COMMENT -> stateXQueryComment()
            STATE_UNEXPECTED_END_OF_BLOCK -> stateUnexpectedEndOfBlock()
            STATE_BRACED_URI_LITERAL -> stateStringLiteral('}')
            else -> throw AssertionError("Invalid state: $state")
        }
    }

    // endregion
}
