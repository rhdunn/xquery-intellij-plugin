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
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import xqt.platform.xml.lexer.*
import xqt.platform.xml.model.XmlChar
import xqt.platform.xml.model.XmlCharReader

@Suppress("DuplicatedCode")
class XQueryLexer : XPathLexer() {
    companion object {
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

    override fun ncnameToKeyword(name: CharSequence): IKeywordOrNCNameType? {
        return KEYWORDS[name] ?: super.ncnameToKeyword(name)
    }

    override fun stateDefault(state: Int): IElementType? = when (characters.currentChar) {
        in S -> {
            characters.advanceWhile { it in S }
            XPathTokenType.WHITE_SPACE
        }

        FullStop -> run {
            var tokenType = XPathTokenType.DECIMAL_LITERAL
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

                LeftCurlyBracket -> {
                    characters.advance()
                    return XPathTokenType.CONTEXT_FUNCTION
                }

                in Digit -> {
                    characters.currentOffset = savedOffset
                }

                else -> {
                    return XPathTokenType.DOT
                }
            }
            characters.advance()
            characters.advanceWhile { it in Digit }
            if (characters.currentChar == LatinSmallLetterE || characters.currentChar == LatinCapitalLetterE) {
                savedOffset = characters.currentOffset
                characters.advance()
                if (characters.currentChar == PlusSign || characters.currentChar == HyphenMinus) {
                    characters.advance()
                }
                if (characters.currentChar in Digit) {
                    characters.advanceWhile { it in Digit }
                    tokenType = XPathTokenType.DOUBLE_LITERAL
                } else {
                    pushState(STATE_DOUBLE_EXPONENT)
                    characters.currentOffset = savedOffset
                }
            }
            tokenType
        }

        in Digit -> {
            characters.advanceWhile { it in Digit }
            var tokenType = if (characters.currentChar == FullStop) {
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
                    tokenType = XPathTokenType.DOUBLE_LITERAL
                } else {
                    pushState(STATE_DOUBLE_EXPONENT)
                    characters.currentOffset = savedOffset
                }
            }
            tokenType
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

        RightParenthesis -> {
            characters.advance()
            XPathTokenType.PARENTHESIS_CLOSE
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

                else -> XPathTokenType.MAP_OPERATOR // XQuery 3.0
            }
        }

        DollarSign -> {
            characters.advance()
            XPathTokenType.VARIABLE_INDICATOR
        }

        Asterisk -> {
            characters.advance()
            XPathTokenType.STAR
        }

        PlusSign -> {
            characters.advance()
            XPathTokenType.PLUS
        }

        Comma -> {
            characters.advance()
            XPathTokenType.COMMA
        }

        HyphenMinus -> {
            characters.advance()
            when (characters.currentChar) {
                HyphenMinus -> {
                    val savedOffset = characters.currentOffset
                    characters.advance()
                    if (characters.currentChar == GreaterThanSign) {
                        characters.advance()
                        XQueryTokenType.XML_COMMENT_END_TAG
                    } else {
                        characters.currentOffset = savedOffset
                        XPathTokenType.MINUS
                    }
                }

                GreaterThanSign -> {
                    characters.advance()
                    XPathTokenType.THIN_ARROW
                }

                else -> XPathTokenType.MINUS
            }
        }

        Semicolon -> {
            characters.advance()
            XQueryTokenType.SEPARATOR
        }

