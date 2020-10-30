/*
 * Copyright (C) 2020 Reece H. Dunn
 * Copyright 2000-2020 JetBrains s.r.o.
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
import com.intellij.compat.util.ui.UIUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmElementNode
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import java.awt.Color
import java.util.*
import kotlin.math.min

// NOTE: The IntelliJ XmlTagTreeHighlightingUtil helper methods are package private.

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

fun containsTagsWithSameName(elements: Array<out PsiElement?>): Boolean {
    val names: MutableSet<String> = HashSet()
    return elements.asSequence().filterIsInstance<XQueryDirElemConstructor>().any { element ->
        val name = (element as XdmElementNode).nodeName?.let { op_qname_presentation(it) }
        name != null && !names.add(name)
    }
}

// NOTE: The IntelliJ XmlTagTreeHighlightingPass helper methods are package private.

fun toLineMarkerColor(gray: Int, color: Color?): Color? {
    return if (color == null) null else Color(
        toLineMarkerColor(gray, color.red),
        toLineMarkerColor(gray, color.green),
        toLineMarkerColor(gray, color.blue)
    )
}

private fun toLineMarkerColor(gray: Int, color: Int): Int {
    val value = (gray * 0.6 + 0.32 * color).toInt()
    return if (value < 0) 0 else min(value, 255)
}

fun toColorsForEditor(baseColors: Array<Color?>, tagBackground: Color): Array<Color?> {
    val transparency = WebEditorOptions.getInstance().tagTreeHighlightingOpacity * 0.01
    return Array(baseColors.size) {
        val color = baseColors[it]
        if (color != null) UIUtil.makeTransparent(color, tagBackground, transparency) else null
    }
}

fun toColorsForLineMarkers(baseColors: Array<Color?>): Array<Color?> {
    return Array(baseColors.size) { toLineMarkerColor(239, baseColors[it]) }
}
