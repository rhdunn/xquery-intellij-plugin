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
package uk.co.reecedunn.intellij.plugin.exquery.restxq.endpoints

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import uk.co.reecedunn.intellij.microservices.endpoints.EndpointsGroup
import uk.co.reecedunn.intellij.microservices.endpoints.EndpointsProvider
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.exquery.intellij.resources.EXQueryBundle
import uk.co.reecedunn.intellij.plugin.exquery.intellij.resources.EXQueryIcons
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryLibraryModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import javax.swing.Icon

object RestXqEndpointProvider : EndpointsProvider(), ItemPresentation {
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon? = EXQueryIcons.RESTXQ.EndpointsFramework

    override fun getLocationString(): String? = null

    override fun getPresentableText(): String? = EXQueryBundle.message("endpoints.restxq.label")

    // endregion
    // region EndpointsFramework

    override val id: String = "xijp.exquery-restxq"

    override val presentation: ItemPresentation get() = this

    override fun groups(project: Project): List<EndpointsGroup> {
        val groups = ArrayList<EndpointsGroup>()
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
        return groups
    }

    // endregion
}
