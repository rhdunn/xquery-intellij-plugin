/*
 * Copyright (C) 2018-2021 Reece H. Dunn
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
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath

@Suppress("Reformat")
object XPathTokenType {
    // region Symbols

    val ALL_DESCENDANTS_PATH: IElementType = IElementType("XPATH_ALL_DESCENDANTS_PATH_TOKEN", XPath) // XPath 1.0
    val ARROW: IElementType = IElementType("XPATH_ARROW_TOKEN", XPath) // XPath 3.1
    val ASSIGN_EQUAL: IElementType = IElementType("XPATH_ASSIGN_EQUAL_TOKEN", XPath) // XPath 3.0 ; XQuery 1.0
    val ATTRIBUTE_SELECTOR: IElementType = IElementType("XPATH_ATTRIBUTE_SELECTOR_TOKEN", XPath) // XPath 1.0
    val AXIS_SEPARATOR: IElementType = IElementType("XPATH_AXIS_SEPARATOR_TOKEN", XPath) // XPath 2.0
    val BLOCK_CLOSE: IElementType = IElementType("XPATH_BLOCK_CLOSE_TOKEN", XPath) // XPath 3.0 ; XQuery 1.0
    val BLOCK_OPEN: IElementType = IElementType("XPATH_BLOCK_OPEN_TOKEN", XPath) // XPath 3.0 ; XQuery 1.0
    val COMMA: IElementType = IElementType("XPATH_COMMA_TOKEN", XPath) // XPath 1.0
    val CONCATENATION: IElementType = IElementType("XPATH_CONCATENATION", XPath) // XPath 3.0
    val CONTEXT_FUNCTION: IElementType = IElementType("XPATH_CONTEXT_FUNCTION", XPath) // Saxon 10.0
    val DIRECT_DESCENDANTS_PATH: IElementType = IElementType("XPATH_DIRECT_DESCENDANTS_PATH_TOKEN", XPath) // XPath 1.0
    val DOT: IElementType = IElementType("XPATH_DOT_TOKEN", XPath) // XPath 1.0
    val ELLIPSIS: IElementType = IElementType("XQUERY_ELLIPSIS_TOKEN", XPath) // EXPath XPath/XQuery NG Proposal 1
    val ELVIS: IElementType = IElementType("XPATH_ELVIS_TOKEN", XPath) // BaseX 9.1 (XQuery)
    val EQUAL: IElementType = IElementType("XPATH_EQUAL_TOKEN", XPath) // XPath 1.0
    val FUNCTION_REF_OPERATOR: IElementType = IElementType("XPATH_FUNCTION_REF_OPERATOR_TOKEN", XPath) // XPath 3.0
    val GREATER_THAN: IElementType = IElementType("XPATH_GREATER_THAN_TOKEN", XPath) // XPath 1.0
    val GREATER_THAN_OR_EQUAL: IElementType = IElementType("XPATH_GREATER_THAN_OR_EQUAL_TOKEN", XPath) // XPath 1.0
    val LAMBDA_FUNCTION: IElementType = IElementType("XPATH_LAMBDA_FUNCTION", XPath) // Saxon 10.0
    val LESS_THAN: IElementType = IElementType("XPATH_LESS_THAN_TOKEN", XPath) // XPath 1.0
    val LESS_THAN_OR_EQUAL: IElementType = IElementType("XPATH_LESS_THAN_OR_EQUAL_TOKEN", XPath) // XPath 1.0
    val MAP_OPERATOR: IElementType = IElementType("XPATH_MAP_OPERATOR_TOKEN", XPath) // XPath 3.0
    val MINUS: IElementType = IElementType("XPATH_MINUS_TOKEN", XPath) // XPath 1.0
    val NODE_AFTER: IElementType = IElementType("XPATH_NODE_AFTER_TOKEN", XPath) // XPath 2.0
    val NODE_BEFORE: IElementType = IElementType("XPATH_NODE_BEFORE_TOKEN", XPath) // XPath 2.0
    val NOT_EQUAL: IElementType = IElementType("XPATH_NOT_EQUAL_TOKEN", XPath) // XPath 1.0
    val OPTIONAL: IElementType = IElementType("XPATH_OPTIONAL_TOKEN", XPath) // XPath 2.0
    val PARENT_SELECTOR: IElementType = IElementType("XPATH_PARENT_SELECTOR_TOKEN", XPath) // XPath 1.0
    val PARENTHESIS_CLOSE: IElementType = IElementType("XPATH_PARENTHESIS_CLOSE_TOKEN", XPath) // XPath 1.0
    val PARENTHESIS_OPEN: IElementType = IElementType("XPATH_PARENTHESIS_OPEN_TOKEN", XPath) // XPath 1.0
    val PLUS: IElementType = IElementType("XPATH_PLUS_TOKEN", XPath) // XPath 1.0
    val SQUARE_CLOSE: IElementType = IElementType("XPATH_SQUARE_CLOSE_TOKEN", XPath) // XPath 1.0
    val SQUARE_OPEN: IElementType = IElementType("XPATH_SQUARE_OPEN_TOKEN", XPath) // XPath 1.0
    val STAR: IElementType = IElementType("XPATH_STAR_TOKEN", XPath) // XPath 1.0
    val TERNARY_ELSE: IElementType = IElementType("XPATH_TERNARY_ELSE_TOKEN", XPath) // XPath 4.0 ED
    val TERNARY_IF: IElementType = IElementType("XPATH_TERNARY_IF_TOKEN", XPath) // XPath 4.0 ED
    val THIN_ARROW: IElementType = IElementType("XPATH_THIN_ARROW_TOKEN", XPath) // XPath 4.0 ED
    val TYPE_ALIAS: IElementType = IElementType("XPATH_TYPE_ALIAS", XPath) // Saxon 9.8
    val UNION: IElementType = IElementType("XPATH_UNION_TOKEN", XPath) // XPath 1.0
    val VARIABLE_INDICATOR: IElementType = IElementType("XPATH_VARIABLE_INDICATOR_TOKEN", XPath) // XPath 1.0

    // endregion
    // region Keywords

    val K__: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME__", XPath) // Saxon 10.0
    val K_ALL: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ALL", XPath) // Full Text 1.0
    val K_ANCESTOR: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ANCESTOR", XPath) // XPath 1.0
    val K_ANCESTOR_OR_SELF: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ANCESTOR_OR_SELF", XPath) // XPath 1.0
    val K_AND: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_AND", XPath) // XPath 1.0
    val K_ANDALSO: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ANDALSO", XPath) // Saxon 9.9
    val K_ARRAY: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ARRAY", XPath) // XPath 3.1
    val K_ARRAY_NODE: IKeywordOrNCNameType = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ARRAY_NODE", XPath, IKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME) // MarkLogic 8.0
    val K_AS: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_AS", XPath) // XPath 2.0
    val K_AT: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_AT", XPath) // Full Text 1.0; XQuery 1.0
    val K_ATTRIBUTE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ATTRIBUTE", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 1.0
    val K_ANY: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ANY", XPath) // Full Text 1.0
    val K_BOOLEAN_NODE: IKeywordOrNCNameType = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BOOLEAN_NODE", XPath, IKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME) // MarkLogic 8.0
    val K_CASE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_CASE", XPath) // Full Text 1.0; XQuery 1.0
    val K_CAST: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_CAST", XPath) // XPath 2.0
    val K_CASTABLE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_CASTABLE", XPath) // XPath 2.0
    val K_CHILD: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_CHILD", XPath) // XPath 1.0
    val K_COMMENT: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_COMMENT", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 1.0
    val K_CONTAINS: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_CONTAINS", XPath) // Full Text 1.0
    val K_CONTENT: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_CONTENT", XPath) // Full Text 1.0
    val K_CURRENT: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_CURRENT", XPath) // xsl:mode
    val K_DEFAULT: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_DEFAULT", XPath) // Full Text 1.0; XQuery 1.0
    val K_DESCENDANT: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_DESCENDANT", XPath) // XPath 1.0
    val K_DESCENDANT_OR_SELF: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_DESCENDANT_OR_SELF", XPath) // XPath 1.0
    val K_DIACRITICS: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_DIACRITICS", XPath) // Full Text 1.0
    val K_DIFFERENT: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_DIFFERENT", XPath) // Full Text 1.0
    val K_DISTANCE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_DISTANCE", XPath) // Full Text 1.0
    val K_DIV: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_DIV", XPath) // XPath 1.0
    val K_DOCUMENT_NODE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_DOCUMENT_NODE", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 2.0
    val K_ELEMENT: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ELEMENT", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 2.0
    val K_ELSE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ELSE", XPath) // XPath 2.0
    val K_EMPTY: IKeywordOrNCNameType = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EMPTY", XPath) // XPath 2.0 WD 02 May 2003
    val K_EMPTY_SEQUENCE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_EMPTY_SEQUENCE", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 2.0
    val K_END: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_END", XPath) // Full Text 1.0; XQuery 3.0
    val K_ENTIRE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ENTIRE", XPath) // Full Text 1.0
    val K_ENUM: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ENUM", XPath) // XPath 4.0 ED
    val K_EQ: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_EQ", XPath) // XPath 2.0
    val K_EVERY: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_EVERY", XPath) // XPath 2.0
    val K_EXACTLY: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_EXACTLY", XPath) // Full Text 1.0
    val K_EXCEPT: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_EXCEPT", XPath) // XPath 2.0
    val K_FN: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_FN", XPath) // Saxon 9.8
    val K_FOLLOWING: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_FOLLOWING", XPath) // XPath 1.0
    val K_FOLLOWING_SIBLING: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_FOLLOWING_SIBLING", XPath) // XPath 1.0
    val K_FOR: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_FOR", XPath) // XPath 2.0
    val K_FROM: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_FROM", XPath) // Full Text 1.0
    val K_FTAND: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_FTAND", XPath) // Full Text 1.0
    val K_FTNOT: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_FTNOT", XPath) // Full Text 1.0
    val K_FTOR: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_FTOR", XPath) // Full Text 1.0
    val K_FUNCTION: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_FUNCTION", XPath, IKeywordOrNCNameType.KeywordType.XQUERY30_RESERVED_FUNCTION_NAME) // XPath 3.0 ; XQuery 1.0
    val K_GE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_GE", XPath) // XPath 2.0
    val K_GT: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_GT", XPath) // XPath 2.0
    val K_IDIV: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_IDIV", XPath) // XPath 2.0
    val K_IF: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_IF", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 2.0
    val K_IN: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_IN", XPath) // XPath 2.0
    val K_INSENSITIVE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_INSENSITIVE", XPath) // Full Text 1.0
    val K_INSTANCE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_INSTANCE", XPath) // XPath 2.0
    val K_INTERSECT: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_INTERSECT", XPath) // XPath 2.0
    val K_IS: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_IS", XPath) // XPath 2.0
    val K_ITEM: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ITEM", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 2.0
    val K_LANGUAGE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_LANGUAGE", XPath) // Full Text 1.0
    val K_LE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_LE", XPath) // XPath 2.0
    val K_LEAST: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_LEAST", XPath) // Full Text 1.0; XQuery 1.0
    val K_LET: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_LET", XPath) // XPath 3.0 ; XQuery 1.0
    val K_LEVELS: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_LEVELS", XPath) // Full Text 1.0
    val K_LOWERCASE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_LOWERCASE", XPath) // Full Text 1.0
    val K_LT: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_LT", XPath) // XPath 2.0
    val K_MAP: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_MAP", XPath) // XPath 3.1
    val K_MEMBER: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_MEMBER", XPath) // Saxon 10.0
    val K_MOD: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_MOD", XPath) // XPath 1.0
    val K_MOST: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_MOST", XPath) // Full Text 1.0
    val K_NAMESPACE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_NAMESPACE", XPath) // XPath 1.0
    val K_NAMESPACE_NODE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_NAMESPACE_NODE", XPath, IKeywordOrNCNameType.KeywordType.XQUERY30_RESERVED_FUNCTION_NAME) // XPath 3.0
    val K_NE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_NE", XPath) // XPath 2.0
    val K_NO: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_NO", XPath) // Full Text 1.0
    val K_NODE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_NODE", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 1.0
    val K_NOT: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_NOT", XPath) // Full Text 1.0
    val K_NULL_NODE: IKeywordOrNCNameType = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NULL_NODE", XPath, IKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME) // MarkLogic 8.0
    val K_NUMBER_NODE: IKeywordOrNCNameType = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NUMBER_NODE", XPath, IKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME) // MarkLogic 8.0
    val K_OBJECT_NODE: IKeywordOrNCNameType = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_OBJECT_NODE", XPath, IKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME) // MarkLogic 8.0
    val K_OCCURS: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_OCCURS", XPath) // Full Text 1.0
    val K_OF: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_OF", XPath) // XPath 2.0
    val K_OPTION: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_OPTION", XPath) // Full Text 1.0; XQuery 1.0
    val K_OR: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_OR", XPath) // XPath 1.0
    val K_ORDERED: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_ORDERED", XPath) // Full Text 1.0; XQuery 1.0
    val K_ORELSE: IKeywordOrNCNameType = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ORELSE", XPath) // Saxon 9.9
    val K_OTHERWISE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_OTHERWISE", XPath) // Saxon 10.0
    val K_PARAGRAPH: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_PARAGRAPH", XPath) // Full Text 1.0
    val K_PARAGRAPHS: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_PARAGRAPHS", XPath) // Full Text 1.0
    val K_PARENT: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_PARENT", XPath) // XPath 1.0
    val K_PHRASE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_PHRASE", XPath) // Full Text 1.0
    val K_PRECEDING: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_PRECEDING", XPath) // XPath 1.0
    val K_PRECEDING_SIBLING: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_PRECEDING_SIBLING", XPath) // XPath 1.0
    val K_PROCESSING_INSTRUCTION: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_PROCESSING_INSTRUCTION", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 1.0
    val K_PROPERTY: IKeywordOrNCNameType = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PROPERTY", XPath) // MarkLogic 6.0
    val K_RECORD: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_RECORD", XPath) // XPath 4.0 ED
    val K_RELATIONSHIP: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_RELATIONSHIP", XPath) // Full Text 1.0
    val K_RETURN: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_RETURN", XPath) // XPath 2.0
    val K_SAME: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_SAME", XPath) // Full Text 1.0
    val K_SATISFIES: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_SATISFIES", XPath) // XPath 2.0
    val K_SCHEMA_ATTRIBUTE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_SCHEMA_ATTRIBUTE", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 2.0
    val K_SCHEMA_ELEMENT: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_SCHEMA_ELEMENT", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 2.0
    val K_SCORE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_SCORE", XPath) // Full Text 1.0
    val K_SELF: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_SELF", XPath) // XPath 1.0
    val K_SENSITIVE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_SENSITIVE", XPath) // Full Text 1.0
    val K_SENTENCE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_SENTENCE", XPath) // Full Text 1.0
    val K_SENTENCES: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_SENTENCES", XPath) // Full Text 1.0
    val K_SOME: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_SOME", XPath) // XPath 2.0
    val K_START: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_START", XPath) // Full Text 1.0; XQuery 3.0
    val K_STEMMING: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_STEMMING", XPath) // Full Text 1.0
    val K_STOP: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_STOP", XPath) // Full Text 1.0
    val K_TEXT: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_TEXT", XPath, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME) // XPath 1.0
    val K_THEN: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_THEN", XPath) // XPath 2.0
    val K_THESAURUS: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_THESAURUS", XPath) // Full Text 1.0
    val K_TIMES: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_TIMES", XPath) // Full Text 1.0
    val K_TO: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_TO", XPath) // XPath 2.0
    val K_TREAT: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_TREAT", XPath) // XPath 2.0
    val K_TUPLE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_TUPLE", XPath) // Saxon 9.8
    val K_TYPE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_TYPE", XPath) // XQuery 3.0 ; Saxon 10.0
    val K_UNION: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_UNION", XPath) // XPath 2.0
    val K_UNNAMED: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_UNNAMED", XPath) // xsl:default-mode-type
    val K_UPPERCASE: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_UPPERCASE", XPath) // Full Text 1.0
    val K_USING: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_USING", XPath) // Full Text 1.0 ; MarkLogic 6.0
    val K_WEIGHT: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_WEIGHT", XPath) // Full Text 1.0
    val K_WILDCARDS: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_WILDCARDS", XPath) // Full Text 1.0
    val K_WINDOW: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_WINDOW", XPath) // Full Text 1.0; XQuery 3.0
    val K_WITH: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_WITH", XPath) // XPath 4.0 ED; Update Facility 1.0
    val K_WITHOUT: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_WITHOUT", XPath) // Full Text 1.0
    val K_WORD: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_WORD", XPath) // Full Text 1.0
    val K_WORDS: IKeywordOrNCNameType = IKeywordOrNCNameType("XPATH_KEYWORD_OR_NCNAME_WORDS", XPath) // Full Text 1.0

    // endregion
    // region Terminal Symbols

    val INTEGER_LITERAL: IElementType = IElementType("XPATH_INTEGER_LITERAL_TOKEN", XPath) // XPath 1.0 Number ; XPath 2.0
    val DECIMAL_LITERAL: IElementType = IElementType("XPATH_DECIMAL_LITERAL_TOKEN", XPath) // XPath 1.0 Number ; XPath 2.0
    val DOUBLE_LITERAL: IElementType = IElementType("XPATH_DOUBLE_LITERAL_TOKEN", XPath) // XPath 2.0

    val STRING_LITERAL_START: IElementType = IElementType("XPATH_STRING_LITERAL_START_TOKEN", XPath) // XPath 1.0 Literal ; XPath 2.0 StringLiteral
    val STRING_LITERAL_CONTENTS: IElementType = IElementType("XPATH_STRING_LITERAL_CONTENTS_TOKEN", XPath) // XPath 1.0 Literal ; XPath 2.0 StringLiteral
    val STRING_LITERAL_END: IElementType = IElementType("XPATH_STRING_LITERAL_END_TOKEN", XPath) // XPath 1.0 Literal ; XPath 2.0 StringLiteral
    val ESCAPED_CHARACTER: IElementType = IElementType("XPATH_ESCAPED_CHARACTER_TOKEN", XPath) // XPath 2.0 EscapeQuot ; XPath 2.0 EscapeApos ; XQuery 1.0 CommonContent

    val BRACED_URI_LITERAL_START: IElementType = IElementType("XPATH_BRACED_URI_LITERAL_START_TOKEN", XPath) // XPath 3.0
    val BRACED_URI_LITERAL_END: IElementType = IElementType("XPATH_BRACED_URI_LITERAL_END_TOKEN", XPath) // XPath 3.0

    val COMMENT: IElementType = IElementType("XPATH_COMMENT_TOKEN", XPath) // XPath 2.0
    val COMMENT_START_TAG: IElementType = IElementType("XPATH_COMMENT_START_TAG_TOKEN", XPath) // XPath 2.0
    val COMMENT_END_TAG: IElementType = IElementType("XPATH_COMMENT_END_TAG_TOKEN", XPath) // XPath 2.0

    val PRAGMA_CONTENTS: IElementType = IElementType("XPATH_PRAGMA_CONTENTS_TOKEN", XPath) // Full Text 1.0; XQuery 1.0
    val PRAGMA_BEGIN: IElementType = IElementType("XPATH_PRAGMA_BEGIN_TOKEN", XPath) // Full Text 1.0; XQuery 1.0
    val PRAGMA_END: IElementType = IElementType("XPATH_PRAGMA_END_TOKEN", XPath) // Full Text 1.0; XQuery 1.0

    val NCNAME: IElementType = INCNameType("XPATH_NCNAME_TOKEN", XPath) // Namespaces in XML 1.0
    val QNAME_SEPARATOR: IElementType = IElementType("XPATH_QNAME_SEPARATOR_TOKEN", XPath) // Namespaces in XML 1.0

    val WHITE_SPACE: IElementType = TokenType.WHITE_SPACE // XML 1.0

    // endregion
    // region Error Reporting and Recovery

    val PARTIAL_DOUBLE_LITERAL_EXPONENT: IElementType = IElementType("XPATH_PARTIAL_DOUBLE_LITERAL_EXPONENT_TOKEN", XPath)
    val UNEXPECTED_END_OF_BLOCK: IElementType = IElementType("XPATH_UNEXPECTED_END_OF_BLOCK_TOKEN", XPath)

    val BAD_CHARACTER: IElementType = TokenType.BAD_CHARACTER

    // endregion
    // region Token Sets

    val ADDITIVE_EXPR_TOKENS: TokenSet = TokenSet.create(PLUS, MINUS)

    val AND_EXPR_TOKENS: TokenSet = TokenSet.create(K_AND, K_ANDALSO)

    val ARROW_TARGET_TOKENS: TokenSet = TokenSet.create(ARROW, THIN_ARROW)

    val COMMENT_TOKENS: TokenSet = TokenSet.create(COMMENT)

    val COMP_SYMBOL_TOKENS: TokenSet = TokenSet.create(
        EQUAL,
        NOT_EQUAL,
        LESS_THAN,
        LESS_THAN_OR_EQUAL,
        GREATER_THAN,
        GREATER_THAN_OR_EQUAL,
        NODE_BEFORE,
        NODE_AFTER
    )

    val COMPARISON_EXPR_TOKENS: TokenSet = TokenSet.create(
        EQUAL, NOT_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL, // GeneralComp
        K_IS, NODE_BEFORE, NODE_AFTER, // NodeComp
        K_EQ, K_NE, K_LT, K_LE, K_GT, K_GE // ValueComp
    )

    val CURLY_ARRAY_CONSTRUCTOR_TOKENS: TokenSet = TokenSet.create(K_ARRAY, K_ARRAY_NODE)

    val FORWARD_AXIS_TOKENS: TokenSet = TokenSet.create(
        K_ATTRIBUTE,
        K_CHILD,
        K_DESCENDANT,
        K_DESCENDANT_OR_SELF,
        K_FOLLOWING,
        K_FOLLOWING_SIBLING,
        K_NAMESPACE,
        K_PROPERTY,
        K_SELF
    )

    val FTCASE_SENSITIVITY_QUALIFIER_TOKENS: TokenSet = TokenSet.create(K_SENSITIVE, K_INSENSITIVE)

    val FTCONTENT_AT_QUALIFIER_TOKENS: TokenSet = TokenSet.create(K_START, K_END)

    val FTDIACRITICS_SENSITIVITY_QUALIFIER_TOKENS: TokenSet = FTCASE_SENSITIVITY_QUALIFIER_TOKENS

    val FTMATCH_OPTION_START_TOKENS: TokenSet = TokenSet.create(
        K_CASE,
        K_DIACRITICS,
        K_LANGUAGE,
        K_LOWERCASE,
        K_NO,
        K_OPTION,
        K_STEMMING,
        K_STOP,
        K_THESAURUS,
        K_UPPERCASE,
        K_WILDCARDS
    )

    val FTRANGE_AT_QUALIFIER_TOKENS: TokenSet = TokenSet.create(K_LEAST, K_MOST)

    val FTSCOPE_QUALIFIER_TOKENS: TokenSet = TokenSet.create(K_SAME, K_DIFFERENT)

    val FTSTOP_WORDS_INCL_EXCL_QUALIFIER_TOKENS: TokenSet = TokenSet.create(K_UNION, K_EXCEPT)

    val GENERAL_COMP_TOKENS: TokenSet = TokenSet.create(
        EQUAL,
        NOT_EQUAL,
        LESS_THAN,
        LESS_THAN_OR_EQUAL,
        GREATER_THAN,
        GREATER_THAN_OR_EQUAL
    )

    val INLINE_FUNCTION_TOKENS: TokenSet = TokenSet.create(K_FUNCTION, THIN_ARROW)

    val INTERSECT_EXCEPT_EXPR_TOKENS: TokenSet = TokenSet.create(K_INTERSECT, K_EXCEPT)

    val LITERAL_TOKENS: TokenSet = TokenSet.create(
        INTEGER_LITERAL,
        DECIMAL_LITERAL,
        DOUBLE_LITERAL,
        PARTIAL_DOUBLE_LITERAL_EXPONENT
    )

    val MAP_CONSTRUCTOR_TOKENS: TokenSet = TokenSet.create(K_MAP, K_OBJECT_NODE)

    val MAP_ENTRY_SEPARATOR_TOKENS: TokenSet = TokenSet.create(QNAME_SEPARATOR, ASSIGN_EQUAL)

    val MULTIPLICATIVE_EXPR_TOKENS: TokenSet = TokenSet.create(STAR, K_DIV, K_IDIV, K_MOD)

    val NODE_COMP_TOKENS: TokenSet = TokenSet.create(K_IS, NODE_BEFORE, NODE_AFTER)

    val OCCURRENCE_INDICATOR_TOKENS: TokenSet = TokenSet.create(OPTIONAL, STAR, PLUS)

    val OR_EXPR_TOKENS: TokenSet = TokenSet.create(K_OR, K_ORELSE)

    val QUANTIFIED_EXPR_QUALIFIER_TOKENS: TokenSet = TokenSet.create(K_SOME, K_EVERY)

    val RECORD_TEST_TOKENS: TokenSet = TokenSet.create(K_RECORD, K_TUPLE)

    val RELATIVE_PATH_EXPR_TOKENS: TokenSet = TokenSet.create(DIRECT_DESCENDANTS_PATH, ALL_DESCENDANTS_PATH)

    val REVERSE_AXIS_TOKENS: TokenSet = TokenSet.create(
        K_ANCESTOR,
        K_ANCESTOR_OR_SELF,
        K_PARENT,
        K_PRECEDING,
        K_PRECEDING_SIBLING
    )

    val SEQUENCE_TYPE_TOKENS: TokenSet = TokenSet.create(K_EMPTY_SEQUENCE, K_EMPTY)

    val STRING_LITERAL_TOKENS: TokenSet = TokenSet.create(
        STRING_LITERAL_CONTENTS
    )

    val TUPLE_FIELD_SEQUENCE_INDICATOR: TokenSet = TokenSet.create(QNAME_SEPARATOR, K_AS)

    val UNARY_EXPR_TOKENS: TokenSet = ADDITIVE_EXPR_TOKENS

    val UNION_EXPR_TOKENS: TokenSet = TokenSet.create(UNION, K_UNION)

    val VALUE_COMP_TOKENS: TokenSet = TokenSet.create(K_EQ, K_NE, K_LT, K_LE, K_GT, K_GE)

    // endregion
}
