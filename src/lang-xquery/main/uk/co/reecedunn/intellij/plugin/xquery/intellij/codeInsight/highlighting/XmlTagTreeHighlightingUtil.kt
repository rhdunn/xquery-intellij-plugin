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
package uk.co.reecedunn.intellij.plugin.xquery.intellij.codeInsight.highlighting

import com.intellij.application.options.editor.WebEditorOptions
import com.intellij.codeInsight.daemon.impl.tagTreeHighlighting.XmlTagTreeHighlightingColors
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import java.awt.Color

// NOTE: The IntelliJ XmlTagTreeHighlightingUtil methods are package private.

fun isTagTreeHighlightingActive(file: PsiFile?): Boolean = when {
    ApplicationManager.getApplication().isUnitTestMode -> false
    file !is XQueryModule -> false
    else -> WebEditorOptions.getInstance().isTagTreeHighlightingEnabled
}

fun getBaseColors(): Array<Color?> {
    val colorKeys = XmlTagTreeHighlightingColors.getColorKeys()
    val colorsScheme = EditorColorsManager.getInstance().globalScheme
    return Array(colorKeys.size) { colorsScheme.getColor(colorKeys[it]) }
}
