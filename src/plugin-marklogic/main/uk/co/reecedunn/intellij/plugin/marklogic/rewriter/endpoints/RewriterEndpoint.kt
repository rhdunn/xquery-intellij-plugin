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
package uk.co.reecedunn.intellij.plugin.marklogic.rewriter.endpoints

import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.microservices.endpoints.Endpoint
import uk.co.reecedunn.intellij.plugin.core.xml.ancestors
import uk.co.reecedunn.intellij.plugin.intellij.resources.MarkLogicIcons
import uk.co.reecedunn.intellij.plugin.marklogic.rewriter.lang.Rewriter
import uk.co.reecedunn.intellij.plugin.marklogic.rewriter.reference.ModuleUriElementReference
import javax.swing.Icon
import javax.swing.JComponent

class RewriterEndpoint(private val endpoint: XmlTag) : Endpoint(), DataProvider {
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon? = MarkLogicIcons.Rewriter.Endpoint

    // endregion
    // region Endpoint

    override val details: JComponent? = null

    override val reference: PsiReference?
        get() = when {
            endpoint.value.text.isBlank() -> null
            else -> ModuleUriElementReference(endpoint)
        }

    override val element: PsiElement = endpoint

    override val method: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-method").firstOrNull()?.getAttributeValue("any-of")

    override val path: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-path").firstOrNull()?.let { matchPath ->
            val matches = matchPath.getAttributeValue("matches")
            val anyOf = matchPath.getAttributeValue("any-of")?.split("\\s+".toRegex())?.getOrNull(0)
            val prefix = matchPath.getAttributeValue("prefix")
            matches ?: anyOf ?: prefix
        }

    // endregion
    // region DataProvider

    override fun getData(dataId: String): Any? = when (dataId) {
        CommonDataKeys.PSI_ELEMENT.name -> endpoint
        else -> null
    }

    // endregion
}
