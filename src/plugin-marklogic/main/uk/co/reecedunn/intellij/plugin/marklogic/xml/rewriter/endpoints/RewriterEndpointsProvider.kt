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
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ModificationTracker
import uk.co.reecedunn.intellij.microservices.endpoints.*

class RewriterEndpointsProvider : EndpointsProvider<RewriterEndpointsGroup, RewriterEndpoint> {
    override val endpointType: EndpointType = HTTP_SERVER_TYPE

    override val presentation: FrameworkPresentation
        get() = RewriterEndpointsFramework.presentation

    override fun getEndpointData(group: RewriterEndpointsGroup, endpoint: RewriterEndpoint, dataId: String): Any? {
        return endpoint.getData(dataId)
    }

    private var cachedEndpointGroups: List<RewriterEndpointsGroup> = listOf()

    override fun getEndpointGroups(project: Project, filter: EndpointsFilter): Iterable<RewriterEndpointsGroup> {
        return cachedEndpointGroups
    }

    override fun getEndpointPresentation(group: RewriterEndpointsGroup, endpoint: RewriterEndpoint): ItemPresentation {
        return endpoint.presentation
    }

    override fun getEndpoints(group: RewriterEndpointsGroup): Iterable<RewriterEndpoint> {
        return group.endpoints.toList()
    }

    override fun getModificationTracker(project: Project): ModificationTracker = ModificationTracker.NEVER_CHANGED

    override fun getStatus(project: Project): EndpointsProviderStatus {
        cachedEndpointGroups = RewriterEndpointsFramework.groups(project)
        return if (cachedEndpointGroups.isNotEmpty())
            EndpointsProviderStatus.AVAILABLE
        else
            EndpointsProviderStatus.HAS_ENDPOINTS
    }

    override fun isValidEndpoint(group: RewriterEndpointsGroup, endpoint: RewriterEndpoint): Boolean = true
}
