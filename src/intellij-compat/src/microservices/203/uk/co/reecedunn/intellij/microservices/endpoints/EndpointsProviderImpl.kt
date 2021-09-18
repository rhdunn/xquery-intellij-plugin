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
package uk.co.reecedunn.intellij.microservices.endpoints

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ModificationTracker

@Suppress("UnstableApiUsage")
abstract class EndpointsProviderImpl : EndpointsProvider<EndpointsGroup, Endpoint>, EndpointsFramework {
    private var cachedEndpointGroups: List<EndpointsGroup> = listOf()

    override val endpointType: EndpointType = HTTP_SERVER_TYPE

    override fun getEndpointData(group: EndpointsGroup, endpoint: Endpoint, dataId: String): Any? {
        return (endpoint as? DataProvider)?.getData(dataId)
    }

    override fun getEndpointGroups(project: Project, filter: EndpointsFilter): List<EndpointsGroup> {
        return cachedEndpointGroups
    }

    override fun getEndpointPresentation(group: EndpointsGroup, endpoint: Endpoint): ItemPresentation {
        return endpoint.presentation
    }

    override fun getEndpoints(group: EndpointsGroup): List<Endpoint> = group.endpoints.toList()

    override fun getModificationTracker(project: Project): ModificationTracker {
        return ModificationTracker.NEVER_CHANGED
    }

    override fun getStatus(project: Project): EndpointsProviderStatus {
        cachedEndpointGroups = groups(project)
        return if (cachedEndpointGroups.isNotEmpty())
            EndpointsProviderStatus.AVAILABLE
        else
            EndpointsProviderStatus.HAS_ENDPOINTS
    }

    override fun isValidEndpoint(group: EndpointsGroup, endpoint: Endpoint): Boolean = true
}
