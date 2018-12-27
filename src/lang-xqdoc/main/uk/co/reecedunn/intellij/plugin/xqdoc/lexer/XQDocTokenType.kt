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
package uk.co.reecedunn.intellij.plugin.xqdoc.lexer

import com.intellij.lang.Language
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

object XQDocTokenType {
    val XQDoc = Language.ANY

    val WHITE_SPACE: IElementType = TokenType.WHITE_SPACE
    val TRIM = IElementType("XQDOC_TRIM", XQDoc)

    val XQDOC_COMMENT_MARKER = IElementType("XQDOC_COMMENT_MARKER", XQDoc)
    val CONTENTS = IElementType("XQDOC_CONTENTS", XQDoc)

    val TAG_MARKER = IElementType("XQDOC_TAG_MARKER", XQDoc)
    val TAG = IElementType("XQDOC_TAG", XQDoc)

    val T_AUTHOR = IElementType("XQDOC_T_AUTHOR", XQDoc)
    val T_DEPRECATED = IElementType("XQDOC_T_DEPRECATED", XQDoc)
    val T_ERROR = IElementType("XQDOC_T_ERROR", XQDoc)
    val T_PARAM = IElementType("XQDOC_T_PARAM", XQDoc)
    val T_RETURN = IElementType("XQDOC_T_RETURN", XQDoc)
    val T_SEE = IElementType("XQDOC_T_SEE", XQDoc)
    val T_SINCE = IElementType("XQDOC_T_SINCE", XQDoc)
    val T_VERSION = IElementType("XQDOC_T_VERSION", XQDoc)

    val VARIABLE_INDICATOR = IElementType("XQDOC_VARIABLE_INDICATOR", XQDoc)
    val NCNAME = IElementType("XQDOC_NCNAME", XQDoc)

    val OPEN_XML_TAG = IElementType("XQDOC_OPEN_XML_TAG_TOKEN", XQDoc)
    val END_XML_TAG = IElementType("XQUERY_END_XML_TAG_TOKEN", XQDoc)
    val CLOSE_XML_TAG = IElementType("XQUERY_CLOSE_XML_TAG_TOKEN", XQDoc)
    val SELF_CLOSING_XML_TAG = IElementType("XQDOC_SELF_CLOSING_XML_TAG_TOKEN", XQDoc)

    val XML_TAG = IElementType("XQDOC_XML_TAG", XQDoc)
    val XML_EQUAL = IElementType("XQDOC_XML_EQUAL_TOKEN", XQDoc)
    val XML_ATTRIBUTE_VALUE_START = IElementType("XQDOC_XML_ATTRIBUTE_VALUE_START_TOKEN", XQDoc)
    val XML_ATTRIBUTE_VALUE_CONTENTS = IElementType("XQDOC_XML_ATTRIBUTE_VALUE_CONTENTS_TOKEN", XQDoc)
    val XML_ATTRIBUTE_VALUE_END = IElementType("XQDOC_XML_ATTRIBUTE_VALUE_END_TOKEN", XQDoc)

    val XML_ELEMENT_CONTENTS = IElementType("XQDOC_XML_ELEMENT_CONTENTS_TOKEN", XQDoc)

    val PREDEFINED_ENTITY_REFERENCE = IElementType("XQDOC_PREDEFINED_ENTITY_REFERENCE", XQDoc)
    val PARTIAL_ENTITY_REFERENCE = IElementType("XQDOC_PARTIAL_ENTITY_REFERENCE", XQDoc)
    val EMPTY_ENTITY_REFERENCE = IElementType("XQDOC_EMPTY_ENTITY_REFERENCE", XQDoc)
    val CHARACTER_REFERENCE = IElementType("XQDOC_CHARACTER_REFERENCE", XQDoc)

    val INVALID = IElementType("XQDOC_INVALID_TOKEN", XQDoc)
}
