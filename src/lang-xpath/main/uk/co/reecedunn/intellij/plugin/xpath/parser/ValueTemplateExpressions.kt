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
package uk.co.reecedunn.intellij.plugin.xpath.parser

import com.intellij.openapi.util.TextRange
import com.intellij.psi.xml.XmlAttribute

fun String.valueTemplateExpressions(): List<TextRange> {
    val expressions = ArrayList<TextRange>()

    var start = -1
    var depth = 0
    withIndex().forEach { (index, c) ->
        if (c == '{') {
            if (start == -1) {
                start = index
                depth = 0
            } else {
                depth++
            }
        } else if (c == '}') {
            if (depth == 0) {
                expressions.add(TextRange(start, index + 1))
                start = -1
            } else {
                depth--
            }
        }
    }

    if (start != -1) {
        expressions.add(TextRange(start, length))
    }
    return expressions
}

fun XmlAttribute.attributeValueTemplateExpressions(): List<TextRange> {
    return value?.valueTemplateExpressions()?.map { it.shiftRight(1) } ?: listOf()
}
