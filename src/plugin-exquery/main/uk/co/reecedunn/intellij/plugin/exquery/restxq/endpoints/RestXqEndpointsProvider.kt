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
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ModificationTracker
import uk.co.reecedunn.intellij.microservices.endpoints.*

@Suppress("unused")
class RestXqEndpointsProvider : EndpointsProvider<RestXqEndpointsGroup, RestXqEndpoint> {
    override val endpointType: EndpointType = HTTP_SERVER_TYPE

    override val presentation: FrameworkPresentation
        get() = RestXqEndpointsFramework.presentation


    override fun getEndpointData(group: RestXqEndpointsGroup, endpoint: RestXqEndpoint, dataId: String): Any? {
        return endpoint.getData(dataId)
    }

    private var cachedEndpointGroups: List<RestXqEndpointsGroup> = listOf()

    override fun getEndpointGroups(project: Project, filter: EndpointsFilter): Iterable<RestXqEndpointsGroup> {
        return cachedEndpointGroups
    }

    override fun getEndpointPresentation(group: RestXqEndpointsGroup, endpoint: RestXqEndpoint): ItemPresentation {
        return endpoint.presentation
    }

    override fun getEndpoints(group: RestXqEndpointsGroup): Iterable<RestXqEndpoint> {
        return group.endpoints.asIterable()
    }

    override fun getModificationTracker(project: Project): ModificationTracker = ModificationTracker.NEVER_CHANGED

    override fun getStatus(project: Project): EndpointsProviderStatus {
        cachedEndpointGroups = RestXqEndpointsFramework.groups(project)
        return if (cachedEndpointGroups.isNotEmpty())
            EndpointsProviderStatus.AVAILABLE
        else
            EndpointsProviderStatus.HAS_ENDPOINTS
    }

    override fun isValidEndpoint(group: RestXqEndpointsGroup, endpoint: RestXqEndpoint): Boolean = true
}
