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

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocTokenType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

internal object XQuerySyntaxHighlighterKeys {
    val BAD_CHARACTER_KEYS: Array<out TextAttributesKey> = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.BAD_CHARACTER
    )

    val ENTITY_REFERENCE_KEYS: Array<out TextAttributesKey> = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.ENTITY_REFERENCE
    )

    val ESCAPED_CHARACTER_KEYS: Array<out TextAttributesKey> = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.ESCAPED_CHARACTER
    )

    val IDENTIFIER_KEYS: Array<out TextAttributesKey> = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.IDENTIFIER
    )

    val KEYWORD_KEYS: Array<out TextAttributesKey> = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.KEYWORD
    )

    val ANNOTATION_KEYS: Array<out TextAttributesKey> = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.ANNOTATION
    )

    val NUMBER_KEYS: Array<out TextAttributesKey> = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.NUMBER
    )

    val STRING_KEYS: Array<out TextAttributesKey> = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.STRING
    )

    val COMMENT_KEYS: Array<out TextAttributesKey> = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.COMMENT
    )

    val XQDOC_TAG_KEYS: Array<out TextAttributesKey> = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.COMMENT,
        XQuerySyntaxHighlighterColors.XQDOC_TAG
    )

    val XQDOC_TAG_VALUE_KEYS: Array<out TextAttributesKey> = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.COMMENT,
        XQuerySyntaxHighlighterColors.XQDOC_TAG_VALUE
    )

    val XQDOC_MARKUP_KEYS: Array<out TextAttributesKey> = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.COMMENT,
        XQuerySyntaxHighlighterColors.XQDOC_MARKUP
    )

    val XML_TAG_KEYS: Array<out TextAttributesKey> = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.XML_TAG
    )

    val XML_TAG_NAME_KEYS: Array<out TextAttributesKey> = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.XML_TAG,
        XQuerySyntaxHighlighterColors.XML_TAG_NAME
    )

    val XML_ATTRIBUTE_NAME_KEYS: Array<out TextAttributesKey> = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.XML_TAG,
        XQuerySyntaxHighlighterColors.XML_ATTRIBUTE_NAME
    )

    val XML_ATTRIBUTE_VALUE_KEYS: Array<out TextAttributesKey> = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.XML_TAG,
        XQuerySyntaxHighlighterColors.XML_ATTRIBUTE_VALUE
    )

    val XML_ENTITY_REFERENCE_KEYS: Array<out TextAttributesKey> = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.XML_TAG,
        XQuerySyntaxHighlighterColors.XML_ENTITY_REFERENCE
    )

    val XML_ESCAPED_CHARACTER_KEYS: Array<out TextAttributesKey> = SyntaxHighlighterBase.pack(
        XQuerySyntaxHighlighterColors.XML_TAG,
        XQuerySyntaxHighlighterColors.XML_ESCAPED_CHARACTER
    )

    val KEYS = mapOf(
        XPathTokenType.INTEGER_LITERAL to NUMBER_KEYS,
        XPathTokenType.DECIMAL_LITERAL to NUMBER_KEYS,
        XPathTokenType.DOUBLE_LITERAL to NUMBER_KEYS,
        XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT to NUMBER_KEYS,
        XPathTokenType.STRING_LITERAL_START to STRING_KEYS,
        XPathTokenType.STRING_LITERAL_CONTENTS to STRING_KEYS,
        XPathTokenType.STRING_LITERAL_END to STRING_KEYS,
        XQueryTokenType.STRING_CONSTRUCTOR_START to STRING_KEYS,
        XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS to STRING_KEYS,
        XQueryTokenType.STRING_CONSTRUCTOR_END to STRING_KEYS,
        XPathTokenType.BRACED_URI_LITERAL_START to STRING_KEYS,
        XPathTokenType.BRACED_URI_LITERAL_END to STRING_KEYS,
        XPathTokenType.ESCAPED_CHARACTER to XQuerySyntaxHighlighterKeys.ESCAPED_CHARACTER_KEYS,
        XQueryTokenType.PREDEFINED_ENTITY_REFERENCE to XQuerySyntaxHighlighterKeys.ENTITY_REFERENCE_KEYS,
        XQueryTokenType.PARTIAL_ENTITY_REFERENCE to XQuerySyntaxHighlighterKeys.ENTITY_REFERENCE_KEYS,
        XQueryTokenType.EMPTY_ENTITY_REFERENCE to XQuerySyntaxHighlighterKeys.ENTITY_REFERENCE_KEYS,
        XQueryTokenType.CHARACTER_REFERENCE to XQuerySyntaxHighlighterKeys.ENTITY_REFERENCE_KEYS,
        XPathTokenType.BAD_CHARACTER to BAD_CHARACTER_KEYS,
        XPathTokenType.NCNAME to XQuerySyntaxHighlighterKeys.IDENTIFIER_KEYS,
        XPathTokenType.COMMENT_START_TAG to COMMENT_KEYS,
        XPathTokenType.COMMENT to COMMENT_KEYS,
        XPathTokenType.COMMENT_END_TAG to COMMENT_KEYS,
        XQueryTokenType.XML_COMMENT_END_TAG to COMMENT_KEYS,
        XQueryTokenType.XML_COMMENT to COMMENT_KEYS,
        XQueryTokenType.XML_COMMENT_START_TAG to COMMENT_KEYS,
        XQDocTokenType.XQDOC_COMMENT_MARKER to COMMENT_KEYS,
        XQDocTokenType.CONTENTS to COMMENT_KEYS,
        XQDocTokenType.TRIM to COMMENT_KEYS,
        XQDocTokenType.XML_ELEMENT_CONTENTS to COMMENT_KEYS,
        XQueryTokenType.ANNOTATION_INDICATOR to XQuerySyntaxHighlighterKeys.ANNOTATION_KEYS,
        XQueryTokenType.K_PRIVATE to XQuerySyntaxHighlighterKeys.ANNOTATION_KEYS,
        XQueryTokenType.K_PUBLIC to XQuerySyntaxHighlighterKeys.ANNOTATION_KEYS,
        XQueryTokenType.K_SIMPLE to XQuerySyntaxHighlighterKeys.ANNOTATION_KEYS,
        XQueryTokenType.K_SEQUENTIAL to XQuerySyntaxHighlighterKeys.ANNOTATION_KEYS,
        XQueryTokenType.K_UPDATING to XQuerySyntaxHighlighterKeys.ANNOTATION_KEYS,
        XQueryTokenType.OPEN_XML_TAG to XQuerySyntaxHighlighterKeys.XML_TAG_KEYS,
        XQueryTokenType.END_XML_TAG to XQuerySyntaxHighlighterKeys.XML_TAG_KEYS,
        XQueryTokenType.CLOSE_XML_TAG to XQuerySyntaxHighlighterKeys.XML_TAG_KEYS,
        XQueryTokenType.SELF_CLOSING_XML_TAG to XQuerySyntaxHighlighterKeys.XML_TAG_KEYS,
        XQueryTokenType.XML_WHITE_SPACE to XQuerySyntaxHighlighterKeys.XML_TAG_KEYS,
        XQueryTokenType.XML_TAG_NCNAME to XQuerySyntaxHighlighterKeys.XML_TAG_NAME_KEYS,
        XQueryTokenType.XML_TAG_QNAME_SEPARATOR to XQuerySyntaxHighlighterKeys.XML_TAG_NAME_KEYS,
        XQueryTokenType.XML_EQUAL to XQuerySyntaxHighlighterKeys.XML_ATTRIBUTE_NAME_KEYS,
        XQueryTokenType.XML_ATTRIBUTE_NCNAME to XQuerySyntaxHighlighterKeys.XML_ATTRIBUTE_NAME_KEYS,
        XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR to XQuerySyntaxHighlighterKeys.XML_ATTRIBUTE_NAME_KEYS,
        XQueryTokenType.XML_ATTRIBUTE_VALUE_START to XQuerySyntaxHighlighterKeys.XML_ATTRIBUTE_VALUE_KEYS,
        XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS to XQuerySyntaxHighlighterKeys.XML_ATTRIBUTE_VALUE_KEYS,
        XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE to XQuerySyntaxHighlighterKeys.XML_ATTRIBUTE_VALUE_KEYS,
        XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE to XQuerySyntaxHighlighterKeys.XML_ATTRIBUTE_VALUE_KEYS,
        XQueryTokenType.XML_ATTRIBUTE_VALUE_END to XQuerySyntaxHighlighterKeys.XML_ATTRIBUTE_VALUE_KEYS,
        XQueryTokenType.XML_ESCAPED_CHARACTER to XQuerySyntaxHighlighterKeys.XML_ESCAPED_CHARACTER_KEYS,
        XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE to XQuerySyntaxHighlighterKeys.XML_ENTITY_REFERENCE_KEYS,
        XQueryTokenType.XML_CHARACTER_REFERENCE to XQuerySyntaxHighlighterKeys.XML_ENTITY_REFERENCE_KEYS,
        XQDocTokenType.TAG_MARKER to XQuerySyntaxHighlighterKeys.XQDOC_TAG_KEYS,
        XQDocTokenType.TAG to XQuerySyntaxHighlighterKeys.XQDOC_TAG_KEYS,
        XQDocTokenType.T_AUTHOR to XQuerySyntaxHighlighterKeys.XQDOC_TAG_KEYS,
        XQDocTokenType.T_DEPRECATED to XQuerySyntaxHighlighterKeys.XQDOC_TAG_KEYS,
        XQDocTokenType.T_ERROR to XQuerySyntaxHighlighterKeys.XQDOC_TAG_KEYS,
        XQDocTokenType.T_PARAM to XQuerySyntaxHighlighterKeys.XQDOC_TAG_KEYS,
        XQDocTokenType.T_RETURN to XQuerySyntaxHighlighterKeys.XQDOC_TAG_KEYS,
        XQDocTokenType.T_SEE to XQuerySyntaxHighlighterKeys.XQDOC_TAG_KEYS,
        XQDocTokenType.T_SINCE to XQuerySyntaxHighlighterKeys.XQDOC_TAG_KEYS,
        XQDocTokenType.T_VERSION to XQuerySyntaxHighlighterKeys.XQDOC_TAG_KEYS,
        XQDocTokenType.VARIABLE_INDICATOR to XQuerySyntaxHighlighterKeys.XQDOC_TAG_VALUE_KEYS,
        XQDocTokenType.NCNAME to XQuerySyntaxHighlighterKeys.XQDOC_TAG_VALUE_KEYS,
        XQDocTokenType.OPEN_XML_TAG to XQuerySyntaxHighlighterKeys.XQDOC_MARKUP_KEYS,
        XQDocTokenType.END_XML_TAG to XQuerySyntaxHighlighterKeys.XQDOC_MARKUP_KEYS,
        XQDocTokenType.CLOSE_XML_TAG to XQuerySyntaxHighlighterKeys.XQDOC_MARKUP_KEYS,
        XQDocTokenType.SELF_CLOSING_XML_TAG to XQuerySyntaxHighlighterKeys.XQDOC_MARKUP_KEYS,
        XQDocTokenType.XML_TAG to XQuerySyntaxHighlighterKeys.XQDOC_MARKUP_KEYS,
        XQDocTokenType.XML_EQUAL to XQuerySyntaxHighlighterKeys.XQDOC_MARKUP_KEYS,
        XQDocTokenType.XML_ATTRIBUTE_VALUE_START to XQuerySyntaxHighlighterKeys.XQDOC_MARKUP_KEYS,
        XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS to XQuerySyntaxHighlighterKeys.XQDOC_MARKUP_KEYS,
        XQDocTokenType.XML_ATTRIBUTE_VALUE_END to XQuerySyntaxHighlighterKeys.XQDOC_MARKUP_KEYS,
        XQDocTokenType.PREDEFINED_ENTITY_REFERENCE to XQuerySyntaxHighlighterKeys.XQDOC_MARKUP_KEYS,
        XQDocTokenType.PARTIAL_ENTITY_REFERENCE to XQuerySyntaxHighlighterKeys.XQDOC_MARKUP_KEYS,
        XQDocTokenType.EMPTY_ENTITY_REFERENCE to XQuerySyntaxHighlighterKeys.XQDOC_MARKUP_KEYS,
        XQDocTokenType.CHARACTER_REFERENCE to XQuerySyntaxHighlighterKeys.XQDOC_MARKUP_KEYS,
        XQDocTokenType.INVALID to XQuerySyntaxHighlighterKeys.XQDOC_MARKUP_KEYS
    )
}
