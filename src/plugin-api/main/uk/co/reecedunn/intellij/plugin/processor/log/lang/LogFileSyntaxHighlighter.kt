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
package uk.co.reecedunn.intellij.plugin.processor.log.lang

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.processor.log.LogFileContentType

@Suppress("PropertyName")
abstract class LogFileSyntaxHighlighter : SyntaxHighlighterBase() {
    // region SyntaxHighlighter

    abstract override fun getHighlightingLexer(): Lexer

    override fun getTokenHighlights(type: IElementType): Array<out TextAttributesKey> {
        return KEYS.getOrDefault(type, TextAttributesKey.EMPTY_ARRAY)
    }

    abstract val KEYS: Map<IElementType, Array<TextAttributesKey>>

    // endregion
    // region Syntax Highlighting :: Log Lines

    val DATE_TIME_KEYS: Array<TextAttributesKey> = pack(LogFileContentType.DATE_TIME_KEY)
    val THREAD_KEYS: Array<TextAttributesKey> = pack(LogFileContentType.THREAD_KEY)
    val SERVER_KEYS: Array<TextAttributesKey> = pack(LogFileContentType.SERVER_KEY)

    // endregion
    // region Syntax Highlighting :: Log Levels :: Verbose

    val VERBOSE_KEYS: Array<TextAttributesKey> = pack(LogFileContentType.VERBOSE_KEY)
    val FINEST_KEYS: Array<TextAttributesKey> = pack(LogFileContentType.FINEST_KEY)
    val FINER_KEYS: Array<TextAttributesKey> = pack(LogFileContentType.FINER_KEY)
    val FINE_KEYS: Array<TextAttributesKey> = pack(LogFileContentType.FINE_KEY)

    // endregion
    // region Syntax Highlighting :: Log Levels :: Debug

    val DEBUG_KEYS: Array<TextAttributesKey> = pack(LogFileContentType.DEBUG_KEY)

    // endregion
    // region Syntax Highlighting :: Log Levels :: Information

    val INFO_KEYS: Array<TextAttributesKey> = pack(LogFileContentType.INFO_KEY)
    val CONFIG_KEYS: Array<TextAttributesKey> = pack(LogFileContentType.CONFIG_KEY)
    val OK_KEYS: Array<TextAttributesKey> = pack(LogFileContentType.OK_KEY)
    val REQUEST_KEYS: Array<TextAttributesKey> = pack(LogFileContentType.REQUEST_KEY)

    // endregion
    // region Syntax Highlighting :: Log Levels :: Warning

    val WARNING_KEYS: Array<TextAttributesKey> = pack(LogFileContentType.WARNING_KEY)
    val NOTICE_KEYS: Array<TextAttributesKey> = pack(LogFileContentType.NOTICE_KEY)

    // endregion
    // region Syntax Highlighting :: Log Levels :: Error

    val ERROR_KEYS: Array<TextAttributesKey> = pack(LogFileContentType.ERROR_KEY)
    val CRITICAL_KEYS: Array<TextAttributesKey> = pack(LogFileContentType.CRITICAL_KEY)
    val ALERT_KEYS: Array<TextAttributesKey> = pack(LogFileContentType.ALERT_KEY)

    // endregion
    // region Syntax Highlighting :: Log Levels :: Fatal

    val FATAL_KEYS: Array<TextAttributesKey> = pack(LogFileContentType.FATAL_KEY)
    val EMERGENCY_KEYS: Array<TextAttributesKey> = pack(LogFileContentType.EMERGENCY_KEY)

    // endregion
}
