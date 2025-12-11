// Copyright (C) 2021, 2023, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.microservices.endpoints

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.PsiElement

interface EndpointsProvider<Group : Any, Endpoint : Any> {
    val endpointType: EndpointType

    val presentation: FrameworkPresentation

    fun getEndpointData(group: Group, endpoint: Endpoint, dataId: String): Any? = null

    fun getDocumentationElement(group: Group, endpoint: Endpoint): PsiElement? = null

    fun getNavigationElement(group: Group, endpoint: Endpoint): PsiElement? {
        return getDocumentationElement(group, endpoint)
    }

    fun getEndpointGroups(project: Project, filter: EndpointsFilter): Iterable<Group>

    fun getEndpointPresentation(group: Group, endpoint: Endpoint): ItemPresentation

    fun getEndpoints(group: Group): Iterable<Endpoint>

    fun getModificationTracker(project: Project): ModificationTracker

    fun getStatus(project: Project): EndpointsProviderStatus

    fun isValidEndpoint(group: Group, endpoint: Endpoint): Boolean
}
