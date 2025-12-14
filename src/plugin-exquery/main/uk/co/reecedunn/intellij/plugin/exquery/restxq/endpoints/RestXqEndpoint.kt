// Copyright (C) 2020-2023, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.exquery.restxq.endpoints

import com.intellij.compat.actionSystem.DataSink
import com.intellij.compat.actionSystem.UiDataProvider
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.psi.PsiElement
import com.intellij.compat.microservices.endpoints.presentation.EndpointMethodPresentation
import com.intellij.compat.microservices.endpoints.presentation.HttpMethodPresentation
import uk.co.reecedunn.intellij.plugin.exquery.resources.EXQueryIcons
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
import javax.swing.Icon

@Suppress("RedundantSuppression", "UnstableApiUsage")
class RestXqEndpoint(val endpoint: XpmFunctionDeclaration) :
    EndpointMethodPresentation,
    ItemPresentation,
    UiDataProvider,
    DataProvider {
    // region ItemPresentation

    override fun getPresentableText(): String? = rest?.path

    override fun getLocationString(): String? = endpoint.functionRefPresentableText

    override fun getIcon(unused: Boolean): Icon = EXQueryIcons.RESTXQ.Endpoint

    // endregion
    // region EndpointMethodPresentation

    override val endpointMethodPresentation: String by lazy {
        HttpMethodPresentation.getHttpMethodsPresentation(endpointMethods)
    }

    override val endpointMethodOrder: Int by lazy {
        HttpMethodPresentation.getHttpMethodOrder(endpointMethods.firstOrNull())
    }

    override val endpointMethods: List<String>
        get() = rest?.methods ?: listOf()

    // endregion
    // region UiDataProvider

    override fun uiDataSnapshot(sink: DataSink) {
        sink[CommonDataKeys.PSI_ELEMENT] = endpoint as PsiElement
    }

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
