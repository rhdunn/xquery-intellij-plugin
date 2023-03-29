/*
 * Copyright (C) 2020-2021, 2023 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.rewriter.navigation

import com.intellij.ide.util.PsiElementListCellRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.plugin.core.psi.resourcePath
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicIcons
import uk.co.reecedunn.intellij.plugin.marklogic.rewriter.endpoints.RewriterEndpoint
import javax.swing.Icon

class RewriterListCellRenderer(private val endpoints: List<RewriterEndpoint>) : PsiElementListCellRenderer<XmlTag>() {
    override fun getContainerText(element: XmlTag, name: String?): String = element.containingFile.resourcePath()

    override fun getElementText(element: XmlTag): String {
        val endpoint = endpoints.find { it.endpoint === element } ?: return element.localName
        return endpoint.path?.let { path ->
            endpoint.endpointMethodPresentation?.let { method -> "[$method] $path" } ?: path
        } ?: element.localName
    }

    override fun getIcon(element: PsiElement?): Icon = MarkLogicIcons.Markers.Endpoint

    override fun getIconFlags(): Int = 0
}
