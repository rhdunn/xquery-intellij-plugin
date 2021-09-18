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
package uk.co.reecedunn.intellij.plugin.marklogic.xml.rewriter.endpoints

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlTag
import uk.co.reecedunn.intellij.microservices.endpoints.presentation.EndpointMethodPresentation
import uk.co.reecedunn.intellij.plugin.core.xml.ancestors
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicBundle
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicIcons
import uk.co.reecedunn.intellij.plugin.marklogic.xml.rewriter.Rewriter
import uk.co.reecedunn.intellij.plugin.xquery.psi.reference.ModuleUriReference
import uk.co.reecedunn.intellij.plugin.xdm.xml.impl.XmlPsiAccessorsProvider
import javax.swing.Icon

class RewriterEndpoint(val endpoint: XmlTag) :
    ItemPresentation,
    EndpointMethodPresentation,
    DataProvider {
    // region ItemPresentation

    override fun getPresentableText(): String? = path

    override fun getLocationString(): String? = dispatch.split("/").lastOrNull()

    override fun getIcon(unused: Boolean): Icon = MarkLogicIcons.Rewriter.Endpoint

    // endregion
    // region EndpointMethodPresentation

    override val endpointMethod: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-method").firstOrNull()?.getAttributeValue("any-of")

    override val endpointMethodOrder: Int
        get() = EndpointMethodPresentation.getHttpMethodOrder(endpointMethod?.split("\\s+")?.get(0))

    // endregion
    // region DataProvider

    override fun getData(dataId: String): Any? = when (dataId) {
        CommonDataKeys.PSI_ELEMENT.name -> endpoint.value.textElements.firstOrNull() ?: endpoint
        else -> null
    }

    // endregion
    // region RewriterEndpoint

    val endpointTarget: PsiElement? = when {
        endpoint.value.text.isBlank() -> null
        else -> ModuleUriReference(endpoint, endpoint, XmlPsiAccessorsProvider).resolve()
    }

    val path: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-path").firstOrNull()?.let { matchPath ->
            val matches = matchPath.getAttributeValue("matches")
            val anyOf = matchPath.getAttributeValue("any-of")?.split("\\s+".toRegex())?.getOrNull(0)
            val prefix = matchPath.getAttributeValue("prefix")
            matches ?: anyOf ?: prefix
        }

    private val dispatch: String
        get() = endpoint.value.text

    @Suppress("unused")
    private val accept: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-accept").firstOrNull()?.getAttributeValue("any-of")

    @Suppress("unused")
    private val contentType: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-content-type").firstOrNull()?.getAttributeValue("any-of")

    @Suppress("unused")
    private val cookie: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-cookie").firstOrNull()?.getAttributeValue("name")

    @Suppress("unused")
    private val executePrivilege: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-execute-privilege").firstOrNull()?.let { privilege ->
            val anyOf = privilege.getAttributeValue("any-of")
            val allOf = privilege.getAttributeValue("all-of")
            anyOf ?: allOf
        }

    @Suppress("unused")
    private val header: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-header").firstOrNull()?.let { matchPath ->
            val name = matchPath.getAttributeValue("name")
            val matches = matchPath.getAttributeValue("matches")
            name ?: matches
        }

    @Suppress("unused")
    private val paths: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-path").firstOrNull()?.let { matchPath ->
            val matches = matchPath.getAttributeValue("matches")
            val anyOf = matchPath.getAttributeValue("any-of")
            val prefix = matchPath.getAttributeValue("prefix")
            matches ?: anyOf ?: prefix
        }

    @Suppress("unused")
    private val queryParam: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-query-param").firstOrNull()?.getAttributeValue("name")

    @Suppress("unused")
    private val role: String?
        get() = endpoint.ancestors(Rewriter.NAMESPACE, "match-role").firstOrNull()?.let { matchPath ->
            val anyOf = matchPath.getAttributeValue("any-of")
            val allOf = matchPath.getAttributeValue("all-of")
            anyOf ?: allOf
        }

    @Suppress("unused")
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
