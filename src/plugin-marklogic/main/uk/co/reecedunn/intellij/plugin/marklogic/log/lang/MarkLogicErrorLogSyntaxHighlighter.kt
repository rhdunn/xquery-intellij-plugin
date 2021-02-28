/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.log.lang

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.marklogic.log.lexer.MarkLogicErrorLogLexer
import uk.co.reecedunn.intellij.plugin.marklogic.log.lexer.MarkLogicErrorLogTokenType
import uk.co.reecedunn.intellij.plugin.processor.log.LogFileContentType

object MarkLogicErrorLogSyntaxHighlighter : SyntaxHighlighterBase() {
    // region SyntaxHighlighter

    override fun getHighlightingLexer(): Lexer = MarkLogicErrorLogLexer()

    override fun getTokenHighlights(type: IElementType): Array<out TextAttributesKey> {
        return KEYS.getOrDefault(type, TextAttributesKey.EMPTY_ARRAY)
    }

    // endregion
    // region SyntaxHighlighterFactory

    class Factory : SyntaxHighlighterFactory() {
        override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
            return MarkLogicErrorLogSyntaxHighlighter
        }
    }

    // endregion
    // region Syntax Highlighting (Lexical Tokens)

    private val DATE_TIME_KEYS = pack(LogFileContentType.DATE_TIME_KEY)

    // endregion
    // region Keys

    private val KEYS = mapOf<IElementType, Array<TextAttributesKey>>(
        MarkLogicErrorLogTokenType.DATE to DATE_TIME_KEYS,
        MarkLogicErrorLogTokenType.TIME to DATE_TIME_KEYS
    )

    // endregion
}
