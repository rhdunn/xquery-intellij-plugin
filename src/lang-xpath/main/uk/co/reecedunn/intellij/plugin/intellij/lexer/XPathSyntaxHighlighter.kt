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
package uk.co.reecedunn.intellij.plugin.intellij.lexer

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XmlCodePointRangeImpl
import uk.co.reecedunn.intellij.plugin.xpath.lexer.IKeywordOrNCNameType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

object XPathSyntaxHighlighter : SyntaxHighlighterBase() {
    val DEFAULT: Array<out TextAttributesKey> = emptyArray()

    override fun getHighlightingLexer(): Lexer = XPathLexer(XmlCodePointRangeImpl())

    override fun getTokenHighlights(type: IElementType): Array<out TextAttributesKey> {
        val default =
            if (type is IKeywordOrNCNameType && type !== XPathTokenType.K__)
                XPathSyntaxHighlighterKeys.KEYWORD_KEYS
            else
                DEFAULT
        return XPathSyntaxHighlighterKeys.KEYS.getOrDefault(type, default)
    }

    object Factory : SyntaxHighlighterFactory() {
        override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
            return XPathSyntaxHighlighter
        }
    }
}
