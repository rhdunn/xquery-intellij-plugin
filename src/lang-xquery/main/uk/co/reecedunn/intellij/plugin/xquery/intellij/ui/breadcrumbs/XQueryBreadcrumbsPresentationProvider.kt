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
package uk.co.reecedunn.intellij.plugin.xquery.intellij.ui.breadcrumbs

import com.intellij.psi.PsiElement
import com.intellij.xml.breadcrumbs.BreadcrumbsPresentationProvider
import com.intellij.xml.breadcrumbs.CrumbPresentation
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.intellij.codeInsight.highlighting.containsTagsWithSameName
import uk.co.reecedunn.intellij.plugin.xquery.intellij.codeInsight.highlighting.getBaseColors
import uk.co.reecedunn.intellij.plugin.xquery.intellij.codeInsight.highlighting.isTagTreeHighlightingActive

class XQueryBreadcrumbsPresentationProvider : BreadcrumbsPresentationProvider() {
    override fun getCrumbPresentations(elements: Array<out PsiElement>): Array<CrumbPresentation?>? = when {
        elements.isEmpty() -> null
        !isTagTreeHighlightingActive(elements.last().containingFile) -> null
        !containsTagsWithSameName(elements) -> null
        else -> {
            val baseColors = getBaseColors()
            Array(elements.size) {
                val index = elements.size - it - 1
                if (elements[index] is XQueryDirElemConstructor) {
                    ColoredCrumbPresentation(baseColors[index % baseColors.size])
                } else
                    null
            }
        }
    }
}
