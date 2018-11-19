/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocTokenType
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xpath.lexer.INCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

object XQueryTokenType {
    val XML_COMMENT = IElementType("XQUERY_XML_COMMENT_TOKEN", XQuery)
    val XML_COMMENT_START_TAG = IElementType("XQUERY_XML_COMMENT_START_TAG_TOKEN", XQuery)
    val XML_COMMENT_END_TAG = IElementType("XQUERY_XML_COMMENT_END_TAG_TOKEN", XQuery)

    val PROCESSING_INSTRUCTION_BEGIN = IElementType("XQUERY_PROCESSING_INSTRUCTION_BEGIN_TOKEN", XQuery)
    val PROCESSING_INSTRUCTION_END = IElementType("XQUERY_PROCESSING_INSTRUCTION_END_TOKEN", XQuery)
    val PROCESSING_INSTRUCTION_CONTENTS = IElementType("XQUERY_PROCESSING_INSTRUCTION_CONTENTS_TOKEN", XQuery)

    val DIRELEM_MAYBE_OPEN_XML_TAG = IElementType("XQUERY_DIRELEM_MAYBE_OPEN_XML_TAG_TOKEN", XQuery)
    val DIRELEM_OPEN_XML_TAG = IElementType("XQUERY_DIRELEM_OPEN_XML_TAG_TOKEN", XQuery)

    val OPEN_XML_TAG = IElementType("XQUERY_OPEN_XML_TAG_TOKEN", XQuery)
    val END_XML_TAG = IElementType("XQUERY_END_XML_TAG_TOKEN", XQuery)
    val CLOSE_XML_TAG = IElementType("XQUERY_CLOSE_XML_TAG_TOKEN", XQuery)
    val SELF_CLOSING_XML_TAG = IElementType("XQUERY_SELF_CLOSING_XML_TAG_TOKEN", XQuery)
    val XML_ELEMENT_CONTENTS = IElementType("XQUERY_XML_ELEMENT_CONTENTS_TOKEN", XQuery)
    val XML_EQUAL = IElementType("XQUERY_XML_EQUAL_TOKEN", XQuery)
    val XML_WHITE_SPACE = IElementType("XQUERY_XML_WHITE_SPACE_TOKEN", XQuery)
    val XML_TAG_NCNAME: IElementType = INCNameType("XQUERY_XML_TAG_NCNAME_TOKEN", XQuery)
    val XML_TAG_QNAME_SEPARATOR = IElementType("XQUERY_XML_TAG_QNAME_SEPARATOR_TOKEN", XQuery)
    val XML_ATTRIBUTE_NCNAME: IElementType = INCNameType("XQUERY_XML_ATTRIBUTE_NCNAME_TOKEN", XQuery)
    val XML_ATTRIBUTE_QNAME_SEPARATOR = IElementType("XQUERY_XML_ATTRIBUTE_QNAME_SEPARATOR_TOKEN", XQuery)

    val XML_ATTRIBUTE_VALUE_START = IElementType("XQUERY_XML_ATTRIBUTE_VALUE_START_TOKEN", XQuery)
    val XML_ATTRIBUTE_VALUE_CONTENTS = IElementType("XQUERY_XML_ATTRIBUTE_VALUE_CONTENTS_TOKEN", XQuery)
    val XML_ATTRIBUTE_VALUE_END = IElementType("XQUERY_XML_ATTRIBUTE_VALUE_END_TOKEN", XQuery)
    val XML_ESCAPED_CHARACTER = IElementType("XQUERY_XML_ESCAPED_CHARACTER_TOKEN", XQuery)

    val XML_CHARACTER_REFERENCE = IElementType("XQUERY_XML_CHARACTER_REFERENCE_TOKEN", XQuery)
    val XML_PREDEFINED_ENTITY_REFERENCE = IElementType("XQUERY_XML_PREDEFINED_ENTITY_REFERENCE_TOKEN", XQuery)
    val XML_PARTIAL_ENTITY_REFERENCE = IElementType("XQUERY_XML_PARTIAL_ENTITY_REFERENCE_TOKEN", XQuery)
    val XML_EMPTY_ENTITY_REFERENCE = IElementType("XQUERY_XML_EMPTY_ENTITY_REFERENCE_TOKEN", XQuery)

