// Copyright (C) 2019-2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xpm.module.path.impl

import com.intellij.openapi.project.Project
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePath
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePathFactory

class XpmModuleLocationPath internal constructor(
    val project: Project,
    val path: String,
    override val moduleTypes: Array<XdmModuleType>,
    val isResource: Boolean?
) : XpmModulePath {
    companion object : XpmModulePathFactory {
        private const val EXISTDB_PATH = "xmldb:exist://"
        private const val RES_PATH = "resource:"

        override fun create(project: Project, uri: XsAnyUriValue): XpmModuleLocationPath? = when (uri.context) {
            XdmUriContext.Location -> create(project, uri.data, uri.moduleTypes)
            else -> null
        }

        fun create(project: Project, path: String, moduleTypes: Array<XdmModuleType>): XpmModuleLocationPath? = when {
            path.isEmpty() -> null
            path.startsWith(EXISTDB_PATH) /* eXist-db */ -> {
                XpmModuleLocationPath(project, path.substring(14), moduleTypes, false)
            }
            path.startsWith(RES_PATH) /* eXist-db */ -> {
                XpmModuleLocationPath(project, path.substring(9), moduleTypes, true)
            }
            path.contains(':') && !path.contains('/') -> null
            else -> XpmModuleLocationPath(project, path, moduleTypes, false) // eXist-db, MarkLogic
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as XpmModuleLocationPath

        if (project != other.project) return false
        if (path != other.path) return false
        if (!moduleTypes.contentEquals(other.moduleTypes)) return false
        if (isResource != other.isResource) return false

        return true
    }

    override fun hashCode(): Int {
        var result = project.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + moduleTypes.contentHashCode()
        result = 31 * result + (isResource?.hashCode() ?: 0)
        return result
    }
}
