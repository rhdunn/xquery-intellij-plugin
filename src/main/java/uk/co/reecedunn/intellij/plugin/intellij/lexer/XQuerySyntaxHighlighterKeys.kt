/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.lexer

import com.intellij.openapi.fileTypes.SyntaxHighlighterBase

internal object XQuerySyntaxHighlighterKeys {
    val BAD_CHARACTER_KEYS = SyntaxHighlighterBase.pack(XQuerySyntaxHighlighterColors.BAD_CHARACTER)

    val ENTITY_REFERENCE_KEYS = SyntaxHighlighterBase.pack(XQuerySyntaxHighlighterColors.ENTITY_REFERENCE)

    val ESCAPED_CHARACTER_KEYS = SyntaxHighlighterBase.pack(XQuerySyntaxHighlighterColors.ESCAPED_CHARACTER)

    val IDENTIFIER_KEYS = SyntaxHighlighterBase.pack(XQuerySyntaxHighlighterColors.IDENTIFIER)

    val KEYWORD_KEYS = SyntaxHighlighterBase.pack(XQuerySyntaxHighlighterColors.KEYWORD)

    val ANNOTATION_KEYS = SyntaxHighlighterBase.pack(XQuerySyntaxHighlighterColors.ANNOTATION)

    val NUMBER_KEYS = SyntaxHighlighterBase.pack(XQuerySyntaxHighlighterColors.NUMBER)

    val STRING_KEYS = SyntaxHighlighterBase.pack(XQuerySyntaxHighlighterColors.STRING)

    val COMMENT_KEYS = SyntaxHighlighterBase.pack(XQuerySyntaxHighlighterColors.COMMENT)

    val XQDOC_TAG_KEYS = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.COMMENT,
        XQuerySyntaxHighlighterColors.XQDOC_TAG
    )

    val XQDOC_TAG_VALUE_KEYS = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.COMMENT,
        XQuerySyntaxHighlighterColors.XQDOC_TAG_VALUE
    )

    val XQDOC_MARKUP_KEYS = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.COMMENT,
        XQuerySyntaxHighlighterColors.XQDOC_MARKUP
    )

    val XML_TAG_KEYS = SyntaxHighlighterBase.pack(XQuerySyntaxHighlighterColors.XML_TAG)

    val XML_TAG_NAME_KEYS = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.XML_TAG,
        XQuerySyntaxHighlighterColors.XML_TAG_NAME
    )

    val XML_ATTRIBUTE_NAME_KEYS = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.XML_TAG,
        XQuerySyntaxHighlighterColors.XML_ATTRIBUTE_NAME
    )

    val XML_ATTRIBUTE_VALUE_KEYS = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.XML_TAG,
        XQuerySyntaxHighlighterColors.XML_ATTRIBUTE_VALUE
    )

    val XML_ENTITY_REFERENCE_KEYS = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.XML_TAG,
        XQuerySyntaxHighlighterColors.XML_ENTITY_REFERENCE
    )

    val XML_ESCAPED_CHARACTER_KEYS = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.XML_TAG,
        XQuerySyntaxHighlighterColors.XML_ESCAPED_CHARACTER
    )
}
