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
package uk.co.reecedunn.intellij.plugin.xquery.lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocTokenType;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery;

public interface XQueryTokenType extends TokenType {
    IElementType UNEXPECTED_END_OF_BLOCK = new IElementType("XQUERY_UNEXPECTED_END_OF_BLOCK_TOKEN", XQuery.INSTANCE);

    IElementType COMMENT = new IElementType("XQUERY_COMMENT_TOKEN", XQuery.INSTANCE);
    IElementType COMMENT_START_TAG = new IElementType("XQUERY_COMMENT_START_TAG_TOKEN", XQuery.INSTANCE);
    IElementType COMMENT_END_TAG = new IElementType("XQUERY_COMMENT_END_TAG_TOKEN", XQuery.INSTANCE);

    IElementType XML_COMMENT = new IElementType("XQUERY_XML_COMMENT_TOKEN", XQuery.INSTANCE);
    IElementType XML_COMMENT_START_TAG = new IElementType("XQUERY_XML_COMMENT_START_TAG_TOKEN", XQuery.INSTANCE);
    IElementType XML_COMMENT_END_TAG = new IElementType("XQUERY_XML_COMMENT_END_TAG_TOKEN", XQuery.INSTANCE);

    IElementType PROCESSING_INSTRUCTION_BEGIN = new IElementType("XQUERY_PROCESSING_INSTRUCTION_BEGIN_TOKEN", XQuery.INSTANCE);
    IElementType PROCESSING_INSTRUCTION_END = new IElementType("XQUERY_PROCESSING_INSTRUCTION_END_TOKEN", XQuery.INSTANCE);
    IElementType PROCESSING_INSTRUCTION_CONTENTS = new IElementType("XQUERY_PROCESSING_INSTRUCTION_CONTENTS_TOKEN", XQuery.INSTANCE);

    IElementType OPEN_XML_TAG = new IElementType("XQUERY_OPEN_XML_TAG_TOKEN", XQuery.INSTANCE);
    IElementType END_XML_TAG = new IElementType("XQUERY_END_XML_TAG_TOKEN", XQuery.INSTANCE);
    IElementType CLOSE_XML_TAG = new IElementType("XQUERY_CLOSE_XML_TAG_TOKEN", XQuery.INSTANCE);
    IElementType SELF_CLOSING_XML_TAG = new IElementType("XQUERY_SELF_CLOSING_XML_TAG_TOKEN", XQuery.INSTANCE);
    IElementType XML_ELEMENT_CONTENTS = new IElementType("XQUERY_XML_ELEMENT_CONTENTS_TOKEN", XQuery.INSTANCE);
    IElementType XML_EQUAL = new IElementType("XQUERY_XML_EQUAL_TOKEN", XQuery.INSTANCE);
    IElementType XML_WHITE_SPACE = new IElementType("XQUERY_XML_WHITE_SPACE_TOKEN", XQuery.INSTANCE);
    IElementType XML_TAG_NCNAME = new INCNameType("XQUERY_XML_TAG_NCNAME_TOKEN");
    IElementType XML_TAG_QNAME_SEPARATOR = new IElementType("XQUERY_XML_TAG_QNAME_SEPARATOR_TOKEN", XQuery.INSTANCE);
    IElementType XML_ATTRIBUTE_NCNAME = new INCNameType("XQUERY_XML_ATTRIBUTE_NCNAME_TOKEN");
    IElementType XML_ATTRIBUTE_QNAME_SEPARATOR = new IElementType("XQUERY_XML_ATTRIBUTE_QNAME_SEPARATOR_TOKEN", XQuery.INSTANCE);

    IElementType XML_ATTRIBUTE_VALUE_START = new IElementType("XQUERY_XML_ATTRIBUTE_VALUE_START_TOKEN", XQuery.INSTANCE);
    IElementType XML_ATTRIBUTE_VALUE_CONTENTS = new IElementType("XQUERY_XML_ATTRIBUTE_VALUE_CONTENTS_TOKEN", XQuery.INSTANCE);
    IElementType XML_ATTRIBUTE_VALUE_END = new IElementType("XQUERY_XML_ATTRIBUTE_VALUE_END_TOKEN", XQuery.INSTANCE);
    IElementType XML_ESCAPED_CHARACTER = new IElementType("XQUERY_XML_ESCAPED_CHARACTER_TOKEN", XQuery.INSTANCE);

