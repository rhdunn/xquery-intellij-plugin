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

import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.core.lexer.LexerImpl
import uk.co.reecedunn.intellij.plugin.core.lexer.STATE_DEFAULT
import xqt.platform.xml.lexer.*
import xqt.platform.xml.model.XmlChar
import xqt.platform.xml.model.XmlCharReader

@Suppress("DuplicatedCode")
open class XPathLexer : LexerImpl(STATE_DEFAULT) {
    // region States

    protected open fun ncnameToKeyword(name: CharSequence): IKeywordOrNCNameType? = KEYWORDS[name]

    protected open fun stateDefault(state: Int): IElementType? {
        return when (characters.currentChar) {
            Asterisk -> {
                characters.advance()
                XPathTokenType.STAR
            }

            CommercialAt -> {
                characters.advance()
                XPathTokenType.ATTRIBUTE_SELECTOR
            }

            Colon -> {
                characters.advance()
                when (characters.currentChar) {
                    RightParenthesis -> {
                        characters.advance()
                        XPathTokenType.COMMENT_END_TAG
                    }

                    Colon -> {
                        characters.advance()
                        XPathTokenType.AXIS_SEPARATOR
                    }

                    EqualsSign -> {
                        characters.advance()
                        XPathTokenType.ASSIGN_EQUAL
                    }

                    else -> XPathTokenType.QNAME_SEPARATOR
                }
            }

            Comma -> {
                characters.advance()
                XPathTokenType.COMMA
            }

            RightCurlyBracket -> {
                characters.advance()
                popState()
                XPathTokenType.BLOCK_CLOSE
            }

            LeftCurlyBracket -> {
                characters.advance()
                pushState(state)
                XPathTokenType.BLOCK_OPEN
            }

            in Digit -> {
                characters.advanceWhile { it in Digit }
                mType = if (characters.currentChar == FullStop) {
                    characters.advance()
                    characters.advanceWhile { it in Digit }
                    XPathTokenType.DECIMAL_LITERAL
                } else {
                    XPathTokenType.INTEGER_LITERAL
                }
                if (characters.currentChar == LatinSmallLetterE || characters.currentChar == LatinCapitalLetterE) {
                    val savedOffset = characters.currentOffset
                    characters.advance()
                    if (characters.currentChar == PlusSign || characters.currentChar == HyphenMinus) {
                        characters.advance()
                    }
                    if (characters.currentChar in Digit) {
                        characters.advanceWhile { it in Digit }
                        mType = XPathTokenType.DOUBLE_LITERAL
                    } else {
                        pushState(STATE_DOUBLE_EXPONENT)
                        characters.currentOffset = savedOffset
                    }
                }
                mType
            }

            DollarSign -> {
                characters.advance()
                XPathTokenType.VARIABLE_INDICATOR
            }

            FullStop -> {
                var savedOffset = characters.currentOffset
                characters.advance()
                when (characters.currentChar) {
                    FullStop -> {
                        characters.advance()
                        return if (characters.currentChar == FullStop) {
                            characters.advance()
                            XPathTokenType.ELLIPSIS
                        } else {
                            XPathTokenType.PARENT_SELECTOR
                        }
                    }

                    in Digit -> {
                        characters.currentOffset = savedOffset
                        mType = XPathTokenType.DECIMAL_LITERAL
                    }

                    LeftCurlyBracket -> {
                        characters.advance()
                        return XPathTokenType.CONTEXT_FUNCTION
                    }

                    else -> {
                        return XPathTokenType.DOT
                    }
                }

                characters.advance()
                characters.advanceWhile { it in Digit }
                mType = XPathTokenType.DECIMAL_LITERAL
                if (characters.currentChar == LatinSmallLetterE || characters.currentChar == LatinCapitalLetterE) {
                    savedOffset = characters.currentOffset
                    characters.advance()
                    if (characters.currentChar == PlusSign || characters.currentChar == HyphenMinus) {
                        characters.advance()
                    }
                    if (characters.currentChar in Digit) {
                        characters.advanceWhile { it in Digit }
                        mType = XPathTokenType.DOUBLE_LITERAL
                    } else {
                        pushState(STATE_DOUBLE_EXPONENT)
                        characters.currentOffset = savedOffset
                    }
                }
                mType
            }

            EqualsSign -> {
                characters.advance()
                if (characters.currentChar == GreaterThanSign) {
                    characters.advance()
                    XPathTokenType.ARROW
                } else {
                    XPathTokenType.EQUAL
                }
            }

            ExclamationMark -> {
                characters.advance()
                when (characters.currentChar) {
                    EqualsSign -> {
                        characters.advance()
                        XPathTokenType.NOT_EQUAL
                    }

                    ExclamationMark -> {
                        characters.advance()
                        XPathTokenType.TERNARY_ELSE // EXPath XPath/XQuery NG Proposal
                    }

                    else -> XPathTokenType.MAP_OPERATOR
                }
            }

            Solidus -> {
                characters.advance()
                if (characters.currentChar == Solidus) {
                    characters.advance()
                    XPathTokenType.ALL_DESCENDANTS_PATH
                } else {
                    XPathTokenType.DIRECT_DESCENDANTS_PATH
                }
            }

            GreaterThanSign -> {
                characters.advance()
                when (characters.currentChar) {
                    GreaterThanSign -> {
                        characters.advance()
                        XPathTokenType.NODE_AFTER
                    }

                    EqualsSign -> {
                        characters.advance()
                        XPathTokenType.GREATER_THAN_OR_EQUAL
                    }

                    else -> XPathTokenType.GREATER_THAN
                }
            }

            NumberSign -> {
                characters.advance()
                if (characters.currentChar == RightParenthesis) {
                    characters.advance()
                    XPathTokenType.PRAGMA_END
                } else {
                    XPathTokenType.FUNCTION_REF_OPERATOR
                }
            }

            HyphenMinus -> {
                characters.advance()
                if (characters.currentChar == GreaterThanSign) {
                    characters.advance()
                    XPathTokenType.THIN_ARROW
                } else {
                    XPathTokenType.MINUS
                }
            }

            LessThanSign -> {
                characters.advance()
                when (characters.currentChar) {
                    LessThanSign -> {
                        characters.advance()
                        XPathTokenType.NODE_BEFORE
                    }

                    EqualsSign -> {
                        characters.advance()
                        XPathTokenType.LESS_THAN_OR_EQUAL
                    }

                    else -> XPathTokenType.LESS_THAN
                }
            }

            in NameStartChar -> {
                val pc = characters.currentChar
                characters.advance()
                if (pc == LatinCapitalLetterQ && characters.currentChar == LeftCurlyBracket) {
                    characters.advance()
                    pushState(STATE_BRACED_URI_LITERAL)
                    XPathTokenType.BRACED_URI_LITERAL_START
                } else if (pc == LowLine && characters.currentChar == LeftCurlyBracket) {
                    characters.advance()
                    XPathTokenType.LAMBDA_FUNCTION
                } else {
                    characters.advanceWhile { it in NameChar && it != Colon }
                    ncnameToKeyword(tokenText) ?: XPathTokenType.NCNAME
                }
            }

            RightParenthesis -> {
                characters.advance()
                XPathTokenType.PARENTHESIS_CLOSE
            }

            LeftParenthesis -> {
                characters.advance()
                when (characters.currentChar) {
                    Colon -> {
                        characters.advance()
                        pushState(STATE_XQUERY_COMMENT)
                        XPathTokenType.COMMENT_START_TAG
                    }

                    NumberSign -> {
                        characters.advance()
                        pushState(STATE_PRAGMA_PRE_QNAME)
                        XPathTokenType.PRAGMA_BEGIN
                    }

                    else -> XPathTokenType.PARENTHESIS_OPEN
                }
            }

            PlusSign -> {
                characters.advance()
                XPathTokenType.PLUS
            }

            QuestionMark -> {
                characters.advance()
                when (characters.currentChar) {
                    QuestionMark -> {
                        characters.advance()
                        XPathTokenType.TERNARY_IF
                    }

                    Colon -> {
                        characters.advance()
                        XPathTokenType.ELVIS // EXPath XPath/XQuery NG Proposal
                    }

                    else -> XPathTokenType.OPTIONAL
                }
            }

            QuotationMark -> {
                characters.advance()
                pushState(STATE_STRING_LITERAL_QUOTE)
                XPathTokenType.STRING_LITERAL_START
            }

            Apostrophe -> {
                characters.advance()
                pushState(STATE_STRING_LITERAL_APOSTROPHE)
                XPathTokenType.STRING_LITERAL_START
            }

            RightSquareBracket -> {
                characters.advance()
                XPathTokenType.SQUARE_CLOSE
            }

            LeftSquareBracket -> {
                characters.advance()
                XPathTokenType.SQUARE_OPEN
            }

            Tilde -> {
                characters.advance()
                XPathTokenType.TYPE_ALIAS
            }

            VerticalLine -> {
                characters.advance()
                if (characters.currentChar == VerticalLine) {
                    characters.advance()
                    XPathTokenType.CONCATENATION
                } else {
                    XPathTokenType.UNION
                }
            }

            in S -> {
                characters.advanceWhile { it in S }
                XPathTokenType.WHITE_SPACE
            }

            XmlCharReader.EndOfBuffer -> null
            else -> {
                characters.advance()
                XPathTokenType.BAD_CHARACTER
            }
        }
    }

