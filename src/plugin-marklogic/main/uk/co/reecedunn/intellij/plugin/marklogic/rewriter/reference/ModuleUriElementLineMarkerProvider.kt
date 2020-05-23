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
package uk.co.reecedunn.intellij.plugin.marklogic.rewriter.reference

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.plugin.intellij.resources.MarkLogicIcons
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.marklogic.rewriter.endpoints.RewriterEndpoint
import uk.co.reecedunn.intellij.plugin.marklogic.rewriter.endpoints.RewriterEndpointsProvider
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryQueryBody

class ModuleUriElementLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element !is XQueryQueryBody) return null

        return getModuleUriElements(element).takeIf { it.isNotEmpty() }?.let {
            NavigationGutterIconBuilder.create(MarkLogicIcons.Markers.Endpoint)
                .setTargets(it)
                .setTooltipText(PluginApiBundle.message("line-marker.rewriter-endpoint.tooltip-text"))
                .setCellRenderer(ModuleUriElementListCellRenderer)
                .createLineMarkerInfo(element)
        }
    }

    private fun getModuleUriElements(element: XQueryQueryBody): List<XmlTag> {
        val elements = ArrayList<XmlTag>()
        RewriterEndpointsProvider.groups(element.project).asSequence().forEach { group ->
            group.endpoints
                .filter { it.reference?.resolve() === element }
                .forEach { elements.add((it as RewriterEndpoint).endpoint) }
        }
        return elements
    }
}
