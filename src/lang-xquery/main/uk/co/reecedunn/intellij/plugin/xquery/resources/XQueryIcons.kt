/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.resources

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object XQueryIcons {
    private fun getIcon(path: String): Icon = IconLoader.getIcon(path, XQueryIcons::class.java)

    val FileType: Icon = getIcon("/icons/xquery/fileType.svg")
    val RunConfiguration: Icon = getIcon("/icons/xquery/runConfiguration.svg")

    object Endpoints {
        val EndpointOverlay: Icon = getIcon("/icons/web_overlay.svg")
        val FunctionEndpoint: Icon = getIcon("/icons/nodes/functionEndpoint.svg")
    }

    object Nodes {
        val Annotation: Icon = AllIcons.Nodes.Annotationtype
        var QueryBody: Icon = AllIcons.Nodes.EntryPoints
    }
}
