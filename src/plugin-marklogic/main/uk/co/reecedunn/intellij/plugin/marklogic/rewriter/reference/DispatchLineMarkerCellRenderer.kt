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

import com.intellij.ide.util.PsiElementListCellRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.plugin.core.psi.resourcePath
import uk.co.reecedunn.intellij.plugin.core.xml.ancestors
import uk.co.reecedunn.intellij.plugin.intellij.resources.MarkLogicIcons
import uk.co.reecedunn.intellij.plugin.marklogic.rewriter.lang.Rewriter
import javax.swing.Icon

object DispatchLineMarkerCellRenderer : PsiElementListCellRenderer<XmlTag>() {
    override fun getContainerText(element: XmlTag, name: String?): String? = element.containingFile.resourcePath()

    override fun getElementText(element: XmlTag): String {
        val matchMethod = element.ancestors(Rewriter.NAMESPACE, "match-method").firstOrNull()
            ?.getAttributeValue("any-of")
        val matchPath = element.ancestors(Rewriter.NAMESPACE, "match-path").firstOrNull()
            ?.getAttributeValue("matches")

        if (matchPath != null) {
            if (matchMethod != null) {
                return "[$matchMethod] $matchPath"
            }
            return matchPath
        }
        return element.localName
    }

    override fun getIcon(element: PsiElement?): Icon = MarkLogicIcons.Markers.Dispatch

    override fun getIconFlags(): Int = 0
}
