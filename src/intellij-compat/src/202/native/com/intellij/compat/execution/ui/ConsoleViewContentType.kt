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
package com.intellij.compat.execution.ui

import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.ex.MarkupModelEx
import com.intellij.openapi.editor.ex.RangeHighlighterEx
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.util.Key

fun ConsoleViewContentType.addRangeHighlighterAndChangeAttributes(
    markupModel: MarkupModelEx,
    startOffset: Int,
    endOffset: Int,
    layer: Int,
    targetArea: HighlighterTargetArea,
    isPersistent: Boolean,
    contentTypeKey: Key<ConsoleViewContentType>
): RangeHighlighterEx {
    return markupModel.addRangeHighlighterAndChangeAttributes(
        attributesKey, startOffset, endOffset, layer, targetArea, isPersistent
    ) { ex: RangeHighlighterEx ->
        if (ex.textAttributesKey == null) {
            ex.setTextAttributes(attributes)
        }
        ex.putUserData(contentTypeKey, this)
    }
}