    val CDATA_SECTION = IElementType("XQUERY_CDATA_SECTION_TOKEN", XQuery)
    val CDATA_SECTION_START_TAG = IElementType("XQUERY_CDATA_SECTION_START_TAG_TOKEN", XQuery)
    val CDATA_SECTION_END_TAG = IElementType("XQUERY_CDATA_SECTION_END_TAG_TOKEN", XQuery)

    val PRAGMA_CONTENTS = IElementType("XQUERY_PRAGMA_CONTENTS_TOKEN", XQuery)
    val PRAGMA_BEGIN = IElementType("XQUERY_PRAGMA_BEGIN_TOKEN", XQuery)
    val PRAGMA_END = IElementType("XQUERY_PRAGMA_END_TOKEN", XQuery)

    val CHARACTER_REFERENCE = IElementType("XQUERY_CHARACTER_REFERENCE_TOKEN", XQuery)
    val PREDEFINED_ENTITY_REFERENCE = IElementType("XQUERY_PREDEFINED_ENTITY_REFERENCE_TOKEN", XQuery)
    val PARTIAL_ENTITY_REFERENCE = IElementType("XQUERY_PARTIAL_ENTITY_REFERENCE_TOKEN", XQuery)
    val ENTITY_REFERENCE_NOT_IN_STRING = IElementType("XQUERY_ENTITY_REFERENCE_NOT_IN_STRING_TOKEN", XQuery)
    val EMPTY_ENTITY_REFERENCE = IElementType("XQUERY_EMPTY_ENTITY_REFERENCE_TOKEN", XQuery)

    val INVALID = IElementType("XQUERY_INVALID_TOKEN", XQuery)
    val SEPARATOR = IElementType("XQUERY_SEPARATOR_TOKEN", XQuery)
    val BLOCK_OPEN = IElementType("XQUERY_BLOCK_OPEN_TOKEN", XQuery)
    val BLOCK_CLOSE = IElementType("XQUERY_BLOCK_CLOSE_TOKEN", XQuery)
    val ASSIGN_EQUAL = IElementType("XQUERY_ASSIGN_EQUAL_TOKEN", XQuery)

    val CONCATENATION = IElementType("XQUERY_CONCATENATION", XQuery) // XQuery 3.0
    val MAP_OPERATOR = IElementType("XQUERY_MAP_OPERATOR_TOKEN", XQuery) // XQuery 3.0
    val FUNCTION_REF_OPERATOR = IElementType("XQUERY_FUNCTION_REF_OPERATOR_TOKEN", XQuery) // XQuery 3.0
    val ANNOTATION_INDICATOR = IElementType("XQUERY_ANNOTATION_INDICATOR_TOKEN", XQuery) // XQuery 3.0

    val ARROW = IElementType("XQUERY_ARROW_TOKEN", XQuery) // XQuery 3.1
    val STRING_CONSTRUCTOR_START = IElementType("XQUERY_STRING_CONSTRUCTOR_START_TOKEN", XQuery) // XQuery 3.1
    val STRING_CONSTRUCTOR_CONTENTS = IElementType("XQUERY_STRING_CONSTRUCTOR_CONTENTS_TOKEN", XQuery) // XQuery 3.1
    val STRING_CONSTRUCTOR_END = IElementType("XQUERY_STRING_CONSTRUCTOR_END_TOKEN", XQuery) // XQuery 3.1
    val STRING_INTERPOLATION_OPEN = IElementType("XQUERY_STRING_INTERPOLATION_OPEN_TOKEN", XQuery) // XQuery 3.1
    val STRING_INTERPOLATION_CLOSE = IElementType("XQUERY_STRING_INTERPOLATION_CLOSE_TOKEN", XQuery) // XQuery 3.1

