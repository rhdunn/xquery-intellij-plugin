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
package uk.co.reecedunn.intellij.plugin.processor.log

import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey

object LogFileContentType {
    // region Log Line Tokens

    val DATE_TIME_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_DATE_TIME", DefaultLanguageHighlighterColors.LINE_COMMENT
    )

    val THREAD_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_THREAD", DefaultLanguageHighlighterColors.INSTANCE_FIELD
    )

    val DATE_TIME: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_DATE_TIME", DATE_TIME_KEY)
    val THREAD: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_THREAD", THREAD_KEY)

    // endregion
    // region Log Levels :: Verbose

    val VERBOSE_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_VERBOSE", ConsoleViewContentType.LOG_VERBOSE_OUTPUT_KEY
    )

    val FINEST_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_FINEST", VERBOSE_KEY
    )

    val FINER_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_FINER", VERBOSE_KEY
    )

    val FINE_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_FINE", VERBOSE_KEY
    )

    val FINEST: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_FINEST", FINEST_KEY)
    val FINER: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_FINER", FINER_KEY)
    val FINE: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_FINE", FINE_KEY)

    // endregion
    // region Log Levels :: Debug

    val DEBUG_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_DEBUG", ConsoleViewContentType.LOG_DEBUG_OUTPUT_KEY
    )

    val DEBUG: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_DEBUG", DEBUG_KEY)

    // endregion
    // region Log Levels :: Information

    val INFO_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_INFO", ConsoleViewContentType.NORMAL_OUTPUT_KEY
    )

    val CONFIG_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_CONFIG", INFO_KEY
    )

    val OK_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_OK", INFO_KEY
    )

    val REQUEST_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_REQUEST", INFO_KEY
    )

    val INFO: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_INFO", INFO_KEY)
    val CONFIG: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_CONFIG", CONFIG_KEY)
    val OK: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_OK", OK_KEY)
    val REQUEST: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_REQUEST", REQUEST_KEY)

    // endregion
    // region Log Levels :: Warning

    val WARNING_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_WARNING", ConsoleViewContentType.LOG_WARNING_OUTPUT_KEY
    )

    val NOTICE_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_NOTICE", WARNING_KEY
    )

    val WARNING: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_WARNING", WARNING_KEY)
    val NOTICE: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_NOTICE", NOTICE_KEY)

    // endregion
    // region Log Levels :: Error

    val ERROR_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_ERROR", ConsoleViewContentType.LOG_ERROR_OUTPUT_KEY
    )

    val CRITICAL_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_CRITICAL", ERROR_KEY
    )

    val ALERT_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_ALERT", ERROR_KEY
    )

    val ERROR: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_ERROR", ERROR_KEY)
    val CRITICAL: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_CRITICAL", CRITICAL_KEY)
    val ALERT: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_ALERT", ALERT_KEY)

    // endregion
    // region Log Levels :: Fatal

    val FATAL_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_FATAL", ERROR_KEY
    )

    val EMERGENCY_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_EMERGENCY", FATAL_KEY
    )

    val FATAL: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_FATAL", FATAL_KEY)
    val EMERGENCY: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_EMERGENCY", EMERGENCY_KEY)

    // endregion
}
