// Copyright (C) 2020-2021, 2023-2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.marklogic.rewriter.navigation

import com.intellij.codeInsight.navigation.impl.PsiTargetPresentationRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.plugin.core.psi.resourcePath
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicIcons
import uk.co.reecedunn.intellij.plugin.marklogic.rewriter.endpoints.RewriterEndpoint
import javax.swing.Icon

class RewriterListCellRenderer(private val endpoints: List<RewriterEndpoint>) :
    PsiTargetPresentationRenderer<PsiElement>() {

    override fun getContainerText(element: PsiElement): String = element.containingFile.resourcePath()

    override fun getElementText(element: PsiElement): String {
        val localName = (element as XmlTag).localName
        val endpoint = endpoints.find { it.endpoint === element } ?: return localName
        return endpoint.path?.let { path -> "${endpoint.endpointMethodPresentation} $path" } ?: localName
    }

    override fun getIcon(element: PsiElement): Icon = MarkLogicIcons.Markers.Endpoint
}