        LessThanSign -> {
            characters.advance()
            when (characters.currentChar) {
                Solidus -> {
                    characters.advance()
                    XQueryTokenType.CLOSE_XML_TAG
                }

                LessThanSign -> {
                    val savedOffset = characters.currentOffset
                    characters.advance()
                    val tokenType = matchOpenXmlTag()
                    if (tokenType === XQueryTokenType.DIRELEM_OPEN_XML_TAG) {
                        // For when adding a DirElemConstructor before another one -- i.e. <<a/>
                        characters.currentOffset = savedOffset
                        XPathTokenType.LESS_THAN
                    } else {
                        characters.currentOffset = savedOffset
                        characters.advance()
                        XPathTokenType.NODE_BEFORE
                    }
                }

                EqualsSign -> {
                    characters.advance()
                    XPathTokenType.LESS_THAN_OR_EQUAL
                }

                QuestionMark -> {
                    characters.advance()
                    pushState(STATE_PROCESSING_INSTRUCTION)
                    XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN
                }

                ExclamationMark -> {
                    characters.advance()
                    if (characters.currentChar == HyphenMinus) {
                        characters.advance()
                        if (characters.currentChar == HyphenMinus) {
                            characters.advance()
                            pushState(STATE_XML_COMMENT)
                            XQueryTokenType.XML_COMMENT_START_TAG
                        } else {
                            XQueryTokenType.INVALID
                        }
                    } else if (characters.currentChar == LeftSquareBracket) {
                        characters.advance()
                        if (characters.currentChar == LatinCapitalLetterC) {
                            characters.advance()
                            if (characters.currentChar == LatinCapitalLetterD) {
                                characters.advance()
                                if (characters.currentChar == LatinCapitalLetterA) {
                                    characters.advance()
                                    if (characters.currentChar == LatinCapitalLetterT) {
                                        characters.advance()
                                        if (characters.currentChar == LatinCapitalLetterA) {
                                            characters.advance()
                                            if (characters.currentChar == LeftSquareBracket) {
                                                characters.advance()
                                                pushState(STATE_CDATA_SECTION)
                                                XQueryTokenType.CDATA_SECTION_START_TAG
                                            } else {
                                                XQueryTokenType.INVALID
                                            }
                                        } else {
                                            XQueryTokenType.INVALID
                                        }
                                    } else {
                                        XQueryTokenType.INVALID
                                    }
                                } else {
                                    XQueryTokenType.INVALID
                                }
                            } else {
                                XQueryTokenType.INVALID
                            }
                        } else {
                            XQueryTokenType.INVALID
                        }
                    } else {
                        XQueryTokenType.INVALID
                    }
                }

                else -> {
                    if (state == STATE_MAYBE_DIR_ELEM_CONSTRUCTOR) {
                        XPathTokenType.LESS_THAN
                    } else {
                        matchOpenXmlTag()
                    }
                }
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

        EqualsSign -> {
            characters.advance()
            if (characters.currentChar == GreaterThanSign) {
                characters.advance()
                XPathTokenType.ARROW
            } else {
                XPathTokenType.EQUAL
            }
        }

        LeftCurlyBracket -> {
            characters.advance()
            pushState(state)
            XPathTokenType.BLOCK_OPEN
        }

        RightCurlyBracket -> {
            characters.advance()
            popState()
            if (characters.currentChar == GraveAccent && state == STATE_DEFAULT_STRING_INTERPOLATION) {
                characters.advance()
                XQueryTokenType.STRING_INTERPOLATION_CLOSE
            } else {
                XPathTokenType.BLOCK_CLOSE
            }
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

        Solidus -> {
            characters.advance()
            when (characters.currentChar) {
                Solidus -> {
                    characters.advance()
                    XPathTokenType.ALL_DESCENDANTS_PATH
                }

                GreaterThanSign -> {
                    characters.advance()
                    XQueryTokenType.SELF_CLOSING_XML_TAG
                }

                else -> XPathTokenType.DIRECT_DESCENDANTS_PATH
            }
        }

        CommercialAt -> {
            characters.advance()
            XPathTokenType.ATTRIBUTE_SELECTOR
        }

        LeftSquareBracket -> {
            characters.advance()
            XPathTokenType.SQUARE_OPEN
        }

        RightSquareBracket -> {
            characters.advance()
            when (characters.currentChar) {
                RightSquareBracket -> {
                    val savedOffset = characters.currentOffset
                    characters.advance()
                    if (characters.currentChar == GreaterThanSign) {
                        characters.advance()
                        XQueryTokenType.CDATA_SECTION_END_TAG
                    } else {
                        characters.currentOffset = savedOffset
                        XPathTokenType.SQUARE_CLOSE
                    }
                }

                GraveAccent -> {
                    characters.advance()
                    if (characters.currentChar == GraveAccent) {
                        characters.advance()
                        XQueryTokenType.STRING_CONSTRUCTOR_END
                    } else {
                        XQueryTokenType.INVALID
                    }
                }

                else -> XPathTokenType.SQUARE_CLOSE
            }
        }

        QuestionMark -> {
            characters.advance()
            when (characters.currentChar) {
                GreaterThanSign -> {
                    characters.advance()
                    XQueryTokenType.PROCESSING_INSTRUCTION_END
                }

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

        PercentSign -> {
            characters.advance()
            XQueryTokenType.ANNOTATION_INDICATOR
        }

        Ampersand -> {
            characters.advance()
            XQueryTokenType.ENTITY_REFERENCE_NOT_IN_STRING
        }

        GraveAccent -> {
            characters.advance()
            if (characters.currentChar == GraveAccent) {
                characters.advance()
                if (characters.currentChar == LeftSquareBracket) {
                    characters.advance()
                    pushState(STATE_STRING_CONSTRUCTOR_CONTENTS)
                    XQueryTokenType.STRING_CONSTRUCTOR_START
                } else {
                    XQueryTokenType.INVALID
                }
            } else {
                XQueryTokenType.INVALID
            }
        }

        Tilde -> {
            characters.advance()
            XPathTokenType.TYPE_ALIAS
        }

        XmlCharReader.EndOfBuffer -> null
        else -> {
            characters.advance()
            XPathTokenType.BAD_CHARACTER
        }
    }

    override fun stateStringLiteral(type: XmlChar): IElementType? {
        var c = characters.currentChar
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

            c == Ampersand -> when (type) {
                QuotationMark -> matchEntityReference(STATE_STRING_LITERAL_QUOTE)
                else -> matchEntityReference(STATE_STRING_LITERAL_APOSTROPHE)
            }

            c == LeftCurlyBracket && type == RightCurlyBracket -> {
                characters.advance()
                XPathTokenType.BAD_CHARACTER
            }

            c == XmlCharReader.EndOfBuffer -> null
            else -> {
                while (
                    c != type &&
                    c != XmlCharReader.EndOfBuffer &&
                    c != Ampersand &&
                    !(type == RightCurlyBracket && c == LeftCurlyBracket)
                ) {
                    characters.advance()
                    c = characters.currentChar
                }
                XPathTokenType.STRING_LITERAL_CONTENTS
            }
        }
    }

    private fun stateXmlComment(): IElementType? {
        when (characters.currentChar) {
            XmlCharReader.EndOfBuffer -> {
                return null
            }

            HyphenMinus -> {
                val savedOffset = characters.currentOffset
                characters.advance()
                if (characters.currentChar == HyphenMinus) {
                    characters.advance()
                    if (characters.currentChar == GreaterThanSign) {
                        characters.advance()
                        popState()
                        return XQueryTokenType.XML_COMMENT_END_TAG
                    } else {
                        characters.currentOffset = savedOffset
                    }
                } else {
                    characters.currentOffset = savedOffset
                }
            }
        }

        while (true) {
            when (characters.currentChar) {
                XmlCharReader.EndOfBuffer -> {
                    characters.advance()
                    popState()
                    pushState(STATE_UNEXPECTED_END_OF_BLOCK)
                    return XQueryTokenType.XML_COMMENT
                }

                HyphenMinus -> {
                    val savedOffset = characters.currentOffset
                    characters.advance()
                    if (characters.currentChar == HyphenMinus) {
                        characters.advance()
                        if (characters.currentChar == GreaterThanSign) {
                            characters.currentOffset = savedOffset
                            return XQueryTokenType.XML_COMMENT
                        }
                    }
                }

                else -> characters.advance()
            }
        }
    }

    private fun stateCDataSection(): IElementType? {
        when (characters.currentChar) {
            XmlCharReader.EndOfBuffer -> {
                return null
            }

            RightSquareBracket -> {
                val savedOffset = characters.currentOffset
                characters.advance()
                if (characters.currentChar == RightSquareBracket) {
                    characters.advance()
                    if (characters.currentChar == GreaterThanSign) {
                        characters.advance()
                        popState()
                        return XQueryTokenType.CDATA_SECTION_END_TAG
                    } else {
                        characters.currentOffset = savedOffset
                    }
                } else {
                    characters.currentOffset = savedOffset
                }
            }
        }

        while (true) {
            when (characters.currentChar) {
                XmlCharReader.EndOfBuffer -> {
                    characters.advance()
                    popState()
                    pushState(STATE_UNEXPECTED_END_OF_BLOCK)
                    return XQueryTokenType.CDATA_SECTION
                }

                RightSquareBracket -> {
                    val savedOffset = characters.currentOffset
                    characters.advance()
                    if (characters.currentChar == RightSquareBracket) {
                        characters.advance()
                        if (characters.currentChar == GreaterThanSign) {
                            characters.currentOffset = savedOffset
                            return XQueryTokenType.CDATA_SECTION
                        }
                    }
                }

                else -> characters.advance()
            }
        }
    }

    private fun stateDirElemConstructor(state: Int): IElementType? = when (characters.currentChar) {
        in S -> {
            characters.advanceWhile { it in S }
            if (state == STATE_DIR_ELEM_CONSTRUCTOR) {
                popState()
                pushState(STATE_DIR_ATTRIBUTE_LIST)
            }
            XQueryTokenType.XML_WHITE_SPACE
        }

        Colon -> {
            characters.advance()
            if (state == STATE_DIR_ATTRIBUTE_LIST)
                XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR
            else
                XQueryTokenType.XML_TAG_QNAME_SEPARATOR
        }

        in NameStartChar -> {
            characters.advanceWhile { it in NameChar && it != Colon }
            if (state == STATE_DIR_ATTRIBUTE_LIST)
                if (tokenText == "xmlns")
                    XQueryTokenType.XML_ATTRIBUTE_XMLNS
                else
                    XQueryTokenType.XML_ATTRIBUTE_NCNAME
            else
                XQueryTokenType.XML_TAG_NCNAME
        }

        GreaterThanSign -> {
            characters.advance()
            popState()
            if (state == STATE_DIR_ELEM_CONSTRUCTOR || state == STATE_DIR_ATTRIBUTE_LIST) {
                pushState(STATE_DIR_ELEM_CONTENT)
            }
            XQueryTokenType.END_XML_TAG
        }

        Solidus -> {
            characters.advance()
            if (characters.currentChar == GreaterThanSign) {
                characters.advance()
                popState()
                XQueryTokenType.SELF_CLOSING_XML_TAG
            } else {
                XQueryTokenType.INVALID
            }
        }

        EqualsSign -> {
            characters.advance()
            XQueryTokenType.XML_EQUAL
        }

        QuotationMark -> {
            characters.advance()
            pushState(STATE_DIR_ATTRIBUTE_VALUE_QUOTE)
            XQueryTokenType.XML_ATTRIBUTE_VALUE_START
        }

        Apostrophe -> {
            characters.advance()
            pushState(STATE_DIR_ATTRIBUTE_VALUE_APOSTROPHE)
            XQueryTokenType.XML_ATTRIBUTE_VALUE_START
        }

        XmlCharReader.EndOfBuffer -> null
        else -> {
            characters.advance()
            XPathTokenType.BAD_CHARACTER
        }
    }

    private fun stateDirAttributeValue(type: XmlChar): IElementType? = when (characters.currentChar) {
        type -> {
            characters.advance()
            if (characters.currentChar == type) {
                characters.advance()
                XQueryTokenType.XML_ESCAPED_CHARACTER
            } else {
                popState()
                XQueryTokenType.XML_ATTRIBUTE_VALUE_END
            }
        }

        LeftCurlyBracket -> {
            characters.advance()
            if (characters.currentChar == LeftCurlyBracket) {
                characters.advance()
                XQueryTokenType.XML_ESCAPED_CHARACTER
            } else {
                when (type) {
                    QuotationMark -> pushState(STATE_DEFAULT_ATTRIBUTE_QUOT)
                    else -> pushState(STATE_DEFAULT_ATTRIBUTE_APOSTROPHE)
                }
                XPathTokenType.BLOCK_OPEN
            }
        }

        RightCurlyBracket -> {
            characters.advance()
            if (characters.currentChar == RightCurlyBracket) {
                characters.advance()
                XQueryTokenType.XML_ESCAPED_CHARACTER
            } else {
                XPathTokenType.BLOCK_CLOSE
            }
        }

        LessThanSign -> {
            characters.advance()
            XPathTokenType.BAD_CHARACTER
        }

        Ampersand -> when (type) {
            QuotationMark -> matchEntityReference(STATE_DIR_ATTRIBUTE_VALUE_QUOTE)
            else -> matchEntityReference(STATE_DIR_ATTRIBUTE_VALUE_APOSTROPHE)
        }

        XmlCharReader.EndOfBuffer -> null
        else -> run {
            while (true) {
                when (characters.currentChar) {
                    XmlCharReader.EndOfBuffer, LeftCurlyBracket, RightCurlyBracket, LessThanSign, Ampersand -> {
                        return XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS
                    }

                    type -> {
                        return XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS
                    }

                    else -> characters.advance()
                }
            }
            @Suppress("UNREACHABLE_CODE") null
        }
    }

    private fun stateDirElemContent(): IElementType? = when (characters.currentChar) {
        LeftCurlyBracket -> {
            characters.advance()
            if (characters.currentChar == LeftCurlyBracket) {
                characters.advance()
                XPathTokenType.ESCAPED_CHARACTER
            } else {
                pushState(STATE_DEFAULT_ELEM_CONTENT)
                XPathTokenType.BLOCK_OPEN
            }
        }

        RightCurlyBracket -> {
            characters.advance()
            if (characters.currentChar == RightCurlyBracket) {
                characters.advance()
                XPathTokenType.ESCAPED_CHARACTER
            } else {
                XPathTokenType.BLOCK_CLOSE
            }
        }

        LessThanSign -> {
            characters.advance()
            when (characters.currentChar) {
                Solidus -> {
                    characters.advance()
                    popState()
                    pushState(STATE_DIR_ELEM_CONSTRUCTOR_CLOSING)
                    XQueryTokenType.CLOSE_XML_TAG
                }

                in NameStartChar -> {
                    pushState(STATE_DIR_ELEM_CONSTRUCTOR)
                    XQueryTokenType.OPEN_XML_TAG
                }

                QuestionMark -> {
                    characters.advance()
                    pushState(STATE_PROCESSING_INSTRUCTION_ELEM_CONTENT)
                    XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN
                }

                ExclamationMark -> {
                    characters.advance()
                    if (characters.currentChar == HyphenMinus) {
                        characters.advance()
                        if (characters.currentChar == HyphenMinus) {
                            characters.advance()
                            pushState(STATE_XML_COMMENT_ELEM_CONTENT)
                            XQueryTokenType.XML_COMMENT_START_TAG
                        } else {
                            XQueryTokenType.INVALID
                        }
                    } else if (characters.currentChar == LeftSquareBracket) {
                        characters.advance()
                        if (characters.currentChar == LatinCapitalLetterC) {
                            characters.advance()
                            if (characters.currentChar == LatinCapitalLetterD) {
                                characters.advance()
                                if (characters.currentChar == LatinCapitalLetterA) {
                                    characters.advance()
                                    if (characters.currentChar == LatinCapitalLetterT) {
                                        characters.advance()
                                        if (characters.currentChar == LatinCapitalLetterA) {
                                            characters.advance()
                                            if (characters.currentChar == LeftSquareBracket) {
                                                characters.advance()
                                                pushState(STATE_CDATA_SECTION_ELEM_CONTENT)
                                                XQueryTokenType.CDATA_SECTION_START_TAG
                                            } else {
                                                XQueryTokenType.INVALID
                                            }
                                        } else {
                                            XQueryTokenType.INVALID
                                        }
                                    } else {
                                        XQueryTokenType.INVALID
                                    }
                                } else {
                                    XQueryTokenType.INVALID
                                }
                            } else {
                                XQueryTokenType.INVALID
                            }
                        } else {
                            XQueryTokenType.INVALID
                        }
                    } else {
                        XQueryTokenType.INVALID
                    }
                }

                else -> XPathTokenType.BAD_CHARACTER
            }
        }

        Ampersand -> matchEntityReference(STATE_DIR_ELEM_CONTENT)
        XmlCharReader.EndOfBuffer -> null
        else -> run {
            while (true) {
                when (characters.currentChar) {
                    XmlCharReader.EndOfBuffer, LeftCurlyBracket, RightCurlyBracket, LessThanSign, Ampersand -> {
                        return XQueryTokenType.XML_ELEMENT_CONTENTS
                    }

                    else -> characters.advance()
                }
            }
            @Suppress("UNREACHABLE_CODE") null
        }
    }

    private fun stateProcessingInstruction(state: Int): IElementType? = when (characters.currentChar) {
        in S -> {
            characters.advanceWhile { it in S }
            popState()
            when (state) {
                STATE_PROCESSING_INSTRUCTION -> pushState(STATE_PROCESSING_INSTRUCTION_CONTENTS)
                else -> pushState(STATE_PROCESSING_INSTRUCTION_CONTENTS_ELEM_CONTENT)
            }
            XQueryTokenType.XML_WHITE_SPACE
        }

        Colon -> {
            characters.advance()
            XQueryTokenType.XML_TAG_QNAME_SEPARATOR
        }

        in NameStartChar -> {
            characters.advanceWhile { it in NameChar && it != Colon }
            XQueryTokenType.XML_PI_TARGET_NCNAME
        }

        QuestionMark -> {
            characters.advance()
            if (characters.currentChar == GreaterThanSign) {
                characters.advance()
                popState()
                XQueryTokenType.PROCESSING_INSTRUCTION_END
            } else {
                XQueryTokenType.INVALID
            }
        }

        XmlCharReader.EndOfBuffer -> null
        else -> {
            characters.advance()
            XPathTokenType.BAD_CHARACTER
        }
    }

    private fun stateProcessingInstructionContents(): IElementType? {
        when (characters.currentChar) {
            XmlCharReader.EndOfBuffer -> {
                return null
            }

            QuestionMark -> {
                if (characters.nextChar == GreaterThanSign) {
                    characters.advance()
                    characters.advance()
                    popState()
                    return XQueryTokenType.PROCESSING_INSTRUCTION_END
                }
            }
        }

        while (true) {
            when (characters.currentChar) {
                XmlCharReader.EndOfBuffer -> {
                    characters.advance()
                    popState()
                    pushState(STATE_UNEXPECTED_END_OF_BLOCK)
                    return XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS
                }

                QuestionMark -> {
                    if (characters.nextChar == GreaterThanSign) {
                        return XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS
                    } else {
                        characters.advance()
                    }
                }

                else -> characters.advance()
            }
        }
    }

    private fun stateStringConstructorContents(): IElementType {
        when (characters.currentChar) {
            GraveAccent -> {
                val savedOffset = characters.currentOffset
                characters.advance()
                if (characters.currentChar == LeftCurlyBracket) {
                    characters.advance()
                    pushState(STATE_DEFAULT_STRING_INTERPOLATION)
                    return XQueryTokenType.STRING_INTERPOLATION_OPEN
                } else {
                    characters.currentOffset = savedOffset
                }
            }
        }
        while (characters.currentChar != XmlCharReader.EndOfBuffer) {
            when (characters.currentChar) {
                RightSquareBracket -> {
                    val savedOffset = characters.currentOffset
                    characters.advance()
                    if (characters.currentChar == GraveAccent) {
                        characters.advance()
                        if (characters.currentChar == GraveAccent) {
                            characters.currentOffset = savedOffset
                            popState()
                            return XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS
                        }
                    }
                }

                GraveAccent -> {
                    val savedOffset = characters.currentOffset
                    characters.advance()
                    if (characters.currentChar == LeftCurlyBracket) {
                        characters.currentOffset = savedOffset
                        return XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS
                    } else {
                        characters.currentOffset = savedOffset
                    }
                }
            }
            characters.advance()
        }
        popState()
        pushState(STATE_UNEXPECTED_END_OF_BLOCK)
        return XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS
    }

    private fun stateStartDirElemConstructor(): IElementType? = when (characters.currentChar) {
        LessThanSign -> {
            characters.advance()
            if (characters.currentChar !in S) {
                popState()
                pushState(STATE_DIR_ELEM_CONSTRUCTOR)
            }
            XQueryTokenType.OPEN_XML_TAG
        }

        in S -> {
            characters.advanceWhile { it in S }
            popState()
            pushState(STATE_DIR_ELEM_CONSTRUCTOR)
            XQueryTokenType.XML_WHITE_SPACE
        }

        else -> null
    }

    private fun matchNCName(): Boolean {
        if (characters.currentChar !in NameStartChar)
            return false

        characters.advanceWhile { it in NameChar && it != Colon }
        return true
    }

    private fun matchQName(): Boolean {
        if (!matchNCName())
            return false

        if (characters.currentChar == Colon) {
            characters.advance()
            matchNCName()
        }
        return true
    }

    private fun matchOpenXmlTag(): IElementType {
        // Whitespace between the '<' and the NCName/QName is invalid. The lexer
        // allows this to provide better error reporting in the parser.
        val savedOffset = characters.currentOffset
        characters.advanceWhile { it in S }

        if (!matchQName()) {
            characters.currentOffset = savedOffset
            return XPathTokenType.LESS_THAN
        }

        characters.advanceWhile { it in S }

        return when (characters.currentChar) {
            Solidus -> {
                if (characters.nextChar == GreaterThanSign) {
                    characters.advance()
                    characters.advance()
                    return XQueryTokenType.DIRELEM_OPEN_XML_TAG
                }
                XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG
            }

            GreaterThanSign -> {
                characters.advance()
                pushState(STATE_DIR_ELEM_CONTENT)
                XQueryTokenType.DIRELEM_OPEN_XML_TAG
            }

            in NameStartChar -> {
                pushState(STATE_DIR_ATTRIBUTE_LIST)
                XQueryTokenType.DIRELEM_OPEN_XML_TAG
            }

            else -> XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG
        }
    }

    private fun matchEntityReference(state: Int): IElementType = when (state) {
        STATE_DIR_ATTRIBUTE_VALUE_QUOTE, STATE_DIR_ATTRIBUTE_VALUE_APOSTROPHE -> when (characters.matchEntityReference()) {
            EntityReferenceType.CharacterReference -> XQueryTokenType.XML_CHARACTER_REFERENCE
            EntityReferenceType.EmptyEntityReference -> XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE
            EntityReferenceType.PartialEntityReference -> XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE
            else -> XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE
        }

        else -> when (characters.matchEntityReference()) {
            EntityReferenceType.CharacterReference -> XQueryTokenType.CHARACTER_REFERENCE
            EntityReferenceType.EmptyEntityReference -> XQueryTokenType.EMPTY_ENTITY_REFERENCE
            EntityReferenceType.PartialEntityReference -> XQueryTokenType.PARTIAL_ENTITY_REFERENCE
            else -> XQueryTokenType.PREDEFINED_ENTITY_REFERENCE
        }
    }

    override fun advance(state: Int): IElementType? = when (state) {
        STATE_DEFAULT -> stateDefault(state)
        STATE_DEFAULT_ATTRIBUTE_QUOT -> stateDefault(state)
        STATE_DEFAULT_ATTRIBUTE_APOSTROPHE -> stateDefault(state)
        STATE_DEFAULT_ELEM_CONTENT -> stateDefault(state)
        STATE_DEFAULT_STRING_INTERPOLATION -> stateDefault(state)
        STATE_MAYBE_DIR_ELEM_CONSTRUCTOR -> stateDefault(state)
        STATE_XML_COMMENT -> stateXmlComment()
        STATE_XML_COMMENT_ELEM_CONTENT -> stateXmlComment()
        STATE_CDATA_SECTION -> stateCDataSection()
        STATE_CDATA_SECTION_ELEM_CONTENT -> stateCDataSection()
        STATE_DIR_ELEM_CONSTRUCTOR -> stateDirElemConstructor(state)
        STATE_DIR_ELEM_CONSTRUCTOR_CLOSING -> stateDirElemConstructor(state)
        STATE_DIR_ATTRIBUTE_LIST -> stateDirElemConstructor(state)
        STATE_DIR_ATTRIBUTE_VALUE_QUOTE -> stateDirAttributeValue(QuotationMark)
        STATE_DIR_ATTRIBUTE_VALUE_APOSTROPHE -> stateDirAttributeValue(Apostrophe)
        STATE_DIR_ELEM_CONTENT -> stateDirElemContent()
        STATE_PROCESSING_INSTRUCTION -> stateProcessingInstruction(state)
        STATE_PROCESSING_INSTRUCTION_ELEM_CONTENT -> stateProcessingInstruction(state)
        STATE_PROCESSING_INSTRUCTION_CONTENTS -> stateProcessingInstructionContents()
        STATE_PROCESSING_INSTRUCTION_CONTENTS_ELEM_CONTENT -> stateProcessingInstructionContents()
        STATE_STRING_CONSTRUCTOR_CONTENTS -> stateStringConstructorContents()
        STATE_START_DIR_ELEM_CONSTRUCTOR -> stateStartDirElemConstructor()
        else -> super.advance(state)
    }
}
