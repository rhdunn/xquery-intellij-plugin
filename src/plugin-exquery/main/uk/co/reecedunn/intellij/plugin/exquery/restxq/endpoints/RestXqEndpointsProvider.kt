// Copyright (C) 2020-2022, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.exquery.restxq.endpoints

import com.intellij.compat.microservices.endpoints.*
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
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
        return endpoint.getData(dataId) ?: super.getEndpointData(group, endpoint, dataId)
    }

    override fun getDocumentationElement(group: RestXqEndpointsGroup, endpoint: RestXqEndpoint): PsiElement? {
        return endpoint.endpoint as? PsiElement
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
            CachedValueProvider.Result.create(groups, getModificationTracker(project))
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

    override fun getModificationTracker(project: Project): ModificationTracker {
        return PsiModificationTracker.getInstance(project).forLanguage(XQuery)
    }

    override fun getStatus(project: Project): EndpointsProviderStatus = when {
        getEndpointGroups(project).isNotEmpty() -> EndpointsProviderStatus.AVAILABLE
        else -> EndpointsProviderStatus.HAS_ENDPOINTS
    }

    override fun isValidEndpoint(group: RestXqEndpointsGroup, endpoint: RestXqEndpoint): Boolean = true

    // endregion
}
