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
    IElementType COMMENT = new IElementType("XQUERY_COMMENT_TOKEN", XQuery.INSTANCE);
    IElementType COMMENT_END_TAG = new IElementType("XQUERY_COMMENT_END_TAG_TOKEN", XQuery.INSTANCE);
    IElementType PARTIAL_COMMENT = new IElementType("XQUERY_PARTIAL_COMMENT_TOKEN", XQuery.INSTANCE);

    IElementType XML_COMMENT = new IElementType("XQUERY_XML_COMMENT_TOKEN", XQuery.INSTANCE);
    IElementType INCOMPLETE_XML_COMMENT_START_TAG = new IElementType("XQUERY_INCOMPLETE_XML_COMMENT_START_TAG_TOKEN", XQuery.INSTANCE);
    IElementType XML_COMMENT_END_TAG = new IElementType("XQUERY_XML_COMMENT_END_TAG_TOKEN", XQuery.INSTANCE);
    IElementType PARTIAL_XML_COMMENT = new IElementType("XQUERY_XML_PARTIAL_COMMENT_TOKEN", XQuery.INSTANCE);
    IElementType MINUS_MINUS = new IElementType("XQUERY_MINUS_MINUS_TOKEN", XQuery.INSTANCE); // Invalid (Partial) XML Close Tag

    IElementType INTEGER_LITERAL = new IElementType("XQUERY_INTEGER_LITERAL_TOKEN", XQuery.INSTANCE);
    IElementType DECIMAL_LITERAL = new IElementType("XQUERY_DECIMAL_LITERAL_TOKEN", XQuery.INSTANCE);
    IElementType DOUBLE_LITERAL = new IElementType("XQUERY_DOUBLE_LITERAL_TOKEN", XQuery.INSTANCE);
    IElementType PARTIAL_DOUBLE_LITERAL_EXPONENT = new IElementType("XQUERY_PARTIAL_DOUBLE_LITERAL_EXPONENT_TOKEN", XQuery.INSTANCE);

    IElementType STRING_LITERAL_START = new IElementType("XQUERY_STRING_LITERAL_START_TOKEN", XQuery.INSTANCE);
    IElementType STRING_LITERAL_CONTENTS = new IElementType("XQUERY_STRING_LITERAL_CONTENTS_TOKEN", XQuery.INSTANCE);
    IElementType STRING_LITERAL_END = new IElementType("XQUERY_STRING_LITERAL_END_TOKEN", XQuery.INSTANCE);
    IElementType STRING_LITERAL_ESCAPED_CHARACTER = new IElementType("XQUERY_STRING_LITERAL_ESCAPED_CHARACTER_TOKEN", XQuery.INSTANCE);

    IElementType CHARACTER_REFERENCE = new IElementType("XQUERY_CHARACTER_REFERENCE_TOKEN", XQuery.INSTANCE);
    IElementType PREDEFINED_ENTITY_REFERENCE = new IElementType("XQUERY_PREDEFINED_ENTITY_REFERENCE_TOKEN", XQuery.INSTANCE);
    IElementType PARTIAL_ENTITY_REFERENCE = new IElementType("XQUERY_PARTIAL_ENTITY_REFERENCE_TOKEN", XQuery.INSTANCE);
    IElementType ENTITY_REFERENCE_NOT_IN_STRING = new IElementType("XQUERY_ENTITY_REFERENCE_NOT_IN_STRING_TOKEN", XQuery.INSTANCE);

    IElementType NCNAME = new IElementType("XQUERY_NCNAME_TOKEN", XQuery.INSTANCE);

    IElementType PARENTHESIS_OPEN = new IElementType("XQUERY_PARENTHESIS_OPEN_TOKEN", XQuery.INSTANCE);
    IElementType PARENTHESIS_CLOSE = new IElementType("XQUERY_PARENTHESIS_CLOSE_TOKEN", XQuery.INSTANCE);
    IElementType PRAGMA_BEGIN = new IElementType("XQUERY_PRAGMA_BEGIN_TOKEN", XQuery.INSTANCE);
    IElementType PRAGMA_END = new IElementType("XQUERY_PRAGMA_END_TOKEN", XQuery.INSTANCE);
    IElementType NOT_EQUAL = new IElementType("XQUERY_NOT_EQUAL_TOKEN", XQuery.INSTANCE);
    IElementType VARIABLE_INDICATOR = new IElementType("XQUERY_VARIABLE_INDICATOR_TOKEN", XQuery.INSTANCE);
    IElementType STAR = new IElementType("XQUERY_STAR_TOKEN", XQuery.INSTANCE);
    IElementType COMMA = new IElementType("XQUERY_COMMA_TOKEN", XQuery.INSTANCE);
    IElementType MINUS = new IElementType("XQUERY_MINUS_TOKEN", XQuery.INSTANCE);
    IElementType DOT = new IElementType("XQUERY_DOT_TOKEN", XQuery.INSTANCE);
    IElementType SEMICOLON = new IElementType("XQUERY_SEMICOLON_TOKEN", XQuery.INSTANCE);
    IElementType PLUS = new IElementType("XQUERY_PLUS_TOKEN", XQuery.INSTANCE);
    IElementType EQUAL = new IElementType("XQUERY_EQUAL_TOKEN", XQuery.INSTANCE);
    IElementType BLOCK_OPEN = new IElementType("XQUERY_BLOCK_OPEN_TOKEN", XQuery.INSTANCE);
    IElementType BLOCK_CLOSE = new IElementType("XQUERY_BLOCK_CLOSE_TOKEN", XQuery.INSTANCE);
    IElementType LESS_THAN = new IElementType("XQUERY_LESS_THAN_TOKEN", XQuery.INSTANCE);
    IElementType GREATER_THAN = new IElementType("XQUERY_GREATER_THAN_TOKEN", XQuery.INSTANCE);
    IElementType LESS_THAN_OR_EQUAL = new IElementType("XQUERY_LESS_THAN_OR_EQUAL_TOKEN", XQuery.INSTANCE);
    IElementType GREATER_THAN_OR_EQUAL = new IElementType("XQUERY_GREATER_THAN_OR_EQUAL_TOKEN", XQuery.INSTANCE);
    IElementType UNION = new IElementType("XQUERY_UNION_TOKEN", XQuery.INSTANCE);
    IElementType QUESTION = new IElementType("XQUERY_QUESTION_TOKEN", XQuery.INSTANCE);
    IElementType QNAME_SEPARATOR = new IElementType("XQUERY_QNAME_SEPARATOR_TOKEN", XQuery.INSTANCE);
    IElementType AXIS_SEPARATOR = new IElementType("XQUERY_AXIS_SEPARATOR_TOKEN", XQuery.INSTANCE);
    IElementType ASSIGN_EQUAL = new IElementType("XQUERY_ASSIGN_EQUAL_TOKEN", XQuery.INSTANCE);
    IElementType DIRECT_DESCENDANTS_PATH = new IElementType("XQUERY_DIRECT_DESCENDANTS_PATH_TOKEN", XQuery.INSTANCE);
    IElementType ALL_DESCENDANTS_PATH = new IElementType("XQUERY_ALL_DESCENDANTS_PATH_TOKEN", XQuery.INSTANCE);
    IElementType ATTRIBUTE_SELECTOR = new IElementType("XQUERY_ATTRIBUTE_SELECTOR_TOKEN", XQuery.INSTANCE);
    IElementType PREDICATE_BEGIN = new IElementType("XQUERY_PREDICATE_BEGIN_TOKEN", XQuery.INSTANCE);
    IElementType PREDICATE_END = new IElementType("XQUERY_PREDICATE_END_TOKEN", XQuery.INSTANCE);
    IElementType PARENT_SELECTOR = new IElementType("XQUERY_PARENT_SELECTOR_TOKEN", XQuery.INSTANCE);
    IElementType CLOSE_XML_TAG = new IElementType("XQUERY_CLOSE_XML_TAG_TOKEN", XQuery.INSTANCE);
    IElementType SELF_CLOSING_XML_TAG = new IElementType("XQUERY_SELF_CLOSING_XML_TAG_TOKEN", XQuery.INSTANCE);
    IElementType PROCESSING_INSTRUCTION_BEGIN = new IElementType("XQUERY_PROCESSING_INSTRUCTION_BEGIN_TOKEN", XQuery.INSTANCE);
    IElementType PROCESSING_INSTRUCTION_END = new IElementType("XQUERY_PROCESSING_INSTRUCTION_END_TOKEN", XQuery.INSTANCE);
    IElementType NODE_BEFORE = new IElementType("XQUERY_NODE_BEFORE_TOKEN", XQuery.INSTANCE);
    IElementType NODE_AFTER = new IElementType("XQUERY_NODE_AFTER_TOKEN", XQuery.INSTANCE);

    IElementType MAP_OPERATOR = new IElementType("XQUERY_MAP_OPERATOR_TOKEN", XQuery.INSTANCE); // XQuery 3.0
    IElementType FUNCTION_REF_OPERATOR = new IElementType("XQUERY_FUNCTION_REF_OPERATOR_TOKEN", XQuery.INSTANCE); // XQuery 3.0
    IElementType ANNOTATION_INDICATOR = new IElementType("XQUERY_ANNOTATION_INDICATOR_TOKEN", XQuery.INSTANCE); // XQuery 3.0

    TokenSet WHITESPACE_TOKENS = TokenSet.create(WHITE_SPACE);
    TokenSet COMMENT_TOKENS = TokenSet.create(COMMENT, PARTIAL_COMMENT, XML_COMMENT, PARTIAL_XML_COMMENT);
    TokenSet STRING_LITERAL_TOKENS = TokenSet.create(
        STRING_LITERAL_START,
        STRING_LITERAL_CONTENTS,
        STRING_LITERAL_END,
        STRING_LITERAL_ESCAPED_CHARACTER,
        CHARACTER_REFERENCE,
        PREDEFINED_ENTITY_REFERENCE,
        PARTIAL_ENTITY_REFERENCE);
}