    IElementType XML_CHARACTER_REFERENCE = new IElementType("XQUERY_XML_CHARACTER_REFERENCE_TOKEN", XQuery.INSTANCE);
    IElementType XML_PREDEFINED_ENTITY_REFERENCE = new IElementType("XQUERY_XML_PREDEFINED_ENTITY_REFERENCE_TOKEN", XQuery.INSTANCE);
    IElementType XML_PARTIAL_ENTITY_REFERENCE = new IElementType("XQUERY_XML_PARTIAL_ENTITY_REFERENCE_TOKEN", XQuery.INSTANCE);
    IElementType XML_EMPTY_ENTITY_REFERENCE = new IElementType("XQUERY_XML_EMPTY_ENTITY_REFERENCE_TOKEN", XQuery.INSTANCE);

    IElementType CDATA_SECTION = new IElementType("XQUERY_CDATA_SECTION_TOKEN", XQuery.INSTANCE);
    IElementType CDATA_SECTION_START_TAG = new IElementType("XQUERY_CDATA_SECTION_START_TAG_TOKEN", XQuery.INSTANCE);
    IElementType CDATA_SECTION_END_TAG = new IElementType("XQUERY_CDATA_SECTION_END_TAG_TOKEN", XQuery.INSTANCE);

    IElementType PRAGMA_CONTENTS = new IElementType("XQUERY_PRAGMA_CONTENTS_TOKEN", XQuery.INSTANCE);
    IElementType PRAGMA_BEGIN = new IElementType("XQUERY_PRAGMA_BEGIN_TOKEN", XQuery.INSTANCE);
    IElementType PRAGMA_END = new IElementType("XQUERY_PRAGMA_END_TOKEN", XQuery.INSTANCE);

    IElementType INTEGER_LITERAL = new IElementType("XQUERY_INTEGER_LITERAL_TOKEN", XQuery.INSTANCE);
    IElementType DECIMAL_LITERAL = new IElementType("XQUERY_DECIMAL_LITERAL_TOKEN", XQuery.INSTANCE);
    IElementType DOUBLE_LITERAL = new IElementType("XQUERY_DOUBLE_LITERAL_TOKEN", XQuery.INSTANCE);
    IElementType PARTIAL_DOUBLE_LITERAL_EXPONENT = new IElementType("XQUERY_PARTIAL_DOUBLE_LITERAL_EXPONENT_TOKEN", XQuery.INSTANCE);

    IElementType STRING_LITERAL_START = new IElementType("XQUERY_STRING_LITERAL_START_TOKEN", XQuery.INSTANCE);
    IElementType STRING_LITERAL_CONTENTS = new IElementType("XQUERY_STRING_LITERAL_CONTENTS_TOKEN", XQuery.INSTANCE);
    IElementType STRING_LITERAL_END = new IElementType("XQUERY_STRING_LITERAL_END_TOKEN", XQuery.INSTANCE);
    IElementType ESCAPED_CHARACTER = new IElementType("XQUERY_ESCAPED_CHARACTER_TOKEN", XQuery.INSTANCE);

    IElementType BRACED_URI_LITERAL_START = new IElementType("XQUERY_BRACED_URI_LITERAL_START_TOKEN", XQuery.INSTANCE);
    IElementType BRACED_URI_LITERAL_END = new IElementType("XQUERY_BRACED_URI_LITERAL_END_TOKEN", XQuery.INSTANCE);

