/*
 * Copyright (C) 2018-2022 Reece H. Dunn
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

@Suppress("DuplicatedCode")
open class XPathLexer : LexerImpl(STATE_DEFAULT) {
    // region States

    protected open fun ncnameToKeyword(name: CharSequence): IKeywordOrNCNameType? = KEYWORDS[name]

    protected open fun stateDefault(state: Int) {
        var c = mTokenRange.codePoint.codepoint
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
                c = mTokenRange.codePoint.codepoint
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
            CharacterClass.COMMA -> {
                mTokenRange.match()
                mType = XPathTokenType.COMMA
            }
            CharacterClass.CURLY_BRACE_CLOSE -> {
                mTokenRange.match()
                mType = XPathTokenType.BLOCK_CLOSE
                popState()
            }
            CharacterClass.CURLY_BRACE_OPEN -> {
                mTokenRange.match()
                mType = XPathTokenType.BLOCK_OPEN
                pushState(state)
            }
            CharacterClass.DIGIT -> {
                mTokenRange.match()
                while (CharacterClass.getCharClass(mTokenRange.codePoint.codepoint) == CharacterClass.DIGIT)
                    mTokenRange.match()
                mType = if (CharacterClass.getCharClass(mTokenRange.codePoint.codepoint) == CharacterClass.DOT) {
                    mTokenRange.match()
                    while (CharacterClass.getCharClass(mTokenRange.codePoint.codepoint) == CharacterClass.DIGIT)
                        mTokenRange.match()
                    XPathTokenType.DECIMAL_LITERAL
                } else {
                    XPathTokenType.INTEGER_LITERAL
                }
                c = mTokenRange.codePoint.codepoint
                if (c == 'e'.code || c == 'E'.code) {
                    mTokenRange.save()
                    mTokenRange.match()
                    c = mTokenRange.codePoint.codepoint
                    if (c == '+'.code || c == '-'.code) {
                        mTokenRange.match()
                        c = mTokenRange.codePoint.codepoint
                    }
                    if (CharacterClass.getCharClass(c) == CharacterClass.DIGIT) {
                        mTokenRange.match()
                        while (CharacterClass.getCharClass(mTokenRange.codePoint.codepoint) == CharacterClass.DIGIT)
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
                mTokenRange.save()
                mTokenRange.match()
                cc = CharacterClass.getCharClass(mTokenRange.codePoint.codepoint)
                when (cc) {
                    CharacterClass.DOT -> {
                        mTokenRange.match()
                        mType = if (mTokenRange.codePoint.codepoint == '.'.code) {
                            mTokenRange.match()
                            XPathTokenType.ELLIPSIS
                        } else {
                            XPathTokenType.PARENT_SELECTOR
                        }
                        return
                    }
                    CharacterClass.DIGIT -> {
                        mTokenRange.restore()
                        mType = XPathTokenType.DECIMAL_LITERAL
                    }
                    CharacterClass.CURLY_BRACE_OPEN -> {
                        mTokenRange.match()
                        mType = XPathTokenType.CONTEXT_FUNCTION
                        return
                    }
                    else -> {
                        mType = XPathTokenType.DOT
                        return
                    }
                }

                mTokenRange.match()
                while (CharacterClass.getCharClass(mTokenRange.codePoint.codepoint) == CharacterClass.DIGIT)
                    mTokenRange.match()
                mType = XPathTokenType.DECIMAL_LITERAL
                c = mTokenRange.codePoint.codepoint
                if (c == 'e'.code || c == 'E'.code) {
                    mTokenRange.save()
                    mTokenRange.match()
                    c = mTokenRange.codePoint.codepoint
                    if (c == '+'.code || c == '-'.code) {
                        mTokenRange.match()
                        c = mTokenRange.codePoint.codepoint
                    }
                    if (CharacterClass.getCharClass(c) == CharacterClass.DIGIT) {
                        mTokenRange.match()
                        while (CharacterClass.getCharClass(mTokenRange.codePoint.codepoint) == CharacterClass.DIGIT)
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
                c = mTokenRange.codePoint.codepoint
                mType = if (c == '>'.code) {
                    mTokenRange.match()
                    XPathTokenType.ARROW
                } else {
                    XPathTokenType.EQUAL
                }
            }
            CharacterClass.EXCLAMATION_MARK -> {
                mTokenRange.match()
                c = mTokenRange.codePoint.codepoint
                mType = when (c) {
                    '='.code -> {
                        mTokenRange.match()
                        XPathTokenType.NOT_EQUAL
                    }
                    '!'.code -> {
                        mTokenRange.match()
                        XPathTokenType.TERNARY_ELSE // EXPath XPath/XQuery NG Proposal
                    }
                    else -> XPathTokenType.MAP_OPERATOR
                }
            }
            CharacterClass.FORWARD_SLASH -> {
                mTokenRange.match()
                c = mTokenRange.codePoint.codepoint
                mType = if (c == '/'.code) {
                    mTokenRange.match()
                    XPathTokenType.ALL_DESCENDANTS_PATH
                } else {
                    XPathTokenType.DIRECT_DESCENDANTS_PATH
                }
            }
            CharacterClass.GREATER_THAN -> {
                mTokenRange.match()
                c = mTokenRange.codePoint.codepoint
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
            CharacterClass.HASH -> {
                mTokenRange.match()
                mType = if (mTokenRange.codePoint.codepoint == ')'.code) {
                    mTokenRange.match()
                    XPathTokenType.PRAGMA_END
                } else {
                    XPathTokenType.FUNCTION_REF_OPERATOR
                }
            }
            CharacterClass.HYPHEN_MINUS -> {
                mTokenRange.match()
                mType = if (mTokenRange.codePoint.codepoint == '>'.code) {
                    mTokenRange.match()
                    XPathTokenType.THIN_ARROW
                } else {
                    XPathTokenType.MINUS
                }
            }
            CharacterClass.LESS_THAN -> {
                mTokenRange.match()
                c = mTokenRange.codePoint.codepoint
                mType = when (c) {
                    '<'.code -> {
                        mTokenRange.match()
                        XPathTokenType.NODE_BEFORE
                    }
                    '='.code -> {
                        mTokenRange.match()
                        XPathTokenType.LESS_THAN_OR_EQUAL
                    }
                    else -> XPathTokenType.LESS_THAN
                }
            }
            CharacterClass.NAME_START_CHAR -> {
                mTokenRange.match()
                cc = CharacterClass.getCharClass(mTokenRange.codePoint.codepoint)
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
                        cc = CharacterClass.getCharClass(mTokenRange.codePoint.codepoint)
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
                c = mTokenRange.codePoint.codepoint
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
            CharacterClass.PLUS -> {
                mTokenRange.match()
                mType = XPathTokenType.PLUS
            }
            CharacterClass.QUESTION_MARK -> {
                mTokenRange.match()
                c = mTokenRange.codePoint.codepoint
                mType = when (c) {
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
            CharacterClass.TILDE -> {
                mTokenRange.match()
                mType = XPathTokenType.TYPE_ALIAS
            }
            CharacterClass.VERTICAL_BAR -> {
                mTokenRange.match()
                mType = if (mTokenRange.codePoint.codepoint == '|'.code) {
                    mTokenRange.match()
                    XPathTokenType.CONCATENATION
                } else {
                    XPathTokenType.UNION
                }
            }
            CharacterClass.WHITESPACE -> {
                mTokenRange.match()
                while (CharacterClass.getCharClass(mTokenRange.codePoint.codepoint) == CharacterClass.WHITESPACE)
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
        var c = mTokenRange.codePoint.codepoint
        if (c == type.code) {
            mTokenRange.match()
            if (mTokenRange.codePoint.codepoint == type.code && type != '}') {
                mTokenRange.match()
                mType = XPathTokenType.ESCAPED_CHARACTER
            } else {
                mType = if (type == '}') XPathTokenType.BRACED_URI_LITERAL_END else XPathTokenType.STRING_LITERAL_END
                popState()
            }
        } else if (c == '{'.code && type == '}') {
            mTokenRange.match()
            mType = XPathTokenType.BAD_CHARACTER
        } else if (c == CodePointRange.END_OF_BUFFER) {
            mType = null
        } else {
            while (c != type.code && c != CodePointRange.END_OF_BUFFER && !(type == '}' && c == '{'.code)) {
                mTokenRange.match()
                c = mTokenRange.codePoint.codepoint
            }
            mType = XPathTokenType.STRING_LITERAL_CONTENTS
        }
    }

    private fun stateDoubleExponent() {
        mTokenRange.match()
        val c = mTokenRange.codePoint.codepoint
        if (c == '+'.code || c == '-'.code) {
            mTokenRange.match()
        }
        mType = XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT
        popState()
    }

    private fun stateXQueryComment() {
        var c = mTokenRange.codePoint.codepoint
        if (c == CodePointRange.END_OF_BUFFER) {
            mType = null
            return
        } else if (c == ':'.code) {
            mTokenRange.save()
            mTokenRange.match()
            if (mTokenRange.codePoint.codepoint == ')'.code) {
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
            } else if (c == '('.code) {
                mTokenRange.match()
                if (mTokenRange.codePoint.codepoint == ':'.code) {
                    mTokenRange.match()
                    ++depth
                }
            } else if (c == ':'.code) {
                mTokenRange.save()
                mTokenRange.match()
                if (mTokenRange.codePoint.codepoint == ')'.code) {
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
            c = mTokenRange.codePoint.codepoint
        }
    }

    private fun statePragmaPreQName() {
        val c = mTokenRange.codePoint.codepoint
        var cc = CharacterClass.getCharClass(c)
        when (cc) {
            CharacterClass.WHITESPACE -> {
                mTokenRange.match()
                while (CharacterClass.getCharClass(mTokenRange.codePoint.codepoint) == CharacterClass.WHITESPACE)
                    mTokenRange.match()
                mType = XPathTokenType.WHITE_SPACE
            }
            CharacterClass.COLON -> {
                mTokenRange.match()
                mType = XPathTokenType.QNAME_SEPARATOR
                popState()
                pushState(STATE_PRAGMA_QNAME)
            }
            CharacterClass.NAME_START_CHAR -> {
                mTokenRange.match()
                cc = CharacterClass.getCharClass(mTokenRange.codePoint.codepoint)
                if (c == 'Q'.code && cc == CharacterClass.CURLY_BRACE_OPEN) {
                    mTokenRange.match()
                    mType = XPathTokenType.BRACED_URI_LITERAL_START
                    popState()
                    pushState(STATE_PRAGMA_QNAME)
                    pushState(STATE_BRACED_URI_LITERAL_PRAGMA)
                } else {
                    while (
                        cc == CharacterClass.NAME_START_CHAR ||
                        cc == CharacterClass.DIGIT ||
                        cc == CharacterClass.DOT ||
                        cc == CharacterClass.HYPHEN_MINUS ||
                        cc == CharacterClass.NAME_CHAR
                    ) {
                        mTokenRange.match()
                        cc = CharacterClass.getCharClass(mTokenRange.codePoint.codepoint)
                    }
                    mType = XPathTokenType.NCNAME
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
        val c = mTokenRange.codePoint.codepoint
        var cc = CharacterClass.getCharClass(c)
        when (cc) {
            CharacterClass.WHITESPACE -> {
                mTokenRange.match()
                while (CharacterClass.getCharClass(mTokenRange.codePoint.codepoint) == CharacterClass.WHITESPACE)
                    mTokenRange.match()
                mType = XPathTokenType.WHITE_SPACE
                popState()
                pushState(STATE_PRAGMA_CONTENTS)
            }
            CharacterClass.COLON -> {
                mTokenRange.match()
                mType = XPathTokenType.QNAME_SEPARATOR
            }
            CharacterClass.NAME_START_CHAR -> {
                mTokenRange.match()
                cc = CharacterClass.getCharClass(mTokenRange.codePoint.codepoint)
                while (
                    cc == CharacterClass.NAME_START_CHAR ||
                    cc == CharacterClass.DIGIT ||
                    cc == CharacterClass.DOT ||
                    cc == CharacterClass.HYPHEN_MINUS ||
                    cc == CharacterClass.NAME_CHAR
                ) {
                    mTokenRange.match()
                    cc = CharacterClass.getCharClass(mTokenRange.codePoint.codepoint)
                }
                mType = XPathTokenType.NCNAME
            }
            else -> {
                popState()
                pushState(STATE_PRAGMA_CONTENTS)
                statePragmaContents()
            }
        }
    }

    private fun statePragmaContents() {
        var c = mTokenRange.codePoint.codepoint
        if (c == CodePointRange.END_OF_BUFFER) {
            mType = null
            return
        } else if (c == '#'.code) {
            mTokenRange.save()
            mTokenRange.match()
            if (mTokenRange.codePoint.codepoint == ')'.code) {
                mTokenRange.match()
                mType = XPathTokenType.PRAGMA_END
                popState()
                return
            } else {
                mTokenRange.restore()
            }
        }

        while (true) {
            if (c == CodePointRange.END_OF_BUFFER) {
                mTokenRange.match()
                mType = XPathTokenType.PRAGMA_CONTENTS
                popState()
                pushState(STATE_UNEXPECTED_END_OF_BLOCK)
                return
            } else if (c == '#'.code) {
                mTokenRange.save()
                mTokenRange.match()
                if (mTokenRange.codePoint.codepoint == ')'.code) {
                    mTokenRange.restore()
                    mType = XPathTokenType.PRAGMA_CONTENTS
                    return
                }
            } else {
                mTokenRange.match()
            }
            c = mTokenRange.codePoint.codepoint
        }
    }

    private fun stateUnexpectedEndOfBlock() {
        mType = XPathTokenType.UNEXPECTED_END_OF_BLOCK
        popState()
    }

    // endregion
    // region Lexer

    override fun advance(state: Int): Unit = when (state) {
        STATE_DEFAULT -> stateDefault(state)
        STATE_STRING_LITERAL_QUOTE -> stateStringLiteral('"')
        STATE_STRING_LITERAL_APOSTROPHE -> stateStringLiteral('\'')
        STATE_DOUBLE_EXPONENT -> stateDoubleExponent()
        STATE_XQUERY_COMMENT -> stateXQueryComment()
        STATE_UNEXPECTED_END_OF_BLOCK -> stateUnexpectedEndOfBlock()
        STATE_PRAGMA_PRE_QNAME -> statePragmaPreQName()
        STATE_PRAGMA_QNAME -> statePragmaQName()
        STATE_PRAGMA_CONTENTS -> statePragmaContents()
        STATE_BRACED_URI_LITERAL -> stateStringLiteral('}')
        STATE_BRACED_URI_LITERAL_PRAGMA -> stateStringLiteral('}')
        else -> throw AssertionError("Invalid state: $state")
    }

    // endregion

    companion object {
        // region State Constants

        const val STATE_STRING_LITERAL_QUOTE: Int = 1
        const val STATE_STRING_LITERAL_APOSTROPHE: Int = 2
        const val STATE_DOUBLE_EXPONENT: Int = 3
        const val STATE_XQUERY_COMMENT: Int = 4
        const val STATE_UNEXPECTED_END_OF_BLOCK: Int = 6
        const val STATE_PRAGMA_PRE_QNAME: Int = 8
        const val STATE_PRAGMA_QNAME: Int = 9
        const val STATE_PRAGMA_CONTENTS: Int = 10
        const val STATE_BRACED_URI_LITERAL: Int = 26
        const val STATE_BRACED_URI_LITERAL_PRAGMA: Int = 31

        // endregion

        private val KEYWORDS = mapOf(
            "_" to XPathTokenType.K__, // Saxon 10.0
            "all" to XPathTokenType.K_ALL, // Full Text 1.0
            "ancestor" to XPathTokenType.K_ANCESTOR, // XPath 1.0
            "ancestor-or-self" to XPathTokenType.K_ANCESTOR_OR_SELF, // XPath 1.0
            "and" to XPathTokenType.K_AND, // XPath 1.0
            "andAlso" to XPathTokenType.K_ANDALSO, // Saxon 9.9
            "any" to XPathTokenType.K_ANY, // Full Text 1.0
            "array" to XPathTokenType.K_ARRAY, // XPath 3.1
            "array-node" to XPathTokenType.K_ARRAY_NODE, // MarkLogic 8.0
            "as" to XPathTokenType.K_AS, // XPath 2.0
            "at" to XPathTokenType.K_AT, // Full Text 1.0; XQuery 1.0
            "attribute" to XPathTokenType.K_ATTRIBUTE, // XPath 1.0
            "boolean-node" to XPathTokenType.K_BOOLEAN_NODE, // MarkLogic 8.0
            "case" to XPathTokenType.K_CASE, // Full Text 1.0; XQuery 1.0
            "cast" to XPathTokenType.K_CAST, // XPath 2.0
            "castable" to XPathTokenType.K_CASTABLE, // XPath 2.0
            "child" to XPathTokenType.K_CHILD, // XPath 1.0
            "comment" to XPathTokenType.K_COMMENT, // XPath 1.0
            "contains" to XPathTokenType.K_CONTAINS, // Full Text 1.0
            "content" to XPathTokenType.K_CONTENT, // Full Text 1.0
            "current" to XPathTokenType.K_CURRENT, // xsl:mode
            "default" to XPathTokenType.K_DEFAULT, // Full Text 1.0; XQuery 1.0
            "descendant" to XPathTokenType.K_DESCENDANT, // XPath 1.0
            "descendant-or-self" to XPathTokenType.K_DESCENDANT_OR_SELF, // XPath 1.0
            "diacritics" to XPathTokenType.K_DIACRITICS, // Full Text 1.0
            "different" to XPathTokenType.K_DIFFERENT, // Full Text 1.0
            "distance" to XPathTokenType.K_DISTANCE, // Full Text 1.0
            "div" to XPathTokenType.K_DIV, // XPath 1.0
            "document-node" to XPathTokenType.K_DOCUMENT_NODE, // XPath 2.0
            "element" to XPathTokenType.K_ELEMENT, // XPath 2.0
            "else" to XPathTokenType.K_ELSE, // XPath 2.0
            "empty" to XPathTokenType.K_EMPTY, // XPath 2.0 WD 02 May 2003
            "empty-sequence" to XPathTokenType.K_EMPTY_SEQUENCE, // XPath 2.0
            "end" to XPathTokenType.K_END, // Full Text 1.0; XQuery 3.0
            "entire" to XPathTokenType.K_ENTIRE, // Full Text 1.0
            "enum" to XPathTokenType.K_ENUM, // XPath 4.0 ED
            "eq" to XPathTokenType.K_EQ, // XPath 2.0
            "every" to XPathTokenType.K_EVERY, // XPath 2.0
            "exactly" to XPathTokenType.K_EXACTLY, // Full Text 1.0
            "except" to XPathTokenType.K_EXCEPT, // XPath 2.0
            "fn" to XPathTokenType.K_FN, // Saxon 9.8
            "following" to XPathTokenType.K_FOLLOWING, // XPath 1.0
            "following-sibling" to XPathTokenType.K_FOLLOWING_SIBLING, // XPath 1.0
            "for" to XPathTokenType.K_FOR, // XPath 2.0
            "from" to XPathTokenType.K_FROM, // Full Text 1.0
            "ftand" to XPathTokenType.K_FTAND, // Full Text 1.0
            "ftnot" to XPathTokenType.K_FTNOT, // Full Text 1.0
            "ftor" to XPathTokenType.K_FTOR, // Full Text 1.0
            "function" to XPathTokenType.K_FUNCTION, // XPath 3.0 ; XQuery 1.0
            "ge" to XPathTokenType.K_GE, // XPath 2.0
            "gt" to XPathTokenType.K_GT, // XPath 2.0
            "idiv" to XPathTokenType.K_IDIV, // XPath 2.0
            "if" to XPathTokenType.K_IF, // XPath 2.0
            "in" to XPathTokenType.K_IN, // XPath 2.0
            "insensitive" to XPathTokenType.K_INSENSITIVE, // Full Text 1.0
            "instance" to XPathTokenType.K_INSTANCE, // XPath 2.0
            "intersect" to XPathTokenType.K_INTERSECT, // XPath 2.0
            "is" to XPathTokenType.K_IS, // XPath 2.0
            "item" to XPathTokenType.K_ITEM, // XPath 2.0
            "language" to XPathTokenType.K_LANGUAGE, // Full Text 1.0
            "le" to XPathTokenType.K_LE, // XPath 2.0
            "least" to XPathTokenType.K_LEAST, // Full Text 1.0; XQuery 1.0
            "levels" to XPathTokenType.K_LEVELS, // Full Text 1.0
            "let" to XPathTokenType.K_LET, // XPath 3.0 ; XQuery 1.0
            "lowercase" to XPathTokenType.K_LOWERCASE, // Full Text 1.0
            "lt" to XPathTokenType.K_LT, // XPath 2.0
            "map" to XPathTokenType.K_MAP, // XPath 3.1
            "member" to XPathTokenType.K_MEMBER, // Saxon 10.0
            "mod" to XPathTokenType.K_MOD, // XPath 1.0
            "most" to XPathTokenType.K_MOST, // Full Text 1.0
            "namespace" to XPathTokenType.K_NAMESPACE, // XPath 1.0
            "namespace-node" to XPathTokenType.K_NAMESPACE_NODE, // XPath 3.0
            "ne" to XPathTokenType.K_NE, // XPath 2.0
            "no" to XPathTokenType.K_NO, // Full Text 1.0
            "node" to XPathTokenType.K_NODE, // XPath 1.0
            "not" to XPathTokenType.K_NOT, // Full Text 1.0
            "null-node" to XPathTokenType.K_NULL_NODE, // MarkLogic 8.0
            "number-node" to XPathTokenType.K_NUMBER_NODE, // MarkLogic 8.0
            "object-node" to XPathTokenType.K_OBJECT_NODE, // MarkLogic 8.0
            "occurs" to XPathTokenType.K_OCCURS, // Full Text 1.0
            "of" to XPathTokenType.K_OF, // XPath 2.0
            "option" to XPathTokenType.K_OPTION, // Full Text 1.0; XQuery 1.0
            "or" to XPathTokenType.K_OR, // XPath 1.0
            "ordered" to XPathTokenType.K_ORDERED, // Full Text 1.0; XQuery 1.0
            "orElse" to XPathTokenType.K_ORELSE, // Saxon 9.9
            "otherwise" to XPathTokenType.K_OTHERWISE, // Saxon 10.0
            "paragraph" to XPathTokenType.K_PARAGRAPH, // Full Text 1.0
            "paragraphs" to XPathTokenType.K_PARAGRAPHS, // Full Text 1.0
            "parent" to XPathTokenType.K_PARENT, // XPath 1.0
            "phrase" to XPathTokenType.K_PHRASE, // Full Text 1.0
            "preceding" to XPathTokenType.K_PRECEDING, // XPath 1.0
            "preceding-sibling" to XPathTokenType.K_PRECEDING_SIBLING, // XPath 1.0
            "processing-instruction" to XPathTokenType.K_PROCESSING_INSTRUCTION, // XPath 1.0
            "property" to XPathTokenType.K_PROPERTY, // MarkLogic 6.0
            "record" to XPathTokenType.K_RECORD, // XPath 4.0 ED
            "relationship" to XPathTokenType.K_RELATIONSHIP, // Full Text 1.0
            "return" to XPathTokenType.K_RETURN, // XPath 2.0
            "same" to XPathTokenType.K_SAME, // Full Text 1.0
            "satisfies" to XPathTokenType.K_SATISFIES, // XPath 2.0
            "schema-attribute" to XPathTokenType.K_SCHEMA_ATTRIBUTE, // XPath 2.0
            "schema-element" to XPathTokenType.K_SCHEMA_ELEMENT, // XPath 2.0
            "score" to XPathTokenType.K_SCORE, // Full Text 1.0
            "self" to XPathTokenType.K_SELF, // XPath 1.0
            "sensitive" to XPathTokenType.K_SENSITIVE, // Full Text 1.0
            "sentence" to XPathTokenType.K_SENTENCE, // Full Text 1.0
            "sentences" to XPathTokenType.K_SENTENCES, // Full Text 1.0
            "some" to XPathTokenType.K_SOME, // XPath 2.0
            "start" to XPathTokenType.K_START, // Full Text 1.0; XQuery 3.0
            "stemming" to XPathTokenType.K_STEMMING, // Full Text 1.0
            "stop" to XPathTokenType.K_STOP, // Full Text 1.0
            "text" to XPathTokenType.K_TEXT, // XPath 1.0
            "then" to XPathTokenType.K_THEN, // XPath 2.0
            "thesaurus" to XPathTokenType.K_THESAURUS, // Full Text 1.0
            "times" to XPathTokenType.K_TIMES, // Full Text 1.0
            "to" to XPathTokenType.K_TO, // XPath 2.0
            "treat" to XPathTokenType.K_TREAT, // XPath 2.0
            "tuple" to XPathTokenType.K_TUPLE, // Saxon 9.8
            "type" to XPathTokenType.K_TYPE, // XQuery 3.0 ; Saxon 10.0
            "union" to XPathTokenType.K_UNION, // XPath 2.0
            "unnamed" to XPathTokenType.K_UNNAMED, // xsl:default-mode-type
            "uppercase" to XPathTokenType.K_UPPERCASE, // Full Text 1.0
            "using" to XPathTokenType.K_USING, // Full Text 1.0 ; MarkLogic 6.0
            "weight" to XPathTokenType.K_WEIGHT, // Full Text 1.0
            "wildcards" to XPathTokenType.K_WILDCARDS, // Full Text 1.0
            "window" to XPathTokenType.K_WINDOW, // XQuery 3.0; Full Text 1.0
            "with" to XPathTokenType.K_WITH, // XPath 4.0 ED; Update Facility 1.0
            "without" to XPathTokenType.K_WITHOUT, // Full Text 1.0
            "word" to XPathTokenType.K_WORD, // Full Text 1.0
            "words" to XPathTokenType.K_WORDS // Full Text 1.0
        )
    }
}
