/*
 * Copyright (C) 2016; 2018 Reece H. Dunn
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

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lang.XQuery

object XQDocTokenType {
    val WHITE_SPACE: IElementType = TokenType.WHITE_SPACE
    val TRIM: IElementType = IElementType("XQDOC_TRIM", XQuery)

    val XQDOC_COMMENT_MARKER: IElementType = IElementType("XQDOC_COMMENT_MARKER", XQuery)
    val CONTENTS: IElementType = IElementType("XQDOC_CONTENTS", XQuery)

    val TAG_MARKER: IElementType = IElementType("XQDOC_TAG_MARKER", XQuery)
    val TAG: IElementType = IElementType("XQDOC_TAG", XQuery)

    val T_AUTHOR: IElementType = IElementType("XQDOC_T_AUTHOR", XQuery)
    val T_DEPRECATED: IElementType = IElementType("XQDOC_T_DEPRECATED", XQuery)
    val T_ERROR: IElementType = IElementType("XQDOC_T_ERROR", XQuery)
    val T_PARAM: IElementType = IElementType("XQDOC_T_PARAM", XQuery)
    val T_RETURN: IElementType = IElementType("XQDOC_T_RETURN", XQuery)
    val T_SEE: IElementType = IElementType("XQDOC_T_SEE", XQuery)
    val T_SINCE: IElementType = IElementType("XQDOC_T_SINCE", XQuery)
    val T_VERSION: IElementType = IElementType("XQDOC_T_VERSION", XQuery)

    val VARIABLE_INDICATOR: IElementType = IElementType("XQDOC_VARIABLE_INDICATOR", XQuery)
    val NCNAME: IElementType = IElementType("XQDOC_NCNAME", XQuery)

    val OPEN_XML_TAG: IElementType = IElementType("XQDOC_OPEN_XML_TAG_TOKEN", XQuery)
    val END_XML_TAG: IElementType = IElementType("XQUERY_END_XML_TAG_TOKEN", XQuery)
    val CLOSE_XML_TAG: IElementType = IElementType("XQUERY_CLOSE_XML_TAG_TOKEN", XQuery)
    val SELF_CLOSING_XML_TAG: IElementType = IElementType("XQDOC_SELF_CLOSING_XML_TAG_TOKEN", XQuery)

    val XML_TAG: IElementType = IElementType("XQDOC_XML_TAG", XQuery)
    val XML_EQUAL: IElementType = IElementType("XQDOC_XML_EQUAL_TOKEN", XQuery)
    val XML_ATTRIBUTE_VALUE_START: IElementType = IElementType("XQDOC_XML_ATTRIBUTE_VALUE_START_TOKEN", XQuery)
    val XML_ATTRIBUTE_VALUE_CONTENTS: IElementType = IElementType("XQDOC_XML_ATTRIBUTE_VALUE_CONTENTS_TOKEN", XQuery)
    val XML_ATTRIBUTE_VALUE_END: IElementType = IElementType("XQDOC_XML_ATTRIBUTE_VALUE_END_TOKEN", XQuery)

    val XML_ELEMENT_CONTENTS: IElementType = IElementType("XQDOC_XML_ELEMENT_CONTENTS_TOKEN", XQuery)

    val PREDEFINED_ENTITY_REFERENCE: IElementType = IElementType("XQDOC_PREDEFINED_ENTITY_REFERENCE", XQuery)
    val PARTIAL_ENTITY_REFERENCE: IElementType = IElementType("XQDOC_PARTIAL_ENTITY_REFERENCE", XQuery)
    val EMPTY_ENTITY_REFERENCE: IElementType = IElementType("XQDOC_EMPTY_ENTITY_REFERENCE", XQuery)
    val CHARACTER_REFERENCE: IElementType = IElementType("XQDOC_CHARACTER_REFERENCE", XQuery)

    val INVALID: IElementType = IElementType("XQDOC_INVALID_TOKEN", XQuery)

    val XQDOC_TOKENS: TokenSet = TokenSet.create(
        WHITE_SPACE, TRIM,
        XQDOC_COMMENT_MARKER, CONTENTS,
        TAG_MARKER, TAG,
        T_AUTHOR, T_DEPRECATED, T_ERROR, T_PARAM, T_RETURN, T_SEE, T_SINCE, T_VERSION,
        VARIABLE_INDICATOR, NCNAME,
        OPEN_XML_TAG, END_XML_TAG, CLOSE_XML_TAG, SELF_CLOSING_XML_TAG,
        XML_TAG, XML_EQUAL, XML_ATTRIBUTE_VALUE_START, XML_ATTRIBUTE_VALUE_CONTENTS, XML_ATTRIBUTE_VALUE_END,
        XML_ELEMENT_CONTENTS,
        PREDEFINED_ENTITY_REFERENCE, PARTIAL_ENTITY_REFERENCE, EMPTY_ENTITY_REFERENCE, CHARACTER_REFERENCE,
        INVALID
    )

    val DESCRIPTION_LINE_TOKENS: TokenSet = TokenSet.create(
        CONTENTS,
        WHITE_SPACE,
        OPEN_XML_TAG,
        END_XML_TAG,
        CLOSE_XML_TAG,
        SELF_CLOSING_XML_TAG,
        XML_TAG,
        XML_EQUAL,
        XML_ATTRIBUTE_VALUE_START,
        XML_ATTRIBUTE_VALUE_CONTENTS,
        XML_ATTRIBUTE_VALUE_END,
        XML_ELEMENT_CONTENTS,
        CHARACTER_REFERENCE,
        PREDEFINED_ENTITY_REFERENCE,
        PARTIAL_ENTITY_REFERENCE,
        EMPTY_ENTITY_REFERENCE
    )
}