    IElementType CHARACTER_REFERENCE = new IElementType("XQUERY_CHARACTER_REFERENCE_TOKEN", XQuery.INSTANCE);
    IElementType PREDEFINED_ENTITY_REFERENCE = new IElementType("XQUERY_PREDEFINED_ENTITY_REFERENCE_TOKEN", XQuery.INSTANCE);
    IElementType PARTIAL_ENTITY_REFERENCE = new IElementType("XQUERY_PARTIAL_ENTITY_REFERENCE_TOKEN", XQuery.INSTANCE);
    IElementType ENTITY_REFERENCE_NOT_IN_STRING = new IElementType("XQUERY_ENTITY_REFERENCE_NOT_IN_STRING_TOKEN", XQuery.INSTANCE);
    IElementType EMPTY_ENTITY_REFERENCE = new IElementType("XQUERY_EMPTY_ENTITY_REFERENCE_TOKEN", XQuery.INSTANCE);

    IElementType NCNAME = new INCNameType("XQUERY_NCNAME_TOKEN");
    IElementType QNAME_SEPARATOR = new IElementType("XQUERY_QNAME_SEPARATOR_TOKEN", XQuery.INSTANCE);

    IElementType INVALID = new IElementType("XQUERY_INVALID_TOKEN", XQuery.INSTANCE);
    IElementType PARENTHESIS_OPEN = new IElementType("XQUERY_PARENTHESIS_OPEN_TOKEN", XQuery.INSTANCE);
    IElementType PARENTHESIS_CLOSE = new IElementType("XQUERY_PARENTHESIS_CLOSE_TOKEN", XQuery.INSTANCE);
    IElementType NOT_EQUAL = new IElementType("XQUERY_NOT_EQUAL_TOKEN", XQuery.INSTANCE);
    IElementType VARIABLE_INDICATOR = new IElementType("XQUERY_VARIABLE_INDICATOR_TOKEN", XQuery.INSTANCE);
    IElementType STAR = new IElementType("XQUERY_STAR_TOKEN", XQuery.INSTANCE);
    IElementType COMMA = new IElementType("XQUERY_COMMA_TOKEN", XQuery.INSTANCE);
    IElementType MINUS = new IElementType("XQUERY_MINUS_TOKEN", XQuery.INSTANCE);
    IElementType DOT = new IElementType("XQUERY_DOT_TOKEN", XQuery.INSTANCE);
    IElementType SEPARATOR = new IElementType("XQUERY_SEPARATOR_TOKEN", XQuery.INSTANCE);
    IElementType PLUS = new IElementType("XQUERY_PLUS_TOKEN", XQuery.INSTANCE);
    IElementType EQUAL = new IElementType("XQUERY_EQUAL_TOKEN", XQuery.INSTANCE);
    IElementType BLOCK_OPEN = new IElementType("XQUERY_BLOCK_OPEN_TOKEN", XQuery.INSTANCE);
    IElementType BLOCK_CLOSE = new IElementType("XQUERY_BLOCK_CLOSE_TOKEN", XQuery.INSTANCE);
    IElementType LESS_THAN = new IElementType("XQUERY_LESS_THAN_TOKEN", XQuery.INSTANCE);
    IElementType GREATER_THAN = new IElementType("XQUERY_GREATER_THAN_TOKEN", XQuery.INSTANCE);
    IElementType LESS_THAN_OR_EQUAL = new IElementType("XQUERY_LESS_THAN_OR_EQUAL_TOKEN", XQuery.INSTANCE);
    IElementType GREATER_THAN_OR_EQUAL = new IElementType("XQUERY_GREATER_THAN_OR_EQUAL_TOKEN", XQuery.INSTANCE);
    IElementType UNION = new IElementType("XQUERY_UNION_TOKEN", XQuery.INSTANCE);
    IElementType OPTIONAL = new IElementType("XQUERY_OPTIONAL_TOKEN", XQuery.INSTANCE);
    IElementType AXIS_SEPARATOR = new IElementType("XQUERY_AXIS_SEPARATOR_TOKEN", XQuery.INSTANCE);
    IElementType ASSIGN_EQUAL = new IElementType("XQUERY_ASSIGN_EQUAL_TOKEN", XQuery.INSTANCE);
    IElementType DIRECT_DESCENDANTS_PATH = new IElementType("XQUERY_DIRECT_DESCENDANTS_PATH_TOKEN", XQuery.INSTANCE);
    IElementType ALL_DESCENDANTS_PATH = new IElementType("XQUERY_ALL_DESCENDANTS_PATH_TOKEN", XQuery.INSTANCE);
    IElementType ATTRIBUTE_SELECTOR = new IElementType("XQUERY_ATTRIBUTE_SELECTOR_TOKEN", XQuery.INSTANCE);
    IElementType SQUARE_OPEN = new IElementType("XQUERY_SQUARE_OPEN_TOKEN", XQuery.INSTANCE);
    IElementType SQUARE_CLOSE = new IElementType("XQUERY_SQUARE_CLOSE_TOKEN", XQuery.INSTANCE);
    IElementType PARENT_SELECTOR = new IElementType("XQUERY_PARENT_SELECTOR_TOKEN", XQuery.INSTANCE);
    IElementType NODE_BEFORE = new IElementType("XQUERY_NODE_BEFORE_TOKEN", XQuery.INSTANCE);
    IElementType NODE_AFTER = new IElementType("XQUERY_NODE_AFTER_TOKEN", XQuery.INSTANCE);

