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

    IElementType NCNAME = new IElementType("XQUERY_NCNAME_TOKEN", XQuery.INSTANCE);

    TokenSet WHITESPACE_TOKENS = TokenSet.create(WHITE_SPACE);
    TokenSet COMMENT_TOKENS = TokenSet.create(COMMENT, PARTIAL_COMMENT);
    TokenSet STRING_LITERAL_TOKENS = TokenSet.create(
        STRING_LITERAL_START,
        STRING_LITERAL_CONTENTS,
        STRING_LITERAL_END,
        STRING_LITERAL_ESCAPED_CHARACTER,
        CHARACTER_REFERENCE,
        PREDEFINED_ENTITY_REFERENCE,
        PARTIAL_ENTITY_REFERENCE);
}
