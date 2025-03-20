// Copyright (C) 2020-2021, 2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.marklogic.search.options.navigation

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.compat.codeInsight.navigation.impl.*
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.marklogic.search.options.SearchOptions
import uk.co.reecedunn.intellij.plugin.marklogic.search.options.reference.CustomConstraintFunctionReference
import uk.co.reecedunn.intellij.plugin.processor.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.XmlNCNameImpl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl

class CustomConstraintLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element !is XmlNCNameImpl || element.prevSibling != null) return null

        val qname = element.parent as? XsQNameValue ?: return null
        val decl = qname.element?.parent as? XQueryFunctionDecl ?: return null

        val facets = SearchOptions.getInstance().getCustomConstraintFunctionReferences(decl)
        if (facets.isEmpty()) return null

        return NavigationGutterIconBuilder.create(CustomConstraintFunctionReference.getIcon(facets[0].referenceType))
            .setTargets(facets.map { it.element })
            .setTooltipText(PluginApiBundle.message("line-marker.search-constraint.tooltip-text"))
            .setTargetRenderer { CustomConstraintListCellRenderer(facets) }
            .createLineMarkerInfo(element)
    }
}
