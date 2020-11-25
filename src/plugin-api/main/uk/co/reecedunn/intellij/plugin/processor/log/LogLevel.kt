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

object LogLevel {
    val DATE_TIME_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_DATE_TIME", DefaultLanguageHighlighterColors.LINE_COMMENT
    )

    val FINEST_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_FINEST", ConsoleViewContentType.LOG_VERBOSE_OUTPUT_KEY
    )

    val FINER_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_FINER", ConsoleViewContentType.LOG_VERBOSE_OUTPUT_KEY
    )

    val FINE_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_FINE", ConsoleViewContentType.LOG_VERBOSE_OUTPUT_KEY
    )

    val DEBUG_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_DEBUG", ConsoleViewContentType.LOG_DEBUG_OUTPUT_KEY
    )

    val CONFIG_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_CONFIG", ConsoleViewContentType.NORMAL_OUTPUT_KEY
    )

    val INFO_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_INFO", ConsoleViewContentType.NORMAL_OUTPUT_KEY
    )

    val NOTICE_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_NOTICE", ConsoleViewContentType.LOG_WARNING_OUTPUT_KEY
    )

    val WARNING_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_WARNING", ConsoleViewContentType.LOG_WARNING_OUTPUT_KEY
    )

    val ERROR_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_ERROR", ConsoleViewContentType.LOG_ERROR_OUTPUT_KEY
    )

    val CRITICAL_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_CRITICAL", ConsoleViewContentType.LOG_ERROR_OUTPUT_KEY
    )

    val ALERT_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_ALERT", ConsoleViewContentType.LOG_ERROR_OUTPUT_KEY
    )

    val EMERGENCY_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_EMERGENCY", ConsoleViewContentType.LOG_ERROR_OUTPUT_KEY
    )

    val OK_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_OK", ConsoleViewContentType.NORMAL_OUTPUT_KEY
    )

    val REQUEST_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "QUERY_LOG_LEVEL_REQUEST", ConsoleViewContentType.LOG_INFO_OUTPUT_KEY
    )

    val DATE_TIME: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_DATE_TIME", DATE_TIME_KEY)

    val FINEST: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_FINEST", FINEST_KEY)
    val FINER: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_FINER", FINER_KEY)
    val FINE: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_FINE", FINE_KEY)
    val DEBUG: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_DEBUG", DEBUG_KEY)
    val CONFIG: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_CONFIG", CONFIG_KEY)
    val INFO: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_INFO", INFO_KEY)
    val NOTICE: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_NOTICE", NOTICE_KEY)
    val WARNING: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_WARNING", WARNING_KEY)
    val ERROR: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_ERROR", ERROR_KEY)
    val CRITICAL: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_CRITICAL", CRITICAL_KEY)
    val ALERT: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_ALERT", ALERT_KEY)
    val EMERGENCY: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_EMERGENCY", EMERGENCY_KEY)

    val OK: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_OK", OK_KEY)
    val REQUEST: ConsoleViewContentType = ConsoleViewContentType("QUERY_LOG_LEVEL_REQUEST", REQUEST_KEY)
}
