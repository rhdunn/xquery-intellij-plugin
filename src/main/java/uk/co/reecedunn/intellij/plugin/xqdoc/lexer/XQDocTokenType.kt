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
package uk.co.reecedunn.intellij.plugin.xqdoc.lexer

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery

object XQDocTokenType {
    val WHITE_SPACE: IElementType = TokenType.WHITE_SPACE

    val XQDOC_COMMENT_MARKER = IElementType("XQDOC_COMMENT_MARKER", XQuery)

    val COMMENT_CONTENTS = IElementType("XQDOC_COMMENT_CONTENTS", XQuery)
    val CONTENTS = IElementType("XQDOC_CONTENTS", XQuery)

    val TRIM = IElementType("XQDOC_TRIM", XQuery)

    val TAG_MARKER = IElementType("XQDOC_TAG_MARKER", XQuery)
    val TAG = IElementType("XQDOC_TAG", XQuery)

    val T_AUTHOR = IElementType("XQDOC_T_AUTHOR", XQuery)
    val T_DEPRECATED = IElementType("XQDOC_T_DEPRECATED", XQuery)
    val T_ERROR = IElementType("XQDOC_T_ERROR", XQuery)
    val T_PARAM = IElementType("XQDOC_T_PARAM", XQuery)
    val T_RETURN = IElementType("XQDOC_T_RETURN", XQuery)
    val T_SEE = IElementType("XQDOC_T_SEE", XQuery)
    val T_SINCE = IElementType("XQDOC_T_SINCE", XQuery)
    val T_VERSION = IElementType("XQDOC_T_VERSION", XQuery)

    val VARIABLE_INDICATOR = IElementType("XQDOC_VARIABLE_INDICATOR", XQuery)
    val NCNAME = IElementType("XQDOC_NCNAME", XQuery)

    val OPEN_XML_TAG = IElementType("XQDOC_OPEN_XML_TAG_TOKEN", XQuery)
    val END_XML_TAG = IElementType("XQUERY_END_XML_TAG_TOKEN", XQuery)
    val CLOSE_XML_TAG = IElementType("XQUERY_CLOSE_XML_TAG_TOKEN", XQuery)
    val SELF_CLOSING_XML_TAG = IElementType("XQDOC_SELF_CLOSING_XML_TAG_TOKEN", XQuery)

    val XML_TAG = IElementType("XQDOC_XML_TAG", XQuery)
    val XML_EQUAL = IElementType("XQDOC_XML_EQUAL_TOKEN", XQuery)
    val XML_ATTRIBUTE_VALUE_START = IElementType("XQDOC_XML_ATTRIBUTE_VALUE_START_TOKEN", XQuery)
    val XML_ATTRIBUTE_VALUE_CONTENTS = IElementType("XQDOC_XML_ATTRIBUTE_VALUE_CONTENTS_TOKEN", XQuery)
    val XML_ATTRIBUTE_VALUE_END = IElementType("XQDOC_XML_ATTRIBUTE_VALUE_END_TOKEN", XQuery)

    val XML_ELEMENT_CONTENTS = IElementType("XQDOC_XML_ELEMENT_CONTENTS_TOKEN", XQuery)

    val PREDEFINED_ENTITY_REFERENCE = IElementType("XQDOC_PREDEFINED_ENTITY_REFERENCE", XQuery)
    val PARTIAL_ENTITY_REFERENCE = IElementType("XQDOC_PARTIAL_ENTITY_REFERENCE", XQuery)
    val EMPTY_ENTITY_REFERENCE = IElementType("XQDOC_EMPTY_ENTITY_REFERENCE", XQuery)
    val CHARACTER_REFERENCE = IElementType("XQDOC_CHARACTER_REFERENCE", XQuery)

    val INVALID = IElementType("XQDOC_INVALID_TOKEN", XQuery)
}
