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
package uk.co.reecedunn.intellij.plugin.core.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder

private fun before(text: String, offset: Int): String {
    var i = offset - 1
    while (i > 0) {
        val c = text[i]
        if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
            return text.substring(0, i + 1)
        }
        --i
    }
    return ""
}

fun CompletionParameters.schemaListCompletions(items: List<String>): List<LookupElement> {
    val text = originalPosition?.text ?: ""
    val offset = offset - (originalPosition?.textOffset ?: 0)

    val before = before(text, offset)
    return items.map { LookupElementBuilder.create("$before$it") }
}
