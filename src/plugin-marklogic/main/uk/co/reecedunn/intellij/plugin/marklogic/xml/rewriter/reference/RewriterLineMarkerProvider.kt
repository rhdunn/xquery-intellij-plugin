/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.xml.rewriter.reference

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafElement
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicIcons
import uk.co.reecedunn.intellij.plugin.marklogic.xml.rewriter.endpoints.Rewriter
import uk.co.reecedunn.intellij.plugin.processor.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryQueryBody

class RewriterLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element !is LeafElement) return null

        val body = getQueryBody(element) ?: return null
        val targets = getModuleUriElements(body)
        if (targets.isEmpty()) return null

        return NavigationGutterIconBuilder.create(MarkLogicIcons.Markers.Endpoint)
            .setTargets(targets)
            .setTooltipText(PluginApiBundle.message("line-marker.rewriter-endpoint.tooltip-text"))
            .setCellRenderer(RewriterListCellRenderer)
            .createLineMarkerInfo(element)
    }

    private fun getQueryBody(element: PsiElement): XQueryQueryBody? = when {
        element is XQueryQueryBody -> element
        element.prevSibling != null -> null
        else -> element.parent?.let { getQueryBody(it) }
    }

    private fun getModuleUriElements(element: XQueryQueryBody): List<PsiElement> {
        return Rewriter.getInstance().getEndpointGroups(element.project).flatMap { group ->
            group.endpoints.filter { it.endpointTarget?.resolve() === element }.map { it.endpoint }
        }
    }
}
