/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xdm.module.path

import com.intellij.openapi.project.Project
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUriValue

class XdmModuleLocationPath internal constructor(
    val project: Project,
    val path: String,
    override val moduleTypes: Array<XdmModuleType>,
    val isResource: Boolean?
) : XdmModulePath {
    companion object : XdmModulePathFactory {
        private const val EXISTDB_PATH = "xmldb:exist://"
        private const val RES_PATH = "resource:"

        override fun create(project: Project, uri: XsAnyUriValue): XdmModuleLocationPath? {
            return when (uri.context) {
                XdmUriContext.Location -> {
                    val path = uri.data
                    when {
                        path.isEmpty() -> null
                        path.startsWith(EXISTDB_PATH) /* eXist-db */ -> {
                            XdmModuleLocationPath(project, path.substring(14), uri.moduleTypes, false)
                        }
                        path.startsWith(RES_PATH) /* eXist-db */ -> {
                            XdmModuleLocationPath(project, path.substring(9), uri.moduleTypes, true)
                        }
                        path.contains(':') && !path.contains('/') -> null
                        else -> XdmModuleLocationPath(project, path, uri.moduleTypes, false) // eXist-db, MarkLogic
                    }
                }
                else -> null
            }
        }
    }
}
