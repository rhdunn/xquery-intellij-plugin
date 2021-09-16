/*
 * Copyright (C) 2016-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.lang.highlighter

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.core.lexer.CombinedLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQDocLexer
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQDocTokenType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

object XQuerySyntaxHighlighter : SyntaxHighlighterBase() {
    // region SyntaxHighlighter

    override fun getHighlightingLexer(): Lexer {
        val lexer = CombinedLexer(XQueryLexer())
        lexer.addState(
            XQueryLexer(), 0x50000000, 0,
            XQueryLexer.STATE_MAYBE_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG
        )
        lexer.addState(
            XQueryLexer(), 0x60000000, 0,
            XQueryLexer.STATE_START_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_OPEN_XML_TAG
        )
        lexer.addState(
            XQDocLexer(), 0x70000000, XPathLexer.STATE_XQUERY_COMMENT, XPathTokenType.COMMENT
        )
        return lexer
    }

    override fun getTokenHighlights(type: IElementType): Array<out TextAttributesKey> {
        val default =
            if (type is IKeywordOrNCNameType && type !== XPathTokenType.K__)
                KEYWORD_KEYS
            else
                TextAttributesKey.EMPTY_ARRAY
        return KEYS.getOrDefault(type, default)
    }

    // endregion
    // region SyntaxHighlighterFactory

    class Factory : SyntaxHighlighterFactory() {
        override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
            return XQuerySyntaxHighlighter
        }
    }

    // endregion
    // region Syntax Highlighting (Lexical Tokens)

    private val BAD_CHARACTER_KEYS = pack(XQuerySyntaxHighlighterColors.BAD_CHARACTER)

    private val COMMENT_KEYS = pack(XQuerySyntaxHighlighterColors.COMMENT)

    private val ENTITY_REFERENCE_KEYS = pack(XQuerySyntaxHighlighterColors.ENTITY_REFERENCE)

    private val ESCAPED_CHARACTER_KEYS = pack(XQuerySyntaxHighlighterColors.ESCAPED_CHARACTER)

    private val IDENTIFIER_KEYS = pack(XQuerySyntaxHighlighterColors.IDENTIFIER)

    private val KEYWORD_KEYS = pack(XQuerySyntaxHighlighterColors.KEYWORD)

    private val NUMBER_KEYS = pack(XQuerySyntaxHighlighterColors.NUMBER)

    private val STRING_KEYS = pack(XQuerySyntaxHighlighterColors.STRING)

    private val XML_ATTRIBUTE_NAME_KEYS = pack(
        XQuerySyntaxHighlighterColors.XML_TAG,
        XQuerySyntaxHighlighterColors.ATTRIBUTE
    )

    private val XML_ATTRIBUTE_VALUE_KEYS = pack(
        XQuerySyntaxHighlighterColors.XML_TAG,
        XQuerySyntaxHighlighterColors.XML_ATTRIBUTE_VALUE
    )

    private val XML_ENTITY_REFERENCE_KEYS = pack(
        XQuerySyntaxHighlighterColors.XML_TAG,
        XQuerySyntaxHighlighterColors.XML_ENTITY_REFERENCE
    )

    private val XML_ESCAPED_CHARACTER_KEYS = pack(
        XQuerySyntaxHighlighterColors.XML_TAG,
        XQuerySyntaxHighlighterColors.XML_ESCAPED_CHARACTER
    )

    private val XML_PI_TAG_KEYS = pack(XQuerySyntaxHighlighterColors.XML_PI_TAG)

    private val XML_PI_TARGET_KEYS = pack(
        XQuerySyntaxHighlighterColors.XML_TAG,
        XQuerySyntaxHighlighterColors.PROCESSING_INSTRUCTION
    )

    private val XML_TAG_KEYS = pack(XQuerySyntaxHighlighterColors.XML_TAG)

    private val XML_TAG_NAME_KEYS = pack(
        XQuerySyntaxHighlighterColors.XML_TAG,
        XQuerySyntaxHighlighterColors.ELEMENT
    )

    private val XQDOC_MARKUP_KEYS = pack(
        XQuerySyntaxHighlighterColors.COMMENT,
        XQuerySyntaxHighlighterColors.XQDOC_MARKUP
    )

    private val XQDOC_TAG_KEYS = pack(
        XQuerySyntaxHighlighterColors.COMMENT,
        XQuerySyntaxHighlighterColors.XQDOC_TAG
    )

    private val XQDOC_TAG_VALUE_KEYS = pack(
        XQuerySyntaxHighlighterColors.COMMENT,
        XQuerySyntaxHighlighterColors.XQDOC_TAG_VALUE
    )

    // endregion
    // region Semantic Highlighting (Usage and Reference Types)

    private val ANNOTATION_KEYS = pack(XQuerySyntaxHighlighterColors.ANNOTATION)

    private val ATTRIBUTE_KEYS = pack(XQuerySyntaxHighlighterColors.ATTRIBUTE)

    // endregion
    // region Keys

    private val KEYS = mapOf(
        // Annotation (Semantic)
        XQueryTokenType.ANNOTATION_INDICATOR to ANNOTATION_KEYS,
        XQueryTokenType.K_PRIVATE to ANNOTATION_KEYS,
        XQueryTokenType.K_PUBLIC to ANNOTATION_KEYS,
        XQueryTokenType.K_SIMPLE to ANNOTATION_KEYS,
        XQueryTokenType.K_SEQUENTIAL to ANNOTATION_KEYS,
        XQueryTokenType.K_UPDATING to ANNOTATION_KEYS,
        // Attribute (Semantic)
        XPathTokenType.ATTRIBUTE_SELECTOR to ATTRIBUTE_KEYS,
        // Bad Character
        XPathTokenType.BAD_CHARACTER to BAD_CHARACTER_KEYS,
        // Comment
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
        // Entity Reference
        XQueryTokenType.PREDEFINED_ENTITY_REFERENCE to ENTITY_REFERENCE_KEYS,
        XQueryTokenType.PARTIAL_ENTITY_REFERENCE to ENTITY_REFERENCE_KEYS,
        XQueryTokenType.EMPTY_ENTITY_REFERENCE to ENTITY_REFERENCE_KEYS,
        XQueryTokenType.CHARACTER_REFERENCE to ENTITY_REFERENCE_KEYS,
        // Escaped Character
        XPathTokenType.ESCAPED_CHARACTER to ESCAPED_CHARACTER_KEYS,
        // Identifier
        XPathTokenType.NCNAME to IDENTIFIER_KEYS,
        // Number
        XPathTokenType.INTEGER_LITERAL to NUMBER_KEYS,
        XPathTokenType.DECIMAL_LITERAL to NUMBER_KEYS,
        XPathTokenType.DOUBLE_LITERAL to NUMBER_KEYS,
        XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT to NUMBER_KEYS,
        // String
        XPathTokenType.STRING_LITERAL_START to STRING_KEYS,
        XPathTokenType.STRING_LITERAL_CONTENTS to STRING_KEYS,
        XPathTokenType.STRING_LITERAL_END to STRING_KEYS,
        XQueryTokenType.STRING_CONSTRUCTOR_START to STRING_KEYS,
        XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS to STRING_KEYS,
        XQueryTokenType.STRING_CONSTRUCTOR_END to STRING_KEYS,
        XPathTokenType.BRACED_URI_LITERAL_START to STRING_KEYS,
        XPathTokenType.BRACED_URI_LITERAL_END to STRING_KEYS,
        // XML Attribute Name
        XQueryTokenType.XML_ATTRIBUTE_NCNAME to XML_ATTRIBUTE_NAME_KEYS,
        XQueryTokenType.XML_ATTRIBUTE_XMLNS to XML_ATTRIBUTE_NAME_KEYS,
        XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR to XML_ATTRIBUTE_NAME_KEYS,
        // XML Attribute Value
        XQueryTokenType.XML_EQUAL to XML_ATTRIBUTE_VALUE_KEYS,
        XQueryTokenType.XML_ATTRIBUTE_VALUE_START to XML_ATTRIBUTE_VALUE_KEYS,
        XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS to XML_ATTRIBUTE_VALUE_KEYS,
        XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE to XML_ATTRIBUTE_VALUE_KEYS,
        XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE to XML_ATTRIBUTE_VALUE_KEYS,
        XQueryTokenType.XML_ATTRIBUTE_VALUE_END to XML_ATTRIBUTE_VALUE_KEYS,
        XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS to XML_ATTRIBUTE_VALUE_KEYS,
        // XML Entity Reference
        XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE to XML_ENTITY_REFERENCE_KEYS,
        XQueryTokenType.XML_CHARACTER_REFERENCE to XML_ENTITY_REFERENCE_KEYS,
        // XML Escaped Character
        XQueryTokenType.XML_ESCAPED_CHARACTER to XML_ESCAPED_CHARACTER_KEYS,
        // XML Processing Instruction Tag
        XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN to XML_PI_TAG_KEYS,
        XQueryTokenType.PROCESSING_INSTRUCTION_END to XML_PI_TAG_KEYS,
        // XML Processing Instruction Target
        XQueryTokenType.XML_PI_TARGET_NCNAME to XML_PI_TARGET_KEYS,
        // XML Tag
        XQueryTokenType.OPEN_XML_TAG to XML_TAG_KEYS,
        XQueryTokenType.END_XML_TAG to XML_TAG_KEYS,
        XQueryTokenType.CLOSE_XML_TAG to XML_TAG_KEYS,
        XQueryTokenType.SELF_CLOSING_XML_TAG to XML_TAG_KEYS,
        XQueryTokenType.XML_WHITE_SPACE to XML_TAG_KEYS,
        // XML Tag Name
        XQueryTokenType.XML_TAG_NCNAME to XML_TAG_NAME_KEYS,
        XQueryTokenType.XML_TAG_QNAME_SEPARATOR to XML_TAG_NAME_KEYS,
        // XQDoc Markup
        XQDocTokenType.OPEN_XML_TAG to XQDOC_MARKUP_KEYS,
        XQDocTokenType.END_XML_TAG to XQDOC_MARKUP_KEYS,
        XQDocTokenType.CLOSE_XML_TAG to XQDOC_MARKUP_KEYS,
        XQDocTokenType.SELF_CLOSING_XML_TAG to XQDOC_MARKUP_KEYS,
        XQDocTokenType.XML_TAG to XQDOC_MARKUP_KEYS,
        XQDocTokenType.XML_EQUAL to XQDOC_MARKUP_KEYS,
        XQDocTokenType.XML_ATTRIBUTE_VALUE_START to XQDOC_MARKUP_KEYS,
        XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS to XQDOC_MARKUP_KEYS,
        XQDocTokenType.XML_ATTRIBUTE_VALUE_END to XQDOC_MARKUP_KEYS,
        XQDocTokenType.PREDEFINED_ENTITY_REFERENCE to XQDOC_MARKUP_KEYS,
        XQDocTokenType.PARTIAL_ENTITY_REFERENCE to XQDOC_MARKUP_KEYS,
        XQDocTokenType.EMPTY_ENTITY_REFERENCE to XQDOC_MARKUP_KEYS,
        XQDocTokenType.CHARACTER_REFERENCE to XQDOC_MARKUP_KEYS,
        XQDocTokenType.INVALID to XQDOC_MARKUP_KEYS,
        // XQDoc Tag
        XQDocTokenType.TAG_MARKER to XQDOC_TAG_KEYS,
        XQDocTokenType.TAG to XQDOC_TAG_KEYS,
        XQDocTokenType.T_AUTHOR to XQDOC_TAG_KEYS,
        XQDocTokenType.T_DEPRECATED to XQDOC_TAG_KEYS,
        XQDocTokenType.T_ERROR to XQDOC_TAG_KEYS,
        XQDocTokenType.T_PARAM to XQDOC_TAG_KEYS,
        XQDocTokenType.T_RETURN to XQDOC_TAG_KEYS,
        XQDocTokenType.T_SEE to XQDOC_TAG_KEYS,
        XQDocTokenType.T_SINCE to XQDOC_TAG_KEYS,
        XQDocTokenType.T_VERSION to XQDOC_TAG_KEYS,
        // XQDoc Tag Value
        XQDocTokenType.VARIABLE_INDICATOR to XQDOC_TAG_VALUE_KEYS,
        XQDocTokenType.NCNAME to XQDOC_TAG_VALUE_KEYS
    )

    // endregion
}
