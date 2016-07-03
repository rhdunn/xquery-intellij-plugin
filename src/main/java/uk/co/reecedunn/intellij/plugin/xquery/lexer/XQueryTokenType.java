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
    IElementType INTEGER_LITERAL = new IElementType("INTEGER_LITERAL", XQuery.INSTANCE);
    IElementType DECIMAL_LITERAL = new IElementType("DECIMAL_LITERAL", XQuery.INSTANCE);
    IElementType DOUBLE_LITERAL = new IElementType("DOUBLE_LITERAL", XQuery.INSTANCE);
    IElementType PARTIAL_DOUBLE_LITERAL_EXPONENT = new IElementType("PARTIAL_DOUBLE_LITERAL_EXPONENT", XQuery.INSTANCE);

    IElementType STRING_LITERAL_START = new IElementType("STRING_LITERAL_START", XQuery.INSTANCE);
    IElementType STRING_LITERAL_CONTENTS = new IElementType("STRING_LITERAL_CONTENTS", XQuery.INSTANCE);
    IElementType STRING_LITERAL_END = new IElementType("STRING_LITERAL_END", XQuery.INSTANCE);
    IElementType STRING_LITERAL_ESCAPED_CHARACTER = new IElementType("STRING_LITERAL_ESCAPED_CHARACTER", XQuery.INSTANCE);

    IElementType CHARACTER_REFERENCE = new IElementType("CHARACTER_REFERENCE", XQuery.INSTANCE);
    IElementType PREDEFINED_ENTITY_REFERENCE = new IElementType("PREDEFINED_ENTITY_REFERENCE", XQuery.INSTANCE);
    IElementType PARTIAL_ENTITY_REFERENCE = new IElementType("PARTIAL_ENTITY_REFERENCE", XQuery.INSTANCE);

    IElementType NCNAME = new IElementType("NCNAME", XQuery.INSTANCE);

    TokenSet WHITESPACE_TOKENS = TokenSet.create(WHITE_SPACE);
    TokenSet COMMENT_TOKENS = TokenSet.EMPTY;
    TokenSet STRING_LITERAL_TOKENS = TokenSet.create(
        STRING_LITERAL_START,
        STRING_LITERAL_CONTENTS,
        STRING_LITERAL_END,
        STRING_LITERAL_ESCAPED_CHARACTER,
        CHARACTER_REFERENCE,
        PREDEFINED_ENTITY_REFERENCE,
        PARTIAL_ENTITY_REFERENCE);
}
