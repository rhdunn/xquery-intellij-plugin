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

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.microservices.endpoints.Endpoint
import uk.co.reecedunn.intellij.plugin.core.ui.layout.details
import uk.co.reecedunn.intellij.plugin.core.ui.layout.detailsPanel
import uk.co.reecedunn.intellij.plugin.core.xml.ancestors
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicBundle
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicIcons
import uk.co.reecedunn.intellij.plugin.marklogic.rewriter.lang.Rewriter
import uk.co.reecedunn.intellij.plugin.xquery.psi.reference.ModuleUriReference
import javax.swing.Icon
import javax.swing.JPanel

class RewriterEndpoint(private val endpoint: XmlTag) : ItemPresentation, Endpoint, DataProvider {
    // region ItemPresentation

    override fun getPresentableText(): String? = path

    override fun getLocationString(): String? = dispatch.split("/").lastOrNull()

    override fun getIcon(unused: Boolean): Icon = MarkLogicIcons.Rewriter.Endpoint

    // endregion
    // region Endpoint

    override val presentation: ItemPresentation
        get() = this

    override val details: JPanel
        get() = detailsPanel {
            val detailsLabel = when (endpoint.localName) {
                "dispatch" -> MarkLogicBundle.message("endpoints.rewriter.dispatch.label")
                "set-error-handler" -> MarkLogicBundle.message("endpoints.rewriter.set-error-handler.label")
                "set-path" -> MarkLogicBundle.message("endpoints.rewriter.set-path.label")
                else -> null
            }
            detailsLabel?.let { details(it, dispatch) }
            details(MarkLogicBundle.message("endpoints.rewriter.path.label"), paths)
            details(MarkLogicBundle.message("endpoints.rewriter.method.label"), method)
            details(MarkLogicBundle.message("endpoints.rewriter.accept.label"), accept)
            details(MarkLogicBundle.message("endpoints.rewriter.content-type.label"), contentType)
            details(MarkLogicBundle.message("endpoints.rewriter.cookie.label"), cookie)
            details(MarkLogicBundle.message("endpoints.rewriter.execute-privilege.label"), executePrivilege)
            details(MarkLogicBundle.message("endpoints.rewriter.header.label"), header)
            details(MarkLogicBundle.message("endpoints.rewriter.query-param.label"), queryParam)
            details(MarkLogicBundle.message("endpoints.rewriter.role.label"), role)
            details(MarkLogicBundle.message("endpoints.rewriter.user.label"), user)
        }

    override val reference: PsiReference?
        get() = when {
            endpoint.value.text.isBlank() -> null
            else -> ModuleUriReference(endpoint)
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
        CommonDataKeys.PSI_ELEMENT.name -> endpoint.value.textElements.firstOrNull() ?: endpoint
        else -> null
    }

    // endregion
    // region RewriterEndpoint

    private val dispatch: String
        get() = endpoint.value.text

    private val accept: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-accept").firstOrNull()?.getAttributeValue("any-of")

    private val contentType: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-content-type").firstOrNull()?.getAttributeValue("any-of")

    private val cookie: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-cookie").firstOrNull()?.getAttributeValue("name")

    private val executePrivilege: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-execute-privilege").firstOrNull()?.let { privilege ->
            val anyOf = privilege.getAttributeValue("any-of")
            val allOf = privilege.getAttributeValue("all-of")
            anyOf ?: allOf
        }

    private val header: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-header").firstOrNull()?.let { matchPath ->
            val name = matchPath.getAttributeValue("name")
            val matches = matchPath.getAttributeValue("matches")
            name ?: matches
        }

    private val paths: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-path").firstOrNull()?.let { matchPath ->
            val matches = matchPath.getAttributeValue("matches")
            val anyOf = matchPath.getAttributeValue("any-of")
            val prefix = matchPath.getAttributeValue("prefix")
            matches ?: anyOf ?: prefix
        }

    private val queryParam: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-query-param").firstOrNull()?.getAttributeValue("name")

    private val role: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-role").firstOrNull()?.let { matchPath ->
            val anyOf = matchPath.getAttributeValue("any-of")
            val allOf = matchPath.getAttributeValue("all-of")
            anyOf ?: allOf
        }

    private val user: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-user").firstOrNull()?.let { matchPath ->
            val name = matchPath.getAttributeValue("name")
            val default = matchPath.getAttributeValue("default-user") == "true"
            if (default)
                MarkLogicBundle.message("endpoints.rewriter.default-user.label")
            else
                name
        }

    // endregion
}
