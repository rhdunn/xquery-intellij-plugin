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
package uk.co.reecedunn.intellij.plugin.xquery.lang.editor.breadcrumbs

import com.intellij.util.ui.UIUtil.makeTransparent
import com.intellij.xml.breadcrumbs.DefaultCrumbsPresentation
import uk.co.reecedunn.intellij.plugin.xquery.lang.highlighter.xml.toLineMarkerColor
import java.awt.Color

class ColoredCrumbPresentation(private val color: Color?) : DefaultCrumbsPresentation() {
    override fun getBackgroundColor(selected: Boolean, hovered: Boolean, light: Boolean): Color? {
        val baseColor = super.getBackgroundColor(selected, hovered, light)
        return when {
            baseColor == null -> toLineMarkerColor(0x92, color)
            color != null -> makeTransparent(color, baseColor, 0.1)
            else -> baseColor
        }
    }
}
