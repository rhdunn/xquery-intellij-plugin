/*
 * Copyright (C) 2020-2023 Reece H. Dunn
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
import uk.co.reecedunn.intellij.microservices.endpoints.presentation.EndpointMethodPresentation
import uk.co.reecedunn.intellij.microservices.endpoints.presentation.HttpMethodPresentation
import uk.co.reecedunn.intellij.plugin.exquery.resources.EXQueryIcons
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
import javax.swing.Icon

class RestXqEndpoint(private val endpoint: XpmFunctionDeclaration) :
    ItemPresentation,
    DataProvider {
    // region ItemPresentation

    override fun getPresentableText(): String? = rest?.path

    override fun getLocationString(): String? = endpoint.functionRefPresentableText

    override fun getIcon(unused: Boolean): Icon = EXQueryIcons.RESTXQ.Endpoint

    // endregion
    // region EndpointMethodPresentation

    val endpointMethod: String?
        get() = rest?.methods?.joinToString(" ")

    @Suppress("UnstableApiUsage")
    val endpointMethodOrder: Int
        get() = HttpMethodPresentation.getHttpMethodOrder(rest?.methods?.firstOrNull())

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
