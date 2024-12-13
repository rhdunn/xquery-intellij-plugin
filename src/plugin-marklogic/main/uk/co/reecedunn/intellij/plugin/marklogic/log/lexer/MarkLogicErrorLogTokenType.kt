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
        val UNKNOWN: ILogLevelElementType = ILogLevelElementType("Unknown", MarkLogicErrorLog)
        val FINEST: ILogLevelElementType = ILogLevelElementType("Finest", MarkLogicErrorLog)
        val FINER: ILogLevelElementType = ILogLevelElementType("Finer", MarkLogicErrorLog)
        val FINE: ILogLevelElementType = ILogLevelElementType("Fine", MarkLogicErrorLog)
        val DEBUG: ILogLevelElementType = ILogLevelElementType("Debug", MarkLogicErrorLog)
        val CONFIG: ILogLevelElementType = ILogLevelElementType("Config", MarkLogicErrorLog)
        val INFO: ILogLevelElementType = ILogLevelElementType("Info", MarkLogicErrorLog)
        val NOTICE: ILogLevelElementType = ILogLevelElementType("Notice", MarkLogicErrorLog)
        val WARNING: ILogLevelElementType = ILogLevelElementType("Warning", MarkLogicErrorLog)
        val ERROR: ILogLevelElementType = ILogLevelElementType("Error", MarkLogicErrorLog)
        val CRITICAL: ILogLevelElementType = ILogLevelElementType("Critical", MarkLogicErrorLog)
        val ALERT: ILogLevelElementType = ILogLevelElementType("Alert", MarkLogicErrorLog)
        val EMERGENCY: ILogLevelElementType = ILogLevelElementType("Emergency", MarkLogicErrorLog)

        private val VALUES: Array<ILogLevelElementType> = arrayOf(
            FINEST, FINER, FINE, DEBUG, CONFIG, INFO, NOTICE, WARNING, ERROR, CRITICAL, ALERT, EMERGENCY
        )

        fun token(name: CharSequence): ILogLevelElementType = VALUES.find { it.name == name } ?: UNKNOWN
    }
}
