/*
 * Copyright (C) 2016 Reece H. Dunn
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
    IElementType XML_ATTRIBUTE_NCNAME = new INCNameType("XQUERY_XML_ATTRIBUTE_NCNAME_TOKEN");

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
    IElementType PREDICATE_BEGIN = new IElementType("XQUERY_PREDICATE_BEGIN_TOKEN", XQuery.INSTANCE);
    IElementType PREDICATE_END = new IElementType("XQUERY_PREDICATE_END_TOKEN", XQuery.INSTANCE);
    IElementType PARENT_SELECTOR = new IElementType("XQUERY_PARENT_SELECTOR_TOKEN", XQuery.INSTANCE);
    IElementType NODE_BEFORE = new IElementType("XQUERY_NODE_BEFORE_TOKEN", XQuery.INSTANCE);
    IElementType NODE_AFTER = new IElementType("XQUERY_NODE_AFTER_TOKEN", XQuery.INSTANCE);

    IElementType MAP_OPERATOR = new IElementType("XQUERY_MAP_OPERATOR_TOKEN", XQuery.INSTANCE); // XQuery 3.0
    IElementType FUNCTION_REF_OPERATOR = new IElementType("XQUERY_FUNCTION_REF_OPERATOR_TOKEN", XQuery.INSTANCE); // XQuery 3.0
    IElementType ANNOTATION_INDICATOR = new IElementType("XQUERY_ANNOTATION_INDICATOR_TOKEN", XQuery.INSTANCE); // XQuery 3.0

    IXQueryKeywordOrNCNameType K_ANCESTOR = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ANCESTOR");
    IXQueryKeywordOrNCNameType K_ANCESTOR_OR_SELF = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ANCESTOR_OR_SELF");
    IXQueryKeywordOrNCNameType K_AND = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_AND");
    IXQueryKeywordOrNCNameType K_AS = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_AS");
    IXQueryKeywordOrNCNameType K_ASCENDING = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ASCENDING");
    IXQueryKeywordOrNCNameType K_AT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_AT");
    IXQueryReservedFunctionNameOrNCNameType K_ATTRIBUTE = new IXQueryReservedFunctionNameOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ATTRIBUTE");
    IXQueryKeywordOrNCNameType K_BASE_URI = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BASE_URI");
    IXQueryKeywordOrNCNameType K_BOUNDARY_SPACE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BOUNDARY_SPACE");
    IXQueryKeywordOrNCNameType K_BY = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_BY");
    IXQueryKeywordOrNCNameType K_CASE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_CASE");
    IXQueryKeywordOrNCNameType K_CAST = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_CAST");
    IXQueryKeywordOrNCNameType K_CASTABLE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_CASTABLE");
    IXQueryKeywordOrNCNameType K_CHILD = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_CHILD");
    IXQueryKeywordOrNCNameType K_COLLATION = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_COLLATION");
    IXQueryReservedFunctionNameOrNCNameType K_COMMENT = new IXQueryReservedFunctionNameOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_COMMENT");
    IXQueryKeywordOrNCNameType K_CONSTRUCTION = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_CONSTRUCTION");
    IXQueryKeywordOrNCNameType K_COPY_NAMESPACES = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_COPY_NAMESPACES");
    IXQueryKeywordOrNCNameType K_DECLARE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DECLARE");
    IXQueryKeywordOrNCNameType K_DEFAULT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DEFAULT");
    IXQueryKeywordOrNCNameType K_DESCENDANT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DESCENDANT");
    IXQueryKeywordOrNCNameType K_DESCENDANT_OR_SELF = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DESCENDANT_OR_SELF");
    IXQueryKeywordOrNCNameType K_DESCENDING = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DESCENDING");
    IXQueryKeywordOrNCNameType K_DIV = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DIV");
    IXQueryKeywordOrNCNameType K_DOCUMENT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DOCUMENT");
    IXQueryReservedFunctionNameOrNCNameType K_DOCUMENT_NODE = new IXQueryReservedFunctionNameOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_DOCUMENT_NODE");
    IXQueryReservedFunctionNameOrNCNameType K_ELEMENT = new IXQueryReservedFunctionNameOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ELEMENT");
    IXQueryKeywordOrNCNameType K_ELSE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ELSE");
    IXQueryKeywordOrNCNameType K_EMPTY = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EMPTY");
    IXQueryReservedFunctionNameOrNCNameType K_EMPTY_SEQUENCE = new IXQueryReservedFunctionNameOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EMPTY_SEQUENCE");
    IXQueryKeywordOrNCNameType K_ENCODING = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ENCODING");
    IXQueryKeywordOrNCNameType K_EQ = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EQ");
    IXQueryKeywordOrNCNameType K_EVERY = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EVERY");
    IXQueryKeywordOrNCNameType K_EXCEPT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EXCEPT");
    IXQueryKeywordOrNCNameType K_EXTERNAL = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_EXTERNAL");
    IXQueryKeywordOrNCNameType K_FOLLOWING = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FOLLOWING");
    IXQueryKeywordOrNCNameType K_FOLLOWING_SIBLING = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FOLLOWING_SIBLING");
    IXQueryKeywordOrNCNameType K_FOR = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FOR");
    IXQueryKeywordOrNCNameType K_FUNCTION = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_FUNCTION");
    IXQueryKeywordOrNCNameType K_GE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_GE");
    IXQueryKeywordOrNCNameType K_GREATEST = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_GREATEST");
    IXQueryKeywordOrNCNameType K_GT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_GT");
    IXQueryKeywordOrNCNameType K_IDIV = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_IDIV");
    IXQueryReservedFunctionNameOrNCNameType K_IF = new IXQueryReservedFunctionNameOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_IF");
    IXQueryKeywordOrNCNameType K_IMPORT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_IMPORT");
    IXQueryKeywordOrNCNameType K_IN = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_IN");
    IXQueryKeywordOrNCNameType K_INHERIT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INHERIT");
    IXQueryKeywordOrNCNameType K_INSTANCE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INSTANCE");
    IXQueryKeywordOrNCNameType K_INTERSECT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_INTERSECT");
    IXQueryKeywordOrNCNameType K_IS = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_IS");
    IXQueryReservedFunctionNameOrNCNameType K_ITEM = new IXQueryReservedFunctionNameOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ITEM");
    IXQueryKeywordOrNCNameType K_LAX = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_LAX");
    IXQueryKeywordOrNCNameType K_LE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_LE");
    IXQueryKeywordOrNCNameType K_LEAST = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_LEAST");
    IXQueryKeywordOrNCNameType K_LET = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_LET");
    IXQueryKeywordOrNCNameType K_LT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_LT");
    IXQueryKeywordOrNCNameType K_MOD = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_MOD");
    IXQueryKeywordOrNCNameType K_MODULE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_MODULE");
    IXQueryKeywordOrNCNameType K_NAMESPACE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NAMESPACE");
    IXQueryKeywordOrNCNameType K_NE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NE");
    IXQueryKeywordOrNCNameType K_NO_INHERIT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NO_INHERIT");
    IXQueryKeywordOrNCNameType K_NO_PRESERVE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NO_PRESERVE");
    IXQueryReservedFunctionNameOrNCNameType K_NODE = new IXQueryReservedFunctionNameOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_NODE");
    IXQueryKeywordOrNCNameType K_OF = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_OF");
    IXQueryKeywordOrNCNameType K_OPTION = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_OPTION");
    IXQueryKeywordOrNCNameType K_OR = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_OR");
    IXQueryKeywordOrNCNameType K_ORDER = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ORDER");
    IXQueryKeywordOrNCNameType K_ORDERED = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ORDERED");
    IXQueryKeywordOrNCNameType K_ORDERING = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_ORDERING");
    IXQueryKeywordOrNCNameType K_PARENT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PARENT");
    IXQueryKeywordOrNCNameType K_PRECEDING = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PRECEDING");
    IXQueryKeywordOrNCNameType K_PRECEDING_SIBLING = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PRECEDING_SIBLING");
    IXQueryKeywordOrNCNameType K_PRESERVE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PRESERVE");
    IXQueryReservedFunctionNameOrNCNameType K_PROCESSING_INSTRUCTION = new IXQueryReservedFunctionNameOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_PROCESSING_INSTRUCTION");
    IXQueryKeywordOrNCNameType K_RETURN = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_RETURN");
    IXQueryKeywordOrNCNameType K_SATISFIES = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SATISFIES");
    IXQueryKeywordOrNCNameType K_SCHEMA = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SCHEMA");
    IXQueryReservedFunctionNameOrNCNameType K_SCHEMA_ATTRIBUTE = new IXQueryReservedFunctionNameOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SCHEMA_ATTRIBUTE");
    IXQueryReservedFunctionNameOrNCNameType K_SCHEMA_ELEMENT = new IXQueryReservedFunctionNameOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SCHEMA_ELEMENT");
    IXQueryKeywordOrNCNameType K_SELF = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SELF");
    IXQueryKeywordOrNCNameType K_SOME = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_SOME");
    IXQueryKeywordOrNCNameType K_STABLE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_STABLE");
    IXQueryKeywordOrNCNameType K_STRICT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_STRICT");
    IXQueryKeywordOrNCNameType K_STRIP = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_STRIP");
    IXQueryReservedFunctionNameOrNCNameType K_TEXT = new IXQueryReservedFunctionNameOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TEXT");
    IXQueryKeywordOrNCNameType K_THEN = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_THEN");
    IXQueryKeywordOrNCNameType K_TO = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TO");
    IXQueryKeywordOrNCNameType K_TREAT = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TREAT");
    IXQueryReservedFunctionNameOrNCNameType K_TYPESWITCH = new IXQueryReservedFunctionNameOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_TYPESWITCH");
    IXQueryKeywordOrNCNameType K_UNION = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_UNION");
    IXQueryKeywordOrNCNameType K_UNORDERED = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_UNORDERED");
    IXQueryKeywordOrNCNameType K_VALIDATE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_VALIDATE");
    IXQueryKeywordOrNCNameType K_VARIABLE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_VARIABLE");
    IXQueryKeywordOrNCNameType K_VERSION = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_VERSION");
    IXQueryKeywordOrNCNameType K_WHERE = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_WHERE");
    IXQueryKeywordOrNCNameType K_XQUERY = new IXQueryKeywordOrNCNameType("XQUERY_KEYWORD_OR_NCNAME_XQUERY");

    TokenSet STRING_LITERAL_TOKENS = TokenSet.create(
        STRING_LITERAL_START,
        STRING_LITERAL_CONTENTS,
        STRING_LITERAL_END,
            ESCAPED_CHARACTER,
        CHARACTER_REFERENCE,
        PREDEFINED_ENTITY_REFERENCE,
        PARTIAL_ENTITY_REFERENCE);

    TokenSet COMMENT_TOKENS = TokenSet.create(COMMENT, XML_COMMENT);
}
