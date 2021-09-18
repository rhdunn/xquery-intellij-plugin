/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.search.options.navigation

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicIcons
import uk.co.reecedunn.intellij.plugin.marklogic.search.options.SearchOptions
import uk.co.reecedunn.intellij.plugin.processor.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.XmlNCNameImpl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import javax.swing.Icon

class CustomConstraintLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element !is XmlNCNameImpl || element.prevSibling != null) return null

        val qname = element.parent as? XsQNameValue ?: return null
        val decl = qname.element?.parent as? XQueryFunctionDecl ?: return null

        val facets = SearchOptions.customConstraintFunctionReferences(decl)
        if (facets.isEmpty()) return null

        return NavigationGutterIconBuilder.create(icon(facets[0].referenceType))
            .setTargets(facets.map { it.element })
            .setTooltipText(PluginApiBundle.message("line-marker.search-constraint.tooltip-text"))
            .createLineMarkerInfo(element)
    }

    private fun icon(referenceType: String): Icon = when (referenceType) {
        "parse" -> MarkLogicIcons.Markers.CusomSearchFacetParse
        "start-facet" -> MarkLogicIcons.Markers.CusomSearchFacetStart
        "finish-facet" -> MarkLogicIcons.Markers.CusomSearchFacetFinish
        else -> MarkLogicIcons.Markers.CusomSearchFacetParse
    }
}
