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
package uk.co.reecedunn.intellij.plugin.exquery.restxq.endpoints

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import uk.co.reecedunn.intellij.microservices.endpoints.Endpoint
import uk.co.reecedunn.intellij.microservices.endpoints.presentation.EndpointMethodPresentation
import uk.co.reecedunn.intellij.plugin.core.ui.layout.details
import uk.co.reecedunn.intellij.plugin.core.ui.layout.detailsPanel
import uk.co.reecedunn.intellij.plugin.exquery.resources.EXQueryBundle
import uk.co.reecedunn.intellij.plugin.exquery.resources.EXQueryIcons
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
import javax.swing.Icon
import javax.swing.JPanel

class RestXqEndpoint(private val endpoint: XpmFunctionDeclaration) :
    Endpoint,
    ItemPresentation,
    EndpointMethodPresentation,
    DataProvider {
    // region Endpoint

    override val presentation: ItemPresentation
        get() = this

    override val details: JPanel
        get() = detailsPanel {
            val rest = rest ?: return@detailsPanel
            details(EXQueryBundle.message("endpoints.restxq.path.label"), rest.path)
            details(EXQueryBundle.message("endpoints.restxq.method.label"), rest.methods)
            details(EXQueryBundle.message("endpoints.restxq.consumes.label"), rest.consumes)
            details(EXQueryBundle.message("endpoints.restxq.produces.label"), rest.produces)
            details(EXQueryBundle.message("endpoints.restxq.query-params.label"), rest.queryParams)
            details(EXQueryBundle.message("endpoints.restxq.form-params.label"), rest.formParams)
            details(EXQueryBundle.message("endpoints.restxq.header-params.label"), rest.headerParams)
            details(EXQueryBundle.message("endpoints.restxq.cookie-params.label"), rest.cookieParams)
        }

    override val reference: PsiReference? = (endpoint as PsiElement).reference

    override val element: PsiElement = endpoint as PsiElement

    override val path: String? = rest?.path

    // endregion
    // region ItemPresentation

    override fun getPresentableText(): String? = path

    override fun getLocationString(): String? = endpoint.functionRefPresentableText

    override fun getIcon(unused: Boolean): Icon = EXQueryIcons.RESTXQ.Endpoint

    // endregion
    // region EndpointMethodPresentation

    override val endpointMethod: String?
        get() = rest?.methods?.joinToString(" ")

    override val endpointMethodOrder: Int
        get() = EndpointMethodPresentation.getHttpMethodOrder(rest?.methods?.firstOrNull())

    // endregion
    // region DataProvider

    override fun getData(dataId: String): Any? = when (dataId) {
        CommonDataKeys.PSI_ELEMENT.name -> endpoint as PsiElement
        else -> null
    }

    // endregion
    // region RestXqEndpoint

    val rest: RestXqAnnotations?
        get() = RestXqAnnotations.create(endpoint)

    // endregion
}
