/*
 * Copyright (C) 2021 Reece H. Dunn
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
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ModificationTracker

interface EndpointsProvider<Group, Endpoint> {
    companion object {
        val EP_NAME: ExtensionPointName<EndpointsProvider<*, *>> = ExtensionPointName.create(
            "com.intellij.microservices.endpointsProvider"
        )
    }

    val endpointType: EndpointType

    val presentation: FrameworkPresentation

    fun getEndpointData(group: Group, endpoint: Endpoint, dataId: String): Any?

    fun getEndpointGroups(project: Project, filter: EndpointsFilter): Iterable<Group>

    fun getEndpointPresentation(group: Group, endpoint: Endpoint): ItemPresentation

    fun getEndpoints(group: Group): Iterable<Endpoint>

    fun getModificationTracker(project: Project): ModificationTracker

    fun getStatus(project: Project): EndpointsProviderStatus

    fun isValidEndpoint(group: Group, endpoint: Endpoint): Boolean
}
