/*
 * Copyright (C) 2016-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.lang.highlighter

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

object XPathSyntaxHighlighter : SyntaxHighlighterBase() {
    // region SyntaxHighlighter

    override fun getHighlightingLexer(): Lexer = XPathLexer()

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
            return XPathSyntaxHighlighter
        }
    }

    // endregion
    // region Syntax Highlighting (Lexical Tokens)

    private val BAD_CHARACTER_KEYS = pack(XPathSyntaxHighlighterColors.BAD_CHARACTER)

    private val COMMENT_KEYS = pack(XPathSyntaxHighlighterColors.COMMENT)

    private val ESCAPED_CHARACTER_KEYS = pack(XPathSyntaxHighlighterColors.ESCAPED_CHARACTER)

    private val IDENTIFIER_KEYS = pack(XPathSyntaxHighlighterColors.IDENTIFIER)

    private val KEYWORD_KEYS = pack(XPathSyntaxHighlighterColors.KEYWORD)

    private val NUMBER_KEYS = pack(XPathSyntaxHighlighterColors.NUMBER)

    private val STRING_KEYS = pack(XPathSyntaxHighlighterColors.STRING)

    // endregion
    // region Semantic Highlighting (Usage and Reference Types)

    private val ATTRIBUTE_KEYS = pack(XPathSyntaxHighlighterColors.ATTRIBUTE)

    // endregion
    // region Keys

    private val KEYS = mapOf(
        XPathTokenType.INTEGER_LITERAL to NUMBER_KEYS,
        XPathTokenType.DECIMAL_LITERAL to NUMBER_KEYS,
        XPathTokenType.DOUBLE_LITERAL to NUMBER_KEYS,
        XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT to NUMBER_KEYS,
        XPathTokenType.STRING_LITERAL_START to STRING_KEYS,
        XPathTokenType.STRING_LITERAL_CONTENTS to STRING_KEYS,
        XPathTokenType.STRING_LITERAL_END to STRING_KEYS,
        XPathTokenType.BRACED_URI_LITERAL_START to STRING_KEYS,
        XPathTokenType.BRACED_URI_LITERAL_END to STRING_KEYS,
        XPathTokenType.ESCAPED_CHARACTER to ESCAPED_CHARACTER_KEYS,
        XPathTokenType.COMMENT_START_TAG to COMMENT_KEYS,
        XPathTokenType.COMMENT to COMMENT_KEYS,
        XPathTokenType.COMMENT_END_TAG to COMMENT_KEYS,
        XPathTokenType.NCNAME to IDENTIFIER_KEYS,
        XPathTokenType.ATTRIBUTE_SELECTOR to ATTRIBUTE_KEYS,
        XPathTokenType.BAD_CHARACTER to BAD_CHARACTER_KEYS
    )

    // endregion
}
