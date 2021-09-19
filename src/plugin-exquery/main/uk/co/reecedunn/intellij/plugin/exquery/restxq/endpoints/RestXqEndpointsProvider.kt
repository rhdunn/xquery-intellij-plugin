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
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import uk.co.reecedunn.intellij.microservices.endpoints.*
import uk.co.reecedunn.intellij.plugin.core.util.UserDataHolderBase
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.exquery.resources.EXQueryBundle
import uk.co.reecedunn.intellij.plugin.exquery.resources.EXQueryIcons
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryLibraryModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery

@Suppress("unused")
class RestXqEndpointsProvider :
    UserDataHolderBase(),
    EndpointsProvider<RestXqEndpointsGroup, RestXqEndpoint> {
    companion object {
        val GROUPS: Key<CachedValue<List<RestXqEndpointsGroup>>> = Key.create("GROUPS")
    }
    // region EndpointsProvider

    override val endpointType: EndpointType = HTTP_SERVER_TYPE

    override val presentation: FrameworkPresentation = FrameworkPresentation(
        "xijp.exquery-restxq",
        EXQueryBundle.message("endpoints.restxq.label"),
        EXQueryIcons.RESTXQ.EndpointsFramework
    )

    override fun getEndpointData(group: RestXqEndpointsGroup, endpoint: RestXqEndpoint, dataId: String): Any? {
        return endpoint.getData(dataId)
    }

    private fun getEndpointGroups(project: Project): List<RestXqEndpointsGroup> {
        return CachedValuesManager.getManager(project).getCachedValue(this, GROUPS, {
            val groups = ArrayList<RestXqEndpointsGroup>()
            ProjectRootManager.getInstance(project).fileIndex.iterateContent {
                val module = it.toPsiFile(project) as? XQueryModule
                (module?.mainOrLibraryModule as? XQueryLibraryModule)?.prolog?.forEach { prolog ->
                    val group = RestXqEndpointsGroup(prolog)
                    if (group.endpoints.any()) {
                        groups.add(group)
                    }
                }
                true
            }

            val tracker = PsiModificationTracker.SERVICE.getInstance(project).forLanguage(XQuery)
            CachedValueProvider.Result.create(groups, tracker)
        }, false)
    }

    override fun getEndpointGroups(project: Project, filter: EndpointsFilter): Iterable<RestXqEndpointsGroup> {
        return getEndpointGroups(project)
    }

    override fun getEndpointPresentation(group: RestXqEndpointsGroup, endpoint: RestXqEndpoint): ItemPresentation {
        return endpoint
    }

    override fun getEndpoints(group: RestXqEndpointsGroup): Iterable<RestXqEndpoint> {
        return group.endpoints.asIterable()
    }

    override fun getModificationTracker(project: Project): ModificationTracker = ModificationTracker.NEVER_CHANGED

    override fun getStatus(project: Project): EndpointsProviderStatus = when {
        getEndpointGroups(project).isNotEmpty() -> EndpointsProviderStatus.AVAILABLE
        else -> EndpointsProviderStatus.HAS_ENDPOINTS
    }

    override fun isValidEndpoint(group: RestXqEndpointsGroup, endpoint: RestXqEndpoint): Boolean = true

    // endregion
}