    protected open fun stateStringLiteral(type: XmlChar): IElementType? {
        val c = characters.currentChar
        return when {
            c == type -> {
                characters.advance()
                if (characters.currentChar == type && type != RightCurlyBracket) {
                    characters.advance()
                    XPathTokenType.ESCAPED_CHARACTER
                } else {
                    popState()
                    when (type) {
                        RightCurlyBracket -> XPathTokenType.BRACED_URI_LITERAL_END
                        else -> XPathTokenType.STRING_LITERAL_END
                    }
                }
            }

            c == LeftCurlyBracket && type == RightCurlyBracket -> {
                characters.advance()
                XPathTokenType.BAD_CHARACTER
            }

            c == XmlCharReader.EndOfBuffer -> null
            else -> {
                while (
                    characters.currentChar != type &&
                    characters.currentChar != XmlCharReader.EndOfBuffer &&
                    !(type == RightCurlyBracket && characters.currentChar == LeftCurlyBracket)
                ) {
                    characters.advance()
                }
                XPathTokenType.STRING_LITERAL_CONTENTS
            }
        }
    }

    private fun stateDoubleExponent(): IElementType {
        characters.advance()
        if (characters.currentChar == PlusSign || characters.currentChar == HyphenMinus) {
            characters.advance()
        }
        popState()
        return XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT
    }

