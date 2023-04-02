// Copyright (C) 2021, 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.microservices.endpoints

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ModificationTracker

interface EndpointsProvider<Group : Any, Endpoint : Any> {
    val endpointType: EndpointType

    val presentation: FrameworkPresentation

    fun getEndpointData(group: Group, endpoint: Endpoint, dataId: String): Any? = null

    fun getEndpointGroups(project: Project, filter: EndpointsFilter): Iterable<Group>

    fun getEndpointPresentation(group: Group, endpoint: Endpoint): ItemPresentation

    fun getEndpoints(group: Group): Iterable<Endpoint>

    fun getModificationTracker(project: Project): ModificationTracker

    fun getStatus(project: Project): EndpointsProviderStatus

    fun isValidEndpoint(group: Group, endpoint: Endpoint): Boolean
}
