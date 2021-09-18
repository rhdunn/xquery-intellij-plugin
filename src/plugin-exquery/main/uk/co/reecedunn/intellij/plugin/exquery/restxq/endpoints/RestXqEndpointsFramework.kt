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

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import uk.co.reecedunn.intellij.microservices.endpoints.EndpointsFramework
import uk.co.reecedunn.intellij.microservices.endpoints.EndpointsGroup
import uk.co.reecedunn.intellij.microservices.endpoints.FrameworkPresentation
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.exquery.resources.EXQueryBundle
import uk.co.reecedunn.intellij.plugin.exquery.resources.EXQueryIcons
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryLibraryModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule

object RestXqEndpointsFramework : EndpointsFramework {
    override val presentation: FrameworkPresentation = FrameworkPresentation(
        "xijp.exquery-restxq",
        EXQueryBundle.message("endpoints.restxq.label"),
        EXQueryIcons.RESTXQ.EndpointsFramework
    )

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
}
