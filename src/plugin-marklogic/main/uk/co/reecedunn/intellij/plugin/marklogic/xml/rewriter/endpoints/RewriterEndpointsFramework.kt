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

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.xml.XmlFile
import uk.co.reecedunn.intellij.microservices.endpoints.FrameworkPresentation
import uk.co.reecedunn.intellij.plugin.core.util.UserDataHolderBase
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicBundle
import uk.co.reecedunn.intellij.plugin.marklogic.resources.MarkLogicIcons
import uk.co.reecedunn.intellij.plugin.marklogic.xml.rewriter.Rewriter

object RewriterEndpointsFramework : UserDataHolderBase() {
    val presentation: FrameworkPresentation = FrameworkPresentation(
        "xijp.marklogic-rewriter",
        MarkLogicBundle.message("endpoints.rewriter.label"),
        MarkLogicIcons.Rewriter.EndpointsFramework
    )

    fun groups(project: Project): List<RewriterEndpointsGroup> {
        return CachedValuesManager.getManager(project).getCachedValue(this, RewriterEndpointsProvider.GROUPS, {
            val groups = ArrayList<RewriterEndpointsGroup>()
            ProjectRootManager.getInstance(project).fileIndex.iterateContent {
                val file = it.toPsiFile(project) as? XmlFile ?: return@iterateContent true
                val root = file.rootTag ?: return@iterateContent true
                if (root.namespace == Rewriter.NAMESPACE && root.localName == "rewriter") {
                    groups.add(RewriterEndpointsGroup(root))
                }
                true
            }
            CachedValueProvider.Result.create(groups, PsiModificationTracker.MODIFICATION_COUNT)
        }, false)
    }
}
