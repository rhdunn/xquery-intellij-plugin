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
package uk.co.reecedunn.intellij.plugin.xpath.resources

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object XPathIcons {
    private fun getIcon(path: String): Icon = IconLoader.getIcon(path, XPathIcons::class.java)

    val FileType: Icon = getIcon("/icons/xpath/fileType.svg")
    val RunConfiguration: Icon = getIcon("/icons/xpath/runConfiguration.svg")

    object Nodes {
        val Expr: Icon = getIcon("/icons/xpath/nodes/expr.svg")
        val FunctionDecl: Icon = AllIcons.Nodes.Function
        val Param: Icon = AllIcons.Nodes.Parameter
        val TypeDecl: Icon = getIcon("/icons/xpath/nodes/typeDecl.svg")
        val Variable: Icon = AllIcons.Nodes.Variable
        val VarDecl: Icon = AllIcons.Nodes.Variable
    }
}
