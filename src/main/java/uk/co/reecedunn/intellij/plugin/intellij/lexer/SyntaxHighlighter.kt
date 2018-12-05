/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.XmlHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.core.lexer.CombinedLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.STATE_XQUERY_COMMENT
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocLexer
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocTokenType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.*

class SyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer {
        val lexer = CombinedLexer(XQueryLexer())
        lexer.addState(
            XQueryLexer(), 0x50000000, 0, STATE_MAYBE_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG
        )
        lexer.addState(
            XQueryLexer(), 0x60000000, 0, STATE_START_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_OPEN_XML_TAG
        )
        lexer.addState(
            XQDocLexer(), 0x70000000, STATE_XQUERY_COMMENT, XPathTokenType.COMMENT
        )
        return lexer
    }

    override fun getTokenHighlights(type: IElementType): Array<TextAttributesKey> {
        if (
            type === XPathTokenType.INTEGER_LITERAL ||
            type === XPathTokenType.DECIMAL_LITERAL ||
            type === XPathTokenType.DOUBLE_LITERAL ||
            type === XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT
        )
            return NUMBER_KEYS
        else if (
            type === XPathTokenType.STRING_LITERAL_START ||
            type === XPathTokenType.STRING_LITERAL_CONTENTS ||
            type === XPathTokenType.STRING_LITERAL_END ||
            type === XQueryTokenType.STRING_CONSTRUCTOR_START ||
            type === XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS ||
            type === XQueryTokenType.STRING_CONSTRUCTOR_END ||
            type === XPathTokenType.BRACED_URI_LITERAL_START ||
            type === XPathTokenType.BRACED_URI_LITERAL_END
        )
            return STRING_KEYS
        else if (type === XPathTokenType.ESCAPED_CHARACTER)
            return ESCAPED_CHARACTER_KEYS
        else if (
            type === XQueryTokenType.PREDEFINED_ENTITY_REFERENCE ||
            type === XQueryTokenType.PARTIAL_ENTITY_REFERENCE ||
            type === XQueryTokenType.EMPTY_ENTITY_REFERENCE ||
            type === XQueryTokenType.CHARACTER_REFERENCE
        )
            return ENTITY_REFERENCE_KEYS
        else if (type === XPathTokenType.BAD_CHARACTER)
            return BAD_CHARACTER_KEYS
        else if (type === XPathTokenType.NCNAME)
            return IDENTIFIER_KEYS
        else if (
            type === XPathTokenType.COMMENT_START_TAG ||
            type === XPathTokenType.COMMENT ||
            type === XPathTokenType.COMMENT_END_TAG ||
            type === XQueryTokenType.XML_COMMENT_END_TAG ||
            type === XQueryTokenType.XML_COMMENT ||
            type === XQueryTokenType.XML_COMMENT_START_TAG ||
            type === XQDocTokenType.XQDOC_COMMENT_MARKER ||
            type === XQDocTokenType.CONTENTS ||
            type === XQDocTokenType.TRIM ||
            type === XQDocTokenType.XML_ELEMENT_CONTENTS
        )
            return COMMENT_KEYS
        else if (
            type === XQueryTokenType.K_PRIVATE ||
            type === XQueryTokenType.K_PUBLIC ||
            type === XQueryTokenType.K_SIMPLE ||
            type === XQueryTokenType.K_SEQUENTIAL ||
            type === XQueryTokenType.K_UPDATING ||
            type === XQueryTokenType.ANNOTATION_INDICATOR
        )
            return ANNOTATION_KEYS
        else if (type is IKeywordOrNCNameType)
            return KEYWORD_KEYS
        else if (
            type === XQueryTokenType.OPEN_XML_TAG ||
            type === XQueryTokenType.END_XML_TAG ||
            type === XQueryTokenType.CLOSE_XML_TAG ||
            type === XQueryTokenType.SELF_CLOSING_XML_TAG ||
            type === XQueryTokenType.XML_WHITE_SPACE
        )
            return XML_TAG_KEYS
        else if (type === XQueryTokenType.XML_TAG_NCNAME || type === XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
            return XML_TAG_NAME_KEYS
        else if (
            type === XQueryTokenType.XML_EQUAL ||
            type === XQueryTokenType.XML_ATTRIBUTE_NCNAME ||
            type === XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR
        )
            return XML_ATTRIBUTE_NAME_KEYS
        else if (
            type === XQueryTokenType.XML_ATTRIBUTE_VALUE_START ||
            type === XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS ||
            type === XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE ||
            type === XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE ||
            type === XQueryTokenType.XML_ATTRIBUTE_VALUE_END
        )
            return XML_ATTRIBUTE_VALUE_KEYS
        else if (type === XQueryTokenType.XML_ESCAPED_CHARACTER)
            return XML_ESCAPED_CHARACTER_KEYS
        else if (
            type === XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE ||
            type === XQueryTokenType.XML_CHARACTER_REFERENCE
        )
            return XML_ENTITY_REFERENCE_KEYS
        else if (
            type === XQDocTokenType.TAG_MARKER ||
            type === XQDocTokenType.TAG ||
            type === XQDocTokenType.T_AUTHOR ||
            type === XQDocTokenType.T_DEPRECATED ||
            type === XQDocTokenType.T_ERROR ||
            type === XQDocTokenType.T_PARAM ||
            type === XQDocTokenType.T_RETURN ||
            type === XQDocTokenType.T_SEE ||
            type === XQDocTokenType.T_SINCE ||
            type === XQDocTokenType.T_VERSION
        )
            return XQDOC_TAG_KEYS
        else if (type === XQDocTokenType.VARIABLE_INDICATOR || type === XQDocTokenType.NCNAME)
            return XQDOC_TAG_VALUE_KEYS
        else if (
            type === XQDocTokenType.OPEN_XML_TAG ||
            type === XQDocTokenType.END_XML_TAG ||
            type === XQDocTokenType.CLOSE_XML_TAG ||
            type === XQDocTokenType.SELF_CLOSING_XML_TAG ||
            type === XQDocTokenType.XML_TAG ||
            type === XQDocTokenType.XML_EQUAL ||
            type === XQDocTokenType.XML_ATTRIBUTE_VALUE_START ||
            type === XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS ||
            type === XQDocTokenType.XML_ATTRIBUTE_VALUE_END ||
            type === XQDocTokenType.PREDEFINED_ENTITY_REFERENCE ||
            type === XQDocTokenType.PARTIAL_ENTITY_REFERENCE ||
            type === XQDocTokenType.EMPTY_ENTITY_REFERENCE ||
            type === XQDocTokenType.CHARACTER_REFERENCE ||
            type === XQDocTokenType.INVALID
        )
            return XQDOC_MARKUP_KEYS
        return SyntaxHighlighterBase.EMPTY
    }

    companion object {
        val BAD_CHARACTER = TextAttributesKey.createTextAttributesKey("XQUERY_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)
        val COMMENT = TextAttributesKey.createTextAttributesKey("XPATH_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)
        val ENTITY_REFERENCE = TextAttributesKey.createTextAttributesKey("XQUERY_ENTITY_REFERENCE", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE)
        val ESCAPED_CHARACTER = TextAttributesKey.createTextAttributesKey("XQUERY_ESCAPED_CHARACTER", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE)
        val IDENTIFIER = TextAttributesKey.createTextAttributesKey("XQUERY_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
        val KEYWORD = TextAttributesKey.createTextAttributesKey("XQUERY_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val ANNOTATION = TextAttributesKey.createTextAttributesKey("XQUERY_ANNOTATION", DefaultLanguageHighlighterColors.METADATA)
        val NUMBER = TextAttributesKey.createTextAttributesKey("XQUERY_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val STRING = TextAttributesKey.createTextAttributesKey("XQUERY_STRING", DefaultLanguageHighlighterColors.STRING)
        val NS_PREFIX = TextAttributesKey.createTextAttributesKey("XQUERY_NS_PREFIX", DefaultLanguageHighlighterColors.INSTANCE_FIELD)

        val XQDOC_TAG = TextAttributesKey.createTextAttributesKey("XQUERY_XQDOC_TAG", DefaultLanguageHighlighterColors.DOC_COMMENT_TAG)
        val XQDOC_TAG_VALUE = TextAttributesKey.createTextAttributesKey("XQUERY_XQDOC_TAG_VALUE", DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE)
        val XQDOC_MARKUP = TextAttributesKey.createTextAttributesKey("XQUERY_XQDOC_MARKUP", DefaultLanguageHighlighterColors.DOC_COMMENT_MARKUP)

        val XML_TAG = TextAttributesKey.createTextAttributesKey("XQUERY_XML_TAG", XmlHighlighterColors.XML_TAG)
        val XML_TAG_NAME = TextAttributesKey.createTextAttributesKey("XQUERY_XML_TAG_NAME", XmlHighlighterColors.XML_TAG_NAME)
        val XML_ATTRIBUTE_NAME = TextAttributesKey.createTextAttributesKey("XQUERY_XML_ATTRIBUTE_NAME", XmlHighlighterColors.XML_ATTRIBUTE_NAME)
        val XML_ATTRIBUTE_VALUE = TextAttributesKey.createTextAttributesKey("XQUERY_XML_ATTRIBUTE_VALUE", XmlHighlighterColors.XML_ATTRIBUTE_VALUE)
        val XML_ENTITY_REFERENCE = TextAttributesKey.createTextAttributesKey("XQUERY_XML_ENTITY_REFERENCE", DefaultLanguageHighlighterColors.MARKUP_ENTITY)
        val XML_ESCAPED_CHARACTER = TextAttributesKey.createTextAttributesKey("XQUERY_XML_ESCAPED_CHARACTER", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE)

        private val BAD_CHARACTER_KEYS = SyntaxHighlighterBase.pack(BAD_CHARACTER)
        private val ENTITY_REFERENCE_KEYS = SyntaxHighlighterBase.pack(ENTITY_REFERENCE)
        private val ESCAPED_CHARACTER_KEYS = SyntaxHighlighterBase.pack(ESCAPED_CHARACTER)
        private val IDENTIFIER_KEYS = SyntaxHighlighterBase.pack(IDENTIFIER)
        private val KEYWORD_KEYS = SyntaxHighlighterBase.pack(KEYWORD)
        private val ANNOTATION_KEYS = SyntaxHighlighterBase.pack(ANNOTATION)
        private val NUMBER_KEYS = SyntaxHighlighterBase.pack(NUMBER)
        private val STRING_KEYS = SyntaxHighlighterBase.pack(STRING)

        private val COMMENT_KEYS = SyntaxHighlighterBase.pack(COMMENT)
        private val XQDOC_TAG_KEYS = SyntaxHighlighterBase.pack(
            COMMENT,
            XQDOC_TAG
        )
        private val XQDOC_TAG_VALUE_KEYS = SyntaxHighlighterBase.pack(
            COMMENT,
            XQDOC_TAG_VALUE
        )
        private val XQDOC_MARKUP_KEYS = SyntaxHighlighterBase.pack(
            COMMENT,
            XQDOC_MARKUP
        )

        private val XML_TAG_KEYS = SyntaxHighlighterBase.pack(XML_TAG)
        private val XML_TAG_NAME_KEYS = SyntaxHighlighterBase.pack(
            XML_TAG,
            XML_TAG_NAME
        )
        private val XML_ATTRIBUTE_NAME_KEYS = SyntaxHighlighterBase.pack(
            XML_TAG,
            XML_ATTRIBUTE_NAME
        )
        private val XML_ATTRIBUTE_VALUE_KEYS = SyntaxHighlighterBase.pack(
            XML_TAG,
            XML_ATTRIBUTE_VALUE
        )
        private val XML_ENTITY_REFERENCE_KEYS = SyntaxHighlighterBase.pack(
            XML_TAG,
            XML_ENTITY_REFERENCE
        )
        private val XML_ESCAPED_CHARACTER_KEYS = SyntaxHighlighterBase.pack(
            XML_TAG,
            XML_ESCAPED_CHARACTER
        )
    }
}
