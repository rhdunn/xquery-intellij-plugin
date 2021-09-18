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
import com.intellij.openapi.Disposable
import com.intellij.openapi.extensions.ExtensionPointListener
import com.intellij.openapi.extensions.PluginDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.util.CachedValue
import uk.co.reecedunn.intellij.microservices.endpoints.*

class RewriterEndpointsProvider :
    EndpointsProvider<RewriterEndpointsGroup, RewriterEndpoint>,
    ExtensionPointListener<EndpointsProvider<*, *>>,
    Disposable {
    companion object {
        val GROUPS: Key<CachedValue<List<RewriterEndpointsGroup>>> = Key.create("GROUPS")
    }
    // region EndpointsProvider

    override val endpointType: EndpointType = HTTP_SERVER_TYPE

    override val presentation: FrameworkPresentation
        get() = RewriterEndpointsFramework.presentation

    override fun getEndpointData(group: RewriterEndpointsGroup, endpoint: RewriterEndpoint, dataId: String): Any? {
        return endpoint.getData(dataId)
    }

    override fun getEndpointGroups(project: Project, filter: EndpointsFilter): Iterable<RewriterEndpointsGroup> {
        return RewriterEndpointsFramework.groups(project)
    }

    override fun getEndpointPresentation(group: RewriterEndpointsGroup, endpoint: RewriterEndpoint): ItemPresentation {
        return endpoint
    }

    override fun getEndpoints(group: RewriterEndpointsGroup): Iterable<RewriterEndpoint> {
        return group.endpoints.asIterable()
    }

    override fun getModificationTracker(project: Project): ModificationTracker = ModificationTracker.NEVER_CHANGED

    override fun getStatus(project: Project): EndpointsProviderStatus = when {
        RewriterEndpointsFramework.groups(project).isNotEmpty() -> EndpointsProviderStatus.AVAILABLE
        else -> EndpointsProviderStatus.HAS_ENDPOINTS
    }

    override fun isValidEndpoint(group: RewriterEndpointsGroup, endpoint: RewriterEndpoint): Boolean = true

    // endregion
    // region ExtensionPointListener

    override fun extensionAdded(extension: EndpointsProvider<*, *>, pluginDescriptor: PluginDescriptor) {
        RewriterEndpointsFramework.clearUserData(GROUPS)
    }

    override fun extensionRemoved(extension: EndpointsProvider<*, *>, pluginDescriptor: PluginDescriptor) {
        RewriterEndpointsFramework.clearUserData(GROUPS)
    }

    // endregion
    // region Disposable

    override fun dispose() {
    }

    // endregion
    init {
        EndpointsProvider.EP_NAME.point.addExtensionPointListener(this, false, this)
    }
}
