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
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.marklogic.log.lexer.MarkLogicErrorLogLexer
import uk.co.reecedunn.intellij.plugin.marklogic.log.lexer.MarkLogicErrorLogTokenType
import uk.co.reecedunn.intellij.plugin.processor.log.lang.LogFileSyntaxHighlighter

object MarkLogicErrorLogSyntaxHighlighter : LogFileSyntaxHighlighter() {
    // region SyntaxHighlighter

    override fun getHighlightingLexer(): Lexer = MarkLogicErrorLogLexer()

    override val KEYS = mapOf(
        MarkLogicErrorLogTokenType.DATE to DATE_TIME_KEYS,
        MarkLogicErrorLogTokenType.TIME to DATE_TIME_KEYS,
        MarkLogicErrorLogTokenType.LogLevel.FINEST to FINEST_KEYS,
        MarkLogicErrorLogTokenType.LogLevel.FINER to FINER_KEYS,
        MarkLogicErrorLogTokenType.LogLevel.FINE to FINE_KEYS,
        MarkLogicErrorLogTokenType.LogLevel.DEBUG to DEBUG_KEYS,
        MarkLogicErrorLogTokenType.LogLevel.CONFIG to CONFIG_KEYS,
        MarkLogicErrorLogTokenType.LogLevel.INFO to INFO_KEYS,
        MarkLogicErrorLogTokenType.LogLevel.NOTICE to NOTICE_KEYS,
        MarkLogicErrorLogTokenType.LogLevel.WARNING to WARNING_KEYS,
        MarkLogicErrorLogTokenType.LogLevel.ERROR to ERROR_KEYS,
        MarkLogicErrorLogTokenType.LogLevel.CRITICAL to CRITICAL_KEYS,
        MarkLogicErrorLogTokenType.LogLevel.ALERT to ALERT_KEYS,
        MarkLogicErrorLogTokenType.LogLevel.EMERGENCY to EMERGENCY_KEYS
    )

    // endregion
    // region SyntaxHighlighterFactory

    class Factory : SyntaxHighlighterFactory() {
        override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
            return MarkLogicErrorLogSyntaxHighlighter
        }
    }

    // endregion
}
