/*
 * Copyright (C) 2020 Reece H. Dunn
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

import com.intellij.microservices.EndpointType
import com.intellij.microservices.EndpointsProvider
import com.intellij.microservices.EndpointsViewProvider
import com.intellij.microservices.VisibilityScope
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.project.Project
import javax.swing.JComponent

@Suppress("UnstableApiUsage")
abstract class EndpointsProvider :
    EndpointsProvider<EndpointsGroup, Endpoint>,
    EndpointsViewProvider<EndpointsGroup, Endpoint>,
    EndpointsFramework {
    // region EndpointsProvider

    override val endpointType: EndpointType = EndpointType.API_DEFINITION

    abstract override val id: String

    override val viewProvider: EndpointsViewProvider<EndpointsGroup, Endpoint> get() = this

    override fun getEndpointGroups(project: Project, scope: VisibilityScope): List<EndpointsGroup> = groups(project)

    override fun getEndpoints(group: EndpointsGroup): List<Endpoint> = group.endpoints.toList()

    override fun hasEndpointGroups(project: Project, scope: VisibilityScope): Boolean {
        return getEndpointGroups(project, scope).isNotEmpty()
    }

    override fun isAvailable(project: Project): Boolean = true

    // endregion
    // region EndpointsViewProvider

    override val frameworkPresentation: ItemPresentation get() = presentation

    override val frameworkTag: String get() = id

    override fun getEndpointData(group: EndpointsGroup, endpoint: Endpoint, dataId: String): Any? = null

    override fun getEndpointDetails(group: EndpointsGroup, endpoint: Endpoint): JComponent? = null

    override fun getEndpointPresentation(group: EndpointsGroup, endpoint: Endpoint): ItemPresentation {
        return endpoint.presentation
    }

    override fun getGroupData(group: EndpointsGroup, dataId: String): Any? {
        return (group as? DataProvider)?.getData(dataId)
    }

    override fun getGroupPresentation(group: EndpointsGroup): ItemPresentation = group.presentation

    override fun isValidEndpoint(group: EndpointsGroup, endpoint: Endpoint): Boolean = true

    override fun isValidGroup(group: EndpointsGroup): Boolean = true

    // endregion
}
