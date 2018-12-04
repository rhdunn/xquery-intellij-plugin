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

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPath

object XPathTokenType {
    // region Symbols

    val ALL_DESCENDANTS_PATH = IElementType("XPATH_ALL_DESCENDANTS_PATH_TOKEN", XPath) // XPath 1.0
    val ARROW = IElementType("XPATH_ARROW_TOKEN", XPath) // XPath 3.1
    val ASSIGN_EQUAL = IElementType("XPATH_ASSIGN_EQUAL_TOKEN", XPath) // XPath 3.0 ; XQuery 1.0
    val ATTRIBUTE_SELECTOR = IElementType("XPATH_ATTRIBUTE_SELECTOR_TOKEN", XPath) // XPath 1.0
    val AXIS_SEPARATOR = IElementType("XPATH_AXIS_SEPARATOR_TOKEN", XPath) // XPath 2.0
    val BLOCK_CLOSE = IElementType("XPATH_BLOCK_CLOSE_TOKEN", XPath) // XPath 3.0 ; XQuery 1.0
    val BLOCK_OPEN = IElementType("XPATH_BLOCK_OPEN_TOKEN", XPath) // XPath 3.0 ; XQuery 1.0
    val COMMA = IElementType("XPATH_COMMA_TOKEN", XPath) // XPath 1.0
    val CONCATENATION = IElementType("XPATH_CONCATENATION", XPath) // XPath 3.0
    val DIRECT_DESCENDANTS_PATH = IElementType("XPATH_DIRECT_DESCENDANTS_PATH_TOKEN", XPath) // XPath 1.0
    val DOT = IElementType("XPATH_DOT_TOKEN", XPath) // XPath 1.0
    val EQUAL = IElementType("XPATH_EQUAL_TOKEN", XPath) // XPath 1.0
    val FUNCTION_REF_OPERATOR = IElementType("XPATH_FUNCTION_REF_OPERATOR_TOKEN", XPath) // XPath 3.0
    val GREATER_THAN = IElementType("XPATH_GREATER_THAN_TOKEN", XPath) // XPath 1.0
    val GREATER_THAN_OR_EQUAL = IElementType("XPATH_GREATER_THAN_OR_EQUAL_TOKEN", XPath) // XPath 1.0
    val LESS_THAN = IElementType("XPATH_LESS_THAN_TOKEN", XPath) // XPath 1.0
    val LESS_THAN_OR_EQUAL = IElementType("XPATH_LESS_THAN_OR_EQUAL_TOKEN", XPath) // XPath 1.0
    val MAP_OPERATOR = IElementType("XPATH_MAP_OPERATOR_TOKEN", XPath) // XPath 3.0
    val MINUS = IElementType("XPATH_MINUS_TOKEN", XPath) // XPath 1.0
    val NODE_AFTER = IElementType("XPATH_NODE_AFTER_TOKEN", XPath) // XPath 2.0
    val NODE_BEFORE = IElementType("XPATH_NODE_BEFORE_TOKEN", XPath) // XPath 2.0
    val NOT_EQUAL = IElementType("XPATH_NOT_EQUAL_TOKEN", XPath) // XPath 1.0
    val OPTIONAL = IElementType("XPATH_OPTIONAL_TOKEN", XPath) // XPath 2.0
    val PARENT_SELECTOR = IElementType("XPATH_PARENT_SELECTOR_TOKEN", XPath) // XPath 1.0
    val PARENTHESIS_CLOSE = IElementType("XPATH_PARENTHESIS_CLOSE_TOKEN", XPath) // XPath 1.0
    val PARENTHESIS_OPEN = IElementType("XPATH_PARENTHESIS_OPEN_TOKEN", XPath) // XPath 1.0
    val PLUS = IElementType("XPATH_PLUS_TOKEN", XPath) // XPath 1.0
    val SQUARE_CLOSE = IElementType("XPATH_SQUARE_CLOSE_TOKEN", XPath) // XPath 1.0
    val SQUARE_OPEN = IElementType("XPATH_SQUARE_OPEN_TOKEN", XPath) // XPath 1.0
    val STAR = IElementType("XPATH_STAR_TOKEN", XPath) // XPath 1.0
    val UNION = IElementType("XPATH_UNION_TOKEN", XPath) // XPath 1.0
    val VARIABLE_INDICATOR = IElementType("XPATH_VARIABLE_INDICATOR_TOKEN", XPath) // XPath 1.0

