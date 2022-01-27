/*
 * Copyright (C) 2020-2022 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.rewriter.endpoints

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ModificationTracker
import uk.co.reecedunn.intellij.microservices.endpoints.*
import uk.co.reecedunn.intellij.microservices.endpoints.presentation.EndpointMethodPresentation
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicBundle
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicIcons

@Suppress("unused")
class RewriterEndpointsProvider : EndpointsProvider<RewriterEndpointsGroup, RewriterEndpoint> {
    // region EndpointsProvider

    override val endpointType: EndpointType = HTTP_SERVER_TYPE

    override val presentation: FrameworkPresentation = FrameworkPresentation(
        "xijp.marklogic-rewriter",
        MarkLogicBundle.message("endpoints.rewriter.label"),
        MarkLogicIcons.Rewriter.EndpointsFramework
    )

    override fun getEndpointData(group: RewriterEndpointsGroup, endpoint: RewriterEndpoint, dataId: String): Any? {
        return endpoint.getData(dataId)
    }

    private fun getEndpointGroups(project: Project): Iterable<RewriterEndpointsGroup> {
        return Rewriter.getInstance().getEndpointGroups(project)
    }

    override fun getEndpointGroups(project: Project, filter: EndpointsFilter): Iterable<RewriterEndpointsGroup> {
        return getEndpointGroups(project)
    }

    override fun getEndpointPresentation(group: RewriterEndpointsGroup, endpoint: RewriterEndpoint): ItemPresentation {
        return EndpointMethodPresentation(endpoint, endpoint.endpointMethod, endpoint.endpointMethodOrder)
    }

    override fun getEndpoints(group: RewriterEndpointsGroup): Iterable<RewriterEndpoint> {
        return group.endpoints.asIterable()
    }

    override fun getModificationTracker(project: Project): ModificationTracker {
        return Rewriter.getInstance().getModificationTracker(project)
    }

    override fun getStatus(project: Project): EndpointsProviderStatus = when {
        getEndpointGroups(project).any() -> EndpointsProviderStatus.AVAILABLE
        else -> EndpointsProviderStatus.HAS_ENDPOINTS
    }

    override fun isValidEndpoint(group: RewriterEndpointsGroup, endpoint: RewriterEndpoint): Boolean = true

    // endregion
}