    IElementType CONCATENATION = new IElementType("XQUERY_CONCATENATION", XQuery.INSTANCE); // XQuery 3.0
    IElementType MAP_OPERATOR = new IElementType("XQUERY_MAP_OPERATOR_TOKEN", XQuery.INSTANCE); // XQuery 3.0
    IElementType FUNCTION_REF_OPERATOR = new IElementType("XQUERY_FUNCTION_REF_OPERATOR_TOKEN", XQuery.INSTANCE); // XQuery 3.0
    IElementType ANNOTATION_INDICATOR = new IElementType("XQUERY_ANNOTATION_INDICATOR_TOKEN", XQuery.INSTANCE); // XQuery 3.0

    IElementType ARROW = new IElementType("XQUERY_ARROW_TOKEN", XQuery.INSTANCE); // XQuery 3.1
    IElementType STRING_CONSTRUCTOR_START = new IElementType("XQUERY_STRING_CONSTRUCTOR_START_TOKEN", XQuery.INSTANCE); // XQuery 3.1
    IElementType STRING_CONSTRUCTOR_CONTENTS = new IElementType("XQUERY_STRING_CONSTRUCTOR_CONTENTS_TOKEN", XQuery.INSTANCE); // XQuery 3.1
    IElementType STRING_CONSTRUCTOR_END = new IElementType("XQUERY_STRING_CONSTRUCTOR_END_TOKEN", XQuery.INSTANCE); // XQuery 3.1
    IElementType STRING_INTERPOLATION_OPEN = new IElementType("XQUERY_STRING_INTERPOLATION_OPEN_TOKEN", XQuery.INSTANCE); // XQuery 3.1
    IElementType STRING_INTERPOLATION_CLOSE = new IElementType("XQUERY_STRING_INTERPOLATION_CLOSE_TOKEN", XQuery.INSTANCE); // XQuery 3.1

