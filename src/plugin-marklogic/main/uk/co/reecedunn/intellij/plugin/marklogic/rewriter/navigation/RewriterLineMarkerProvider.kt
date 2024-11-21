// Copyright (C) 2020-2021, 2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.marklogic.rewriter.navigation

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafElement
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicIcons
import uk.co.reecedunn.intellij.plugin.processor.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.marklogic.rewriter.endpoints.Rewriter
import uk.co.reecedunn.intellij.plugin.marklogic.rewriter.endpoints.RewriterEndpoint
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryQueryBody

class RewriterLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element !is LeafElement) return null

        val body = getQueryBody(element) ?: return null
        val endpoints = getTargetEndpoints(body)
        if (endpoints.isEmpty()) return null

        return NavigationGutterIconBuilder.create(MarkLogicIcons.Markers.Endpoint)
            .setTargets(endpoints.map { it.endpoint })
            .setTooltipText(PluginApiBundle.message("line-marker.rewriter-endpoint.tooltip-text"))
            .setTargetRenderer { RewriterListCellRenderer(endpoints) }
            .createLineMarkerInfo(element)
    }

    private fun getQueryBody(element: PsiElement): XQueryQueryBody? = when {
        element is XQueryQueryBody -> element
        element.prevSibling != null -> null
        else -> element.parent?.let { getQueryBody(it) }
    }

    private fun getTargetEndpoints(element: XQueryQueryBody): List<RewriterEndpoint> {
        return Rewriter.getInstance().getEndpointGroups(element.project).flatMap { group ->
            group.endpoints.filter { it.endpointTarget?.resolve() === element }
        }
    }
}