    val ELLIPSIS = IElementType("XQUERY_ELLIPSIS_TOKEN", XQuery) // EXPath XPath/XQuery NG Proposal 1
    val TERNARY_IF = IElementType("XQUERY_TERNARY_IF_TOKEN", XQuery) // EXPath XPath/XQuery NG Proposal 2
    val TERNARY_ELSE = IElementType("XQUERY_TERNARY_ELSE_TOKEN", XQuery) // EXPath XPath/XQuery NG Proposal 2
    val ELVIS = IElementType("XQUERY_ELVIS_TOKEN", XQuery) // EXPath XPath/XQuery NG Proposal 2

    val K_AFTER = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_AFTER", XQuery) // Update Facility 1.0
    val K_ALL = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ALL", XQuery) // Full Text 1.0
    val K_ALLOWING = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ALLOWING", XQuery) // XQuery 3.0
    val K_ANDALSO = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ANDALSO", XQuery) // Saxon 9.9
    val K_ANY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ANY", XQuery) // Full Text 1.0
    val K_ARRAY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ARRAY", XQuery) // XQuery 3.1
    val K_ARRAY_NODE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ARRAY_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME) // MarkLogic 8.0
    val K_ASCENDING = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ASCENDING", XQuery)
    val K_ASSIGNABLE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ASSIGNABLE", XQuery) // Scripting Extension 1.0
    val K_AT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_AT", XQuery)
    val K_ATTRIBUTE_DECL = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ATTRIBUTE_DECL_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME) // MarkLogic 7.0
    val K_BASE_URI = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BASE_URI", XQuery)
    val K_BEFORE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BEFORE", XQuery) // Update Facility 1.0
    val K_BINARY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BINARY", XQuery) // MarkLogic 6.0
    val K_BLOCK = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BLOCK", XQuery) // Scripting Extension 1.0
    val K_BOOLEAN_NODE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BOOLEAN_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME) // MarkLogic 8.0
    val K_BOUNDARY_SPACE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BOUNDARY_SPACE", XQuery)
    val K_BY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BY", XQuery)
    val K_CASE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_CASE", XQuery)
    val K_CATCH = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_CATCH", XQuery) // XQuery 3.0
    val K_COLLATION = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_COLLATION", XQuery)
    val K_COMPLEX_TYPE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_COMPLEX_TYPE_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME) // MarkLogic 7.0
    val K_CONSTRUCTION = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_CONSTRUCTION", XQuery)
    val K_CONTAINS = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_CONTAINS", XQuery) // Full Text 1.0
    val K_CONTENT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_CONTENT", XQuery) // Full Text 1.0
    val K_CONTEXT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_CONTEXT", XQuery) // XQuery 3.0
    val K_COPY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_COPY", XQuery) // Update Facility 1.0
    val K_COPY_NAMESPACES = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_COPY_NAMESPACES", XQuery)
    val K_COUNT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_COUNT", XQuery) // XQuery 3.0
    val K_DECIMAL_FORMAT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DECIMAL_FORMAT", XQuery) // XQuery 3.0
    val K_DECIMAL_SEPARATOR = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DECIMAL_SEPARATOR", XQuery) // XQuery 3.0
    val K_DECLARE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DECLARE", XQuery)
    val K_DEFAULT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DEFAULT", XQuery)
    val K_DELETE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DELETE", XQuery) // Update Facility 1.0
    val K_DESCENDING = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DESCENDING", XQuery)
    val K_DIACRITICS = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DIACRITICS", XQuery) // Full Text 1.0
    val K_DIFFERENT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DIFFERENT", XQuery) // Full Text 1.0
    val K_DIGIT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DIGIT", XQuery) // XQuery 3.0
    val K_DISTANCE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DISTANCE", XQuery) // Full Text 1.0
    val K_DOCUMENT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DOCUMENT", XQuery)
    val K_ELEMENT_DECL = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ELEMENT_DECL_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME) // MarkLogic 7.0
    val K_EMPTY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EMPTY", XQuery)
    val K_ENCODING = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ENCODING", XQuery)
    val K_END = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_END", XQuery) // XQuery 3.0
    val K_ENTIRE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ENTIRE", XQuery) // Full Text 1.0
    val K_EXACTLY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EXACTLY", XQuery) // Full Text 1.0
    val K_EXIT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EXIT", XQuery) // Scripting Extension 1.0
    val K_EXPONENT_SEPARATOR = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EXPONENT_SEPARATOR", XQuery) // XQuery 3.1
    val K_EXTERNAL = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EXTERNAL", XQuery)
    val K_FIRST = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FIRST", XQuery) // Update Facility 1.0
    val K_FN = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FN", XQuery) // Saxon 9.8
    val K_FROM = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FROM", XQuery) // Full Text 1.0
    val K_FT_OPTION = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FT_OPTION", XQuery) // Full Text 1.0
    val K_FTAND = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FTAND", XQuery) // Full Text 1.0
    val K_FTNOT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FTNOT", XQuery) // Full Text 1.0
    val K_FTOR = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FTOR", XQuery) // Full Text 1.0
    val K_FUNCTION = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FUNCTION", XQuery, IKeywordOrNCNameType.KeywordType.XQUERY30_RESERVED_FUNCTION_NAME)
    val K_FUZZY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FUZZY", XQuery) // BaseX 6.1
    val K_GREATEST = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_GREATEST", XQuery)
    val K_GROUP = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_GROUP", XQuery) // XQuery 3.0
    val K_GROUPING_SEPARATOR = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_GROUPING_SEPARATOR", XQuery) // XQuery 3.0
    val K_IMPORT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_IMPORT", XQuery)
    val K_INFINITY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INFINITY", XQuery) // XQuery 3.0
    val K_INHERIT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INHERIT", XQuery)
    val K_INSENSITIVE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INSENSITIVE", XQuery) // Full Text 1.0
    val K_INSERT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INSERT", XQuery) // Update Facility 1.0
    val K_INTO = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INTO", XQuery) // Update Facility 1.0
    val K_INVOKE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INVOKE", XQuery) // Update Facility 3.0
    val K_LANGUAGE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_LANGUAGE", XQuery) // Full Text 1.0
    val K_LAST = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_LAST", XQuery) // Update Facility 1.0
    val K_LAX = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_LAX", XQuery)
    val K_LEAST = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_LEAST", XQuery)
    val K_LET = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_LET", XQuery)
    val K_LEVELS = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_LEVELS", XQuery) // Full Text 1.0
    val K_LOWERCASE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_LOWERCASE", XQuery) // Full Text 1.0
    val K_MAP = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_MAP", XQuery) // XQuery 3.1
    val K_MINUS_SIGN = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_MINUS_SIGN", XQuery) // XQuery 3.0
    val K_MODIFY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_MODIFY", XQuery) // Update Facility 1.0
    val K_MODULE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_MODULE", XQuery)
    val K_MOST = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_MOST", XQuery) // Full Text 1.0
    val K_NAMESPACE_NODE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NAMESPACE_NODE", XQuery, IKeywordOrNCNameType.KeywordType.XQUERY30_RESERVED_FUNCTION_NAME) // XQuery 3.0
    val K_NAN = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NAN", XQuery) // XQuery 3.0
    val K_NEXT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NEXT", XQuery) // XQuery 3.0
    val K_NO = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NO", XQuery) // Full Text 1.0
    val K_NO_INHERIT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NO_INHERIT", XQuery)
    val K_NO_PRESERVE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NO_PRESERVE", XQuery)
    val K_NODES = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NODES", XQuery) // Update Facility 1.0
    val K_NON_DETERMINISTIC = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NON_DETERMINISTIC", XQuery) // BaseX 8.4
    val K_NOT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NOT", XQuery) // Full Text 1.0
    val K_NULL_NODE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NULL_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME) // MarkLogic 8.0
    val K_NUMBER_NODE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NUMBER_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME) // MarkLogic 8.0
    val K_OBJECT_NODE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_OBJECT_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME) // MarkLogic 8.0
    val K_OCCURS = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_OCCURS", XQuery) // Full Text 1.0
    val K_ONLY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ONLY", XQuery) // XQuery 3.0
    val K_OPTION = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_OPTION", XQuery)
    val K_ORDER = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ORDER", XQuery)
    val K_ORDERED = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ORDERED", XQuery)
    val K_ORDERING = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ORDERING", XQuery)
    val K_ORELSE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ORELSE", XQuery) // Saxon 9.9
    val K_PARAGRAPH = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PARAGRAPH", XQuery) // Full Text 1.0
    val K_PARAGRAPHS = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PARAGRAPHS", XQuery) // Full Text 1.0
    val K_PATTERN_SEPARATOR = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PATTERN_SEPARATOR", XQuery) // XQuery 3.0
    val K_PER_MILLE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PER_MILLE", XQuery) // XQuery 3.0
    val K_PERCENT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PERCENT", XQuery) // XQuery 3.0
    val K_PHRASE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PHRASE", XQuery) // Full Text 1.0
    val K_PRESERVE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PRESERVE", XQuery)
    val K_PRIVATE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PRIVATE", XQuery) // MarkLogic 6.0
    val K_PREVIOUS = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PREVIOUS", XQuery) // XQuery 3.0
    val K_PROPERTY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PROPERTY", XQuery) // MarkLogic 6.0
    val K_PUBLIC = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PUBLIC", XQuery) // XQuery 3.0 (§4.15 -- Annotations)
    val K_RELATIONSHIP = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_RELATIONSHIP", XQuery) // Full Text 1.0
    val K_RENAME = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_RENAME", XQuery) // Update Facility 1.0
    val K_REPLACE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_REPLACE", XQuery) // Update Facility 1.0
    val K_RETURNING = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_RETURNING", XQuery) // Scripting Extension 1.0
    val K_REVALIDATION = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_REVALIDATION", XQuery) // Update Facility 1.0
    val K_SAME = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SAME", XQuery) // Full Text 1.0
    val K_SCHEMA = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SCHEMA", XQuery)
    val K_SCHEMA_COMPONENT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SCHEMA_COMPONENT_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME) // MarkLogic 7.0
    val K_SCHEMA_ELEMENT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SCHEMA_ELEMENT", XQuery, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME)
    val K_SCHEMA_FACET = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SCHEMA_FACET_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC80_RESERVED_FUNCTION_NAME) // MarkLogic 8.0
    val K_SCHEMA_PARTICLE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SCHEMA_PARTICLE_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME) // MarkLogic 7.0
    val K_SCHEMA_ROOT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SCHEMA_ROOT_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME) // MarkLogic 7.0
    val K_SCHEMA_TYPE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SCHEMA_TYPE_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME) // MarkLogic 7.0
    val K_SCORE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SCORE", XQuery) // Full Text 1.0
    val K_SENSITIVE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SENSITIVE", XQuery) // Full Text 1.0
    val K_SENTENCE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SENTENCE", XQuery) // Full Text 1.0
    val K_SENTENCES = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SENTENCES", XQuery) // Full Text 1.0
    val K_SEQUENTIAL = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SEQUENTIAL", XQuery) // Scripting Extension 1.0
    val K_SIMPLE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SIMPLE", XQuery) // Scripting Extension 1.0
    val K_SIMPLE_TYPE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SIMPLE_TYPE_NODE", XQuery, IKeywordOrNCNameType.KeywordType.MARKLOGIC70_RESERVED_FUNCTION_NAME) // MarkLogic 7.0
    val K_SKIP = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SKIP", XQuery) // Update Facility 1.0
    val K_SLIDING = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SLIDING", XQuery) // XQuery 3.0
    val K_STABLE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_STABLE", XQuery)
    val K_START = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_START", XQuery) // XQuery 3.0
    val K_STEMMING = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_STEMMING", XQuery) // Full Text 1.0
    val K_STOP = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_STOP", XQuery) // Full Text 1.0
    val K_STRICT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_STRICT", XQuery)
    val K_STRIP = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_STRIP", XQuery)
    val K_STYLESHEET = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_STYLESHEET", XQuery) // MarkLogic 6.0
    val K_SWITCH = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SWITCH", XQuery, IKeywordOrNCNameType.KeywordType.XQUERY30_RESERVED_FUNCTION_NAME) // XQuery 3.0
    val K_THESAURUS = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_THESAURUS", XQuery) // Full Text 1.0
    val K_TIMES = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TIMES", XQuery) // Full Text 1.0
    val K_TRANSFORM = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TRANSFORM", XQuery) // Update Facility 3.0
    val K_TRY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TRY", XQuery) // XQuery 3.0
    val K_TUMBLING = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TUMBLING", XQuery) // XQuery 3.0
    val K_TUPLE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TUPLE", XQuery) // Saxon 9.8
    val K_TYPE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TYPE", XQuery) // XQuery 3.0
    val K_TYPESWITCH = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TYPESWITCH", XQuery, IKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME)
    val K_UNASSIGNABLE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_UNASSIGNABLE", XQuery) // Scripting Extension 1.0
    val K_UNORDERED = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_UNORDERED", XQuery)
    val K_UPDATE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_UPDATE", XQuery) // BaseX 7.8
    val K_UPDATING = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_UPDATING", XQuery) // Update Facility 1.0
    val K_UPPERCASE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_UPPERCASE", XQuery) // Full Text 1.0
    val K_USING = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_USING", XQuery) // Full Text 1.0
    val K_VALIDATE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_VALIDATE", XQuery)
    val K_VALUE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_VALUE", XQuery) // Update Facility 1.0
    val K_VARIABLE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_VARIABLE", XQuery)
    val K_VERSION = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_VERSION", XQuery)
    val K_WEIGHT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_WEIGHT", XQuery) // Full Text 1.0
    val K_WHEN = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_WHEN", XQuery) // XQuery 3.0
    val K_WHERE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_WHERE", XQuery)
    val K_WHILE = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_WHILE", XQuery, IKeywordOrNCNameType.KeywordType.SCRIPTING10_RESERVED_FUNCTION_NAME) // Scripting Extension 1.0
    val K_WILDCARDS = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_WILDCARDS", XQuery) // Full Text 1.0
    val K_WINDOW = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_WINDOW", XQuery) // XQuery 3.0; Full Text 1.0
    val K_WITH = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_WITH", XQuery) // Update Facility 1.0
    val K_WITHOUT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_WITHOUT", XQuery) // Full Text 1.0
    val K_WORD = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_WORD", XQuery) // Full Text 1.0
    val K_WORDS = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_WORDS", XQuery) // Full Text 1.0
    val K_XQUERY = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_XQUERY", XQuery)
    val K_ZERO_DIGIT = IKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ZERO_DIGIT", XQuery) // XQuery 3.0

    val STRING_LITERAL_TOKENS = TokenSet.create(
        XPathTokenType.STRING_LITERAL_CONTENTS,
        STRING_CONSTRUCTOR_CONTENTS,
        XML_ATTRIBUTE_VALUE_CONTENTS,
        XML_ELEMENT_CONTENTS
    )

    val COMMENT_TOKENS = TokenSet.create(
        XQDocTokenType.CONTENTS,
        XPathTokenType.COMMENT,
        XML_COMMENT
    )

    val LITERAL_TOKENS = TokenSet.create(
        XPathTokenType.INTEGER_LITERAL,
        XPathTokenType.DECIMAL_LITERAL,
        XPathTokenType.DOUBLE_LITERAL,
        XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT
    )
}