    // endregion
    // region Keywords

    val K_ANCESTOR = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ANCESTOR", XPath) // XPath 1.0
    val K_ANCESTOR_OR_SELF = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ANCESTOR_OR_SELF", XPath) // XPath 1.0
    val K_AND = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_AND", XPath) // XPath 1.0
    val K_ANDALSO = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ANDALSO", XPath) // Saxon 9.9
    val K_ARRAY = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ARRAY", XPath) // XPath 3.1
    val K_ARRAY_NODE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ARRAY_NODE", XPath, IKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME) // MarkLogic 8.0
    val K_AS = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_AS", XPath) // XPath 2.0
    val K_ATTRIBUTE = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ATTRIBUTE", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 1.0
    val K_BOOLEAN_NODE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BOOLEAN_NODE", XPath, IKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME) // MarkLogic 8.0
    val K_CAST = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_CAST", XPath) // XPath 2.0
    val K_CASTABLE = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_CASTABLE", XPath) // XPath 2.0
    val K_CHILD = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_CHILD", XPath) // XPath 1.0
    val K_COMMENT = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_COMMENT", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 1.0
    val K_DESCENDANT = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_DESCENDANT", XPath) // XPath 1.0
    val K_DESCENDANT_OR_SELF = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_DESCENDANT_OR_SELF", XPath) // XPath 1.0
    val K_DIV = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_DIV", XPath) // XPath 1.0
    val K_DOCUMENT_NODE = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_DOCUMENT_NODE", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 2.0
    val K_ELEMENT = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ELEMENT", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 2.0
    val K_ELSE = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ELSE", XPath) // XPath 2.0
    val K_EMPTY_SEQUENCE = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_EMPTY_SEQUENCE", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 2.0
    val K_EQ = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_EQ", XPath) // XPath 2.0
    val K_EVERY = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_EVERY", XPath) // XPath 2.0
    val K_EXCEPT = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_EXCEPT", XPath) // XPath 2.0
    val K_FOLLOWING = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_FOLLOWING", XPath) // XPath 1.0
    val K_FOLLOWING_SIBLING = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_FOLLOWING_SIBLING", XPath) // XPath 1.0
    val K_FOR = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_FOR", XPath) // XPath 2.0
    val K_FUNCTION = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_FUNCTION", XPath, IKeywordOrNCNameType.KeywordType.XQUERY30_RESERVED_FUNCTION_NAME) // XPath 3.0 ; XQuery 1.0
    val K_GE = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_GE", XPath) // XPath 2.0
    val K_GT = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_GT", XPath) // XPath 2.0
    val K_IDIV = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_IDIV", XPath) // XPath 2.0
    val K_IF = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_IF", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 2.0
    val K_IN = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_IN", XPath) // XPath 2.0
    val K_INSTANCE = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_INSTANCE", XPath) // XPath 2.0
    val K_INTERSECT = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_INTERSECT", XPath) // XPath 2.0
    val K_IS = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_IS", XPath) // XPath 2.0
    val K_ITEM = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ITEM", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 2.0
    val K_LE = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_LE", XPath) // XPath 2.0
    val K_LET = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_LET", XPath) // XPath 3.0 ; XQuery 1.0
    val K_LT = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_LT", XPath) // XPath 2.0
    val K_MAP = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_MAP", XPath) // XPath 3.1
    val K_MOD = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_MOD", XPath) // XPath 1.0
    val K_NAMESPACE = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_NAMESPACE", XPath) // XPath 1.0
    val K_NAMESPACE_NODE = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_NAMESPACE_NODE", XPath, IKeywordOrNCNameType.KeywordType.XQUERY30_RESERVED_FUNCTION_NAME) // XPath 3.0
    val K_NE = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_NE", XPath) // XPath 2.0
    val K_NODE = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_NODE", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 1.0
    val K_NULL_NODE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NULL_NODE", XPath, IKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME) // MarkLogic 8.0
    val K_NUMBER_NODE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NUMBER_NODE", XPath, IKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME) // MarkLogic 8.0
    val K_OBJECT_NODE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_OBJECT_NODE", XPath, IKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME) // MarkLogic 8.0
    val K_OF = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_OF", XPath) // XPath 2.0
    val K_OR = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_OR", XPath) // XPath 1.0
    val K_ORELSE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ORELSE", XPath) // Saxon 9.9
    val K_PARENT = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_PARENT", XPath) // XPath 1.0
    val K_PRECEDING = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_PRECEDING", XPath) // XPath 1.0
    val K_PRECEDING_SIBLING = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_PRECEDING_SIBLING", XPath) // XPath 1.0
    val K_PROCESSING_INSTRUCTION = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_PROCESSING_INSTRUCTION", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 1.0
    val K_PROPERTY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PROPERTY", XPath) // MarkLogic 6.0
    val K_RETURN = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_RETURN", XPath) // XPath 2.0
    val K_SATISFIES = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_SATISFIES", XPath) // XPath 2.0
    val K_SCHEMA_ATTRIBUTE = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_SCHEMA_ATTRIBUTE", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 2.0
    val K_SCHEMA_ELEMENT = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_SCHEMA_ELEMENT", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 2.0
    val K_SELF = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_SELF", XPath) // XPath 1.0
    val K_SOME = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_SOME", XPath) // XPath 2.0
    val K_TEXT = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_TEXT", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 1.0
    val K_THEN = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_THEN", XPath) // XPath 2.0
    val K_TREAT = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_TREAT", XPath) // XPath 2.0
    val K_TO = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_TO", XPath) // XPath 2.0
    val K_UNION = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_UNION", XPath) // XPath 2.0