    private fun stateXQueryComment() {
        when (characters.currentChar) {
            XmlCharReader.EndOfBuffer -> {
                mType = null
                return
            }

            Colon -> {
                if (characters.nextChar == RightParenthesis) {
                    characters.advance()
                    characters.advance()
                    mType = XPathTokenType.COMMENT_END_TAG
                    popState()
                    return
                }
            }
        }

        var depth = 1
        while (true) {
            when (characters.currentChar) {
                XmlCharReader.EndOfBuffer -> {
                    characters.advance()
                    mType = XPathTokenType.COMMENT
                    popState()
                    pushState(STATE_UNEXPECTED_END_OF_BLOCK)
                    return
                }

                LeftParenthesis -> {
                    characters.advance()
                    if (characters.currentChar == Colon) {
                        characters.advance()
                        ++depth
                    }
                }

                Colon -> {
                    val savedOffset = characters.currentOffset
                    characters.advance()
                    if (characters.currentChar == RightParenthesis) {
                        characters.advance()
                        if (--depth == 0) {
                            characters.currentOffset = savedOffset
                            mType = XPathTokenType.COMMENT
                            return
                        }
                    }
                }

                else -> {
                    characters.advance()
                }
            }
        }
    }

    private fun statePragmaPreQName() {
        when (characters.currentChar) {
            in S -> {
                characters.advanceWhile { it in S }
                mType = XPathTokenType.WHITE_SPACE
            }

            Colon -> {
                characters.advance()
                mType = XPathTokenType.QNAME_SEPARATOR
                popState()
                pushState(STATE_PRAGMA_QNAME)
            }

            in NameStartChar -> {
                val pc = characters.currentChar
                characters.advance()
                if (pc == LatinCapitalLetterQ && characters.currentChar == LeftCurlyBracket) {
                    characters.advance()
                    mType = XPathTokenType.BRACED_URI_LITERAL_START
                    popState()
                    pushState(STATE_PRAGMA_QNAME)
                    pushState(STATE_BRACED_URI_LITERAL_PRAGMA)
                } else {
                    characters.advanceWhile { it in NameChar && it != Colon }
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
        when (characters.currentChar) {
            in S -> {
                characters.advanceWhile { it in S }
                mType = XPathTokenType.WHITE_SPACE
                popState()
                pushState(STATE_PRAGMA_CONTENTS)
            }

            Colon -> {
                characters.advance()
                mType = XPathTokenType.QNAME_SEPARATOR
            }

            in NameStartChar -> {
                characters.advanceWhile { it in NameChar && it != Colon }
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
        when (characters.currentChar) {
            XmlCharReader.EndOfBuffer -> {
                mType = null
                return
            }

            NumberSign -> {
                if (characters.nextChar == RightParenthesis) {
                    characters.advance()
                    characters.advance()
                    mType = XPathTokenType.PRAGMA_END
                    popState()
                    return
                }
            }
        }

        while (true) {
            when (characters.currentChar) {
                XmlCharReader.EndOfBuffer -> {
                    characters.advance()
                    mType = XPathTokenType.PRAGMA_CONTENTS
                    popState()
                    pushState(STATE_UNEXPECTED_END_OF_BLOCK)
                    return
                }

                NumberSign -> {
                    if (characters.nextChar == RightParenthesis) {
                        mType = XPathTokenType.PRAGMA_CONTENTS
                        return
                    } else {
                        characters.advance()
                    }
                }

                else -> {
                    characters.advance()
                }
            }
        }
    }

    private fun stateUnexpectedEndOfBlock() {
        mType = XPathTokenType.UNEXPECTED_END_OF_BLOCK
        popState()
    }

    // endregion
    // region Lexer

    override fun advance(state: Int): Unit = when (state) {
        STATE_DEFAULT -> mType = stateDefault(state)
        STATE_STRING_LITERAL_QUOTE -> mType = stateStringLiteral(QuotationMark)
        STATE_STRING_LITERAL_APOSTROPHE -> mType = stateStringLiteral(Apostrophe)
        STATE_DOUBLE_EXPONENT -> mType = stateDoubleExponent()
        STATE_XQUERY_COMMENT -> stateXQueryComment()
        STATE_UNEXPECTED_END_OF_BLOCK -> stateUnexpectedEndOfBlock()
        STATE_PRAGMA_PRE_QNAME -> statePragmaPreQName()
        STATE_PRAGMA_QNAME -> statePragmaQName()
        STATE_PRAGMA_CONTENTS -> statePragmaContents()
        STATE_BRACED_URI_LITERAL -> mType = stateStringLiteral(RightCurlyBracket)
        STATE_BRACED_URI_LITERAL_PRAGMA -> mType = stateStringLiteral(RightCurlyBracket)
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
