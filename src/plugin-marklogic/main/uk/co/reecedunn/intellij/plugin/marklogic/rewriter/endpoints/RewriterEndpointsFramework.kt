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
package uk.co.reecedunn.intellij.plugin.marklogic.rewriter.endpoints

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.xml.XmlFile
import uk.co.reecedunn.intellij.microservices.endpoints.EndpointsFramework
import uk.co.reecedunn.intellij.microservices.endpoints.EndpointsGroup
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicBundle
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicIcons
import uk.co.reecedunn.intellij.plugin.marklogic.rewriter.Rewriter
import javax.swing.Icon

object RewriterEndpointsFramework : EndpointsFramework {
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon = MarkLogicIcons.Rewriter.EndpointsFramework

    override fun getLocationString(): String? = null

    override fun getPresentableText(): String = MarkLogicBundle.message("endpoints.rewriter.label")

    // endregion
    // region EndpointsFramework

    override val id: String = "xijp.marklogic-rewriter"

    override fun groups(project: Project): List<EndpointsGroup> {
        val groups = ArrayList<EndpointsGroup>()
        ProjectRootManager.getInstance(project).fileIndex.iterateContent {
            val file = it.toPsiFile(project) as? XmlFile ?: return@iterateContent true
            val root = file.rootTag ?: return@iterateContent true
            if (root.namespace == Rewriter.NAMESPACE && root.localName == "rewriter") {
                groups.add(RewriterEndpointsGroup(root))
            }
            true
        }
        return groups
    }

    // endregion
}