    // endregion
    // region Terminal Symbols

    val INTEGER_LITERAL = IElementType("XPATH_INTEGER_LITERAL_TOKEN", XPath) // XPath 1.0 Number ; XPath 2.0
    val DECIMAL_LITERAL = IElementType("XPATH_DECIMAL_LITERAL_TOKEN", XPath) // XPath 1.0 Number ; XPath 2.0
    val DOUBLE_LITERAL = IElementType("XPATH_DOUBLE_LITERAL_TOKEN", XPath) // XPath 2.0

    val STRING_LITERAL_START = IElementType("XPATH_DOUBLE_LITERAL_START_TOKEN", XPath) // XPath 1.0 Literal ; XPath 2.0 StringLiteral
    val STRING_LITERAL_CONTENTS = IElementType("XPATH_DOUBLE_LITERAL_CONTENTS_TOKEN", XPath) // XPath 1.0 Literal ; XPath 2.0 StringLiteral
    val STRING_LITERAL_END = IElementType("XPATH_DOUBLE_LITERAL_END_TOKEN", XPath) // XPath 1.0 Literal ; XPath 2.0 StringLiteral
    val ESCAPED_CHARACTER = IElementType("XPATH_ESCAPED_CHARACTER_TOKEN", XPath) // XPath 2.0 EscapeQuot ; XPath 2.0 EscapeApos ; XQuery 1.0 CommonContent

    val BRACED_URI_LITERAL_START = IElementType("XPATH_BRACED_URI_LITERAL_START_TOKEN", XPath) // XPath 3.0
    val BRACED_URI_LITERAL_END = IElementType("XPATH_BRACED_URI_LITERAL_END_TOKEN", XPath) // XPath 3.0

    val COMMENT = IElementType("XPATH_COMMENT_TOKEN", XPath) // XPath 2.0
    val COMMENT_START_TAG = IElementType("XPATH_COMMENT_START_TAG_TOKEN", XPath) // XPath 2.0
    val COMMENT_END_TAG = IElementType("XPATH_COMMENT_END_TAG_TOKEN", XPath) // XPath 2.0

    val NCNAME: IElementType = INCNameType("XPATH_NCNAME_TOKEN", XPath) // Namespaces in XML 1.0
    val QNAME_SEPARATOR = IElementType("XPATH_QNAME_SEPARATOR_TOKEN", XPath) // Namespaces in XML 1.0

    val WHITE_SPACE: IElementType = TokenType.WHITE_SPACE // XML 1.0

    // endregion
    // region Error Reporting and Recovery

    val PARTIAL_DOUBLE_LITERAL_EXPONENT = IElementType("XPATH_PARTIAL_DOUBLE_LITERAL_EXPONENT_TOKEN", XPath)
    val UNEXPECTED_END_OF_BLOCK = IElementType("XPATH_UNEXPECTED_END_OF_BLOCK_TOKEN", XPath)

    val BAD_CHARACTER: IElementType = TokenType.BAD_CHARACTER

    // endregion
}
