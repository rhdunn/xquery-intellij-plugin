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
package uk.co.reecedunn.intellij.plugin.processor.tests.debug.frame

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.xdebugger.frame.presentation.XValuePresentation

object ValueTextRenderer : XValuePresentation.XValueTextRenderer {
    var rendered: String = ""

    override fun renderValue(value: String) {
        rendered = value
    }

    override fun renderValue(value: String, key: TextAttributesKey) {
        rendered = value
    }

    override fun renderStringValue(value: String) {
        rendered = value
    }

    override fun renderStringValue(value: String, additionalSpecialCharsToHighlight: String?, maxLength: Int) {
        rendered = value
    }

    override fun renderComment(comment: String) {
        rendered = comment
    }

    override fun renderSpecialSymbol(symbol: String) {
        rendered = symbol
    }

    override fun renderKeywordValue(value: String) {
        rendered = value
    }

    override fun renderError(error: String) {
        rendered = error
    }

    override fun renderNumericValue(value: String) {
        rendered = value
    }
}
