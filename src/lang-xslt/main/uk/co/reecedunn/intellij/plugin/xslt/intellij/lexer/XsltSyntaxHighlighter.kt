/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.intellij.lexer

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.XmlHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.xpath.intellij.lexer.XPathSyntaxHighlighter
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XmlCodePointRangeImpl
import uk.co.reecedunn.intellij.plugin.xslt.lexer.XsltValueTemplateLexer
import uk.co.reecedunn.intellij.plugin.xslt.parser.schema.XslValueTemplate

object XsltSyntaxHighlighter : SyntaxHighlighterBase() {
    // region SyntaxHighlighter

    override fun getHighlightingLexer(): Lexer = XsltValueTemplateLexer(XmlCodePointRangeImpl())

    override fun getTokenHighlights(type: IElementType): Array<out TextAttributesKey> {
        val default = XPathSyntaxHighlighter.getTokenHighlights(type)
        return KEYS.getOrDefault(type, default)
    }

    // endregion
    // region SyntaxHighlighterFactory

    class XslAvtFactory : SyntaxHighlighterFactory() {
        override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
            return XsltSyntaxHighlighter
        }
    }

    // endregion
    // region Syntax Highlighting (Lexical Tokens)

    private val ATTRIBUTE_VALUE_KEYS = pack(
        XmlHighlighterColors.XML_TAG,
        XsltSyntaxHighlighterColors.ATTRIBUTE_VALUE
    )

    private val XML_ESCAPED_CHARACTER_KEYS = pack(
        XmlHighlighterColors.XML_TAG,
        XsltSyntaxHighlighterColors.XML_ESCAPED_CHARACTER
    )

    // endregion
    // region Keys

    private val KEYS = mapOf(
        XslValueTemplate.VALUE_CONTENTS to ATTRIBUTE_VALUE_KEYS,
        XslValueTemplate.ESCAPED_CHARACTER to XML_ESCAPED_CHARACTER_KEYS
    )

    // endregion
}
