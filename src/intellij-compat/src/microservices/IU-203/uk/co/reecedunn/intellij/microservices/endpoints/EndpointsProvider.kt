// Copyright (C) 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.microservices.endpoints

import com.intellij.microservices.endpoints.EndpointsProvider.Companion.DOCUMENTATION_ELEMENT
import com.intellij.psi.PsiElement

@Suppress("UnstableApiUsage")
interface EndpointsProvider<Group : Any, Endpoint : Any> :
    com.intellij.microservices.endpoints.EndpointsProvider<Group, Endpoint> {

    override fun getEndpointData(group: Group, endpoint: Endpoint, dataId: String): Any? = when (dataId) {
        DOCUMENTATION_ELEMENT.name -> getDocumentationElement(group, endpoint)
        else -> null
    }

    fun getDocumentationElement(group: Group, endpoint: Endpoint): PsiElement? = null

    fun getNavigationElement(group: Group, endpoint: Endpoint): PsiElement? {
        return getDocumentationElement(group, endpoint)
    }
}
