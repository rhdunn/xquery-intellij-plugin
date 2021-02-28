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
package uk.co.reecedunn.intellij.plugin.marklogic.log.lexer

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.marklogic.log.lang.MarkLogicErrorLog

object MarkLogicErrorLogTokenType {
    val WHITE_SPACE: IElementType = TokenType.WHITE_SPACE

    val DATE: IElementType = IElementType("MARK_LOGIC_ERROR_LOG_DATE", MarkLogicErrorLog)
    val TIME: IElementType = IElementType("MARK_LOGIC_ERROR_LOG_TIME", MarkLogicErrorLog)
    val SERVER: IElementType = IElementType("MARK_LOGIC_ERROR_LOG_SERVER", MarkLogicErrorLog)
    val MESSAGE: IElementType = IElementType("MARK_LOGIC_ERROR_LOG_MESSAGE", MarkLogicErrorLog)

    val COLON: IElementType = IElementType("MARK_LOGIC_ERROR_LOG_COLON", MarkLogicErrorLog)
    val CONTINUATION: IElementType = IElementType("MARK_LOGIC_ERROR_LOG_CONTINUATION", MarkLogicErrorLog)

    object LogLevel {
        val FINEST: IElementType = IElementType("MARK_LOGIC_ERROR_LOG_LEVEL_FINEST", MarkLogicErrorLog)
        val FINER: IElementType = IElementType("MARK_LOGIC_ERROR_LOG_LEVEL_FINER", MarkLogicErrorLog)
        val FINE: IElementType = IElementType("MARK_LOGIC_ERROR_LOG_LEVEL_FINE", MarkLogicErrorLog)
        val DEBUG: IElementType = IElementType("MARK_LOGIC_ERROR_LOG_LEVEL_DEBUG", MarkLogicErrorLog)
        val CONFIG: IElementType = IElementType("MARK_LOGIC_ERROR_LOG_LEVEL_CONFIG", MarkLogicErrorLog)
        val INFO: IElementType = IElementType("MARK_LOGIC_ERROR_LOG_LEVEL_INFO", MarkLogicErrorLog)
        val NOTICE: IElementType = IElementType("MARK_LOGIC_ERROR_LOG_LEVEL_NOTICE", MarkLogicErrorLog)
        val WARNING: IElementType = IElementType("MARK_LOGIC_ERROR_LOG_LEVEL_WARNING", MarkLogicErrorLog)
        val ERROR: IElementType = IElementType("MARK_LOGIC_ERROR_LOG_LEVEL_ERROR", MarkLogicErrorLog)
        val CRITICAL: IElementType = IElementType("MARK_LOGIC_ERROR_LOG_LEVEL_CRITICAL", MarkLogicErrorLog)
        val ALERT: IElementType = IElementType("MARK_LOGIC_ERROR_LOG_LEVEL_ALERT", MarkLogicErrorLog)
        val EMERGENCY: IElementType = IElementType("MARK_LOGIC_ERROR_LOG_LEVEL_EMERGENCY", MarkLogicErrorLog)
    }
}
