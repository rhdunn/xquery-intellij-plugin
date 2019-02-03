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

class XQuerySyntaxHighlighter : SyntaxHighlighterBase() {
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
            return XQuerySyntaxHighlighterKeys.NUMBER_KEYS
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
            return XQuerySyntaxHighlighterKeys.STRING_KEYS
        else if (type === XPathTokenType.ESCAPED_CHARACTER)
            return XQuerySyntaxHighlighterKeys.ESCAPED_CHARACTER_KEYS
        else if (
            type === XQueryTokenType.PREDEFINED_ENTITY_REFERENCE ||
            type === XQueryTokenType.PARTIAL_ENTITY_REFERENCE ||
            type === XQueryTokenType.EMPTY_ENTITY_REFERENCE ||
            type === XQueryTokenType.CHARACTER_REFERENCE
        )
            return XQuerySyntaxHighlighterKeys.ENTITY_REFERENCE_KEYS
        else if (type === XPathTokenType.BAD_CHARACTER)
            return XQuerySyntaxHighlighterKeys.BAD_CHARACTER_KEYS
        else if (type === XPathTokenType.NCNAME)
            return XQuerySyntaxHighlighterKeys.IDENTIFIER_KEYS
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
            return XQuerySyntaxHighlighterKeys.COMMENT_KEYS
        else if (
            type === XQueryTokenType.K_PRIVATE ||
            type === XQueryTokenType.K_PUBLIC ||
            type === XQueryTokenType.K_SIMPLE ||
            type === XQueryTokenType.K_SEQUENTIAL ||
            type === XQueryTokenType.K_UPDATING ||
            type === XQueryTokenType.ANNOTATION_INDICATOR
        )
            return XQuerySyntaxHighlighterKeys.ANNOTATION_KEYS
        else if (type is IKeywordOrNCNameType)
            return XQuerySyntaxHighlighterKeys.KEYWORD_KEYS
        else if (
            type === XQueryTokenType.OPEN_XML_TAG ||
            type === XQueryTokenType.END_XML_TAG ||
            type === XQueryTokenType.CLOSE_XML_TAG ||
            type === XQueryTokenType.SELF_CLOSING_XML_TAG ||
            type === XQueryTokenType.XML_WHITE_SPACE
        )
            return XQuerySyntaxHighlighterKeys.XML_TAG_KEYS
        else if (type === XQueryTokenType.XML_TAG_NCNAME || type === XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
            return XQuerySyntaxHighlighterKeys.XML_TAG_NAME_KEYS
        else if (
            type === XQueryTokenType.XML_EQUAL ||
            type === XQueryTokenType.XML_ATTRIBUTE_NCNAME ||
            type === XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR
        )
            return XQuerySyntaxHighlighterKeys.XML_ATTRIBUTE_NAME_KEYS
        else if (
            type === XQueryTokenType.XML_ATTRIBUTE_VALUE_START ||
            type === XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS ||
            type === XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE ||
            type === XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE ||
            type === XQueryTokenType.XML_ATTRIBUTE_VALUE_END
        )
            return XQuerySyntaxHighlighterKeys.XML_ATTRIBUTE_VALUE_KEYS
        else if (type === XQueryTokenType.XML_ESCAPED_CHARACTER)
            return XQuerySyntaxHighlighterKeys.XML_ESCAPED_CHARACTER_KEYS
        else if (
            type === XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE ||
            type === XQueryTokenType.XML_CHARACTER_REFERENCE
        )
            return XQuerySyntaxHighlighterKeys.XML_ENTITY_REFERENCE_KEYS
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
            return XQuerySyntaxHighlighterKeys.XQDOC_TAG_KEYS
        else if (type === XQDocTokenType.VARIABLE_INDICATOR || type === XQDocTokenType.NCNAME)
            return XQuerySyntaxHighlighterKeys.XQDOC_TAG_VALUE_KEYS
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
            return XQuerySyntaxHighlighterKeys.XQDOC_MARKUP_KEYS
        return SyntaxHighlighterBase.EMPTY
    }
}
