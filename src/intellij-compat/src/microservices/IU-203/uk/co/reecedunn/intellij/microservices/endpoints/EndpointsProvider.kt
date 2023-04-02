// Copyright (C) 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.microservices.endpoints

import com.intellij.psi.PsiElement

@Suppress("UnstableApiUsage")
interface EndpointsProvider<Group : Any, Endpoint : Any> :
    com.intellij.microservices.endpoints.EndpointsProvider<Group, Endpoint> {

    override fun getEndpointData(group: Group, endpoint: Endpoint, dataId: String): Any? = null

    fun getDocumentationElement(group: Group, endpoint: Endpoint): PsiElement? = null
}