    IXQueryKeywordOrNCNameType K_AFTER = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_AFTER"); // Update Facility 1.0
    IXQueryKeywordOrNCNameType K_ALLOWING = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ALLOWING"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_ANCESTOR = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ANCESTOR");
    IXQueryKeywordOrNCNameType K_ANCESTOR_OR_SELF = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ANCESTOR_OR_SELF");
    IXQueryKeywordOrNCNameType K_AND = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_AND");
    IXQueryKeywordOrNCNameType K_ARRAY = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ARRAY"); // XQuery 3.1
    IXQueryKeywordOrNCNameType K_ARRAY_NODE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ARRAY_NODE", IXQueryKeywordOrNCNameType.KeywordType.MARKLOGIC_RESERVED_FUNCTION_NAME); // MarkLogic 8.0
    IXQueryKeywordOrNCNameType K_AS = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_AS");
    IXQueryKeywordOrNCNameType K_ASCENDING = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ASCENDING");
    IXQueryKeywordOrNCNameType K_ASSIGNABLE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ASSIGNABLE"); // Scripting Extension 1.0
    IXQueryKeywordOrNCNameType K_AT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_AT");
    IXQueryKeywordOrNCNameType K_ATTRIBUTE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ATTRIBUTE", IXQueryKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME);
    IXQueryKeywordOrNCNameType K_BASE_URI = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BASE_URI");
    IXQueryKeywordOrNCNameType K_BEFORE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BEFORE"); // Update Facility 1.0
    IXQueryKeywordOrNCNameType K_BINARY = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BINARY"); // MarkLogic 6.0
    IXQueryKeywordOrNCNameType K_BLOCK = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BLOCK"); // Scripting Extension 1.0
    IXQueryKeywordOrNCNameType K_BOOLEAN_NODE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BOOLEAN_NODE", IXQueryKeywordOrNCNameType.KeywordType.MARKLOGIC_RESERVED_FUNCTION_NAME); // MarkLogic 8.0
    IXQueryKeywordOrNCNameType K_BOUNDARY_SPACE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BOUNDARY_SPACE");
    IXQueryKeywordOrNCNameType K_BY = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BY");
    IXQueryKeywordOrNCNameType K_CASE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_CASE");
    IXQueryKeywordOrNCNameType K_CAST = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_CAST");
    IXQueryKeywordOrNCNameType K_CASTABLE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_CASTABLE");
    IXQueryKeywordOrNCNameType K_CATCH = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_CATCH"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_CHILD = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_CHILD");
    IXQueryKeywordOrNCNameType K_COLLATION = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_COLLATION");
    IXQueryKeywordOrNCNameType K_COMMENT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_COMMENT", IXQueryKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME);
    IXQueryKeywordOrNCNameType K_CONSTRUCTION = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_CONSTRUCTION");
    IXQueryKeywordOrNCNameType K_CONTEXT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_CONTEXT"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_COPY = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_COPY"); // Update Facility 1.0
    IXQueryKeywordOrNCNameType K_COPY_NAMESPACES = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_COPY_NAMESPACES");
    IXQueryKeywordOrNCNameType K_COUNT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_COUNT"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_DECIMAL_FORMAT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DECIMAL_FORMAT"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_DECIMAL_SEPARATOR = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DECIMAL_SEPARATOR"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_DECLARE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DECLARE");
    IXQueryKeywordOrNCNameType K_DEFAULT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DEFAULT");
    IXQueryKeywordOrNCNameType K_DELETE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DELETE"); // Update Facility 1.0
    IXQueryKeywordOrNCNameType K_DESCENDANT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DESCENDANT");
    IXQueryKeywordOrNCNameType K_DESCENDANT_OR_SELF = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DESCENDANT_OR_SELF");
    IXQueryKeywordOrNCNameType K_DESCENDING = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DESCENDING");
    IXQueryKeywordOrNCNameType K_DIGIT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DIGIT"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_DIV = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DIV");
    IXQueryKeywordOrNCNameType K_DOCUMENT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DOCUMENT");
    IXQueryKeywordOrNCNameType K_DOCUMENT_NODE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DOCUMENT_NODE", IXQueryKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME);
    IXQueryKeywordOrNCNameType K_ELEMENT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ELEMENT", IXQueryKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME);
    IXQueryKeywordOrNCNameType K_ELSE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ELSE");
    IXQueryKeywordOrNCNameType K_EMPTY = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EMPTY");
    IXQueryKeywordOrNCNameType K_EMPTY_SEQUENCE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EMPTY_SEQUENCE", IXQueryKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME);
    IXQueryKeywordOrNCNameType K_ENCODING = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ENCODING");
    IXQueryKeywordOrNCNameType K_END = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_END"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_EQ = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EQ");
    IXQueryKeywordOrNCNameType K_EVERY = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EVERY");
    IXQueryKeywordOrNCNameType K_EXCEPT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EXCEPT");
    IXQueryKeywordOrNCNameType K_EXIT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EXIT"); // Scripting Extension 1.0
    IXQueryKeywordOrNCNameType K_EXPONENT_SEPARATOR = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EXPONENT_SEPARATOR"); // XQuery 3.1
    IXQueryKeywordOrNCNameType K_EXTERNAL = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EXTERNAL");
    IXQueryKeywordOrNCNameType K_FIRST = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FIRST"); // Update Facility 1.0
    IXQueryKeywordOrNCNameType K_FOLLOWING = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FOLLOWING");
    IXQueryKeywordOrNCNameType K_FOLLOWING_SIBLING = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FOLLOWING_SIBLING");
    IXQueryKeywordOrNCNameType K_FOR = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FOR");
    IXQueryKeywordOrNCNameType K_FT_OPTION = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FT_OPTION"); // Full Text 1.0
    IXQueryKeywordOrNCNameType K_FUNCTION = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FUNCTION", IXQueryKeywordOrNCNameType.KeywordType.XQUERY30_RESERVED_FUNCTION_NAME);
    IXQueryKeywordOrNCNameType K_GE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_GE");
    IXQueryKeywordOrNCNameType K_GREATEST = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_GREATEST");
    IXQueryKeywordOrNCNameType K_GROUP = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_GROUP"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_GROUPING_SEPARATOR = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_GROUPING_SEPARATOR"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_GT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_GT");
    IXQueryKeywordOrNCNameType K_IDIV = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_IDIV");
    IXQueryKeywordOrNCNameType K_IF = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_IF", IXQueryKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME);
    IXQueryKeywordOrNCNameType K_IMPORT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_IMPORT");
    IXQueryKeywordOrNCNameType K_IN = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_IN");
    IXQueryKeywordOrNCNameType K_INFINITY = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INFINITY"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_INHERIT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INHERIT");
    IXQueryKeywordOrNCNameType K_INSERT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INSERT"); // Update Facility 1.0
    IXQueryKeywordOrNCNameType K_INSTANCE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INSTANCE");
    IXQueryKeywordOrNCNameType K_INTERSECT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INTERSECT");
    IXQueryKeywordOrNCNameType K_INTO = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INTO"); // Update Facility 1.0
    IXQueryKeywordOrNCNameType K_INVOKE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INVOKE"); // Update Facility 3.0
    IXQueryKeywordOrNCNameType K_IS = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_IS");
    IXQueryKeywordOrNCNameType K_ITEM = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ITEM", IXQueryKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME);
    IXQueryKeywordOrNCNameType K_LAST = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_LAST"); // Update Facility 1.0
    IXQueryKeywordOrNCNameType K_LAX = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_LAX");
    IXQueryKeywordOrNCNameType K_LE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_LE");
    IXQueryKeywordOrNCNameType K_LEAST = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_LEAST");
    IXQueryKeywordOrNCNameType K_LET = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_LET");
    IXQueryKeywordOrNCNameType K_LT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_LT");
    IXQueryKeywordOrNCNameType K_MAP = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_MAP"); // XQuery 3.1
    IXQueryKeywordOrNCNameType K_MINUS_SIGN = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_MINUS_SIGN"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_MOD = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_MOD");
    IXQueryKeywordOrNCNameType K_MODIFY = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_MODIFY"); // Update Facility 1.0
    IXQueryKeywordOrNCNameType K_MODULE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_MODULE");
    IXQueryKeywordOrNCNameType K_NAMESPACE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NAMESPACE");
    IXQueryKeywordOrNCNameType K_NAMESPACE_NODE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NAMESPACE_NODE", IXQueryKeywordOrNCNameType.KeywordType.XQUERY30_RESERVED_FUNCTION_NAME); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_NAN = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NAN"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_NE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NE");
    IXQueryKeywordOrNCNameType K_NEXT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NEXT"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_NO_INHERIT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NO_INHERIT");
    IXQueryKeywordOrNCNameType K_NO_PRESERVE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NO_PRESERVE");
    IXQueryKeywordOrNCNameType K_NODE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NODE", IXQueryKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME);
    IXQueryKeywordOrNCNameType K_NODES = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NODES"); // Update Facility 1.0
    IXQueryKeywordOrNCNameType K_NULL_NODE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NULL_NODE", IXQueryKeywordOrNCNameType.KeywordType.MARKLOGIC_RESERVED_FUNCTION_NAME); // MarkLogic 8.0
    IXQueryKeywordOrNCNameType K_NUMBER_NODE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NUMBER_NODE", IXQueryKeywordOrNCNameType.KeywordType.MARKLOGIC_RESERVED_FUNCTION_NAME); // MarkLogic 8.0
    IXQueryKeywordOrNCNameType K_OBJECT_NODE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_OBJECT_NODE", IXQueryKeywordOrNCNameType.KeywordType.MARKLOGIC_RESERVED_FUNCTION_NAME); // MarkLogic 8.0
    IXQueryKeywordOrNCNameType K_OF = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_OF");
    IXQueryKeywordOrNCNameType K_ONLY = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ONLY"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_OPTION = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_OPTION");
    IXQueryKeywordOrNCNameType K_OR = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_OR");
    IXQueryKeywordOrNCNameType K_ORDER = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ORDER");
    IXQueryKeywordOrNCNameType K_ORDERED = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ORDERED");
    IXQueryKeywordOrNCNameType K_ORDERING = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ORDERING");
    IXQueryKeywordOrNCNameType K_PARENT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PARENT");
    IXQueryKeywordOrNCNameType K_PATTERN_SEPARATOR = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PATTERN_SEPARATOR"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_PER_MILLE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PER_MILLE"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_PERCENT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PERCENT"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_PRECEDING = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PRECEDING");
    IXQueryKeywordOrNCNameType K_PRECEDING_SIBLING = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PRECEDING_SIBLING");
    IXQueryKeywordOrNCNameType K_PRESERVE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PRESERVE");
    IXQueryKeywordOrNCNameType K_PRIVATE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PRIVATE"); // MarkLogic 6.0
    IXQueryKeywordOrNCNameType K_PREVIOUS = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PREVIOUS"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_PROCESSING_INSTRUCTION = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PROCESSING_INSTRUCTION", IXQueryKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME);
    IXQueryKeywordOrNCNameType K_PROPERTY = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PROPERTY"); // MarkLogic 6.0
    IXQueryKeywordOrNCNameType K_PUBLIC = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PUBLIC"); // XQuery 3.0 (§4.15 -- Annotations)
    IXQueryKeywordOrNCNameType K_RENAME = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_RENAME"); // Update Facility 1.0
    IXQueryKeywordOrNCNameType K_REPLACE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_REPLACE"); // Update Facility 1.0
    IXQueryKeywordOrNCNameType K_RETURN = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_RETURN");
    IXQueryKeywordOrNCNameType K_RETURNING = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_RETURNING"); // Scripting Extension 1.0
    IXQueryKeywordOrNCNameType K_REVALIDATION = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_REVALIDATION"); // Update Facility 1.0
    IXQueryKeywordOrNCNameType K_SATISFIES = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SATISFIES");
    IXQueryKeywordOrNCNameType K_SCHEMA = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SCHEMA");
    IXQueryKeywordOrNCNameType K_SCHEMA_ATTRIBUTE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SCHEMA_ATTRIBUTE", IXQueryKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME);
    IXQueryKeywordOrNCNameType K_SCHEMA_ELEMENT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SCHEMA_ELEMENT", IXQueryKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME);
    IXQueryKeywordOrNCNameType K_SCORE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SCORE"); // Full Text 1.0
    IXQueryKeywordOrNCNameType K_SELF = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SELF");
    IXQueryKeywordOrNCNameType K_SEQUENTIAL = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SEQUENTIAL"); // Scripting Extension 1.0
    IXQueryKeywordOrNCNameType K_SIMPLE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SIMPLE"); // Scripting Extension 1.0
    IXQueryKeywordOrNCNameType K_SKIP = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SKIP"); // Update Facility 1.0
    IXQueryKeywordOrNCNameType K_SLIDING = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SLIDING"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_SOME = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SOME");
    IXQueryKeywordOrNCNameType K_STABLE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_STABLE");
    IXQueryKeywordOrNCNameType K_START = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_START"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_STRICT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_STRICT");
    IXQueryKeywordOrNCNameType K_STRIP = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_STRIP");
    IXQueryKeywordOrNCNameType K_STYLESHEET = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_STYLESHEET"); // MarkLogic 6.0
    IXQueryKeywordOrNCNameType K_SWITCH = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SWITCH", IXQueryKeywordOrNCNameType.KeywordType.XQUERY30_RESERVED_FUNCTION_NAME); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_TEXT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TEXT", IXQueryKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME);
    IXQueryKeywordOrNCNameType K_THEN = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_THEN");
    IXQueryKeywordOrNCNameType K_TO = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TO");
    IXQueryKeywordOrNCNameType K_TRANSFORM = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TRANSFORM"); // Update Facility 3.0
    IXQueryKeywordOrNCNameType K_TREAT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TREAT");
    IXQueryKeywordOrNCNameType K_TRY = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TRY"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_TUMBLING = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TUMBLING"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_TYPE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TYPE"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_TYPESWITCH = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TYPESWITCH", IXQueryKeywordOrNCNameType.KeywordType.RESERVED_FUNCTION_NAME);
    IXQueryKeywordOrNCNameType K_UNASSIGNABLE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_UNASSIGNABLE"); // Scripting Extension 1.0
    IXQueryKeywordOrNCNameType K_UNION = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_UNION");
    IXQueryKeywordOrNCNameType K_UNORDERED = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_UNORDERED");
    IXQueryKeywordOrNCNameType K_UPDATE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_UPDATE"); // BaseX 7.8
    IXQueryKeywordOrNCNameType K_UPDATING = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_UPDATING"); // Update Facility 1.0
    IXQueryKeywordOrNCNameType K_VALIDATE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_VALIDATE");
    IXQueryKeywordOrNCNameType K_VALUE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_VALUE"); // Update Facility 1.0
    IXQueryKeywordOrNCNameType K_VARIABLE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_VARIABLE");
    IXQueryKeywordOrNCNameType K_VERSION = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_VERSION");
    IXQueryKeywordOrNCNameType K_WHEN = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_WHEN"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_WHERE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_WHERE");
    IXQueryKeywordOrNCNameType K_WHILE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_WHILE", IXQueryKeywordOrNCNameType.KeywordType.SCRIPTING10_RESERVED_FUNCTION_NAME); // Scripting Extension 1.0
    IXQueryKeywordOrNCNameType K_WINDOW = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_WINDOW"); // XQuery 3.0
    IXQueryKeywordOrNCNameType K_WITH = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_WITH"); // Update Facility 1.0
    IXQueryKeywordOrNCNameType K_XQUERY = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_XQUERY");
    IXQueryKeywordOrNCNameType K_ZERO_DIGIT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ZERO_DIGIT"); // XQuery 3.0

    TokenSet STRING_LITERAL_TOKENS = TokenSet.create(
        STRING_LITERAL_CONTENTS,
        STRING_CONSTRUCTOR_CONTENTS,
        XML_ATTRIBUTE_VALUE_CONTENTS,
        XML_ELEMENT_CONTENTS);

    TokenSet COMMENT_TOKENS = TokenSet.create(
        XQDocTokenType.COMMENT_CONTENTS,
        XQDocTokenType.CONTENTS,
        COMMENT,
        XML_COMMENT);

    TokenSet LITERAL_TOKENS = TokenSet.create(
        INTEGER_LITERAL,
        DECIMAL_LITERAL,
        DOUBLE_LITERAL,
        PARTIAL_DOUBLE_LITERAL_EXPONENT);
}
